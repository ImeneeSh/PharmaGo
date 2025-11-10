package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AjouterMedicamentController {

    @FXML private TextField nomField;
    @FXML private TextField DatePerField;
    @FXML private TextField quantiteField;
    @FXML private TextField prixField;
    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    private GestionMedicamentsController.Medicament nouveauMedicament;
    private boolean confirme = false;

    @FXML
    public void initialize() {
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());
    }

    private void valider() {
        String nomComplet = nomField.getText() + " " + DatePerField.getText();
        LocalDate datePer = LocalDate.parse(DatePerField.getText()); // en supposant format ISO yyyy-MM-dd
        int qte = Integer.parseInt(quantiteField.getText());
        float prix = Float.parseFloat(prixField.getText());

        nouveauMedicament = new GestionMedicamentsController.Medicament(
                "C" + (int)(Math.random() * 1000),
                nomField.getText(),
                datePer,
                qte,
                prix
        );

        fermer(true);
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
