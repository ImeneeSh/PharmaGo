package org.pharmago.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.util.StringConverter;

public class AjouterMedicamentController {

    @FXML private TextField nomField;
    @FXML private DatePicker datePerField; // ✔ DatePicker

    @FXML private TextField quantiteField;
    @FXML private TextField prixField;
    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    private GestionMedicamentsController.Medicament nouveauMedicament;
    private GestionMedicamentsController.Medicament medicamentAModifier;
    private boolean confirme = false;
    private boolean modeModification = false;

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @FXML
    public void initialize() {

        // ✔ Convertisseur format dd-MM-yyyy
        datePerField.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? date.format(DATE_FORMAT) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string == null || string.isEmpty()) return null;
                try {
                    return LocalDate.parse(string, DATE_FORMAT);
                } catch (Exception e) {
                    return null;
                }
            }
        });

        // ❌ SUPPRESSION de la contrainte empêchant les dates passées
        // (ne rien mettre ici pour DayCellFactory)

        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());
    }


    /**
     * Mode modification
     */
    public void preparerModification(GestionMedicamentsController.Medicament medicament) {
        this.medicamentAModifier = medicament;
        this.modeModification = true;

        nomField.setText(medicament.getNom());
        datePerField.setValue(medicament.getDatePeremption()); // ✔ on met directement LocalDate
        quantiteField.setText(String.valueOf(medicament.getQuantité()));
        prixField.setText(String.valueOf((int) medicament.getPrix()));
    }

    private void valider() {
        try {
            // Récupération des champs
            String nom = nomField.getText().trim();
            String qteStr = quantiteField.getText().trim();
            String prixStr = prixField.getText().trim();
            LocalDate datePer = datePerField.getValue(); // ✔ pas de texte !

            // --- VALIDATIONS ---

            if (nom.isEmpty() || qteStr.isEmpty() || prixStr.isEmpty() || datePer == null) {
                showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }



            int qte;
            try {
                qte = Integer.parseInt(qteStr);
                if (qte < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Quantité invalide", "Veuillez entrer une quantité valide.");
                return;
            }

            float prix;
            try {
                prix = Float.parseFloat(prixStr);
                if (prix < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Prix invalide", "Veuillez entrer un prix valide.");
                return;
            }

            // --- AJOUT OU MODIFICATION ---

            if (modeModification && medicamentAModifier != null) {
                medicamentAModifier.setNom(nom);
                medicamentAModifier.setDatePeremption(datePer);
                medicamentAModifier.setQuantité(qte);
                medicamentAModifier.setPrix(prix);
                nouveauMedicament = medicamentAModifier;
            } else {
                nouveauMedicament = new GestionMedicamentsController.Medicament(
                        -1, nom, datePer, qte, prix
                );
            }

            fermer(true);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fermer(boolean confirmer) {
        this.confirme = confirmer;
        Stage s = (Stage) btnAnnuler.getScene().getWindow();
        s.close();
    }

    public boolean isConfirme() { return confirme; }

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
