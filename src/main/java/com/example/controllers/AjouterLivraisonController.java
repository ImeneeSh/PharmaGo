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
    private GestionLivraisonsController.Livraison livraisonAModifier;
    private boolean confirme = false;
    private boolean modeModification = false;

    @FXML
    public void initialize() {
        // Initialisation Spinner pour qttField
        qttField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));

        // Initialisation ComboBox pour statutField
        statutField.getItems().addAll("livrée", "en cours", "en attente", "annulée");

        // Initialisation ComboBox pour typeField
        typeField.getItems().addAll("sous chaine du froid", "sous congélation", "dangereuses", "normale");

        // Actions des boutons
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());
    }

    /**
     * Prépare le contrôleur pour la modification d'une livraison existante
     * @param livraison La livraison à modifier
     */
    public void preparerModification(GestionLivraisonsController.Livraison livraison) {
        this.livraisonAModifier = livraison;
        this.modeModification = true;
        
        // Séparer le nom complet en nom et prénom
        String[] nomParts = livraison.getClient().split(" ", 2);
        if (nomParts.length > 0) {
            nomField.setText(nomParts[0]);
            if (nomParts.length > 1) {
                prenomField.setText(nomParts[1]);
            }
        }
        
        dateLivField.setValue(livraison.getDate());
        qttField.getValueFactory().setValue(livraison.getNombreMedicaments());
        taxeField.setText(String.valueOf(livraison.getTaxe()));
        coutField.setText(String.valueOf(livraison.getCout()));
        statutField.setValue(livraison.getStatut());
        typeField.setValue(livraison.getType());
        medicamentField.setText(livraison.getMedicament());
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
            boolean urgent = false; // Par défaut, ou ajouter un Checkbox
            String nomMedicament = medicamentField.getText();

            if (modeModification && livraisonAModifier != null) {
                // Mode modification : mettre à jour la livraison existante
                livraisonAModifier.setClient(nomComplet);
                livraisonAModifier.setDate(dateLiv);
                livraisonAModifier.setNombreMedicaments(quantite);
                livraisonAModifier.setTaxe(taxe);
                livraisonAModifier.setCout(cout);
                livraisonAModifier.setStatut(statut);
                livraisonAModifier.setType(type);
                livraisonAModifier.setMedicament(nomMedicament);
                nouvelleLivraison = livraisonAModifier;
            } else {
                // Mode ajout : créer une nouvelle livraison
                String numero = "L" + (int)(Math.random() * 10000);
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
            }

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
