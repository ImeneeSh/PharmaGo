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

public class InscriptionController {

    @FXML
    private ImageView logoImage;

    @FXML
    private TextField nomField, prenomField, emailField;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private Button btnInscrire;

    @FXML
    private Hyperlink linkConnexion;

    @FXML
    public void initialize() {
        // Charger le logo
        logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/logo.png")));

        // Action bouton inscription
        btnInscrire.setOnAction(event -> sInscrire());

        // Action lien connexion
        linkConnexion.setOnAction(event -> ouvrirConnexion());
    }

    private void sInscrire() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // 🔹 Exemple de vérification simple
        if(!password.equals(confirmPassword)) {
            System.out.println("Les mots de passe ne correspondent pas !");
            return;
        }

        System.out.println("Nom: " + nom + ", Prénom: " + prenom + ", Email: " + email);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/TableauBord.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Appliquer les CSS du tableau de bord et menu
            String tableauCss = getClass().getResource("/styles/tableauBord.css").toExternalForm();
            String menuCss = getClass().getResource("/styles/menu.css").toExternalForm();
            scene.getStylesheets().addAll(tableauCss, menuCss);

            Stage stage = (Stage) btnInscrire.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Tableau de bord");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/Authentification.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Charger CSS login
            String loginCss = getClass().getResource("/styles/Authentification.css").toExternalForm();
            scene.getStylesheets().add(loginCss);

            Stage stage = (Stage) linkConnexion.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle("Connexion");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
