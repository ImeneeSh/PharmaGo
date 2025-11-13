package com.example.controllers;

import javafx.fxml.FXML;
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
        
        // Séparer le nom complet en nom et prénom
        String[] nomParts = client.getNom().split(" ", 2);
        if (nomParts.length > 0) {
            nomField.setText(nomParts[0]);
            if (nomParts.length > 1) {
                prenomField.setText(nomParts[1]);
            }
        }
        
        adresseField.setText(client.getAdresse());
        telephoneField.setText(client.getTelephone());
    }

    private void valider() {
        String nomComplet = nomField.getText() + " " + prenomField.getText();
        
        if (modeModification && clientAModifier != null) {
            // Mode modification : mettre à jour le client existant
            clientAModifier.setNom(nomComplet);
            clientAModifier.setAdresse(adresseField.getText());
            clientAModifier.setTelephone(telephoneField.getText());
            nouveauClient = clientAModifier;
        } else {
            // Mode ajout : créer un nouveau client
            nouveauClient = new GestionClientsController.Client(
                    "C" + (int)(Math.random() * 1000), // Génération temporaire d'un code
                    nomComplet,
                    adresseField.getText(),
                    telephoneField.getText()
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

    public GestionClientsController.Client getNouveauClient() {
        return nouveauClient;
    }
}
