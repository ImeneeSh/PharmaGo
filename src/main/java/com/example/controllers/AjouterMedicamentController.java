package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AjouterMedicamentController {

    @FXML private TextField nomField;
    @FXML private TextField DatePerField;
    @FXML private TextField quantiteField;
    @FXML private TextField prixField;
    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    private GestionMedicamentsController.Medicament nouveauMedicament;
    private GestionMedicamentsController.Medicament medicamentAModifier;
    private boolean confirme = false;
    private boolean modeModification = false;

    @FXML
    public void initialize() {
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());
    }

    /**
     * Prépare le contrôleur pour la modification d'un médicament existant
     * @param medicament Le médicament à modifier
     */
    public void preparerModification(GestionMedicamentsController.Medicament medicament) {
        this.medicamentAModifier = medicament;
        this.modeModification = true;
        
        nomField.setText(medicament.getNom());
        // Formater la date au format yyyy-MM-dd pour le TextField
        DatePerField.setText(medicament.getDatePeremption().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        quantiteField.setText(String.valueOf(medicament.getQuantité()));
        prixField.setText(String.valueOf((int) medicament.getPrix()));
    }

    private void valider() {
        try {
            String nom = nomField.getText() != null ? nomField.getText().trim() : "";
            String dateStr = DatePerField.getText() != null ? DatePerField.getText().trim() : "";
            String qteStr = quantiteField.getText() != null ? quantiteField.getText().trim() : "";
            String prixStr = prixField.getText() != null ? prixField.getText().trim() : "";

            // Validation
            if (nom.isEmpty() || dateStr.isEmpty() || qteStr.isEmpty() || prixStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }

            LocalDate datePer;
            try {
                datePer = LocalDate.parse(dateStr); // Format ISO yyyy-MM-dd
            } catch (DateTimeParseException e) {
                showAlert(Alert.AlertType.ERROR, "Date invalide", "Veuillez entrer une date au format yyyy-MM-dd.");
                return;
            }

            int qte;
            try {
                qte = Integer.parseInt(qteStr);
                if (qte < 0) {
                    showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité ne peut pas être négative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer un nombre valide pour la quantité.");
                return;
            }

            float prix;
            try {
                prix = Float.parseFloat(prixStr);
                if (prix < 0) {
                    showAlert(Alert.AlertType.ERROR, "Prix invalide", "Le prix ne peut pas être négatif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Prix invalide", "Veuillez entrer un nombre valide pour le prix.");
                return;
            }

            if (modeModification && medicamentAModifier != null) {
                // Mode modification : mettre à jour le médicament existant
                medicamentAModifier.setNom(nom);
                medicamentAModifier.setDatePeremption(datePer);
                medicamentAModifier.setQuantité(qte);
                medicamentAModifier.setPrix(prix);
                nouveauMedicament = medicamentAModifier;
            } else {
                // Mode ajout : créer un nouveau médicament (idMed sera généré par la BDD)
                nouveauMedicament = new GestionMedicamentsController.Medicament(-1, nom, datePer, qte, prix);
            }

            fermer(true);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fermer(boolean confirmer) {
        this.confirme = confirmer;
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirme() {
        return confirme;
    }

    public GestionMedicamentsController.Medicament getNouveauMedicament() {
        return nouveauMedicament;
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
