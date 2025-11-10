package com.example.controllers;

import javafx.fxml.FXML;
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

    // 🔹 Boutons
    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    // 🔹 Résultat de la confirmation
    private boolean confirmation = false;

    @FXML
    public void initialize() {
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> fermer(true));

        // Par défaut : cacher les blocs spécifiques
        if (blocClient != null) blocClient.setVisible(false);
        if (blocMedicament != null) blocMedicament.setVisible(false);
    }

    // ============================================================
    // 🧩 Cas Client
    // ============================================================
    public void setClient(GestionClientsController.Client client) {
        titreLabel.setText("Supprimer ce client ?");
        codeLabel.setText("Code : " + client.getCode());
        nomLabel.setText("Nom : " + client.getNom());
        adresseLabel.setText("Adresse : " + client.getAdresse());
        telephoneLabel.setText("Téléphone : " + client.getTelephone());

        blocClient.setVisible(true);
        blocMedicament.setVisible(false);
    }

    // ============================================================
    // 💊 Cas Médicament
    // ============================================================
    public void setMedicament(GestionMedicamentsController.Medicament medicament) {
        titreLabel.setText("Supprimer ce médicament ?");
        codeLabel.setText("Code : " + medicament.getCode());
        nomLabel.setText("Nom : " + medicament.getNom());
        quantite.setText("Quantité : " + medicament.getQuantité());
        prix.setText(String.format("Prix : %.2f DA", medicament.getPrix()));

        blocClient.setVisible(false);
        blocMedicament.setVisible(true);
    }

    // ============================================================
    // ⚙️ Gestion fermeture
    // ============================================================
    private void fermer(boolean confirme) {
        confirmation = confirme;
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmation() {
        return confirmation;
    }
}