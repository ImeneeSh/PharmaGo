package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjouterUtilisateurController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField motDePasseField;
    @FXML private PasswordField confirmerMotDePasseField;

    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    private GestionUtilisateursController.Utilisateur nouvelUtilisateur;
    private boolean confirme = false;

    @FXML
    public void initialize() {
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());
    }

    private void valider() {
        String nomComplet = nomField.getText() + " " + prenomField.getText();
        String email = emailField.getText();
        String motDePasse = motDePasseField.getText();
        String confirmerMotDePasse = confirmerMotDePasseField.getText();

        if (!motDePasse.equals(confirmerMotDePasse)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Les mots de passe ne correspondent pas !");
            alert.showAndWait();
            return;
        }

        nouvelUtilisateur = new GestionUtilisateursController.Utilisateur(
                "C" + (int)(Math.random() * 1000),
                nomComplet,
                email,
                motDePasse
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

    public GestionUtilisateursController.Utilisateur getNouvelUtilisateur() {
        return nouvelUtilisateur;
    }
}
