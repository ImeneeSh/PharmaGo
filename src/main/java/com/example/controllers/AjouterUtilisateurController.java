package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

public class AjouterUtilisateurController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private PasswordField motDePasseField;
    @FXML private PasswordField confirmerMotDePasseField;

    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    private GestionUtilisateursController.Utilisateur nouvelUtilisateur;
    private GestionUtilisateursController.Utilisateur utilisateurAModifier;
    private boolean confirme = false;
    private boolean modeModification = false;

   
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ]+(?:[-' ][A-Za-zÀ-ÖØ-öø-ÿ]+)*$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @FXML
    public void initialize() {
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());
    }

   
    public void preparerModification(GestionUtilisateursController.Utilisateur utilisateur) {
        this.utilisateurAModifier = utilisateur;
        this.modeModification = true;

        
        nomField.setText(utilisateur.getNom());
        prenomField.setText(utilisateur.getPrenom());
        emailField.setText(utilisateur.getMail());

       
        motDePasseField.clear();
        confirmerMotDePasseField.clear();
    }

   
    private void valider() {
        String nom = nomField.getText() != null ? nomField.getText().trim() : "";
        String prenom = prenomField.getText() != null ? prenomField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim().toLowerCase() : "";
        String motDePasse = motDePasseField.getText() != null ? motDePasseField.getText() : "";
        String confirmerMotDePasse = confirmerMotDePasseField.getText() != null ? confirmerMotDePasseField.getText() : "";

       
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        
        if (!NAME_PATTERN.matcher(nom).matches()) {
            showAlert(Alert.AlertType.ERROR, "Nom invalide", "Le nom ne doit contenir que des lettres.");
            return;
        }
        if (!NAME_PATTERN.matcher(prenom).matches()) {
            showAlert(Alert.AlertType.ERROR, "Prénom invalide", "Le prénom ne doit contenir que des lettres.");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Veuillez saisir une adresse email valide.");
            return;
        }

        
        if (!modeModification || (!motDePasse.isEmpty() || !confirmerMotDePasse.isEmpty())) {
            if (!motDePasse.equals(confirmerMotDePasse)) {
                showAlert(Alert.AlertType.ERROR, "Erreur mot de passe", "Les mots de passe ne correspondent pas.");
                return;
            }
            if (motDePasse.length() < 10) {
                showAlert(Alert.AlertType.ERROR, "Mot de passe trop court", "Le mot de passe doit contenir au minimum 10 caractères.");
                return;
            }
        }

       
        if (modeModification && utilisateurAModifier != null) {
            utilisateurAModifier.setNom(nom);
            utilisateurAModifier.setPrenom(prenom);
            utilisateurAModifier.setMail(email);
            if (!motDePasse.isEmpty()) utilisateurAModifier.setMotDePasse(motDePasse);
            nouvelUtilisateur = utilisateurAModifier;
        } else {
            nouvelUtilisateur = new GestionUtilisateursController.Utilisateur(-1, nom, prenom, email);
            nouvelUtilisateur.setMotDePasse(motDePasse);
        }

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

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
