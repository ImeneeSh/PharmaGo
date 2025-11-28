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

    // Classe interne pour représenter un client dans le ComboBox
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

        public MedicamentItem(int idMed, String nomMed, float prixMed) {
            this.idMed = idMed;
            this.nom= nomMed;
            this.prix = prixMed;
        }

        public int getIdMed() { return idMed; }
        public String getNom() { return nom; }
        public float getPrix() { return prix; }

        @Override
        public String toString() {
            return nom + " - " + prix + " DA";
        }
    }


    @FXML
    public void initialize() {

        chargerMedicaments();

        // Charger les clients depuis la BDD
        chargerClients();

        // Initialisation Spinner pour qttField
        qttField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));

        // Initialisation ComboBox pour statutField
        statutField.getItems().addAll("livrée", "en cours", "en attente", "annulée");

        // Initialisation ComboBox pour typeField
        typeField.getItems().addAll("sous chaine du froid", "sous congélation", "dangereuses", "normale");

        // Actions des boutons
        btnAnnuler.setOnAction(e -> fermer(false));
        btnConfirmer.setOnAction(e -> valider());

        //empêche visuellement l’utilisateur de cliquer sur les dates passées.
        dateLivField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0c0;"); // rouge clair
                }
            }
        });

        medicamentComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculerCoutTotal());
        qttField.valueProperty().addListener((obs, oldVal, newVal) -> calculerCoutTotal());
        taxeField.textProperty().addListener((obs, oldVal, newVal) -> calculerCoutTotal());

        coutField.setEditable(false); // le coût est automatique

    }
    //pour le calcul auto du cout
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
        String query = "SELECT idMed, nomMed, prixMed FROM medicament ORDER BY nomMed";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                medicamentComboBox.getItems().add(
                        new MedicamentItem(
                                rs.getInt("idMed"),
                                rs.getString("nomMed"),
                                rs.getFloat("prixMed")
                        )
                );
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur DB", "Impossible de charger les médicaments : " + e.getMessage());
        }
    }



    /**
     * Charge les clients depuis la base de données dans le ComboBox
     */
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

    /**
     * Prépare le contrôleur pour la modification d'une livraison existante
     * @param livraison La livraison à modifier
     */
    public void preparerModification(GestionLivraisonsController.Livraison livraison) {
        this.livraisonAModifier = livraison;
        this.modeModification = true;

        // Trouver le client dans le ComboBox
        for (ClientItem item : clientComboBox.getItems()) {
            if (item.getCodeClt() == livraison.getCodeClt()) {
                clientComboBox.setValue(item);
                break;
            }
        }

        dateLivField.setValue(livraison.getDate());
        qttField.getValueFactory().setValue(livraison.getNombreMedicaments());
        taxeField.setText(String.valueOf(livraison.getTaxe()));
        coutField.setText(String.valueOf(livraison.getCout()));
        statutField.setValue(livraison.getStatut());
        typeField.setValue(livraison.getType());
        urgentCheckBox.setSelected(livraison.isUrgent());
    }

    private void valider() {
        try {
            // Récupération et validations basiques
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

            // Attention : s'assurer qu'un médicament est sélectionné (évite NPE)
            MedicamentItem medSelectionne = medicamentComboBox.getValue();
            if (medSelectionne == null) {
                showAlert(Alert.AlertType.ERROR, "Médicament manquant", "Veuillez sélectionner un médicament.");
                return;
            }

            int quantite = qttField.getValue();

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

            // Calcul du coût en toute sécurité (medSelectionne non null)
            float cout = (medSelectionne.getPrix() * quantite) + taxe;

            boolean urgent = urgentCheckBox.isSelected();
            String clientNom = clientSelected.getNom() + " " + clientSelected.getPrenom();

            // --- Déplacement de la contrainte de date (si tu veux l'autoriser, supprime ce bloc) ---
            // Si tu veux autoriser les dates antérieures (ex : livraisons historiques), supprime ce contrôle.
            if (dateLiv.isBefore(LocalDate.now())) {
                // si tu ne veux pas bloquer, commente/retire ce bloc
                showAlert(Alert.AlertType.ERROR, "Date invalide",
                        "La date de livraison ne peut pas être antérieure à la date actuelle.");
                return;
            }

            // Construire / mettre à jour l'objet avant de fermer la fenêtre
            if (modeModification && livraisonAModifier != null) {
                livraisonAModifier.setCodeClt(clientSelected.getCodeClt());
                livraisonAModifier.setClient(clientNom);
                livraisonAModifier.setDate(dateLiv);
                livraisonAModifier.setNombreMedicaments(quantite);
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
                        ""
                );
            }

            // Tout est OK -> fermer en confirmant
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
