package com.example.controllers;

import com.example.bdd.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Contrôleur pour l'interface de gestion des clients
 * Gère l'affichage, la recherche et les actions sur les clients
 */
public class GestionClientsController implements Initializable {

    // Composants de l'interface FXML
    @FXML
    private TextField searchField; // Champ de recherche
    
    @FXML
    private Button btnAjouterClient; // Bouton pour ajouter un client
    
    @FXML
    private GridPane clientsGrid; // Grille pour afficher les cartes clients

    // Liste des clients
    private List<Client> clients = new ArrayList<>();
    
    // Liste filtrée des clients selon la recherche
    private List<Client> clientsFiltres = new ArrayList<>();

    // Regex pour validation
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ]+(?:[-' ][A-Za-zÀ-ÖØ-öø-ÿ]+)*$");
    private static final Pattern TELEPHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    /**
     * Initialisation du contrôleur
     * Charge les données et configure les événements
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerClients();
        configurerRecherche();
        configurerBoutonAjouter();
        searchField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(
                        getClass().getResource("/styles/GestionClients.css").toExternalForm()
                );
            }
        });
    }

    /**
     * Charge les clients depuis la base de données
     */
    private void chargerClients() {
        clients.clear();
        String query = "SELECT codeClt, nom, prenom, adresse, numTel FROM client ORDER BY codeClt";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int codeClt = rs.getInt("codeClt");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String adresse = rs.getString("adresse");
                String numTel = rs.getString("numTel");
                clients.add(new Client(codeClt, nom, prenom, adresse, numTel));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur DB", "Impossible de charger les clients : " + e.getMessage());
            e.printStackTrace();
        }

        clientsFiltres = new ArrayList<>(clients);
        afficherClients();
    }

    /**
     * Configure la fonctionnalité de recherche en temps réel
     * Filtre les clients selon le texte saisi dans le champ de recherche
     */
    private void configurerRecherche() {
        // Écoute des changements dans le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerClients(newValue);
        });
    }

    /**
     * Filtre la liste des clients selon le critère de recherche
     * @param critere Le texte de recherche (nom, prénom ou code)
     */
    private void filtrerClients(String critere) {
        if (critere == null || critere.trim().isEmpty()) {
            clientsFiltres = new ArrayList<>(clients);
        } else {
            String critereLower = critere.toLowerCase().trim();
            clientsFiltres = clients.stream()
                    .filter(client -> 
                        String.valueOf(client.getCodeClt()).contains(critereLower) ||
                        client.getNom().toLowerCase().contains(critereLower) ||
                        client.getPrenom().toLowerCase().contains(critereLower) ||
                        (client.getNom() + " " + client.getPrenom()).toLowerCase().contains(critereLower)
                    )
                    .collect(Collectors.toList());
        }
        afficherClients();
    }

    /**
     * Configure le bouton d'ajout de client
     * Définit l'action à exécuter lors du clic
     */
    private void configurerBoutonAjouter() {
        btnAjouterClient.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterClient.fxml"));
                Parent root = loader.load();
                AjouterClientController controller = loader.getController();

                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle("Ajouter un client");
                dialog.setResizable(false);
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles/GestionClients.css").toExternalForm());
                dialog.setScene(scene);
                dialog.showAndWait();

                if (controller.isConfirme()) {
                    Client nouveau = controller.getNouveauClient();

                    // Validation
                    String nom = nouveau.getNom().trim();
                    String prenom = nouveau.getPrenom().trim();
                    String adresse = nouveau.getAdresse() != null ? nouveau.getAdresse().trim() : "";
                    String numTel = nouveau.getTelephone() != null ? nouveau.getTelephone().trim() : "";

                    if (!validateClient(nom, prenom, adresse, numTel)) return;

                    // Insertion dans la BDD
                    String insert = "INSERT INTO client (nom, prenom, adresse, numTel) VALUES (?, ?, ?, ?)";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, nom);
                        stmt.setString(2, prenom);
                        stmt.setString(3, adresse);
                        stmt.setString(4, numTel);

                        int affected = stmt.executeUpdate();
                        if (affected > 0) {
                            try (ResultSet keys = stmt.getGeneratedKeys()) {
                                if (keys.next()) {
                                    int id = keys.getInt(1);
                                    clients.add(new Client(id, nom, prenom, adresse, numTel));
                                    filtrerClients(searchField.getText());
                                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Client ajouté avec succès !");
                                }
                            }
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le client.");
                        }
                    }
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Valide les champs d'un client
     */
    private boolean validateClient(String nom, String prenom, String adresse, String numTel) {
        if (nom == null || nom.isEmpty() || prenom == null || prenom.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir le nom et le prénom.");
            return false;
        }

        if (!NAME_PATTERN.matcher(nom).matches()) {
            showAlert(Alert.AlertType.ERROR, "Nom invalide", "Le nom ne doit contenir que des lettres.");
            return false;
        }

        if (!NAME_PATTERN.matcher(prenom).matches()) {
            showAlert(Alert.AlertType.ERROR, "Prénom invalide", "Le prénom ne doit contenir que des lettres.");
            return false;
        }

        if (numTel != null && !numTel.isEmpty() && !TELEPHONE_PATTERN.matcher(numTel).matches()) {
            showAlert(Alert.AlertType.ERROR, "Téléphone invalide", "Le numéro de téléphone doit contenir exactement 10 chiffres.");
            return false;
        }

        return true;
    }


    /**
     * Affiche les cartes clients dans la grille
     * Crée dynamiquement les cartes pour chaque client filtré
     */
    private void afficherClients() {
        // Vider la grille avant de la remplir
        clientsGrid.getChildren().clear();
        
        // Calculer le nombre de colonnes (3 colonnes par ligne)
        int colonnes = 3;
        
        // Créer une carte pour chaque client filtré
        for (int i = 0; i < clientsFiltres.size(); i++) {
            Client client = clientsFiltres.get(i);
            
            // Calculer la position dans la grille
            int colonne = i % colonnes;
            int ligne = i / colonnes;
            
            // Créer et ajouter la carte client
            VBox carteClient = creerCarteClient(client);
            clientsGrid.add(carteClient, colonne, ligne);
        }
    }

    /**
     * Crée une carte visuelle pour un client
     * @param client Le client à afficher
     * @return La carte VBox créée
     */
    private VBox creerCarteClient(Client client) {
        // Conteneur principal de la carte
        VBox carte = new VBox(15);
        carte.getStyleClass().add("client-card");
        carte.setPadding(new Insets(25));
        carte.setPrefWidth(320);
        carte.setPrefHeight(220);
        
        // En-tête de la carte : Code client et boutons d'action
        HBox enTete = new HBox();
        enTete.setAlignment(Pos.CENTER_LEFT);
        enTete.setSpacing(10);
        HBox.setHgrow(enTete, Priority.ALWAYS);
        
        // Code client (ex: C001)
        Label codeLabel = new Label("C" + String.format("%03d", client.getCodeClt()));
        codeLabel.getStyleClass().add("client-code");
        HBox.setHgrow(codeLabel, Priority.ALWAYS);
        
        // Bouton modifier (icône crayon)
        Button btnModifier = new Button();
        btnModifier.getStyleClass().add("btn-icon");
        ImageView iconModifier = new ImageView(new Image("/assets/bouton-modifier.png"));
        iconModifier.setFitWidth(18);
        iconModifier.setFitHeight(18);
        iconModifier.setPreserveRatio(true);
        btnModifier.setGraphic(iconModifier);
        btnModifier.setOnAction(e -> modifierClient(client));
        
        // Bouton supprimer (icône poubelle)
        Button btnSupprimer = new Button();
        btnSupprimer.getStyleClass().add("btn-icon");
        ImageView iconSupprimer = new ImageView(new Image("/assets/supprimer.png"));
        iconSupprimer.setFitWidth(18);
        iconSupprimer.setFitHeight(18);
        iconSupprimer.setPreserveRatio(true);
        btnSupprimer.setGraphic(iconSupprimer);
        btnSupprimer.setOnAction(e -> supprimerClient(client));
        
        enTete.getChildren().addAll(codeLabel, btnModifier, btnSupprimer);
        
        // Nom du client
        Label nomLabel = new Label(client.getNom() + " " + client.getPrenom());
        nomLabel.getStyleClass().add("client-nom");
        
        // Informations de contact
        VBox infosContact = new VBox(10);
        
        // Adresse avec icône de localisation
        HBox adresseBox = new HBox(8);
        adresseBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconLocalisation = new ImageView(new Image("/assets/localisateur.png"));
        iconLocalisation.setFitWidth(16);
        iconLocalisation.setFitHeight(16);
        iconLocalisation.setPreserveRatio(true);
        iconLocalisation.setStyle("-fx-opacity: 0.6;");
        Label adresseLabel = new Label(client.getAdresse());
        adresseLabel.getStyleClass().add("client-info");
        adresseBox.getChildren().addAll(iconLocalisation, adresseLabel);
        
        // Téléphone avec icône de téléphone
        HBox telephoneBox = new HBox(8);
        telephoneBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconTelephone = new ImageView(new Image("/assets/telephone.png"));
        iconTelephone.setFitWidth(16);
        iconTelephone.setFitHeight(16);
        iconTelephone.setPreserveRatio(true);
        iconTelephone.setStyle("-fx-opacity: 0.6;");
        Label telephoneLabel = new Label(client.getTelephone());
        telephoneLabel.getStyleClass().add("client-info");
        telephoneBox.getChildren().addAll(iconTelephone, telephoneLabel);
        
        infosContact.getChildren().addAll(adresseBox, telephoneBox);
        
        // Bouton "Voir les livraisons"
        Button btnLivraisons = new Button("Voir les livraisons");
        btnLivraisons.getStyleClass().add("btn-livraisons");
        btnLivraisons.setOnAction(e -> voirLivraisons(client));
        
        // Assembler tous les éléments dans la carte
        carte.getChildren().addAll(enTete, nomLabel, infosContact, btnLivraisons);
        
        return carte;
    }

    /**
     * Action pour modifier un client
     * @param client Le client à modifier
     */
    private void modifierClient(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterClient.fxml"));
            Parent root = loader.load();

            AjouterClientController controller = loader.getController();
            controller.preparerModification(client);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Modifier un client");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionClients.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirme()) {
                Client modif = controller.getNouveauClient();

                // Validation
                String nom = modif.getNom().trim();
                String prenom = modif.getPrenom().trim();
                String adresse = modif.getAdresse() != null ? modif.getAdresse().trim() : "";
                String numTel = modif.getTelephone() != null ? modif.getTelephone().trim() : "";

                if (!validateClient(nom, prenom, adresse, numTel)) return;

                // Mise à jour dans la BDD
                String update = "UPDATE client SET nom=?, prenom=?, adresse=?, numTel=? WHERE codeClt=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(update)) {

                    stmt.setString(1, nom);
                    stmt.setString(2, prenom);
                    stmt.setString(3, adresse);
                    stmt.setString(4, numTel);
                    stmt.setInt(5, client.getCodeClt());

                    stmt.executeUpdate();

                    // Mise à jour locale
                    client.setNom(nom);
                    client.setPrenom(prenom);
                    client.setAdresse(adresse);
                    client.setTelephone(numTel);

                    filtrerClients(searchField.getText());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Client modifié avec succès !");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Action pour supprimer un client
     * @param client Le client à supprimer
     */
    private void supprimerClient(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ConfirmerSuppression.fxml"));
            Parent root = loader.load();

            ConfirmerSuppressionController controller = loader.getController();
            controller.setClient(client);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Confirmation de suppression");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionClients.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirmation()) {
                String delete = "DELETE FROM client WHERE codeClt=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(delete)) {
                    stmt.setInt(1, client.getCodeClt());
                    stmt.executeUpdate();
                    clients.remove(client);
                    filtrerClients(searchField.getText());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Client supprimé avec succès !");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Action pour voir les livraisons d'un client
     * @param client Le client dont on veut voir les livraisons
     */
    private void voirLivraisons(Client client) {
        // TODO: Naviguer vers la vue des livraisons du client
        System.out.println("Action: Voir les livraisons du client C" + String.format("%03d", client.getCodeClt()));
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Classe interne représentant un client
     */
    public static class Client {
        private int codeClt;
        private String nom;
        private String prenom;
        private String adresse;
        private String telephone;

        public Client(int codeClt, String nom, String prenom, String adresse, String telephone) {
            this.codeClt = codeClt;
            this.nom = nom;
            this.prenom = prenom;
            this.adresse = adresse;
            this.telephone = telephone;
        }

        // Getters
        public int getCodeClt() { return codeClt; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getAdresse() { return adresse; }
        public String getTelephone() { return telephone; }

        // Setters
        public void setCodeClt(int codeClt) { this.codeClt = codeClt; }
        public void setNom(String nom) { this.nom = nom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        public void setAdresse(String adresse) { this.adresse = adresse; }
        public void setTelephone(String telephone) { this.telephone = telephone; }
    }
}
