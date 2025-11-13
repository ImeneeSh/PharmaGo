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
public class GestionUtilisateursController implements Initializable {

    // Composants de l'interface FXML
    @FXML
    private TextField searchField; // Champ de recherche

    @FXML
    private Button btnAjouterUtilisateur; // Bouton pour ajouter un client

    @FXML
    private GridPane utilisateursGrid; // Grille pour afficher les cartes clients

    // Liste des clients (sera remplacée par une vraie source de données)
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    // Liste filtrée des clients selon la recherche
    private List<Utilisateur> utilisateursFiltres = new ArrayList<>();

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
        afficherUtilisateurs();
    }

    /**
     * Initialise des données de test pour la démonstration
     * À remplacer par un appel à la base de données ou service
     */
    private void initialiserDonneesTest() {
        utilisateurs.add(new Utilisateur("U001", "Djammel Debbag", "djammel@example.com", "password1"));
        utilisateurs.add(new Utilisateur("U002", "Amina Berrabah", "amina@example.com", "password2"));
        utilisateurs.add(new Utilisateur("U003", "Karim Benali", "karim@example.com", "password3"));
        utilisateurs.add(new Utilisateur("U004", "Fatima Zohra", "fatima@example.com", "password4"));
        utilisateurs.add(new Utilisateur("U005", "Mohamed Amine", "mohamed@example.com", "password5"));
        utilisateurs.add(new Utilisateur("U006", "Sara Bouzid", "sara@example.com", "password6"));


        utilisateursFiltres = new ArrayList<>(utilisateurs);
    }

    /**
     * Configure la fonctionnalité de recherche en temps réel
     * Filtre les clients selon le texte saisi dans le champ de recherche
     */
    private void configurerRecherche() {
        // Écoute des changements dans le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerUtilisateurs(newValue);
        });
    }

    /**
     * Filtre la liste des clients selon le critère de recherche
     * @param critere Le texte de recherche (nom, prénom ou code)
     */
    private void filtrerUtilisateurs(String critere) {
        if (critere == null || critere.trim().isEmpty()) {
            // Si le champ est vide, afficher tous les utilisateurs
            utilisateursFiltres = new ArrayList<>(utilisateurs);
        } else {
            // Filtrer selon le nom, prénom ou code (insensible à la casse)
            String critereLower = critere.toLowerCase().trim();
            utilisateursFiltres = utilisateurs.stream()
                    .filter(utilisateur ->
                            utilisateur.getCode().toLowerCase().contains(critereLower) ||
                                    utilisateur.getNom().toLowerCase().contains(critereLower)
                    )
                    .collect(Collectors.toList());
        }
        // Réafficher les utilisateurs filtrés
        afficherUtilisateurs();
    }

    /**
     * Configure le bouton d'ajout de client
     * Définit l'action à exécuter lors du clic
     */
    private void configurerBoutonAjouter() {
        btnAjouterUtilisateur.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterUtilisateur.fxml"));
                Parent root = loader.load();

                AjouterUtilisateurController controller = loader.getController();

                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setResizable(false);
                dialogStage.setTitle("Ajouter un utilisateur");
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles/GestionUtilisateurs.css").toExternalForm());
                dialogStage.setScene(scene);
                dialogStage.showAndWait();

                if (controller.isConfirme()) {
                    utilisateurs.add(controller.getNouvelUtilisateur());
                    filtrerUtilisateurs(searchField.getText());
                    System.out.println("Nouvel utilisateur ajouté !");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Affiche les cartes clients dans la grille
     * Crée dynamiquement les cartes pour chaque client filtré
     */
    private void afficherUtilisateurs() {
        // Vider la grille avant de la remplir
        utilisateursGrid.getChildren().clear();

        // Calculer le nombre de colonnes (3 colonnes par ligne)
        int colonnes = 3;

        // Créer une carte pour chaque client filtré
        for (int i = 0; i < utilisateursFiltres.size(); i++) {
            Utilisateur utilisateur = utilisateursFiltres.get(i);

            // Calculer la position dans la grille
            int colonne = i % colonnes;
            int ligne = i / colonnes;

            // Créer et ajouter la carte client
            VBox carteUtilisateur = creerCarteUtilisateur(utilisateur);
            utilisateursGrid.add(carteUtilisateur, colonne, ligne);
        }
    }

    /**
     * Crée une carte visuelle pour un client
     * @param utilisateur Le client à afficher
     * @return La carte VBox créée
     */
    private VBox creerCarteUtilisateur(Utilisateur utilisateur) {
        // Conteneur principal de la carte
        VBox carte = new VBox(15);
        carte.getStyleClass().add("utilisateur-card");
        carte.setPadding(new Insets(25));
        carte.setPrefWidth(320);
        carte.setPrefHeight(220);

        // En-tête de la carte : Code client et boutons d'action
        HBox enTete = new HBox();
        enTete.setAlignment(Pos.CENTER_LEFT);
        enTete.setSpacing(10);
        HBox.setHgrow(enTete, Priority.ALWAYS);

        // Code client (ex: C001)
        Label codeLabel = new Label(utilisateur.getCode());
        codeLabel.getStyleClass().add("utilisateur-code");
        HBox.setHgrow(codeLabel, Priority.ALWAYS);

        // Bouton modifier (icône crayon)
        Button btnModifier = new Button();
        btnModifier.getStyleClass().add("btn-icon");
        ImageView iconModifier = new ImageView(new Image("/assets/bouton-modifier.png"));
        iconModifier.setFitWidth(18);
        iconModifier.setFitHeight(18);
        iconModifier.setPreserveRatio(true);
        btnModifier.setGraphic(iconModifier);
        btnModifier.setOnAction(e -> modifierUtilisateur(utilisateur));

        // Bouton supprimer (icône poubelle)
        Button btnSupprimer = new Button();
        btnSupprimer.getStyleClass().add("btn-icon");
        ImageView iconSupprimer = new ImageView(new Image("/assets/supprimer.png"));
        iconSupprimer.setFitWidth(18);
        iconSupprimer.setFitHeight(18);
        iconSupprimer.setPreserveRatio(true);
        btnSupprimer.setGraphic(iconSupprimer);
        btnSupprimer.setOnAction(e -> supprimerUtilisateur(utilisateur));

        enTete.getChildren().addAll(codeLabel, btnModifier, btnSupprimer);

        // Nom du client
        Label nomLabel = new Label(utilisateur.getNom());
        nomLabel.getStyleClass().add("utilisateur-nom");

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

        // Téléphone avec icône de téléphone
        HBox telephoneBox = new HBox(8);
        telephoneBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconTelephone = new ImageView(new Image("/assets/telephone.png"));
        iconTelephone.setFitWidth(16);
        iconTelephone.setFitHeight(16);
        iconTelephone.setPreserveRatio(true);
        iconTelephone.setStyle("-fx-opacity: 0.6;");

        infosContact.getChildren().addAll(adresseBox, telephoneBox);


        // Assembler tous les éléments dans la carte
        carte.getChildren().addAll(enTete, nomLabel, infosContact );

        return carte;
    }

    /**
     * Action pour modifier un utilisateur
     * @param utilisateur L'utilisateur à modifier
     */
    private void modifierUtilisateur(Utilisateur utilisateur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterUtilisateur.fxml"));
            Parent root = loader.load();

            AjouterUtilisateurController controller = loader.getController();
            controller.preparerModification(utilisateur);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Modifier un utilisateur");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionUtilisateurs.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirme()) {
                // L'utilisateur a déjà été modifié via la référence
                filtrerUtilisateurs(searchField.getText());
                System.out.println("Utilisateur " + utilisateur.getCode() + " modifié !");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Action pour supprimer un client
     * @param utilisateur Le client à supprimer
     */
    private void supprimerUtilisateur(GestionUtilisateursController.Utilisateur utilisateur) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ConfirmerSuppression.fxml"));
            Parent root = loader.load();

            ConfirmerSuppressionController controller = loader.getController();
            controller.setUtilisateur(utilisateur);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Confirmation de suppression");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionUtilisateurs.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirmation()) {
                utilisateurs.remove(utilisateur);
                filtrerUtilisateurs(searchField.getText());
                System.out.println("Utilisateur " + utilisateur.getCode() + " supprimé");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Classe interne représentant un utilisateur
     * À remplacer par une vraie classe de modèle
     */
    public static class Utilisateur {
        private String code;
        private String nom;
        private String email;
        private String motDePasse;

        public Utilisateur(String code, String nom, String email, String motDePasse) {
            this.code = code;
            this.nom = nom;
            this.email = email;
            this.motDePasse = motDePasse;
        }

        // Getters
        public String getCode() { return code; }
        public String getNom() { return nom; }
        public String getEmail() { return email; }
        public String getMotDePasse() { return motDePasse; }

        // Setters
        public void setCode(String code) { this.code = code; }
        public void setNom(String nom) { this.nom = nom; }
        public void setEmail(String email) { this.email = email; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    }

}
