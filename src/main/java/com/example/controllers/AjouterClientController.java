package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjouterClientController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField adresseField;
    @FXML private TextField telephoneField;
    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    private GestionClientsController.Client nouveauClient;
    private boolean confirme = false;

    @FXML
    public void initialize() {
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());
    }

    private void valider() {
        String nomComplet = nomField.getText() + " " + prenomField.getText();
        nouveauClient = new GestionClientsController.Client(
                "C" + (int)(Math.random() * 1000), // Génération temporaire d’un code
                nomComplet,
                adresseField.getText(),
                telephoneField.getText()
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

    public GestionClientsController.Client getNouveauClient() {
        return nouveauClient;
    }
}
