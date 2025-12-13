package com.example.controllers;

import com.example.bdd.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.text.Normalizer;


public class GestionLivraisonsController implements Initializable {

   
    @FXML
    private TextField searchField; 
    
    @FXML
    private Button btnNouvelleLivraison; 
    
    @FXML
    private ComboBox<String> filterStatut;
    
    @FXML
    private ComboBox<String> filterType; 
    
    @FXML
    private GridPane livraisonsGrid;

    private String medicament;

   
    private List<Livraison> livraisons = new ArrayList<>();
    
   
    private List<Livraison> livraisonsFiltres = new ArrayList<>();

   
    private Integer clientFiltre = null; 
   
    public void setClientFiltre(int codeClt) {
        this.clientFiltre = codeClt;
        appliquerFiltreClient(); 
    }

    
    private void appliquerFiltreClient() {
        if (clientFiltre != null) {
            livraisonsFiltres = livraisons.stream()
                    .filter(l -> l.getCodeClt() == clientFiltre)
                    .collect(Collectors.toList());
        } else {
            livraisonsFiltres = new ArrayList<>(livraisons);
        }
        afficherLivraisons();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerLivraisons();
        configurerFiltres();
        configurerRecherche();
        configurerBoutonNouvelleLivraison();
        searchField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(
                        getClass().getResource("/styles/GestionLivraisons.css").toExternalForm()
                );
            }
        });
    }

    
    private void chargerLivraisons() {
        livraisons.clear();
       
        String query = """
    SELECT L.numLiv, L.codeClt, L.dateLiv, L.priorite, L.statut, L.type_liv, L.taxe, L.cout,
           C.nom, C.prenom,
           COALESCE(SUM(I.quantite), 0) AS totalQuantite
    FROM livraison L
    LEFT JOIN client C ON L.codeClt = C.codeClt
    LEFT JOIN inclure I ON L.numLiv = I.numLiv
    GROUP BY L.numLiv, L.codeClt, L.dateLiv, L.priorite, L.statut, 
             L.type_liv, L.taxe, L.cout, C.nom, C.prenom
    ORDER BY L.numLiv DESC
    """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int numLiv = rs.getInt("numLiv");
                int codeClt = rs.getInt("codeClt");
                LocalDate dateLiv = rs.getDate("dateLiv") != null ? rs.getDate("dateLiv").toLocalDate() : null;
                String priorite = rs.getString("priorite");
                String statut = rs.getString("statut");
                String type_liv = rs.getString("type_liv");
                float taxe = rs.getFloat("taxe");
                float cout = rs.getFloat("cout");
                String nomClient = rs.getString("nom");
                String prenomClient = rs.getString("prenom");
                int totalQuantite = rs.getInt("totalQuantite");

               
                int medicamentId = 0;
                int quantite = 0;
                String medicamentQuery = "SELECT idMed, quantite FROM inclure WHERE numLiv = ? LIMIT 1";
                try (PreparedStatement medStmt = conn.prepareStatement(medicamentQuery)) {
                    medStmt.setInt(1, numLiv);
                    try (ResultSet medRs = medStmt.executeQuery()) {
                        if (medRs.next()) {
                            medicamentId = medRs.getInt("idMed");
                            quantite = medRs.getInt("quantite");
                        }
                    }
                }

                boolean urgent = "urgent".equals(priorite);
                String clientNom = (nomClient != null && prenomClient != null) ? nomClient + " " + prenomClient : "";

                String statutDisplay = convertirStatut(statut);
                String typeDisplay = convertirType(type_liv);

                livraisons.add(new Livraison(numLiv, codeClt, clientNom, dateLiv, totalQuantite,
                        (int) taxe, cout, statutDisplay, typeDisplay, urgent, "",
                        medicamentId, quantite));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur DB", "Impossible de charger les livraisons : " + e.getMessage());
            e.printStackTrace();
        }

        livraisonsFiltres = new ArrayList<>(livraisons);
        afficherLivraisons();
    }

   
    private String convertirStatut(String statut) {
        if (statut == null) return "";
        switch (statut) {
            case "en_attente": return "en attente";
            case "livrée": return "livrée";
            case "annulée": return "annulée";
            case "en_cours": return "en cours";
            default: return statut;
        }
    }

    
    private String convertirType(String type) {
        if (type == null) return "";
        switch (type) {
            case "sous_chaine_du_froid": return "sous chaine du froid";
            case "sous_congélation": return "sous congélation";
            case "dangereuse": return "dangereuses";
            case "normale": return "normale";
            default: return type;
        }
    }

   
    private String convertirStatutVersBDD(String statut) {
        if (statut == null) return "";
        switch (statut) {
            case "en attente": return "en_attente";
            case "livrée": return "livrée";
            case "annulée": return "annulée";
            case "en cours": return "en_cours";
            default: return statut;
        }
    }

    
    private String convertirTypeVersBDD(String type) {
        if (type == null) return "";
        switch (type) {
            case "sous chaine du froid": return "sous_chaine_du_froid";
            case "sous congélation": return "sous_congélation";
            case "dangereuses": return "dangereuse";
            case "normale": return "normale";
            default: return type;
        }
    }

   
    private void configurerFiltres() {
       
        filterStatut.getItems().addAll("Tous les statuts", "livrée", "en cours", "en attente", "annulée");
        filterStatut.setValue("Tous les statuts");
        
        
        filterType.getItems().addAll("Tous les types", "sous chaine du froid", "sous congélation", 
            "dangereuses", "normale");
        filterType.setValue("Tous les types");
        
        
        filterStatut.setOnAction(e -> appliquerFiltres());
        filterType.setOnAction(e -> appliquerFiltres());
    }

    
    private void configurerRecherche() {
       
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            appliquerFiltres();
        });
    }

   
    private void appliquerFiltres() {
        String critereRecherche = searchField.getText();
        String statutSelectionne = filterStatut.getValue();
        String typeSelectionne = filterType.getValue();
        
       
        livraisonsFiltres = livraisons.stream()
                .filter(livraison -> {
                   
                    boolean matchRecherche = true;
                    if (critereRecherche != null && !critereRecherche.trim().isEmpty()) {
                        String critereLower = critereRecherche.toLowerCase().trim();
                        matchRecherche = String.valueOf(livraison.getNumLiv()).contains(critereLower) ||
                                       livraison.getClient().toLowerCase().contains(critereLower);
                    }
                    
                    
                    boolean matchStatut = true;
                    if (statutSelectionne != null && !statutSelectionne.equals("Tous les statuts")) {
                        matchStatut = livraison.getStatut().equals(statutSelectionne);
                    }
                    
                   
                    boolean matchType = true;
                    if (typeSelectionne != null && !typeSelectionne.equals("Tous les types")) {
                        matchType = livraison.getType().equals(typeSelectionne);
                    }
                    
                    return matchRecherche && matchStatut && matchType;
                })
                .collect(Collectors.toList());
        
       
        afficherLivraisons();
    }

   
    private void configurerBoutonNouvelleLivraison() {
        btnNouvelleLivraison.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterLivraison.fxml"));
                Parent root = loader.load();

                AjouterLivraisonController controller = loader.getController();

                Stage dialogStage = new Stage();
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setResizable(false);
                dialogStage.setTitle("Ajouter une livraison");
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles/GestionLivraisons.css").toExternalForm());
                dialogStage.setScene(scene);
                dialogStage.showAndWait();

                if (controller.isConfirme()) {
                    Livraison nouvelle = controller.getNouvelleLivraison();

                    
                    int codeClt = nouvelle.getCodeClt();
                    LocalDate dateLiv = nouvelle.getDate();
                    String priorite = nouvelle.isUrgent() ? "urgent" : "normal";
                    String statut = convertirStatutVersBDD(nouvelle.getStatut());
                    String type_liv = convertirTypeVersBDD(nouvelle.getType());
                    float taxe = nouvelle.getTaxe();
                    float cout = nouvelle.getCout();

                  
                    int medicamentId = nouvelle.getMedicamentId();
                    int quantite = nouvelle.getQuantite();

                    if (!validateLivraison(codeClt, dateLiv, statut, type_liv)) return;

                    
                    if (quantite <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être positive.");
                        return;
                    }

                    Connection conn = null;
                    try {
                        conn = DatabaseConnection.getConnection();
                        conn.setAutoCommit(false); 

                       
                        String checkStock = "SELECT nbrBoite FROM medicament WHERE idMed = ?";
                        int stockDisponible = 0;
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkStock)) {
                            checkStmt.setInt(1, medicamentId);
                            try (ResultSet rs = checkStmt.executeQuery()) {
                                if (rs.next()) {
                                    stockDisponible = rs.getInt("nbrBoite");
                                    if (quantite > stockDisponible) {
                                        showAlert(Alert.AlertType.ERROR, "Stock insuffisant",
                                                "Quantité demandée: " + quantite +
                                                        "\nStock disponible: " + stockDisponible +
                                                        "\nVeuillez réduire la quantité.");
                                        conn.rollback();
                                        return;
                                    }
                                } else {
                                    showAlert(Alert.AlertType.ERROR, "Erreur", "Médicament non trouvé.");
                                    conn.rollback();
                                    return;
                                }
                            }
                        }

                       
                        String insertLivraison = "INSERT INTO livraison (codeClt, dateLiv, priorite, statut, type_liv, taxe, cout) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        int livraisonId = -1;
                        try (PreparedStatement stmt = conn.prepareStatement(insertLivraison, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            stmt.setInt(1, codeClt);
                            stmt.setDate(2, java.sql.Date.valueOf(dateLiv));
                            stmt.setString(3, priorite);
                            stmt.setString(4, statut);
                            stmt.setString(5, type_liv);
                            stmt.setFloat(6, taxe);
                            stmt.setFloat(7, cout);

                            int affected = stmt.executeUpdate();
                            if (affected > 0) {
                                try (ResultSet keys = stmt.getGeneratedKeys()) {
                                    if (keys.next()) {
                                        livraisonId = keys.getInt(1);
                                    }
                                }
                            }
                        }

                        if (livraisonId == -1) {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter la livraison.");
                            conn.rollback();
                            return;
                        }

                        
                        String insertInclure = "INSERT INTO inclure (numLiv, idMed, quantite) VALUES (?, ?, ?)";
                        try (PreparedStatement stmtInclure = conn.prepareStatement(insertInclure)) {
                            stmtInclure.setInt(1, livraisonId);
                            stmtInclure.setInt(2, medicamentId);
                            stmtInclure.setInt(3, quantite);
                            stmtInclure.executeUpdate();
                        }

                       
                        String updateStock = "UPDATE medicament SET nbrBoite = nbrBoite - ? WHERE idMed = ?";
                        try (PreparedStatement stmtStock = conn.prepareStatement(updateStock)) {
                            stmtStock.setInt(1, quantite);
                            stmtStock.setInt(2, medicamentId);
                            stmtStock.executeUpdate();
                        }

                       
                        conn.commit();
                        chargerLivraisons();
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Livraison ajoutée avec succès !");

                    } catch (SQLException e) {
                       
                        if (conn != null) {
                            try {
                                conn.rollback();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                        showAlert(Alert.AlertType.ERROR, "Erreur DB", "Erreur lors de l'ajout : " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        if (conn != null) {
                            try {
                                conn.setAutoCommit(true);
                                conn.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    
    private boolean validateLivraison(int codeClt, LocalDate dateLiv, String statut, String type_liv) {
        if (codeClt <= 0) {
            showAlert(Alert.AlertType.ERROR, "Client invalide", "Veuillez sélectionner un client valide.");
            return false;
        }

        if (dateLiv == null) {
            showAlert(Alert.AlertType.ERROR, "Date invalide", "Veuillez entrer une date de livraison valide.");
            return false;
        }

        if (statut == null || statut.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Statut invalide", "Veuillez sélectionner un statut.");
            return false;
        }

        if (type_liv == null || type_liv.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Type invalide", "Veuillez sélectionner un type de livraison.");
            return false;
        }

        return true;
    }

   
    private void afficherLivraisons() {
       
        livraisonsGrid.getChildren().clear();

        
        int colonnes = 3;

       
        for (int i = 0; i < livraisonsFiltres.size(); i++) {
            Livraison livraison = livraisonsFiltres.get(i);

            
            int colonne = i % colonnes;
            int ligne = i / colonnes;

            
            VBox carteLivraison = creerCarteLivraison(livraison);
            livraisonsGrid.add(carteLivraison, colonne, ligne);
        }
    }

    
    private VBox creerCarteLivraison(Livraison livraison) {
       
        VBox carte = new VBox(15);
        carte.getStyleClass().add("livraison-card");
        carte.setPadding(new Insets(25));
        carte.setPrefWidth(320);
        carte.setPrefHeight(280);
        
        
        HBox enTete = new HBox();
        enTete.setAlignment(Pos.CENTER_LEFT);
        enTete.setSpacing(10);
        HBox.setHgrow(enTete, Priority.ALWAYS);
        
       
        Label numeroLabel = new Label("L" + String.format("%04d", livraison.getNumLiv()));
        numeroLabel.getStyleClass().add("livraison-numero");
        HBox.setHgrow(numeroLabel, Priority.ALWAYS);
        
        
        Button btnQR = new Button();
        btnQR.getStyleClass().add("btn-icon");
        ImageView iconQR = new ImageView(new Image("/assets/qr.png"));
        iconQR.setFitWidth(18);
        iconQR.setFitHeight(18);
        iconQR.setPreserveRatio(true);
        btnQR.setGraphic(iconQR);
        btnQR.setOnAction(e -> voirQRCode(livraison));
        
        
        Button btnModifier = new Button();
        btnModifier.getStyleClass().add("btn-icon");
        ImageView iconModifier = new ImageView(new Image("/assets/bouton-modifier.png"));
        iconModifier.setFitWidth(18);
        iconModifier.setFitHeight(18);
        iconModifier.setPreserveRatio(true);
        btnModifier.setGraphic(iconModifier);
        btnModifier.setOnAction(e -> modifierLivraison(livraison));
        
       
        Button btnSupprimer = new Button();
        btnSupprimer.getStyleClass().add("btn-icon");
        ImageView iconSupprimer = new ImageView(new Image("/assets/supprimer.png"));
        iconSupprimer.setFitWidth(18);
        iconSupprimer.setFitHeight(18);
        iconSupprimer.setPreserveRatio(true);
        btnSupprimer.setGraphic(iconSupprimer);
        btnSupprimer.setOnAction(e -> supprimerLivraison(livraison));
        
        enTete.getChildren().addAll(numeroLabel, btnQR, btnModifier, btnSupprimer);
        
       
        Label clientLabel = new Label(livraison.getClient());
        clientLabel.getStyleClass().add("livraison-client");
        
       
        VBox infosLivraison = new VBox(10);
        
        
        HBox dateBox = new HBox(8);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconCalendrier = new ImageView(new Image("/assets/calendrier.png"));
        iconCalendrier.setFitWidth(16);
        iconCalendrier.setFitHeight(16);
        iconCalendrier.setPreserveRatio(true);
        iconCalendrier.setStyle("-fx-opacity: 0.6;");
        Label dateLabel = new Label(livraison.getDateFormatee());
        dateLabel.getStyleClass().add("livraison-info");
        dateBox.getChildren().addAll(iconCalendrier, dateLabel);
        
        
        HBox medicamentsBox = new HBox(8);
        medicamentsBox.setAlignment(Pos.CENTER_LEFT);
        ImageView iconPilule = new ImageView(new Image("/assets/pilule.png"));
        iconPilule.setFitWidth(16);
        iconPilule.setFitHeight(16);
        iconPilule.setPreserveRatio(true);
        iconPilule.setStyle("-fx-opacity: 0.6;");
        Label medicamentsLabel = new Label(livraison.getNombreMedicaments() + " médicament(s)");

        medicamentsLabel.getStyleClass().add("livraison-info");
        medicamentsBox.getChildren().addAll(iconPilule, medicamentsLabel);
        
       
        Label taxeLabel = new Label("Taxe : " + livraison.getTaxe() + " DA");
        taxeLabel.getStyleClass().add("livraison-taxe");

        infosLivraison.getChildren().addAll(dateBox, medicamentsBox, taxeLabel);

       
        Label coutLabel = new Label("Coût total : " + livraison.getCout() + " DA");
        coutLabel.getStyleClass().add("livraison-taxe");
        infosLivraison.getChildren().add(coutLabel);


       
        HBox tagsBox = new HBox(8);
        tagsBox.setAlignment(Pos.CENTER_LEFT);
        tagsBox.setSpacing(8);

        Label tagStatut = new Label(livraison.getStatut());
        tagStatut.getStyleClass().add("tag");
        tagStatut.getStyleClass().add("tag-statut-" + normaliserClasseCSS(livraison.getStatut()));
        tagsBox.getChildren().add(tagStatut);
        
       
        Label tagType = new Label(livraison.getType());
        tagType.getStyleClass().add("tag");
        tagType.getStyleClass().add("tag-type");
        tagsBox.getChildren().add(tagType);
        
       
        if (livraison.isUrgent()) {
            HBox tagUrgentBox = new HBox(4);
            tagUrgentBox.setAlignment(Pos.CENTER);
            tagUrgentBox.getStyleClass().add("tag");
            tagUrgentBox.getStyleClass().add("tag-urgent");
            
           
            Label triangle = new Label("▲");
            triangle.setStyle("-fx-text-fill: #E53935; -fx-font-size: 10px;");
            Label urgentLabel = new Label("urgent");
            urgentLabel.setStyle("-fx-text-fill: #E53935;");
            
            tagUrgentBox.getChildren().addAll(triangle, urgentLabel);
            tagsBox.getChildren().add(tagUrgentBox);
        }
        
       
        carte.getChildren().addAll(enTete, clientLabel, infosLivraison, tagsBox);
        
        return carte;
    }

   
    private void voirQRCode(Livraison livraison) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AfficherQRCode.fxml"));
            Parent root = loader.load();

            AfficherQRCodeController controller = loader.getController();
            controller.setLivraison(livraison);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("QR Code - L" + String.format("%04d", livraison.getNumLiv()));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionLivraisons.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    private void modifierLivraison(Livraison livraison) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterLivraison.fxml"));
            Parent root = loader.load();

            AjouterLivraisonController controller = loader.getController();
            controller.preparerModification(livraison);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Modifier une livraison");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionLivraisons.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirme()) {
                Livraison modif = controller.getNouvelleLivraison();

               
                int codeClt = modif.getCodeClt();
                LocalDate dateLiv = modif.getDate();
                String priorite = modif.isUrgent() ? "urgent" : "normal";
                String statut = convertirStatutVersBDD(modif.getStatut());
                String type_liv = convertirTypeVersBDD(modif.getType());
                float taxe = modif.getTaxe();
                float cout = modif.getCout();

               
                int medicamentId = modif.getMedicamentId();
                int nouvelleQuantite = modif.getQuantite();
                int ancienneQuantite = livraison.getQuantite();
                int ancienMedicamentId = livraison.getMedicamentId();

                if (!validateLivraison(codeClt, dateLiv, statut, type_liv)) return;

                
                if (nouvelleQuantite <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "La quantité doit être positive.");
                    return;
                }

                Connection conn = null;
                try {
                    conn = DatabaseConnection.getConnection();
                    conn.setAutoCommit(false); 

                   
                    if (medicamentId != ancienMedicamentId) {
                        String checkNewStock = "SELECT nbrBoite FROM medicament WHERE idMed = ?";
                        try (PreparedStatement checkStmt = conn.prepareStatement(checkNewStock)) {
                            checkStmt.setInt(1, medicamentId);
                            try (ResultSet rs = checkStmt.executeQuery()) {
                                if (rs.next()) {
                                    int stockDisponible = rs.getInt("nbrBoite");
                                    if (nouvelleQuantite > stockDisponible) {
                                        showAlert(Alert.AlertType.ERROR, "Stock insuffisant",
                                                "Quantité demandée: " + nouvelleQuantite +
                                                        "\nStock disponible: " + stockDisponible);
                                        conn.rollback();
                                        return;
                                    }
                                } else {
                                    showAlert(Alert.AlertType.ERROR, "Erreur", "Nouveau médicament non trouvé.");
                                    conn.rollback();
                                    return;
                                }
                            }
                        }

                       
                        if (ancienMedicamentId > 0) {
                            String refundOldStock = "UPDATE medicament SET nbrBoite = nbrBoite + ? WHERE idMed = ?";
                            try (PreparedStatement refundStmt = conn.prepareStatement(refundOldStock)) {
                                refundStmt.setInt(1, ancienneQuantite);
                                refundStmt.setInt(2, ancienMedicamentId);
                                refundStmt.executeUpdate();
                            }
                        }

                       
                        String deductNewStock = "UPDATE medicament SET nbrBoite = nbrBoite - ? WHERE idMed = ?";
                        try (PreparedStatement deductStmt = conn.prepareStatement(deductNewStock)) {
                            deductStmt.setInt(1, nouvelleQuantite);
                            deductStmt.setInt(2, medicamentId);
                            deductStmt.executeUpdate();
                        }

                       
                        String updateInclure = "UPDATE inclure SET idMed = ?, quantite = ? WHERE numLiv = ?";
                        try (PreparedStatement stmtInclure = conn.prepareStatement(updateInclure)) {
                            stmtInclure.setInt(1, medicamentId);
                            stmtInclure.setInt(2, nouvelleQuantite);
                            stmtInclure.setInt(3, livraison.getNumLiv());
                            stmtInclure.executeUpdate();
                        }

                    } else {
                        
                        int difference = nouvelleQuantite - ancienneQuantite;

                        if (difference > 0) {
                            
                            String checkStock = "SELECT nbrBoite FROM medicament WHERE idMed = ?";
                            try (PreparedStatement checkStmt = conn.prepareStatement(checkStock)) {
                                checkStmt.setInt(1, medicamentId);
                                try (ResultSet rs = checkStmt.executeQuery()) {
                                    if (rs.next()) {
                                        int stockDisponible = rs.getInt("nbrBoite");
                                        if (difference > stockDisponible) {
                                            showAlert(Alert.AlertType.ERROR, "Stock insuffisant",
                                                    "Augmentation demandée: " + difference +
                                                            "\nStock disponible: " + stockDisponible);
                                            conn.rollback();
                                            return;
                                        }
                                    }
                                }
                            }
                        }

                       
                        String updateStock = "UPDATE medicament SET nbrBoite = nbrBoite - ? WHERE idMed = ?";
                        try (PreparedStatement stmtStock = conn.prepareStatement(updateStock)) {
                            stmtStock.setInt(1, difference);
                            stmtStock.setInt(2, medicamentId);
                            stmtStock.executeUpdate();
                        }

                       
                        String updateInclure = "UPDATE inclure SET quantite = ? WHERE numLiv = ? AND idMed = ?";
                        try (PreparedStatement stmtInclure = conn.prepareStatement(updateInclure)) {
                            stmtInclure.setInt(1, nouvelleQuantite);
                            stmtInclure.setInt(2, livraison.getNumLiv());
                            stmtInclure.setInt(3, medicamentId);
                            stmtInclure.executeUpdate();
                        }
                    }

                   
                    String updateLivraison = "UPDATE livraison SET codeClt=?, dateLiv=?, priorite=?, statut=?, type_liv=?, taxe=?, cout=? WHERE numLiv=?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateLivraison)) {
                        stmt.setInt(1, codeClt);
                        stmt.setDate(2, java.sql.Date.valueOf(dateLiv));
                        stmt.setString(3, priorite);
                        stmt.setString(4, statut);
                        stmt.setString(5, type_liv);
                        stmt.setFloat(6, taxe);
                        stmt.setFloat(7, cout);
                        stmt.setInt(8, livraison.getNumLiv());
                        stmt.executeUpdate();
                    }

                    conn.commit(); 
                    chargerLivraisons(); 
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Livraison modifiée avec succès !");

                } catch (SQLException e) {
                    if (conn != null) {
                        try {
                            conn.rollback();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    showAlert(Alert.AlertType.ERROR, "Erreur DB", "Erreur lors de la modification : " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        try {
                            conn.setAutoCommit(true);
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private void supprimerLivraison(GestionLivraisonsController.Livraison livraison) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/ConfirmerSuppression.fxml"));
            Parent root = loader.load();

            ConfirmerSuppressionController controller = loader.getController();
            controller.setLivraison(livraison);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setTitle("Confirmation de suppression");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionLivraisons.css").toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (controller.isConfirmation()) {
                String deleteInclure = "DELETE FROM inclure WHERE numLiv=?";
                String deleteLivraison = "DELETE FROM livraison WHERE numLiv=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt1 = conn.prepareStatement(deleteInclure);
                     PreparedStatement stmt2 = conn.prepareStatement(deleteLivraison)) {
                    stmt1.setInt(1, livraison.getNumLiv());
                    stmt1.executeUpdate();
                    stmt2.setInt(1, livraison.getNumLiv());
                    stmt2.executeUpdate();
                    chargerLivraisons(); 
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Livraison supprimée avec succès !");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

   
    public static class Livraison {
        private int numLiv;
        private int codeClt;
        private String client;
        private LocalDate date;
        private int nombreMedicaments;
        private int taxe;
        private float cout;
        private String statut;
        private String type;
        private boolean urgent;
        private String medicament;
        private int medicamentId;
        private int quantite;

        public Livraison(int numLiv, int codeClt, String client, LocalDate date, int nombreMedicaments,
                         int taxe, float cout, String statut, String type, boolean urgent,
                         String medicament,int medicamentId, int quantite) {
            this.numLiv = numLiv;
            this.codeClt = codeClt;
            this.client = client;
            this.date = date;
            this.nombreMedicaments = nombreMedicaments;
            this.taxe = taxe;
            this.cout = cout;
            this.statut = statut;
            this.type = type;
            this.urgent = urgent;
            this.medicament = medicament;
            this.medicamentId = medicamentId;
            this.quantite = quantite;
        }

       
        public int getNumLiv() { return numLiv; }
        public int getCodeClt() { return codeClt; }
        public String getClient() { return client; }
        public LocalDate getDate() { return date; }
        public int getNombreMedicaments() { return nombreMedicaments; }
        public int getTaxe() { return taxe; }
        public float getCout() { return cout; }
        public String getStatut() { return statut; }
        public String getType() { return type; }
        public boolean isUrgent() { return urgent; }
        public String getMedicament() { return medicament; }
        public int getMedicamentId() { return medicamentId; }
        public void setMedicamentId(int medicamentId) { this.medicamentId = medicamentId; }

        
        public String getDateFormatee() {
            if (date == null) return "";
            return String.format("%02d/%02d/%04d", 
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear());
        }

       
        public void setNumLiv(int numLiv) { this.numLiv = numLiv; }
        public void setCodeClt(int codeClt) { this.codeClt = codeClt; }
        public void setClient(String client) { this.client = client; }
        public void setDate(LocalDate date) { this.date = date; }
        public void setNombreMedicaments(int nombreMedicaments) { this.nombreMedicaments = nombreMedicaments; }
        public void setTaxe(int taxe) { this.taxe = taxe; }
        public void setCout (float cout) { this.cout = cout ;}
        public void setStatut(String statut) { this.statut = statut; }
        public void setType(String type) { this.type = type; }
        public void setUrgent(boolean urgent) { this.urgent = urgent; }
        public void setMedicament(String medicament) { this.medicament = medicament; }
        public int getQuantite() { return quantite; }
        public void setQuantite(int quantite) { this.quantite = quantite; }
    }


    private String normaliserClasseCSS(String texte) {
        if (texte == null) return "";
        
        String sansAccent = Normalizer.normalize(texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sansAccent.toLowerCase().replace(" ", "-");
    }

}

