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

            // Récupérer la scène actuelle et le stage
            Scene currentScene = btnInscrire.getScene();
            Stage currentStage = (Stage) currentScene.getWindow();
            
            // Récupérer les dimensions avec valeurs par défaut si nécessaire
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            
            // Si les dimensions de la scène sont 0, utiliser celles du stage
            if (width == 0 || height == 0) {
                width = currentStage.getWidth() > 0 ? currentStage.getWidth() : 1024;
                height = currentStage.getHeight() > 0 ? currentStage.getHeight() : 768;
            }

            Scene scene = new Scene(root, width, height);

            // Appliquer les CSS du tableau de bord et menu
            String tableauCss = getClass().getResource("/styles/tableauBord.css").toExternalForm();
            String menuCss = getClass().getResource("/styles/menu.css").toExternalForm();
            scene.getStylesheets().addAll(tableauCss, menuCss);

            // Appliquer la police par défaut
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            // Définir la nouvelle scène
            currentStage.setScene(scene);
            currentStage.setTitle("Tableau de bord");
            
            // S'assurer que la fenêtre conserve ses dimensions minimales
            currentStage.setMinWidth(1024);
            currentStage.setMinHeight(720);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/Authentification.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et le stage
            Scene currentScene = linkConnexion.getScene();
            Stage currentStage = (Stage) currentScene.getWindow();
            
            // Récupérer les dimensions avec valeurs par défaut si nécessaire
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            
            // Si les dimensions de la scène sont 0, utiliser celles du stage
            if (width == 0 || height == 0) {
                width = currentStage.getWidth() > 0 ? currentStage.getWidth() : 1024;
                height = currentStage.getHeight() > 0 ? currentStage.getHeight() : 768;
            }

            Scene scene = new Scene(root, width, height);

            // Charger CSS login
            String loginCss = getClass().getResource("/styles/Authentification.css").toExternalForm();
            scene.getStylesheets().add(loginCss);

            currentStage.setScene(scene);
            currentStage.setTitle("Connexion");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
