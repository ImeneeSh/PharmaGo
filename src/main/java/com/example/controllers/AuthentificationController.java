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

            // Récupérer la scène actuelle et le stage
            Scene currentScene = btnLogin.getScene();
            Stage stage = (Stage) currentScene.getWindow();
            
            // Récupérer les dimensions avec valeurs par défaut si nécessaire
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            
            // Si les dimensions de la scène sont 0, utiliser celles du stage
            if (width == 0 || height == 0) {
                width = stage.getWidth() > 0 ? stage.getWidth() : 1024;
                height = stage.getHeight() > 0 ? stage.getHeight() : 768;
            }

            // Créer la scène avec les bonnes dimensions
            Scene scene = new Scene(root, width, height);

            // 🔹 Charger et appliquer les fichiers CSS
            String tableauCss = getClass().getResource("/styles/tableauBord.css").toExternalForm();
            String menuCss = getClass().getResource("/styles/menu.css").toExternalForm();
            scene.getStylesheets().addAll(tableauCss, menuCss);

            // Appliquer la police par défaut
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            // Changer la scène dans la fenêtre actuelle
            stage.setScene(scene);
            stage.setTitle("Tableau de bord");
            
            // S'assurer que la fenêtre conserve ses dimensions minimales
            stage.setMinWidth(1024);
            stage.setMinHeight(720);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void ouvrirInscription() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/Inscription.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle et le stage
            Scene currentScene = linkInscrire.getScene();
            Stage stage = (Stage) currentScene.getWindow();
            
            // Récupérer les dimensions avec valeurs par défaut si nécessaire
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            
            // Si les dimensions de la scène sont 0, utiliser celles du stage
            if (width == 0 || height == 0) {
                width = stage.getWidth() > 0 ? stage.getWidth() : 1024;
                height = stage.getHeight() > 0 ? stage.getHeight() : 768;
            }

            Scene scene = new Scene(root, width, height);

            // Charger CSS login (ou tableauBord si tu veux garder le même style)
            String loginCss = getClass().getResource("/styles/Authentification.css").toExternalForm();
            scene.getStylesheets().add(loginCss);

            stage.setScene(scene);
            stage.setTitle("Inscription");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
