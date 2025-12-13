package com.example.controllers;

import com.example.bdd.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjouterLivraisonController {

    @FXML private ComboBox<ClientItem> clientComboBox;
    @FXML private DatePicker dateLivField;
    @FXML private Spinner<Integer> qttField;
    @FXML private TextField taxeField;
    @FXML private TextField coutField;
    @FXML private ComboBox<String> statutField;
    @FXML private ComboBox<String> typeField;
    @FXML private CheckBox urgentCheckBox;
    @FXML private ComboBox<MedicamentItem> medicamentComboBox;
    @FXML private Button btnAnnuler;
    @FXML private Button btnConfirmer;

    private GestionLivraisonsController.Livraison nouvelleLivraison;
    private GestionLivraisonsController.Livraison livraisonAModifier;
    private boolean confirme = false;
    private boolean modeModification = false;

   
    public static class ClientItem {
        private int codeClt;
        private String nom;
        private String prenom;

        public ClientItem(int codeClt, String nom, String prenom) {
            this.codeClt = codeClt;
            this.nom = nom;
            this.prenom = prenom;
        }

        public int getCodeClt() { return codeClt; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }

        @Override
        public String toString() {
            return nom + " " + prenom + " (C" + String.format("%03d", codeClt) + ")";
        }
    }

    public static class MedicamentItem {
        private int idMed;
        private String nom;
        private float prix;
        private int quantiteDisponible; 

        public MedicamentItem(int idMed, String nomMed, float prixMed, int quantiteDisponible) {
            this.idMed = idMed;
            this.nom = nomMed;
            this.prix = prixMed;
            this.quantiteDisponible = quantiteDisponible;
        }

        public int getIdMed() { return idMed; }
        public String getNom() { return nom; }
        public float getPrix() { return prix; }
        public int getQuantiteDisponible() { return quantiteDisponible; } 

        @Override
        public String toString() {
            return nom + " - " + prix + " DA (Dispo: " + quantiteDisponible + ")";
        }
    }


    @FXML
    public void initialize() {

        chargerMedicaments();

        chargerClients();

        qttField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));

       
        statutField.getItems().addAll("livrée", "en cours", "en attente", "annulée");

       
        typeField.getItems().addAll("sous chaine du froid", "sous congélation", "dangereuses", "normale");

       
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());

        
        dateLivField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0c0;");
                }
            }
        });

        medicamentComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculerCoutTotal());
        qttField.valueProperty().addListener((obs, oldVal, newVal) -> calculerCoutTotal());
        taxeField.textProperty().addListener((obs, oldVal, newVal) -> calculerCoutTotal());

        coutField.setEditable(false); 

    }
   
    private void calculerCoutTotal() {
        try {
            MedicamentItem med = medicamentComboBox.getValue();
            if (med == null) {
                coutField.setText("");
                return;
            }

            int quantite = qttField.getValue();
            int taxe = Integer.parseInt(taxeField.getText());

            float cout = (med.getPrix() * quantite) + taxe;
            coutField.setText(String.valueOf(cout));

        } catch (Exception e) {
            coutField.setText("");
        }
    }


    private void chargerMedicaments() {
        
        String query = "SELECT idMed, nomMed, prixMed, nbrBoite FROM medicament ORDER BY nomMed";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
               
                MedicamentItem med = new MedicamentItem(
                        rs.getInt("idMed"),
                        rs.getString("nomMed"),
                        rs.getFloat("prixMed"),
                        rs.getInt("nbrBoite")
                );
                medicamentComboBox.getItems().add(med);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur DB", "Impossible de charger les médicaments : " + e.getMessage());
        }
    }



    
    private void chargerClients() {
        String query = "SELECT codeClt, nom, prenom FROM client ORDER BY nom, prenom";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int codeClt = rs.getInt("codeClt");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                clientComboBox.getItems().add(new ClientItem(codeClt, nom, prenom));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur DB", "Impossible de charger les clients : " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public void preparerModification(GestionLivraisonsController.Livraison livraison) {
        this.livraisonAModifier = livraison;
        this.modeModification = true;

       
        for (ClientItem item : clientComboBox.getItems()) {
            if (item.getCodeClt() == livraison.getCodeClt()) {
                clientComboBox.setValue(item);
                break;
            }
        }

        dateLivField.setValue(livraison.getDate());
        qttField.getValueFactory().setValue(livraison.getQuantite());
        taxeField.setText(String.valueOf(livraison.getTaxe()));
        coutField.setText(String.valueOf(livraison.getCout()));
        statutField.setValue(livraison.getStatut());
        typeField.setValue(livraison.getType());
        urgentCheckBox.setSelected(livraison.isUrgent());

       
        for (MedicamentItem item : medicamentComboBox.getItems()) {
            if (item.getIdMed() == livraison.getMedicamentId()) {
                medicamentComboBox.setValue(item);
                break;
            }
        }
    }

    private void valider() {
        try {
           
            ClientItem clientSelected = clientComboBox.getValue();
            if (clientSelected == null) {
                showAlert(Alert.AlertType.ERROR, "Client manquant", "Veuillez sélectionner un client.");
                return;
            }

            LocalDate dateLiv = dateLivField.getValue();
            if (dateLiv == null) {
                showAlert(Alert.AlertType.ERROR, "Date manquante", "Veuillez sélectionner une date de livraison.");
                return;
            }

            String statut = statutField.getValue();
            if (statut == null || statut.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Statut manquant", "Veuillez sélectionner un statut.");
                return;
            }

            String type = typeField.getValue();
            if (type == null || type.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Type manquant", "Veuillez sélectionner un type de livraison.");
                return;
            }

           
            MedicamentItem medSelectionne = medicamentComboBox.getValue();
            if (medSelectionne == null) {
                showAlert(Alert.AlertType.ERROR, "Médicament manquant", "Veuillez sélectionner un médicament.");
                return;
            }

            int quantite = qttField.getValue();

           
            if (quantite > medSelectionne.getQuantiteDisponible()) {
                showAlert(Alert.AlertType.ERROR, "Quantité insuffisante",
                        "Quantité demandée: " + quantite +
                                "\nQuantité disponible: " + medSelectionne.getQuantiteDisponible() +
                                "\nVeuillez réduire la quantité.");
                return;
            }

            if (quantite <= 0) {
                showAlert(Alert.AlertType.ERROR, "Quantité invalide", "La quantité doit être supérieure à 0.");
                return;
            }

            int taxe;
            try {
                taxe = Integer.parseInt(taxeField.getText());
                if (taxe < 0) {
                    showAlert(Alert.AlertType.ERROR, "Taxe invalide", "La taxe ne peut pas être négative.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Taxe invalide", "Veuillez entrer un nombre valide pour la taxe.");
                return;
            }

           
            float cout = (medSelectionne.getPrix() * quantite) + taxe;

            boolean urgent = urgentCheckBox.isSelected();
            String clientNom = clientSelected.getNom() + " " + clientSelected.getPrenom();

            
            if (dateLiv.isBefore(LocalDate.now())) {
                
                showAlert(Alert.AlertType.ERROR, "Date invalide",
                        "La date de livraison ne peut pas être antérieure à la date actuelle.");
                return;
            }

           
            if (modeModification && livraisonAModifier != null) {
                livraisonAModifier.setCodeClt(clientSelected.getCodeClt());
                livraisonAModifier.setClient(clientNom);
                livraisonAModifier.setDate(dateLiv);
                livraisonAModifier.setNombreMedicaments(quantite);
                livraisonAModifier.setQuantite(quantite);
                livraisonAModifier.setTaxe(taxe);
                livraisonAModifier.setCout(cout);
                livraisonAModifier.setStatut(statut);
                livraisonAModifier.setType(type);
                livraisonAModifier.setUrgent(urgent);
                nouvelleLivraison = livraisonAModifier;
            } else {
                nouvelleLivraison = new GestionLivraisonsController.Livraison(
                        -1,
                        clientSelected.getCodeClt(),
                        clientNom,
                        dateLiv,
                        quantite,
                        taxe,
                        cout,
                        statut,
                        type,
                        urgent,
                        "",
                        medSelectionne.getIdMed(),
                        quantite
                );
            }

            
            fermer(true);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
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

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
