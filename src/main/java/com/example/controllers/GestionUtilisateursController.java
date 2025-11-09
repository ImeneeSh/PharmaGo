package com.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
        utilisateurs.add(new Utilisateur("U001", "Djammel Debbag", "Rue de la liberté, Bejaia", "+213 7778879767"));
        utilisateurs.add(new Utilisateur("U002", "Amina Berrabah", "Avenue des Martyrs, Alger", "+213 555123456"));
        utilisateurs.add(new Utilisateur("U003", "Karim Benali", "Boulevard Mohamed V, Oran", "+213 666789012"));
        utilisateurs.add(new Utilisateur("U004", "Fatima Zohra", "Rue Didouche Mourad, Constantine", "+213 777345678"));
        utilisateurs.add(new Utilisateur("U005", "Mohamed Amine", "Place Emir Abdelkader, Tlemcen", "+213 555901234"));
        utilisateurs.add(new Utilisateur("U006", "Sara Bouzid", "Avenue Larbi Ben M'hidi, Annaba", "+213 666567890"));

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
            // Si le champ est vide, afficher tous les clients
            utilisateursFiltres = new ArrayList<>(utilisateurs);
        } else {
            // Filtrer selon le nom, prénom ou code (insensible à la casse)
            String critereLower = critere.toLowerCase().trim();
            utilisateurs = utilisateurs.stream()
                    .filter(client ->
                            client.getCode().toLowerCase().contains(critereLower) ||
                                    client.getNom().toLowerCase().contains(critereLower)
                    )
                    .collect(Collectors.toList());
        }
        // Réafficher les clients filtrés
        afficherUtilisateurs();
    }

    /**
     * Configure le bouton d'ajout de client
     * Définit l'action à exécuter lors du clic
     */
    private void configurerBoutonAjouter() {
        btnAjouterUtilisateur.setOnAction(event -> {
            // TODO: Ouvrir une fenêtre/dialogue pour ajouter un nouveau utilisateur
            System.out.println("Action: Ajouter un nouveau utilisateur");
            // Exemple: ouvrir un dialogue ou changer de vue
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
        Label adresseLabel = new Label(utilisateur.getAdresse());
        adresseLabel.getStyleClass().add("utilisateur-info");
        adresseBox.getChildren().addAll(iconLocalisation, adresseLabel);

        // Téléphone avec icône de téléphone
        HBox telephoneBox = new HBox(8);
        telephoneBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconTelephone = new ImageView(new Image("/assets/telephone.png"));
        iconTelephone.setFitWidth(16);
        iconTelephone.setFitHeight(16);
        iconTelephone.setPreserveRatio(true);
        iconTelephone.setStyle("-fx-opacity: 0.6;");
        Label telephoneLabel = new Label(utilisateur.getTelephone());
        telephoneLabel.getStyleClass().add("utilisateur-info");
        telephoneBox.getChildren().addAll(iconTelephone, telephoneLabel);

        infosContact.getChildren().addAll(adresseBox, telephoneBox);


        // Assembler tous les éléments dans la carte
        carte.getChildren().addAll(enTete, nomLabel, infosContact );

        return carte;
    }

    /**
     * Action pour modifier un client
     * @param utilisateur Le client à modifier
     */
    private void modifierUtilisateur(Utilisateur utilisateur) {
        // TODO: Ouvrir une fenêtre/dialogue pour modifier l'utilisateur
        System.out.println("Action: Modifier l'utilisateur " + utilisateur.getCode());
    }

    /**
     * Action pour supprimer un client
     * @param utilisateur Le client à supprimer
     */
    private void supprimerUtilisateur(Utilisateur utilisateur) {
        // Confirmation avant suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer l'utilisateur ?");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur " + utilisateur.getCode() + " ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Supprimer le client de la liste
                utilisateurs.remove(utilisateur);
                filtrerUtilisateurs(searchField.getText());
                System.out.println("Utilisateur " + utilisateur.getCode() + " supprimé");
            }
        });
    }


    /**
     * Classe interne représentant un utilisateur
     * À remplacer par une vraie classe de modèle
     */
    public static class Utilisateur {
        private String code;
        private String nom;
        private String adresse;
        private String telephone;

        public Utilisateur(String code, String nom, String adresse, String telephone) {
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
