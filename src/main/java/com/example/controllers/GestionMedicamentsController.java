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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Contrôleur pour l'interface de gestion des médicaments
 * Gère l'affichage, la recherche et les actions sur les médicaments
 */
public class GestionMedicamentsController implements Initializable {

    // Composants de l'interface FXML
    @FXML
    private TextField searchField; // Champ de recherche
    
    @FXML
    private Button btnAjouterMedicament; // Bouton pour ajouter un médicament
    
    @FXML
    private GridPane medicamentsGrid; // Grille pour afficher les cartes médicaments

    // Liste des médicaments (sera remplacée par une vraie source de données)
    private List<Medicament> medicaments = new ArrayList<>();
    
    // Liste filtrée des médicaments selon la recherche
    private List<Medicament> medicamentsFiltres = new ArrayList<>();

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
        
        // Affichage initial des médicaments
        afficherMedicaments();
    }

    /**
     * Initialise des données de test pour la démonstration
     * À remplacer par un appel à la base de données ou service
     */
    private void initialiserDonneesTest() {
        // Médicament valide (non périmé)
        medicaments.add(new Medicament("M001", "Paracétamol 500mg", LocalDate.of(2025, 12, 31), 5 , 250));
        
        // Médicaments périmés
        medicaments.add(new Medicament("M002", "Ibuprofène 200mg", LocalDate.of(2024, 5, 15), 0 , 250)); // Périmé il y a ~246 jours
        medicaments.add(new Medicament("M003", "Aspirine 500mg", LocalDate.of(2024, 5, 20) , 5 , 250)); // Périmé il y a ~241 jours
        
        // Autres médicaments valides
        medicaments.add(new Medicament("M004", "Doliprane 1000mg", LocalDate.of(2026, 1, 10), 5 , 250));
        medicaments.add(new Medicament("M005", "Dafalgan 500mg", LocalDate.of(2025, 11, 15), 0 , 250));
        medicaments.add(new Medicament("M006", "Efferalgan 1000mg", LocalDate.of(2026, 3, 20), 5 , 250));
        
        medicamentsFiltres = new ArrayList<>(medicaments);
    }

    /**
     * Configure la fonctionnalité de recherche en temps réel
     * Filtre les médicaments selon le texte saisi dans le champ de recherche
     */
    private void configurerRecherche() {
        // Écoute des changements dans le champ de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerMedicaments(newValue);
        });
    }

    /**
     * Filtre la liste des médicaments selon le critère de recherche
     * @param critere Le texte de recherche (nom ou identifiant)
     */
    private void filtrerMedicaments(String critere) {
        if (critere == null || critere.trim().isEmpty()) {
            // Si le champ est vide, afficher tous les médicaments
            medicamentsFiltres = new ArrayList<>(medicaments);
        } else {
            // Filtrer selon le nom ou le code (insensible à la casse)
            String critereLower = critere.toLowerCase().trim();
            medicamentsFiltres = medicaments.stream()
                    .filter(medicament -> 
                        medicament.getCode().toLowerCase().contains(critereLower) ||
                        medicament.getNom().toLowerCase().contains(critereLower)
                    )
                    .collect(Collectors.toList());
        }
        // Réafficher les médicaments filtrés
        afficherMedicaments();
    }

    /**
     * Configure le bouton d'ajout de médicament
     * Définit l'action à exécuter lors du clic
     */
    private void configurerBoutonAjouter() {
        btnAjouterMedicament.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterMedicament.fxml"));
                Parent root = loader.load();

                AjouterMedicamentController controller = loader.getController();

                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setResizable(false);
                dialogStage.setTitle("Ajouter un médicament");
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles/GestionMedicaments.css").toExternalForm());
                dialogStage.setScene(scene);
                dialogStage.showAndWait();

                if (controller.isConfirme()) {
                    medicaments.add(controller.getNouveauMedicament());
                    filtrerMedicaments(searchField.getText());
                    System.out.println("Nouveau médicament ajouté !");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Affiche les cartes médicaments dans la grille
     * Crée dynamiquement les cartes pour chaque médicament filtré
     */
    private void afficherMedicaments() {
        // Vider la grille avant de la remplir
        medicamentsGrid.getChildren().clear();
        
        // Calculer le nombre de colonnes (3 colonnes par ligne)
        int colonnes = 3;
        
        // Créer une carte pour chaque médicament filtré
        for (int i = 0; i < medicamentsFiltres.size(); i++) {
            Medicament medicament = medicamentsFiltres.get(i);
            
            // Calculer la position dans la grille
            int colonne = i % colonnes;
            int ligne = i / colonnes;
            
            // Créer et ajouter la carte médicament
            VBox carteMedicament = creerCarteMedicament(medicament);
            medicamentsGrid.add(carteMedicament, colonne, ligne);
        }
    }

    /**
     * Crée une carte visuelle pour un médicament
     * @param medicament Le médicament à afficher
     * @return La carte VBox créée
     */
    private VBox creerCarteMedicament(Medicament medicament) {
        VBox carte = new VBox(15);
        carte.setPadding(new Insets(25));
        carte.setPrefWidth(320);
        carte.setPrefHeight(240);

        boolean estPerime = medicament.estPerime();
        boolean ruptureStock = medicament.getQuantité() == 0;

        // Styles de base
        carte.getStyleClass().add("medicament-card");
        if (estPerime) carte.getStyleClass().add("medicament-card-expired");

        // --- EN-TETE ---
        HBox enTete = new HBox(10);
        enTete.setAlignment(Pos.CENTER_LEFT);
        Label codeLabel = new Label(medicament.getCode());
        codeLabel.getStyleClass().add("medicament-code");

        Button btnModifier = new Button();
        btnModifier.getStyleClass().add("btn-icon");
        ImageView iconModifier = new ImageView(new Image("/assets/bouton-modifier.png"));
        iconModifier.setFitWidth(18);
        iconModifier.setFitHeight(18);
        btnModifier.setGraphic(iconModifier);
        btnModifier.setOnAction(e -> modifierMedicament(medicament));

        Button btnSupprimer = new Button();
        btnSupprimer.getStyleClass().add("btn-icon");
        ImageView iconSupprimer = new ImageView(new Image("/assets/supprimer.png"));
        iconSupprimer.setFitWidth(18);
        iconSupprimer.setFitHeight(18);
        btnSupprimer.setGraphic(iconSupprimer);
        btnSupprimer.setOnAction(e -> supprimerMedicament(medicament));

        enTete.getChildren().addAll(codeLabel, btnModifier, btnSupprimer);

        // Nom
        Label nomLabel = new Label(medicament.getNom());
        nomLabel.getStyleClass().add("medicament-nom");

        // Informations
        VBox infos = new VBox(10);
        // Date de péremption
        HBox dateBox = new HBox(8);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconCalendrier = new ImageView(new Image("/assets/calendrier.png"));
        iconCalendrier.setFitWidth(16);
        iconCalendrier.setFitHeight(16);
        iconCalendrier.setStyle("-fx-opacity: 0.6;");
        VBox dateLabels = new VBox(2);
        Label dateLabel = new Label("Date de péremption");
        dateLabel.getStyleClass().add("medicament-date-label");
        Label dateValue = new Label(medicament.getDatePeremptionFormatee());
        dateValue.getStyleClass().add("medicament-date-value");
        dateLabels.getChildren().addAll(dateLabel, dateValue);
        dateBox.getChildren().addAll(iconCalendrier, dateLabels);

        // Quantité
        HBox qteBox = new HBox(8);
        qteBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconPilule = new ImageView(new Image("/assets/pilule.png"));
        iconPilule.setFitWidth(16);
        iconPilule.setFitHeight(16);
        iconPilule.setStyle("-fx-opacity: 0.6;");
        Label qteLabel = new Label(medicament.getQuantité() + " médicament(s)");
        qteLabel.getStyleClass().add("livraison-info");
        qteBox.getChildren().addAll(iconPilule, qteLabel);

        // Prix
        Label prixLabel = new Label("Prix : " + medicament.getPrix() + " DA");
        prixLabel.getStyleClass().add("livraison-taxe");

        infos.getChildren().addAll(dateBox, qteBox, prixLabel);

        // --- AJOUTER TOUJOURS EN-TETE, NOM ET INFOS ---
        carte.getChildren().addAll(enTete, nomLabel, infos);

        // --- ALERTES ---
        if (estPerime) {
            VBox perimeBox = creerAlerte(
                    "Périmé",
                    "Expiré il y a " + medicament.getJoursDepuisExpiration() + " jours",
                    "/assets/danger.png",
                    "section-perime",
                    "perime-label",
                    "perime-subtitle"
            );
            carte.getChildren().add(perimeBox);
        }

        if (ruptureStock) {
            VBox ruptureBox = creerAlerte(
                    "Rupture de stock",
                    "Aucune unité disponible",
                    "/assets/danger.png",
                    "section-perime",
                    "perime-label",
                    "perime-subtitle"
            );
            carte.getChildren().add(ruptureBox);
        }

        return carte;
    }


    /**
     * 🔹 Crée une section d'alerte (périmé ou rupture)
     */
    private VBox creerAlerte(String titre, String sousTitre, String iconePath,
                             String styleBox, String styleTitre, String styleSousTitre) {
        HBox section = new HBox(8);
        section.setAlignment(Pos.CENTER_LEFT);
        section.getStyleClass().add(styleBox);
        section.setPadding(new Insets(12, 15, 12, 15));

        ImageView icon = new ImageView(new Image(iconePath));
        icon.setFitWidth(18);
        icon.setFitHeight(18);
        icon.setPreserveRatio(true);

        VBox labels = new VBox(2);
        Label titreLabel = new Label(titre);
        titreLabel.getStyleClass().add(styleTitre);
        Label sousTitreLabel = new Label(sousTitre);
        sousTitreLabel.getStyleClass().add(styleSousTitre);
        labels.getChildren().addAll(titreLabel, sousTitreLabel);

        section.getChildren().addAll(icon, labels);

        VBox wrapper = new VBox(section);
        wrapper.setAlignment(Pos.BOTTOM_CENTER);
        return wrapper;
    }


    /**
     * Action pour modifier un médicament
     * @param medicament Le médicament à modifier
     */
    private void modifierMedicament(Medicament medicament) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterMedicament.fxml"));
            Parent root = loader.load();

            AjouterMedicamentController controller = loader.getController();
            controller.preparerModification(medicament);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Modifier un médicament");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionMedicaments.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirme()) {
                // Le médicament a déjà été modifié via la référence
                filtrerMedicaments(searchField.getText());
                System.out.println("Médicament " + medicament.getCode() + " modifié !");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Action pour supprimer un médicament
     * @param medicament Le médicament à supprimer
     */
    private void supprimerMedicament(Medicament medicament) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ConfirmerSuppression.fxml"));
            Parent root = loader.load();

            ConfirmerSuppressionController controller = loader.getController();
            controller.setMedicament(medicament);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Confirmation de suppression");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionMedicaments.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirmation()) {
                medicaments.remove(medicament);
                filtrerMedicaments(searchField.getText());
                System.out.println("Médicament " + medicament.getCode() + " supprimé");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Classe interne représentant un médicament
     * À remplacer par une vraie classe de modèle
     */
    public static class Medicament {
        private String code;
        private String nom;
        private LocalDate datePeremption;
        private int quantité ;
        private float prix ;

        public Medicament(String code, String nom, LocalDate datePeremption , int quantité , float prix ) {
            this.code = code;
            this.nom = nom;
            this.datePeremption = datePeremption;
            this.quantité= quantité ;
            this.prix = prix ;
        }

        // Getters
        public String getCode() { return code; }
        public String getNom() { return nom; }
        public LocalDate getDatePeremption() { return datePeremption; }
        public int getQuantité() {return quantité ;}
        public float getPrix() { return prix;}

        /**
         * Vérifie si le médicament est périmé
         * @return true si la date de péremption est passée
         */
        public boolean estPerime() {
            return LocalDate.now().isAfter(datePeremption);
        }

        /**
         * Retourne la date de péremption formatée (dd/MM/yyyy)
         * @return La date formatée
         */
        public String getDatePeremptionFormatee() {
            return String.format("%02d/%02d/%04d", 
                datePeremption.getDayOfMonth(),
                datePeremption.getMonthValue(),
                datePeremption.getYear());
        }

        /**
         * Calcule le nombre de jours depuis l'expiration
         * @return Le nombre de jours depuis l'expiration (0 si non expiré)
         */
        public long getJoursDepuisExpiration() {
            if (estPerime()) {
                return ChronoUnit.DAYS.between(datePeremption, LocalDate.now());
            }
            return 0;
        }

        // Setters (si nécessaire)
        public void setCode(String code) { this.code = code; }
        public void setNom(String nom) { this.nom = nom; }
        public void setDatePeremption(LocalDate datePeremption) { this.datePeremption = datePeremption; }
        public void setQuantité(int quantité){ this.quantité= quantité ;}
        public void setPrix(float prix){ this.prix = prix ;}
    }
}

