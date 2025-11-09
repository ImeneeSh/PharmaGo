package com.example.controllers;

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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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

    // Liste des clients (sera remplacée par une vraie source de données)
    private List<Client> clients = new ArrayList<>();
    
    // Liste filtrée des clients selon la recherche
    private List<Client> clientsFiltres = new ArrayList<>();

    /**
     * Initialisation du contrôleur
     * Charge les données et configure les événements
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des données de test
        initialiserDonneesTest();
        
        // Configuration de la recherche en temps réel
        configurerRecherche();
        
        // Configuration du bouton ajouter
        configurerBoutonAjouter();
        
        // Affichage initial des clients
        afficherClients();
    }

    /**
     * Initialise des données de test pour la démonstration
     * À remplacer par un appel à la base de données ou service
     */
    private void initialiserDonneesTest() {
        clients.add(new Client("C001", "Djammel Debbag", "Rue de la liberté, Bejaia", "+213 7778879767"));
        clients.add(new Client("C002", "Amina Berrabah", "Avenue des Martyrs, Alger", "+213 555123456"));
        clients.add(new Client("C003", "Karim Benali", "Boulevard Mohamed V, Oran", "+213 666789012"));
        clients.add(new Client("C004", "Fatima Zohra", "Rue Didouche Mourad, Constantine", "+213 777345678"));
        clients.add(new Client("C005", "Mohamed Amine", "Place Emir Abdelkader, Tlemcen", "+213 555901234"));
        clients.add(new Client("C006", "Sara Bouzid", "Avenue Larbi Ben M'hidi, Annaba", "+213 666567890"));
        
        clientsFiltres = new ArrayList<>(clients);
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
            // Si le champ est vide, afficher tous les clients
            clientsFiltres = new ArrayList<>(clients);
        } else {
            // Filtrer selon le nom, prénom ou code (insensible à la casse)
            String critereLower = critere.toLowerCase().trim();
            clientsFiltres = clients.stream()
                    .filter(client -> 
                        client.getCode().toLowerCase().contains(critereLower) ||
                        client.getNom().toLowerCase().contains(critereLower)
                    )
                    .collect(Collectors.toList());
        }
        // Réafficher les clients filtrés
        afficherClients();
    }

    /**
     * Configure le bouton d'ajout de client
     * Définit l'action à exécuter lors du clic
     */
    private void configurerBoutonAjouter() {
        btnAjouterClient.setOnAction(event -> {
            // TODO: Ouvrir une fenêtre/dialogue pour ajouter un nouveau client
            System.out.println("Action: Ajouter un nouveau client");
            // Exemple: ouvrir un dialogue ou changer de vue
        });
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
        Label codeLabel = new Label(client.getCode());
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
        Label nomLabel = new Label(client.getNom());
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
        // TODO: Ouvrir une fenêtre/dialogue pour modifier le client
        System.out.println("Action: Modifier le client " + client.getCode());
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
                clients.remove(client);
                filtrerClients(searchField.getText());
                System.out.println("Client " + client.getCode() + " supprimé");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Action pour voir les livraisons d'un client
     * @param client Le client dont on veut voir les livraisons
     */
    private void voirLivraisons(Client client) {
        // TODO: Naviguer vers la vue des livraisons du client
        System.out.println("Action: Voir les livraisons du client " + client.getCode());
    }

    /**
     * Classe interne représentant un client
     * À remplacer par une vraie classe de modèle
     */
    public static class Client {
        private String code;
        private String nom;
        private String adresse;
        private String telephone;

        public Client(String code, String nom, String adresse, String telephone) {
            this.code = code;
            this.nom = nom;
            this.adresse = adresse;
            this.telephone = telephone;
        }

        // Getters
        public String getCode() { return code; }
        public String getNom() { return nom; }
        public String getAdresse() { return adresse; }
        public String getTelephone() { return telephone; }

        // Setters (si nécessaire)
        public void setCode(String code) { this.code = code; }
        public void setNom(String nom) { this.nom = nom; }
        public void setAdresse(String adresse) { this.adresse = adresse; }
        public void setTelephone(String telephone) { this.telephone = telephone; }
    }
}
