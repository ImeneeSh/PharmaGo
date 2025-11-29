package org.pharmago.controllers;

import org.pharmago.bdd.DatabaseConnection;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.text.Normalizer;

/**
 * Contrôleur pour l'interface de gestion des livraisons
 * Gère l'affichage, la recherche, le filtrage et les actions sur les livraisons
 */
public class GestionLivraisonsController implements Initializable {

    // Composants de l'interface FXML
    @FXML
    private TextField searchField; // Champ de recherche
    
    @FXML
    private Button btnNouvelleLivraison; // Bouton pour créer une nouvelle livraison
    
    @FXML
    private ComboBox<String> filterStatut; // Dropdown pour filtrer par statut
    
    @FXML
    private ComboBox<String> filterType; // Dropdown pour filtrer par type
    
    @FXML
    private GridPane livraisonsGrid; // Grille pour afficher les cartes livraisons

    private String medicament;

    // Liste des livraisons
    private List<Livraison> livraisons = new ArrayList<>();
    
    // Liste filtrée des livraisons selon la recherche et les filtres
    private List<Livraison> livraisonsFiltres = new ArrayList<>();

    /**
     * Initialisation du contrôleur
     * Charge les données et configure les événements
     */
    // Ajoute ceci parmi tes attributs privés
    private Integer clientFiltre = null; // null = toutes les livraisons
    /**
     * Permet de filtrer les livraisons par code client
     * @param codeClt Code du client
     */
    public void setClientFiltre(int codeClt) {
        this.clientFiltre = codeClt;
        appliquerFiltreClient(); // recharge les livraisons filtrées
    }

    /**
     * Applique le filtre client sur la liste des livraisons
     */
    private void appliquerFiltreClient() {
        if (clientFiltre != null) {
            livraisonsFiltres = livraisons.stream()
                    .filter(l -> l.getCodeClt() == clientFiltre)
                    .collect(Collectors.toList());
        } else {
            livraisonsFiltres = new ArrayList<>(livraisons);
        }
        afficherLivraisons();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerLivraisons();
        configurerFiltres();
        configurerRecherche();
        configurerBoutonNouvelleLivraison();
        searchField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(
                        getClass().getResource("/styles/GestionLivraisons.css").toExternalForm()
                );
            }
        });
    }

    /**
     * Charge les livraisons depuis la base de données avec les informations du client
     */
    private void chargerLivraisons() {
        livraisons.clear();
        String query = """
            SELECT L.numLiv, L.codeClt, L.dateLiv, L.priorite, L.statut, L.type_liv, L.taxe, L.cout,
                   C.nom, C.prenom,
                   (SELECT COUNT(*) FROM inclure I WHERE I.numLiv = L.numLiv) AS nombreMedicaments
            FROM livraison L
            LEFT JOIN client C ON L.codeClt = C.codeClt
            ORDER BY L.numLiv DESC
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int numLiv = rs.getInt("numLiv");
                int codeClt = rs.getInt("codeClt");
                LocalDate dateLiv = rs.getDate("dateLiv") != null ? rs.getDate("dateLiv").toLocalDate() : null;
                String priorite = rs.getString("priorite");
                String statut = rs.getString("statut");
                String type_liv = rs.getString("type_liv");
                float taxe = rs.getFloat("taxe");
                float cout = rs.getFloat("cout");
                String nomClient = rs.getString("nom");
                String prenomClient = rs.getString("prenom");
                int nombreMedicaments = rs.getInt("nombreMedicaments");

                boolean urgent = "urgent".equals(priorite);
                String clientNom = (nomClient != null && prenomClient != null) ? nomClient + " " + prenomClient : "";

                // Conversion des noms pour l'affichage
                String statutDisplay = convertirStatut(statut);
                String typeDisplay = convertirType(type_liv);

                livraisons.add(new Livraison(numLiv, codeClt, clientNom, dateLiv, nombreMedicaments,
                        (int) taxe, cout, statutDisplay, typeDisplay, urgent, ""));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur DB", "Impossible de charger les livraisons : " + e.getMessage());
            e.printStackTrace();
        }

        livraisonsFiltres = new ArrayList<>(livraisons);
        afficherLivraisons();
    }

    /**
     * Convertit le statut de la BDD vers l'affichage
     */
    private String convertirStatut(String statut) {
        if (statut == null) return "";
        switch (statut) {
            case "en_attente": return "en attente";
            case "livrée": return "livrée";
            case "annulée": return "annulée";
            case "en_cours": return "en cours";
            default: return statut;
        }
    }

    /**
     * Convertit le type de la BDD vers l'affichage
     */
    private String convertirType(String type) {
        if (type == null) return "";
        switch (type) {
            case "sous_chaine_du_froid": return "sous chaine du froid";
            case "sous_congélation": return "sous congélation";
            case "dangereuse": return "dangereuses";
            case "normale": return "normale";
            default: return type;
        }
    }

    /**
     * Convertit le statut d'affichage vers la BDD
     */
    private String convertirStatutVersBDD(String statut) {
        if (statut == null) return "";
        switch (statut) {
            case "en attente": return "en_attente";
            case "livrée": return "livrée";
            case "annulée": return "annulée";
            case "en cours": return "en_cours";
            default: return statut;
        }
    }

    /**
     * Convertit le type d'affichage vers la BDD
     */
    private String convertirTypeVersBDD(String type) {
        if (type == null) return "";
        switch (type) {
            case "sous chaine du froid": return "sous_chaine_du_froid";
            case "sous congélation": return "sous_congélation";
            case "dangereuses": return "dangereuse";
            case "normale": return "normale";
            default: return type;
        }
    }

    /**
     * Configure les dropdowns de filtres (statuts et types)
     * Initialise les options disponibles et les listeners
     */
    private void configurerFiltres() {
        // Options pour le filtre statut
        filterStatut.getItems().addAll("Tous les statuts", "livrée", "en cours", "en attente", "annulée");
        filterStatut.setValue("Tous les statuts");
        
        // Options pour le filtre type
        filterType.getItems().addAll("Tous les types", "sous chaine du froid", "sous congélation", 
            "dangereuses", "normale");
        filterType.setValue("Tous les types");
        
        // Écouter les changements de sélection dans les filtres
        filterStatut.setOnAction(e -> appliquerFiltres());
        filterType.setOnAction(e -> appliquerFiltres());
    }

    /**
     * Configure la fonctionnalité de recherche en temps réel
     * Filtre les livraisons selon le texte saisi dans le champ de recherche
     */
    private void configurerRecherche() {
        // Écoute des changements dans le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            appliquerFiltres();
        });
    }

    /**
     * Applique tous les filtres (recherche, statut, type) et met à jour l'affichage
     */
    private void appliquerFiltres() {
        String critereRecherche = searchField.getText();
        String statutSelectionne = filterStatut.getValue();
        String typeSelectionne = filterType.getValue();
        
        // Filtrer selon tous les critères
        livraisonsFiltres = livraisons.stream()
                .filter(livraison -> {
                    // Filtre par recherche (numéro ou client)
                    boolean matchRecherche = true;
                    if (critereRecherche != null && !critereRecherche.trim().isEmpty()) {
                        String critereLower = critereRecherche.toLowerCase().trim();
                        matchRecherche = String.valueOf(livraison.getNumLiv()).contains(critereLower) ||
                                       livraison.getClient().toLowerCase().contains(critereLower);
                    }
                    
                    // Filtre par statut
                    boolean matchStatut = true;
                    if (statutSelectionne != null && !statutSelectionne.equals("Tous les statuts")) {
                        matchStatut = livraison.getStatut().equals(statutSelectionne);
                    }
                    
                    // Filtre par type
                    boolean matchType = true;
                    if (typeSelectionne != null && !typeSelectionne.equals("Tous les types")) {
                        matchType = livraison.getType().equals(typeSelectionne);
                    }
                    
                    return matchRecherche && matchStatut && matchType;
                })
                .collect(Collectors.toList());
        
        // Réafficher les livraisons filtrées
        afficherLivraisons();
    }

    /**
     * Configure le bouton de création de nouvelle livraison
     * Définit l'action à exécuter lors du clic
     */
    private void configurerBoutonNouvelleLivraison() {
        btnNouvelleLivraison.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterLivraison.fxml"));
                Parent root = loader.load();

                AjouterLivraisonController controller = loader.getController();

                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setResizable(false);
                dialogStage.setTitle("Ajouter une livraison");
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles/GestionLivraisons.css").toExternalForm());
                dialogStage.setScene(scene);
                dialogStage.showAndWait();

                if (controller.isConfirme()) {
                    Livraison nouvelle = controller.getNouvelleLivraison();

                    // Validation
                    int codeClt = nouvelle.getCodeClt();
                    LocalDate dateLiv = nouvelle.getDate();
                    String priorite = nouvelle.isUrgent() ? "urgent" : "normal";
                    String statut = convertirStatutVersBDD(nouvelle.getStatut());
                    String type_liv = convertirTypeVersBDD(nouvelle.getType());
                    float taxe = nouvelle.getTaxe();
                    float cout = nouvelle.getCout();

                    if (!validateLivraison(codeClt, dateLiv, statut, type_liv)) return;

                    // Insertion dans la BDD
                    String insert = "INSERT INTO livraison (codeClt, dateLiv, priorite, statut, type_liv, taxe, cout) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        stmt.setInt(1, codeClt);
                        stmt.setDate(2, java.sql.Date.valueOf(dateLiv));
                        stmt.setString(3, priorite);
                        stmt.setString(4, statut);
                        stmt.setString(5, type_liv);
                        stmt.setFloat(6, taxe);
                        stmt.setFloat(7, cout);

                        int affected = stmt.executeUpdate();
                        if (affected > 0) {
                            try (ResultSet keys = stmt.getGeneratedKeys()) {
                                if (keys.next()) {
                                    int id = keys.getInt(1);
                                    chargerLivraisons(); // Recharger pour avoir les bonnes infos
                                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Livraison ajoutée avec succès !");
                                }
                            }
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la livraison.");
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
     * Valide les champs d'une livraison
     */
    private boolean validateLivraison(int codeClt, LocalDate dateLiv, String statut, String type_liv) {
        if (codeClt <= 0) {
            showAlert(Alert.AlertType.ERROR, "Client invalide", "Veuillez sélectionner un client valide.");
            return false;
        }

        if (dateLiv == null) {
            showAlert(Alert.AlertType.ERROR, "Date invalide", "Veuillez entrer une date de livraison valide.");
            return false;
        }

        if (statut == null || statut.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Statut invalide", "Veuillez sélectionner un statut.");
            return false;
        }

        if (type_liv == null || type_liv.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Type invalide", "Veuillez sélectionner un type de livraison.");
            return false;
        }

        return true;
    }

    /**
     * Affiche les cartes livraisons dans la grille
     * Crée dynamiquement les cartes pour chaque livraison filtrée
     */
    private void afficherLivraisons() {
        // Vider la grille avant de la remplir
        livraisonsGrid.getChildren().clear();

        // Calculer le nombre de colonnes (3 colonnes par ligne)
        int colonnes = 3;

        // Créer une carte pour chaque livraison filtrée
        for (int i = 0; i < livraisonsFiltres.size(); i++) {
            Livraison livraison = livraisonsFiltres.get(i);

            // Calculer la position dans la grille
            int colonne = i % colonnes;
            int ligne = i / colonnes;

            // Créer et ajouter la carte livraison
            VBox carteLivraison = creerCarteLivraison(livraison);
            livraisonsGrid.add(carteLivraison, colonne, ligne);
        }
    }

    /**
     * Crée une carte visuelle pour une livraison
     * @param livraison La livraison à afficher
     * @return La carte VBox créée
     */
    private VBox creerCarteLivraison(Livraison livraison) {
        // Conteneur principal de la carte
        VBox carte = new VBox(15);
        carte.getStyleClass().add("livraison-card");
        carte.setPadding(new Insets(25));
        carte.setPrefWidth(320);
        carte.setPrefHeight(280);
        
        // En-tête de la carte : Numéro livraison et boutons d'action
        HBox enTete = new HBox();
        enTete.setAlignment(Pos.CENTER_LEFT);
        enTete.setSpacing(10);
        HBox.setHgrow(enTete, Priority.ALWAYS);
        
        // Numéro livraison (ex: L0001)
        Label numeroLabel = new Label("L" + String.format("%04d", livraison.getNumLiv()));
        numeroLabel.getStyleClass().add("livraison-numero");
        HBox.setHgrow(numeroLabel, Priority.ALWAYS);
        
        // Bouton QR code
        Button btnQR = new Button();
        btnQR.getStyleClass().add("btn-icon");
        ImageView iconQR = new ImageView(new Image("/assets/qr.png"));
        iconQR.setFitWidth(18);
        iconQR.setFitHeight(18);
        iconQR.setPreserveRatio(true);
        btnQR.setGraphic(iconQR);
        btnQR.setOnAction(e -> voirQRCode(livraison));
        
        // Bouton modifier (icône crayon)
        Button btnModifier = new Button();
        btnModifier.getStyleClass().add("btn-icon");
        ImageView iconModifier = new ImageView(new Image("/assets/bouton-modifier.png"));
        iconModifier.setFitWidth(18);
        iconModifier.setFitHeight(18);
        iconModifier.setPreserveRatio(true);
        btnModifier.setGraphic(iconModifier);
        btnModifier.setOnAction(e -> modifierLivraison(livraison));
        
        // Bouton supprimer (icône poubelle)
        Button btnSupprimer = new Button();
        btnSupprimer.getStyleClass().add("btn-icon");
        ImageView iconSupprimer = new ImageView(new Image("/assets/supprimer.png"));
        iconSupprimer.setFitWidth(18);
        iconSupprimer.setFitHeight(18);
        iconSupprimer.setPreserveRatio(true);
        btnSupprimer.setGraphic(iconSupprimer);
        btnSupprimer.setOnAction(e -> supprimerLivraison(livraison));
        
        enTete.getChildren().addAll(numeroLabel, btnQR, btnModifier, btnSupprimer);
        
        // Nom du client
        Label clientLabel = new Label(livraison.getClient());
        clientLabel.getStyleClass().add("livraison-client");
        
        // Informations de la livraison
        VBox infosLivraison = new VBox(10);
        
        // Date avec icône calendrier
        HBox dateBox = new HBox(8);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconCalendrier = new ImageView(new Image("/assets/calendrier.png"));
        iconCalendrier.setFitWidth(16);
        iconCalendrier.setFitHeight(16);
        iconCalendrier.setPreserveRatio(true);
        iconCalendrier.setStyle("-fx-opacity: 0.6;");
        Label dateLabel = new Label(livraison.getDateFormatee());
        dateLabel.getStyleClass().add("livraison-info");
        dateBox.getChildren().addAll(iconCalendrier, dateLabel);
        
        // Nombre de médicaments avec icône pilule
        HBox medicamentsBox = new HBox(8);
        medicamentsBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconPilule = new ImageView(new Image("/assets/pilule.png"));
        iconPilule.setFitWidth(16);
        iconPilule.setFitHeight(16);
        iconPilule.setPreserveRatio(true);
        iconPilule.setStyle("-fx-opacity: 0.6;");
        Label medicamentsLabel = new Label(livraison.getNombreMedicaments() + " médicament(s)");
        medicamentsLabel.getStyleClass().add("livraison-info");
        medicamentsBox.getChildren().addAll(iconPilule, medicamentsLabel);
        
        // Taxe
        Label taxeLabel = new Label("Taxe : " + livraison.getTaxe() + " DA");
        taxeLabel.getStyleClass().add("livraison-taxe");

        infosLivraison.getChildren().addAll(dateBox, medicamentsBox, taxeLabel);

        // Coût total
        Label coutLabel = new Label("Coût total : " + livraison.getCout() + " DA");
        coutLabel.getStyleClass().add("livraison-taxe");
        infosLivraison.getChildren().add(coutLabel);


        // Tags de statut en bas de la carte
        HBox tagsBox = new HBox(8);
        tagsBox.setAlignment(Pos.CENTER_LEFT);
        tagsBox.setSpacing(8);

        Label tagStatut = new Label(livraison.getStatut());
        tagStatut.getStyleClass().add("tag");
        tagStatut.getStyleClass().add("tag-statut-" + normaliserClasseCSS(livraison.getStatut()));
        tagsBox.getChildren().add(tagStatut);
        
        // Tag type (sous chaine du froid, etc.)
        Label tagType = new Label(livraison.getType());
        tagType.getStyleClass().add("tag");
        tagType.getStyleClass().add("tag-type");
        tagsBox.getChildren().add(tagType);
        
        // Tag urgent si applicable
        if (livraison.isUrgent()) {
            HBox tagUrgentBox = new HBox(4);
            tagUrgentBox.setAlignment(Pos.CENTER);
            tagUrgentBox.getStyleClass().add("tag");
            tagUrgentBox.getStyleClass().add("tag-urgent");
            
            // Triangle d'alerte (utiliser un Label avec caractère Unicode ▲)
            Label triangle = new Label("▲");
            triangle.setStyle("-fx-text-fill: #E53935; -fx-font-size: 10px;");
            Label urgentLabel = new Label("urgent");
            urgentLabel.setStyle("-fx-text-fill: #E53935;");
            
            tagUrgentBox.getChildren().addAll(triangle, urgentLabel);
            tagsBox.getChildren().add(tagUrgentBox);
        }
        
        // Assembler tous les éléments dans la carte
        carte.getChildren().addAll(enTete, clientLabel, infosLivraison, tagsBox);
        
        return carte;
    }

    /**
     * Action pour voir le QR code d'une livraison
     * @param livraison La livraison dont on veut voir le QR code
     */
    private void voirQRCode(Livraison livraison) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/pharmago/views/AfficherQRCode.fxml"));
            Parent root = loader.load();

            AfficherQRCodeController controller = loader.getController();
            controller.setLivraison(livraison);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("QR Code - L" + String.format("%04d", livraison.getNumLiv()));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionLivraisons.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Action pour modifier une livraison
     * @param livraison La livraison à modifier
     */
    private void modifierLivraison(Livraison livraison) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterLivraison.fxml"));
            Parent root = loader.load();

            AjouterLivraisonController controller = loader.getController();
            controller.preparerModification(livraison);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Modifier une livraison");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionLivraisons.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirme()) {
                Livraison modif = controller.getNouvelleLivraison();

                // Validation
                int codeClt = modif.getCodeClt();
                LocalDate dateLiv = modif.getDate();
                String priorite = modif.isUrgent() ? "urgent" : "normal";
                String statut = convertirStatutVersBDD(modif.getStatut());
                String type_liv = convertirTypeVersBDD(modif.getType());
                float taxe = modif.getTaxe();
                float cout = modif.getCout();

                if (!validateLivraison(codeClt, dateLiv, statut, type_liv)) return;

                // Mise à jour dans la BDD
                String update = "UPDATE livraison SET codeClt=?, dateLiv=?, priorite=?, statut=?, type_liv=?, taxe=?, cout=? WHERE numLiv=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(update)) {

                    stmt.setInt(1, codeClt);
                    stmt.setDate(2, java.sql.Date.valueOf(dateLiv));
                    stmt.setString(3, priorite);
                    stmt.setString(4, statut);
                    stmt.setString(5, type_liv);
                    stmt.setFloat(6, taxe);
                    stmt.setFloat(7, cout);
                    stmt.setInt(8, livraison.getNumLiv());

                    stmt.executeUpdate();

                    chargerLivraisons(); // Recharger pour avoir les bonnes infos
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Livraison modifiée avec succès !");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Action pour supprimer une livraison
     * @param livraison La livraison à supprimer
     */
    private void supprimerLivraison(GestionLivraisonsController.Livraison livraison) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/pharmago/views/ConfirmerSuppression.fxml"));
            Parent root = loader.load();

            ConfirmerSuppressionController controller = loader.getController();
            controller.setLivraison(livraison);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Confirmation de suppression");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionLivraisons.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirmation()) {
                // Supprimer d'abord les entrées dans inclure (clé étrangère)
                String deleteInclure = "DELETE FROM inclure WHERE numLiv=?";
                String deleteLivraison = "DELETE FROM livraison WHERE numLiv=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt1 = conn.prepareStatement(deleteInclure);
                     PreparedStatement stmt2 = conn.prepareStatement(deleteLivraison)) {
                    stmt1.setInt(1, livraison.getNumLiv());
                    stmt1.executeUpdate();
                    stmt2.setInt(1, livraison.getNumLiv());
                    stmt2.executeUpdate();
                    chargerLivraisons(); // Recharger
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Livraison supprimée avec succès !");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Classe interne représentant une livraison
     */
    public static class Livraison {
        private int numLiv;
        private int codeClt;
        private String client;
        private LocalDate date;
        private int nombreMedicaments;
        private int taxe;
        private float cout;
        private String statut;
        private String type;
        private boolean urgent;
        private String medicament;

        public Livraison(int numLiv, int codeClt, String client, LocalDate date, int nombreMedicaments,
                         int taxe, float cout, String statut, String type, boolean urgent,
                         String medicament) {
            this.numLiv = numLiv;
            this.codeClt = codeClt;
            this.client = client;
            this.date = date;
            this.nombreMedicaments = nombreMedicaments;
            this.taxe = taxe;
            this.cout = cout;
            this.statut = statut;
            this.type = type;
            this.urgent = urgent;
            this.medicament = medicament;
        }

        // Getters
        public int getNumLiv() { return numLiv; }
        public int getCodeClt() { return codeClt; }
        public String getClient() { return client; }
        public LocalDate getDate() { return date; }
        public int getNombreMedicaments() { return nombreMedicaments; }
        public int getTaxe() { return taxe; }
        public float getCout() { return cout; }
        public String getStatut() { return statut; }
        public String getType() { return type; }
        public boolean isUrgent() { return urgent; }
        public String getMedicament() { return medicament; }

        /**
         * Retourne la date formatée (dd/MM/yyyy)
         * @return La date formatée
         */
        public String getDateFormatee() {
            if (date == null) return "";
            return String.format("%02d/%02d/%04d", 
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear());
        }

        // Setters
        public void setNumLiv(int numLiv) { this.numLiv = numLiv; }
        public void setCodeClt(int codeClt) { this.codeClt = codeClt; }
        public void setClient(String client) { this.client = client; }
        public void setDate(LocalDate date) { this.date = date; }
        public void setNombreMedicaments(int nombreMedicaments) { this.nombreMedicaments = nombreMedicaments; }
        public void setTaxe(int taxe) { this.taxe = taxe; }
        public void setCout (float cout) { this.cout = cout ;}
        public void setStatut(String statut) { this.statut = statut; }
        public void setType(String type) { this.type = type; }
        public void setUrgent(boolean urgent) { this.urgent = urgent; }
        public void setMedicament(String medicament) { this.medicament = medicament; }
    }


    private String normaliserClasseCSS(String texte) {
        if (texte == null) return "";
        // Supprime les accents et met en minuscules
        String sansAccent = Normalizer.normalize(texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // Enlève les diacritiques
        return sansAccent.toLowerCase().replace(" ", "-");
    }

}

