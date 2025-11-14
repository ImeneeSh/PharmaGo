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
import java.util.regex.Pattern;

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

    // Regex
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ]+(?:[-' ][A-Za-zÀ-ÖØ-öø-ÿ]+)*$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @FXML
    public void initialize() {
        // Charger le logo (vérifie que le chemin est correct)
        try {
            logoImage.setImage(new Image(getClass().getResourceAsStream("/assets/logo.png")));
        } catch (Exception e) {
            System.out.println("Logo non trouvé: " + e.getMessage());
        }

        // Action bouton inscription
        btnInscrire.setOnAction(event -> sInscrire());

        // Action lien connexion
        linkConnexion.setOnAction(event -> ouvrirConnexion());
    }

    private void sInscrire() {
        String nom = nomField.getText() != null ? nomField.getText().trim() : "";
        String prenom = prenomField.getText() != null ? prenomField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim().toLowerCase() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";
        String confirmPassword = confirmPasswordField.getText() != null ? confirmPasswordField.getText() : "";

        // Vérifications basiques
        if(nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        if(!NAME_PATTERN.matcher(nom).matches()) {
            showAlert(Alert.AlertType.ERROR, "Nom invalide", "Le nom ne doit contenir que des lettres.");
            return;
        }

        if(!NAME_PATTERN.matcher(prenom).matches()) {
            showAlert(Alert.AlertType.ERROR, "Prénom invalide", "Le prénom ne doit contenir que des lettres.");
            return;
        }

        if(!EMAIL_PATTERN.matcher(email).matches()) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Veuillez entrer une adresse e-mail valide (ex: nom@example.com).");
            return;
        }

        if(password.length() < 10) {
            showAlert(Alert.AlertType.ERROR, "Mot de passe trop court", "Le mot de passe doit contenir au minimum 10 caractères.");
            return;
        }

        if(!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Mots de passe différents", "Les mots de passe ne correspondent pas.");
            return;
        }

        // Hachage du mot de passe avec BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

        // Insertion dans la DB
        // Remplace 'utilisateur' par le nom exact de ta table si différent
        String insertSql = "INSERT INTO utilisateur (nom, prenom, mail, mdp) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de se connecter à la base de données.");
                return;
            }


            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, nom);
                pstmt.setString(2, prenom);
                pstmt.setString(3, email);
                pstmt.setString(4, hashedPassword);

                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    // Succès : afficher message puis rediriger vers tableau de bord
                    showAlert(Alert.AlertType.INFORMATION, "Inscription réussie", "Votre compte a été créé avec succès !");
                    ouvrirTableauBord(); // redirection
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de créer le compte. Veuillez réessayer.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Une erreur est survenue : " + e.getMessage());
        }

    }

    private void ouvrirConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/Authentification.fxml"));
            Parent root = loader.load();

            Scene currentScene = linkConnexion.getScene();
            Stage currentStage = (Stage) currentScene.getWindow();

            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            if (width == 0 || height == 0) {
                width = currentStage.getWidth() > 0 ? currentStage.getWidth() : 1024;
                height = currentStage.getHeight() > 0 ? currentStage.getHeight() : 768;
            }

            Scene scene = new Scene(root, width, height);
            String loginCss = getClass().getResource("/styles/Authentification.css").toExternalForm();
            scene.getStylesheets().add(loginCss);

            currentStage.setScene(scene);
            currentStage.setTitle("Connexion");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ouvrirTableauBord() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/TableauBord.fxml"));
            Parent root = loader.load();

            Scene currentScene = btnInscrire.getScene();
            Stage currentStage = (Stage) currentScene.getWindow();

            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            if (width == 0 || height == 0) {
                width = currentStage.getWidth() > 0 ? currentStage.getWidth() : 1024;
                height = currentStage.getHeight() > 0 ? currentStage.getHeight() : 768;
            }

            Scene scene = new Scene(root, width, height);
            String tableauCss = getClass().getResource("/styles/tableauBord.css").toExternalForm();
            String menuCss = getClass().getResource("/styles/menu.css").toExternalForm();
            scene.getStylesheets().addAll(tableauCss, menuCss);
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            currentStage.setScene(scene);
            currentStage.setTitle("Tableau de bord");
            currentStage.setMinWidth(1024);
            currentStage.setMinHeight(720);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
