package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
        prixField.setText(String.valueOf(medicament.getPrix()));
    }

    private void valider() {
        try {
            LocalDate datePer = LocalDate.parse(DatePerField.getText()); // Format ISO yyyy-MM-dd
            int qte = Integer.parseInt(quantiteField.getText());
            float prix = Float.parseFloat(prixField.getText());

            if (modeModification && medicamentAModifier != null) {
                // Mode modification : mettre à jour le médicament existant
                medicamentAModifier.setNom(nomField.getText());
                medicamentAModifier.setDatePeremption(datePer);
                medicamentAModifier.setQuantité(qte);
                medicamentAModifier.setPrix(prix);
                nouveauMedicament = medicamentAModifier;
            } else {
                // Mode ajout : créer un nouveau médicament
                nouveauMedicament = new GestionMedicamentsController.Medicament(
                        "M" + (int)(Math.random() * 1000),
                        nomField.getText(),
                        datePer,
                        qte,
                        prix
                );
            }

            fermer(true);
        } catch (Exception e) {
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
}
