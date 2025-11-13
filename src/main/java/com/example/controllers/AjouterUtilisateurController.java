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
    private GestionUtilisateursController.Utilisateur utilisateurAModifier;
    private boolean confirme = false;
    private boolean modeModification = false;

    @FXML
    public void initialize() {
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());
    }

    /**
     * Prépare le contrôleur pour la modification d'un utilisateur existant
     * @param utilisateur L'utilisateur à modifier
     */
    public void preparerModification(GestionUtilisateursController.Utilisateur utilisateur) {
        this.utilisateurAModifier = utilisateur;
        this.modeModification = true;
        
        // Séparer le nom complet en nom et prénom
        String[] nomParts = utilisateur.getNom().split(" ", 2);
        if (nomParts.length > 0) {
            nomField.setText(nomParts[0]);
            if (nomParts.length > 1) {
                prenomField.setText(nomParts[1]);
            }
        }
        
        emailField.setText(utilisateur.getEmail());
        // Ne pas préremplir les mots de passe pour des raisons de sécurité
        motDePasseField.clear();
        confirmerMotDePasseField.clear();
    }

    private void valider() {
        String nomComplet = nomField.getText() + " " + prenomField.getText();
        String email = emailField.getText();
        String motDePasse = motDePasseField.getText();
        String confirmerMotDePasse = confirmerMotDePasseField.getText();

        // En mode modification, si les champs de mot de passe sont vides, on garde l'ancien
        if (modeModification && utilisateurAModifier != null) {
            if (motDePasse.isEmpty() && confirmerMotDePasse.isEmpty()) {
                // Garder l'ancien mot de passe
                motDePasse = utilisateurAModifier.getMotDePasse();
                confirmerMotDePasse = utilisateurAModifier.getMotDePasse();
            } else if (!motDePasse.equals(confirmerMotDePasse)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Les mots de passe ne correspondent pas !");
                alert.showAndWait();
                return;
            }
            
            // Mode modification : mettre à jour l'utilisateur existant
            utilisateurAModifier.setNom(nomComplet);
            utilisateurAModifier.setEmail(email);
            utilisateurAModifier.setMotDePasse(motDePasse);
            nouvelUtilisateur = utilisateurAModifier;
        } else {
            // Mode ajout : vérifier que les mots de passe correspondent
            if (!motDePasse.equals(confirmerMotDePasse)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Les mots de passe ne correspondent pas !");
                alert.showAndWait();
                return;
            }
            
            nouvelUtilisateur = new GestionUtilisateursController.Utilisateur(
                    "U" + (int)(Math.random() * 1000),
                    nomComplet,
                    email,
                    motDePasse
            );
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
}
