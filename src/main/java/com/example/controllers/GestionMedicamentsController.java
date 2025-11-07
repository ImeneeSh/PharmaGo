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
        medicaments.add(new Medicament("M001", "Paracétamol 500mg", LocalDate.of(2025, 12, 31)));
        
        // Médicaments périmés
        medicaments.add(new Medicament("M002", "Ibuprofène 200mg", LocalDate.of(2024, 5, 15))); // Périmé il y a ~246 jours
        medicaments.add(new Medicament("M003", "Aspirine 500mg", LocalDate.of(2024, 5, 20))); // Périmé il y a ~241 jours
        
        // Autres médicaments valides
        medicaments.add(new Medicament("M004", "Doliprane 1000mg", LocalDate.of(2026, 1, 10)));
        medicaments.add(new Medicament("M005", "Dafalgan 500mg", LocalDate.of(2025, 11, 15)));
        medicaments.add(new Medicament("M006", "Efferalgan 1000mg", LocalDate.of(2026, 3, 20)));
        
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
            // TODO: Ouvrir une fenêtre/dialogue pour ajouter un nouveau médicament
            System.out.println("Action: Ajouter un nouveau médicament");
            // Exemple: ouvrir un dialogue ou changer de vue
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
        // Conteneur principal de la carte
        VBox carte = new VBox(15);
        carte.setPadding(new Insets(25));
        carte.setPrefWidth(320);
        carte.setPrefHeight(220);
        
        // Vérifier si le médicament est périmé
        boolean estPerime = medicament.estPerime();
        
        // Appliquer le style approprié selon l'état
        if (estPerime) {
            carte.getStyleClass().add("medicament-card");
            carte.getStyleClass().add("medicament-card-expired");
        } else {
            carte.getStyleClass().add("medicament-card");
        }
        
        // En-tête de la carte : Code médicament et boutons d'action
        HBox enTete = new HBox();
        enTete.setAlignment(Pos.CENTER_LEFT);
        enTete.setSpacing(10);
        HBox.setHgrow(enTete, Priority.ALWAYS);
        
        // Code médicament (ex: M001)
        Label codeLabel = new Label(medicament.getCode());
        codeLabel.getStyleClass().add("medicament-code");
        HBox.setHgrow(codeLabel, Priority.ALWAYS);
        
        // Bouton modifier (icône crayon)
        Button btnModifier = new Button();
        btnModifier.getStyleClass().add("btn-icon");
        ImageView iconModifier = new ImageView(new Image("/assets/bouton-modifier.png"));
        iconModifier.setFitWidth(18);
        iconModifier.setFitHeight(18);
        iconModifier.setPreserveRatio(true);
        btnModifier.setGraphic(iconModifier);
        btnModifier.setOnAction(e -> modifierMedicament(medicament));
        
        // Bouton supprimer (icône poubelle)
        Button btnSupprimer = new Button();
        btnSupprimer.getStyleClass().add("btn-icon");
        ImageView iconSupprimer = new ImageView(new Image("/assets/supprimer.png"));
        iconSupprimer.setFitWidth(18);
        iconSupprimer.setFitHeight(18);
        iconSupprimer.setPreserveRatio(true);
        btnSupprimer.setGraphic(iconSupprimer);
        btnSupprimer.setOnAction(e -> supprimerMedicament(medicament));
        
        enTete.getChildren().addAll(codeLabel, btnModifier, btnSupprimer);
        
        // Nom et dosage du médicament
        Label nomLabel = new Label(medicament.getNom());
        nomLabel.getStyleClass().add("medicament-nom");
        
        // Informations de péremption
        VBox infosPeremption = new VBox(8);
        
        // Date de péremption avec icône calendrier
        HBox dateBox = new HBox(8);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconCalendrier = new ImageView(new Image("/assets/calendrier.png"));
        iconCalendrier.setFitWidth(16);
        iconCalendrier.setFitHeight(16);
        iconCalendrier.setPreserveRatio(true);
        iconCalendrier.setStyle("-fx-opacity: 0.6;");
        
        VBox dateLabels = new VBox(2);
        Label dateLabel = new Label("Date de péremption");
        dateLabel.getStyleClass().add("medicament-date-label");
        Label dateValue = new Label(medicament.getDatePeremptionFormatee());
        dateValue.getStyleClass().add("medicament-date-value");
        dateLabels.getChildren().addAll(dateLabel, dateValue);
        
        dateBox.getChildren().addAll(iconCalendrier, dateLabels);
        infosPeremption.getChildren().add(dateBox);
        
        // Section "Périmé" si le médicament est périmé
        if (estPerime) {
            HBox sectionPerime = new HBox(8);
            sectionPerime.setAlignment(Pos.CENTER_LEFT);
            sectionPerime.getStyleClass().add("section-perime");
            sectionPerime.setPadding(new Insets(12, 15, 12, 15));
            
            // Icône d'alerte
            ImageView iconDanger = new ImageView(new Image("/assets/danger.png"));
            iconDanger.setFitWidth(18);
            iconDanger.setFitHeight(18);
            iconDanger.setPreserveRatio(true);
            
            VBox perimeLabels = new VBox(2);
            Label perimeLabel = new Label("Périmé");
            perimeLabel.getStyleClass().add("perime-label");
            Label perimeSubtitle = new Label("Expiré il y a " + medicament.getJoursDepuisExpiration() + " jours");
            perimeSubtitle.getStyleClass().add("perime-subtitle");
            perimeLabels.getChildren().addAll(perimeLabel, perimeSubtitle);
            
            sectionPerime.getChildren().addAll(iconDanger, perimeLabels);
            infosPeremption.getChildren().add(sectionPerime);
        }
        
        // Assembler tous les éléments dans la carte
        carte.getChildren().addAll(enTete, nomLabel, infosPeremption);
        
        return carte;
    }

    /**
     * Action pour modifier un médicament
     * @param medicament Le médicament à modifier
     */
    private void modifierMedicament(Medicament medicament) {
        // TODO: Ouvrir une fenêtre/dialogue pour modifier le médicament
        System.out.println("Action: Modifier le médicament " + medicament.getCode());
    }

    /**
     * Action pour supprimer un médicament
     * @param medicament Le médicament à supprimer
     */
    private void supprimerMedicament(Medicament medicament) {
        // Confirmation avant suppression
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le médicament ?");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le médicament " + medicament.getCode() + " ?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Supprimer le médicament de la liste
                medicaments.remove(medicament);
                filtrerMedicaments(searchField.getText());
                System.out.println("Médicament " + medicament.getCode() + " supprimé");
            }
        });
    }

    /**
     * Classe interne représentant un médicament
     * À remplacer par une vraie classe de modèle
     */
    public static class Medicament {
        private String code;
        private String nom;
        private LocalDate datePeremption;

        public Medicament(String code, String nom, LocalDate datePeremption) {
            this.code = code;
            this.nom = nom;
            this.datePeremption = datePeremption;
        }

        // Getters
        public String getCode() { return code; }
        public String getNom() { return nom; }
        public LocalDate getDatePeremption() { return datePeremption; }

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
    }
}

