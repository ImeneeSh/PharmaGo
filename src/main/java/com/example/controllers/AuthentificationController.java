package com.example.controllers;

import com.example.bdd.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/logo.png")));
        btnLogin.setOnAction(event -> seConnecter());
        linkInscrire.setOnAction(event -> ouvrirInscription());
    }

    private void seConnecter() {

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Champs vides
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants",
                    "Veuillez remplir tous les champs.");
            return;
        }

        // Format email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert(Alert.AlertType.ERROR, "Email invalide",
                    "Veuillez entrer une adresse email valide.");
            return;
        }

        String sql = "SELECT mdp FROM utilisateur WHERE mail = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            // Email n'existe pas
            if (!rs.next()) {
                showAlert(Alert.AlertType.ERROR, "Identifiants incorrects",
                        "Adresse email incorrecte ou inexistante.");
                return;
            }

            String hashedPassword = rs.getString("mdp");

            // Vérification du mot de passe
            if (!BCrypt.checkpw(password, hashedPassword)) {
                showAlert(Alert.AlertType.ERROR, "Mot de passe incorrect",
                        "Le mot de passe que vous avez saisi est incorrect.");
                return;
            }

            // Succès
            showAlert(Alert.AlertType.INFORMATION, "Connexion réussie",
                    "Bienvenue !");
            ouvrirTableauBord();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL",
                    "Une erreur est survenue : " + e.getMessage());
        }
    }

    private void ouvrirInscription() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/Inscription.fxml"));
            Parent root = loader.load();

            Scene currentScene = linkInscrire.getScene();
            Stage stage = (Stage) currentScene.getWindow();

            Scene scene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
            scene.getStylesheets().add(getClass().getResource("/styles/Authentification.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Inscription");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirTableauBord() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/TableauBord.fxml"));
            Parent root = loader.load();

            Scene currentScene = btnLogin.getScene();
            Stage stage = (Stage) currentScene.getWindow();

            Scene scene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
            scene.getStylesheets().addAll(
                    getClass().getResource("/styles/tableauBord.css").toExternalForm(),
                    getClass().getResource("/styles/menu.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setTitle("Tableau de bord");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}