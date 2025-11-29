package org.pharmago.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjouterClientController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField adresseField;
    @FXML private TextField telephoneField;
    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    private GestionClientsController.Client nouveauClient;
    private GestionClientsController.Client clientAModifier;
    private boolean confirme = false;
    private boolean modeModification = false;

    @FXML
    public void initialize() {
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());
    }

    /**
     * Prépare le contrôleur pour la modification d'un client existant
     * @param client Le client à modifier
     */
    public void preparerModification(GestionClientsController.Client client) {
        this.clientAModifier = client;
        this.modeModification = true;
        
        nomField.setText(client.getNom());
        prenomField.setText(client.getPrenom());
        adresseField.setText(client.getAdresse() != null ? client.getAdresse() : "");
        telephoneField.setText(client.getTelephone() != null ? client.getTelephone() : "");
    }

    private void valider() {
        String nom = nomField.getText() != null ? nomField.getText().trim() : "";
        String prenom = prenomField.getText() != null ? prenomField.getText().trim() : "";
        String adresse = adresseField.getText() != null ? adresseField.getText().trim() : "";
        String telephone = telephoneField.getText() != null ? telephoneField.getText().trim() : "";

        // 🔥 Nouvelle contrainte : tous les champs doivent être remplis
        if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty() || telephone.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Tous les champs doivent être remplis pour ajouter le client.");
            return; // empêche la création si un champ est vide
        }

        if (modeModification && clientAModifier != null) {
            // Mode modification : mettre à jour le client existant
            clientAModifier.setNom(nom);
            clientAModifier.setPrenom(prenom);
            clientAModifier.setAdresse(adresse);
            clientAModifier.setTelephone(telephone);
            nouveauClient = clientAModifier;
        } else {
            // Mode ajout : créer un nouveau client (codeClt sera généré par la BDD)
            nouveauClient = new GestionClientsController.Client(-1, nom, prenom, adresse, telephone);
        }
        fermer(true);
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void fermer(boolean confirmer) {
        this.confirme = confirmer;
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirme() {
        return confirme;
    }

    public GestionClientsController.Client getNouveauClient() {
        return nouveauClient;
    }
}
