package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmerSuppressionController {

    @FXML private Label codeLabel;
    @FXML private Label nomLabel;
    @FXML private Label adresseLabel;
    @FXML private Label telephoneLabel;
    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    private boolean confirmation = false;

    @FXML
    public void initialize() {
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> fermer(true));
    }

    public void setClient(GestionClientsController.Client client) {
        codeLabel.setText(client.getCode());
        nomLabel.setText(client.getNom());
        adresseLabel.setText(client.getAdresse());
        telephoneLabel.setText(client.getTelephone());
    }

    private void fermer(boolean confirme) {
        confirmation = confirme;
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmation() {
        return confirmation;
    }
}
