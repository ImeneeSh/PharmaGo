package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AjouterLivraisonController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateLivField;           // Changement TextField -> DatePicker
    @FXML private Spinner<Integer> qttField;         // Changement TextField -> Spinner
    @FXML private TextField taxeField;
    @FXML private TextField coutField;
    @FXML private ComboBox<String> statutField;      // Changement TextField -> ComboBox
    @FXML private ComboBox<String> typeField;        // Changement TextField -> ComboBox
    @FXML private TextField medicamentField;


    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    private GestionLivraisonsController.Livraison nouvelleLivraison;
    private boolean confirme = false;

    @FXML
    public void initialize() {
        // Initialisation Spinner pour qttField
        qttField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));

        // Initialisation ComboBox pour statutField
        statutField.getItems().addAll("livrée", "en cours", "en attente", "annulée");

        // Initialisation ComboBox pour typeField
        typeField.getItems().addAll("livrée", "en cours", "en attente", "annulée");

        // Actions des boutons
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());
    }

    private void valider() {
        try {
            String nomComplet = nomField.getText() + " " + prenomField.getText();
            LocalDate dateLiv = dateLivField.getValue(); // DatePicker fournit LocalDate
            int quantite = qttField.getValue();
            int taxe = Integer.parseInt(taxeField.getText());
            float cout = Float.parseFloat(coutField.getText());
            String statut = statutField.getValue();
            String type = typeField.getValue();
            String numero = "L" + (int)(Math.random() * 10000);
            boolean urgent = false; // Par défaut, ou ajouter un Checkbox
            String nomMedicament = medicamentField.getText();

            nouvelleLivraison = new GestionLivraisonsController.Livraison(
                    numero,
                    nomComplet,
                    dateLiv,
                    quantite,
                    taxe,
                    cout,
                    statut,
                    type,
                    urgent,
                    nomMedicament
            );

            fermer(true);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Veuillez remplir correctement tous les champs !");
            alert.showAndWait();
        }
    }

    private void fermer(boolean confirmer) {
        this.confirme = confirmer;
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirme() {
        return confirme;
    }

    public GestionLivraisonsController.Livraison getNouvelleLivraison() {
        return nouvelleLivraison;
    }


}
