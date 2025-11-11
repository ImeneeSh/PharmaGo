package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AuthentificationController {

    @FXML
    private ImageView logoImage;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button btnLogin;

    @FXML
    private Hyperlink linkInscrire;

    @FXML
    public void initialize() {
        // Charger le logo
        logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/logo.png")));

        // Action bouton login
        btnLogin.setOnAction(event -> seConnecter());

        // Action lien inscription
        linkInscrire.setOnAction(event -> ouvrirInscription());
    }

    private void seConnecter() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // TODO: Ajouter la logique d'authentification
        System.out.println("Email: " + email + ", Mot de passe: " + password);
    }

    private void ouvrirInscription() {
        // TODO: Ouvrir la page d'inscription
        System.out.println("Redirection vers l'inscription...");
    }
}
