package com.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

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

        System.out.println("Email: " + email + ", Mot de passe: " + password);

        try {
            // Charger le FXML du tableau de bord
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/TableauBord.fxml"));
            Parent root = loader.load();

            // Créer la scène
            Scene scene = new Scene(root);

            // 🔹 Charger et appliquer les fichiers CSS
            String tableauCss = getClass().getResource("/styles/tableauBord.css").toExternalForm();
            String menuCss = getClass().getResource("/styles/menu.css").toExternalForm();
            scene.getStylesheets().addAll(tableauCss, menuCss);

            // Changer la scène dans la fenêtre actuelle
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tableau de bord");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void ouvrirInscription() {
        // TODO: Redirection vers Inscription.fxml (plus tard)
        System.out.println("Redirection vers l'inscription...");
    }
}
