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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class GestionMedicamentsController implements Initializable {

    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button btnAjouterMedicament; 
    
    @FXML
    private GridPane medicamentsGrid; 

   
    private List<Medicament> medicaments = new ArrayList<>();
    
    
    private List<Medicament> medicamentsFiltres = new ArrayList<>();

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerMedicaments();
        configurerRecherche();
        configurerBoutonAjouter();
        searchField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(
                        getClass().getResource("/styles/GestionMedicaments.css").toExternalForm()
                );
            }
        });
    }

   
    private void chargerMedicaments() {
        medicaments.clear();
        String query = "SELECT idMed, nomMed, datePer, nbrBoite, prixMed FROM medicament ORDER BY idMed";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idMed = rs.getInt("idMed");
                String nomMed = rs.getString("nomMed");
                LocalDate datePer = rs.getDate("datePer").toLocalDate();
                int nbrBoite = rs.getInt("nbrBoite");
                int prixMed = rs.getInt("prixMed");
                medicaments.add(new Medicament(idMed, nomMed, datePer, nbrBoite, prixMed));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur DB", "Impossible de charger les médicaments : " + e.getMessage());
            e.printStackTrace();
        }

        medicamentsFiltres = new ArrayList<>(medicaments);
        afficherMedicaments();
    }

    
    private void configurerRecherche() {
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrerMedicaments(newValue);
        });
    }

    
    private void filtrerMedicaments(String critere) {
        if (critere == null || critere.trim().isEmpty()) {
            medicamentsFiltres = new ArrayList<>(medicaments);
        } else {
            String critereLower = critere.toLowerCase().trim();
            medicamentsFiltres = medicaments.stream()
                    .filter(medicament -> 
                        String.valueOf(medicament.getIdMed()).contains(critereLower) ||
                        medicament.getNom().toLowerCase().contains(critereLower)
                    )
                    .collect(Collectors.toList());
        }
        afficherMedicaments();
    }

   
    private void configurerBoutonAjouter() {
        btnAjouterMedicament.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterMedicament.fxml"));
                Parent root = loader.load();
                AjouterMedicamentController controller = loader.getController();

                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle("Ajouter un médicament");
                dialog.setResizable(false);
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles/GestionMedicaments.css").toExternalForm());
                dialog.setScene(scene);
                dialog.showAndWait();

                if (controller.isConfirme()) {
                    Medicament nouveau = controller.getNouveauMedicament();

                   
                    String nomMed = nouveau.getNom().trim();
                    LocalDate datePer = nouveau.getDatePeremption();
                    int nbrBoite = nouveau.getQuantité();
                    int prixMed = (int) nouveau.getPrix();

                    if (!validateMedicament(nomMed, datePer, nbrBoite, prixMed)) return;

                   
                    String insert = "INSERT INTO medicament (nomMed, datePer, nbrBoite, prixMed) VALUES (?, ?, ?, ?)";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, nomMed);
                        stmt.setDate(2, java.sql.Date.valueOf(datePer));
                        stmt.setInt(3, nbrBoite);
                        stmt.setInt(4, prixMed);

                        int affected = stmt.executeUpdate();
                        if (affected > 0) {
                            try (ResultSet keys = stmt.getGeneratedKeys()) {
                                if (keys.next()) {
                                    int id = keys.getInt(1);
                                    medicaments.add(new Medicament(id, nomMed, datePer, nbrBoite, prixMed));
                                    filtrerMedicaments(searchField.getText());
                                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Médicament ajouté avec succès !");
                                }
                            }
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter le médicament.");
                        }
                    }
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    
    private boolean validateMedicament(String nomMed, LocalDate datePer, int nbrBoite, int prixMed) {
        if (nomMed == null || nomMed.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir le nom du médicament.");
            return false;
        }

        if (datePer == null) {
            showAlert(Alert.AlertType.ERROR, "Date invalide", "Veuillez entrer une date de péremption valide.");
            return false;
        }

        if (nbrBoite < 0) {
            showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité ne peut pas être négative.");
            return false;
        }

        if (prixMed < 0) {
            showAlert(Alert.AlertType.ERROR, "Prix invalide", "Le prix ne peut pas être négatif.");
            return false;
        }

        return true;
    }

   
    private void afficherMedicaments() {
       
        medicamentsGrid.getChildren().clear();
        
        int colonnes = 3;
        
        
        for (int i = 0; i < medicamentsFiltres.size(); i++) {
            Medicament medicament = medicamentsFiltres.get(i);
            
            
            int colonne = i % colonnes;
            int ligne = i / colonnes;
            
           
            VBox carteMedicament = creerCarteMedicament(medicament);
            medicamentsGrid.add(carteMedicament, colonne, ligne);
        }
    }

   
    private VBox creerCarteMedicament(Medicament medicament) {
        VBox carte = new VBox(15);
        carte.setPadding(new Insets(25));
        carte.setPrefWidth(320);
        carte.setPrefHeight(240);

        boolean estPerime = medicament.estPerime();
        boolean ruptureStock = medicament.getQuantité() == 0;

       
        carte.getStyleClass().add("medicament-card");
        if (estPerime) carte.getStyleClass().add("medicament-card-expired");

        
        HBox enTete = new HBox(10);
        enTete.setAlignment(Pos.CENTER_LEFT);
        Label codeLabel = new Label("M" + String.format("%03d", medicament.getIdMed()));
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

       
        Label nomLabel = new Label(medicament.getNom());
        nomLabel.getStyleClass().add("medicament-nom");

       
        VBox infos = new VBox(10);
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

       
        HBox qteBox = new HBox(8);
        qteBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconPilule = new ImageView(new Image("/assets/pilule.png"));
        iconPilule.setFitWidth(16);
        iconPilule.setFitHeight(16);
        iconPilule.setStyle("-fx-opacity: 0.6;");
        Label qteLabel = new Label(medicament.getQuantité() + " médicament(s)");
        qteLabel.getStyleClass().add("livraison-info");
        qteBox.getChildren().addAll(iconPilule, qteLabel);

        
        Label prixLabel = new Label("Prix : " + medicament.getPrix() + " DA");
        prixLabel.getStyleClass().add("livraison-taxe");

        infos.getChildren().addAll(dateBox, qteBox, prixLabel);

        carte.getChildren().addAll(enTete, nomLabel, infos);

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
                Medicament modif = controller.getNouveauMedicament();

                // Validation
                String nomMed = modif.getNom().trim();
                LocalDate datePer = modif.getDatePeremption();
                int nbrBoite = modif.getQuantité();
                int prixMed = (int) modif.getPrix();

                if (!validateMedicament(nomMed, datePer, nbrBoite, prixMed)) return;

                String update = "UPDATE medicament SET nomMed=?, datePer=?, nbrBoite=?, prixMed=? WHERE idMed=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(update)) {

                    stmt.setString(1, nomMed);
                    stmt.setDate(2, java.sql.Date.valueOf(datePer));
                    stmt.setInt(3, nbrBoite);
                    stmt.setInt(4, prixMed);
                    stmt.setInt(5, medicament.getIdMed());

                    stmt.executeUpdate();

                    medicament.setNom(nomMed);
                    medicament.setDatePeremption(datePer);
                    medicament.setQuantité(nbrBoite);
                    medicament.setPrix(prixMed);

                    filtrerMedicaments(searchField.getText());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Médicament modifié avec succès !");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

   
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
                String delete = "DELETE FROM medicament WHERE idMed=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(delete)) {
                    stmt.setInt(1, medicament.getIdMed());
                    stmt.executeUpdate();
                    medicaments.remove(medicament);
                    filtrerMedicaments(searchField.getText());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Médicament supprimé avec succès !");
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

   
    public static class Medicament {
        private int idMed;
        private String nom;
        private LocalDate datePeremption;
        private int quantité ;
        private float prix ;

        public Medicament(int idMed, String nom, LocalDate datePeremption , int quantité , float prix ) {
            this.idMed = idMed;
            this.nom = nom;
            this.datePeremption = datePeremption;
            this.quantité= quantité ;
            this.prix = prix ;
        }

       
        public int getIdMed() { return idMed; }
        public String getNom() { return nom; }
        public LocalDate getDatePeremption() { return datePeremption; }
        public int getQuantité() {return quantité ;}
        public float getPrix() { return prix;}

       
        public boolean estPerime() {
            return LocalDate.now().isAfter(datePeremption);
        }

        
        public String getDatePeremptionFormatee() {
            return String.format("%02d/%02d/%04d", 
                datePeremption.getDayOfMonth(),
                datePeremption.getMonthValue(),
                datePeremption.getYear());
        }

        
        public long getJoursDepuisExpiration() {
            if (estPerime()) {
                return ChronoUnit.DAYS.between(datePeremption, LocalDate.now());
            }
            return 0;
        }

        
        public void setIdMed(int idMed) { this.idMed = idMed; }
        public void setNom(String nom) { this.nom = nom; }
        public void setDatePeremption(LocalDate datePeremption) { this.datePeremption = datePeremption; }
        public void setQuantité(int quantité){ this.quantité= quantité ;}
        public void setPrix(float prix){ this.prix = prix ;}
    }
}

