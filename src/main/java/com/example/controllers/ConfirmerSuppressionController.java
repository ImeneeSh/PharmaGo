package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConfirmerSuppressionController {

    // 🔹 Titres et labels communs
    @FXML private Label titreLabel;
    @FXML private Label codeLabel;
    @FXML private Label nomLabel;

    // 🔹 Partie client
    @FXML private Label adresseLabel;
    @FXML private Label telephoneLabel;
    @FXML private VBox blocClient;

    // 🔹 Partie médicament
    @FXML private Label quantite;
    @FXML private Label prix;
    @FXML private VBox blocMedicament;

    // Partie livraison
    @FXML private Label quantiteLivraison;
    @FXML private Label taxe;
    @FXML private Label coutTotal;
    @FXML private VBox blocLivraison;

    // Partie utilisateur
    @FXML private VBox blocUtilisateur;

    // 🔹 Boutons
    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    // 🔹 Résultat de la confirmation
    private boolean confirmation = false;

    @FXML
    public void initialize() {
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> {
            fermer(true);
            showSuccessAlert();
        });

        // Par défaut : cacher tous les blocs spécifiques
        if (blocClient != null) blocClient.setVisible(false);
        if (blocMedicament != null) blocMedicament.setVisible(false);
        if (blocLivraison != null) blocLivraison.setVisible(false);
        if (blocUtilisateur != null) blocUtilisateur.setVisible(false);
    }

    // ============================================================
    // 🧩 Cas Client
    // ============================================================
    public void setClient(GestionClientsController.Client client) {
        titreLabel.setText("Supprimer ce client ?");
        codeLabel.setText("Code : C" + String.format("%03d", client.getCodeClt()));
        nomLabel.setText("Nom : " + client.getNom() + " " + client.getPrenom());
        adresseLabel.setText("Adresse : " + (client.getAdresse() != null ? client.getAdresse() : ""));
        telephoneLabel.setText("Téléphone : " + (client.getTelephone() != null ? client.getTelephone() : ""));

        blocClient.setVisible(true);
        blocMedicament.setVisible(false);
        blocLivraison.setVisible(false);
        blocUtilisateur.setVisible(false);
    }

    // ============================================================
    // 💊 Cas Médicament
    // ============================================================
    public void setMedicament(GestionMedicamentsController.Medicament medicament) {
        titreLabel.setText("Supprimer ce médicament ?");
        codeLabel.setText("Code : M" + String.format("%03d", medicament.getIdMed()));
        nomLabel.setText("Nom : " + medicament.getNom());
        quantite.setText("Quantité : " + medicament.getQuantité());
        prix.setText(String.format("Prix : %.2f DA", medicament.getPrix()));

        blocClient.setVisible(false);
        blocMedicament.setVisible(true);
        blocLivraison.setVisible(false);
        blocUtilisateur.setVisible(false);
    }

    // ============================================================
    // Cas Livraison
    // ============================================================
    public void setLivraison(GestionLivraisonsController.Livraison livraison) {
        titreLabel.setText("Supprimer cette livraison ?");
        codeLabel.setText("Code : L" + String.format("%04d", livraison.getNumLiv()));
        nomLabel.setText("Nom du client : " + livraison.getClient());
        quantiteLivraison.setText("Quantité : " + livraison.getNombreMedicaments());
        taxe.setText(String.format("Taxe : %d DA", livraison.getTaxe()));
        coutTotal.setText(String.format("Cout total : %.2f DA", livraison.getCout()));

        blocClient.setVisible(false);
        blocMedicament.setVisible(false);
        blocLivraison.setVisible(true);
        blocUtilisateur.setVisible(false);
    }

    // ============================================================
    // Cas Utilisateur
    // ============================================================
    public void setUtilisateur(GestionUtilisateursController.Utilisateur utilisateur) {
        titreLabel.setText("Supprimer cet utilisateur ?");
        codeLabel.setText("Code : U" + String.format("%03d", utilisateur.getIdUser()));
        nomLabel.setText("Nom : " + utilisateur.getNom());

        blocClient.setVisible(false);
        blocMedicament.setVisible(false);
        blocLivraison.setVisible(false);
        blocUtilisateur.setVisible(true);
    }

    // ============================================================
    // ⚙️ Gestion fermeture
    // ============================================================
    private void fermer(boolean confirme) {
        this.confirmation = confirme;
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmation() {
        return confirmation;
    }

    /**
     * Affiche une alerte de succès après suppression
     */
    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Suppression réussie");
        alert.setHeaderText(null);
        alert.setContentText("L'élément a été supprimé avec succès !");
        alert.showAndWait();
    }
}