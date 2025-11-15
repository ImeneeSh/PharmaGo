package com.example.controllers;

import com.example.bdd.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConsultationGlobaleController {

    @FXML private ProgressBar coldChainBar;
    @FXML private ProgressBar frozenBar;
    @FXML private ProgressBar dangerBar;

    @FXML private Label coldChainLabel;
    @FXML private Label frozenLabel;
    @FXML private Label dangerLabel;

    @FXML private Label lblTotalClients;
    @FXML private Label lblNouveauxClients;

    @FXML private Label lblTotalMedicaments;
    @FXML private Label lblTotalLivraisons;

    @FXML private VBox clientsContainer;
    @FXML private VBox medicationsContainer;

    @FXML
    public void initialize() {
        chargerLivraisonsParType();
        chargerStatsClients();
        chargerStatsMedicaments();
        chargerStatsLivraisons();
        chargerClientsActifs();
        chargerMedicamentsPlusLivres();
    }

    // ---------------------- BARRES DE PROGRESSION ----------------------
    private void chargerLivraisonsParType() {
        String query = "SELECT type_liv, COUNT(*) AS total FROM livraison GROUP BY type_liv";
        int cold = 0, frozen = 0, danger = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String type = rs.getString("type_liv");
                int total = rs.getInt("total");

                switch (type) {
                    case "sous_chaine_du_froid" -> cold = total;
                    case "sous_congélation"  -> frozen = total;
                    case "dangereuse"        -> danger = total;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        updateProgressDynamic(coldChainBar, coldChainLabel, cold);
        updateProgressDynamic(frozenBar, frozenLabel, frozen);
        updateProgressDynamic(dangerBar, dangerLabel, danger);
    }

    private void updateProgressDynamic(ProgressBar bar, Label label, int count) {
        double progress = Math.min(1.0, count / 10.0);
        bar.setProgress(progress);

        label.setText(count + " livraison" + (count > 1 ? "s" : ""));

        if (count == 0) {
            bar.setStyle("-fx-accent: #B0BEC5;");
        } else if (count <= 3) {
            bar.setStyle("-fx-accent: #4CAF50;");
        } else if (count <= 6) {
            bar.setStyle("-fx-accent: #FFC107;");
        } else {
            bar.setStyle("-fx-accent: #F44336;");
        }
    }

    // ---------------------- STATISTIQUES ----------------------
    private void chargerStatsClients() {
        String queryTotal = "SELECT COUNT(*) FROM client";
        String queryNouveaux = "SELECT COUNT(*) FROM client WHERE MONTH(dateCreation) = MONTH(CURRENT_DATE())";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs1 = stmt.executeQuery(queryTotal);
            if (rs1.next()) lblTotalClients.setText(String.valueOf(rs1.getInt(1)));

            ResultSet rs2 = stmt.executeQuery(queryNouveaux);
            if (rs2.next()) lblNouveauxClients.setText("+" + rs2.getInt(1) + " ce mois-ci");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chargerStatsMedicaments() {
        String queryTotal = "SELECT COUNT(*) FROM medicament";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryTotal)) {

            if (rs.next()) lblTotalMedicaments.setText(String.valueOf(rs.getInt(1)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chargerStatsLivraisons() {
        String queryTotal = "SELECT COUNT(*) FROM livraison";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(queryTotal)) {

            if (rs.next()) lblTotalLivraisons.setText(String.valueOf(rs.getInt(1)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------- CLIENTS LES PLUS ACTIFS ----------------------
    private void chargerClientsActifs() {
        String query = """
                SELECT CONCAT('C', LPAD(C.codeClt, 3, '0')) AS codeClt,
                       C.nom, C.prenom,
                       COUNT(*) AS totalLivraisons
                FROM client C 
                JOIN livraison L ON C.codeClt = L.codeClt
                GROUP BY C.codeClt
                ORDER BY totalLivraisons DESC
                LIMIT 3
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            clientsContainer.getChildren().clear();
            int rank = 1;

            while (rs.next()) {
                String code = rs.getString("codeClt");
                String nom = rs.getString("nom") + " " + rs.getString("prenom");
                int total = rs.getInt("totalLivraisons");

                HBox ligne = new HBox(15);
                ligne.getStyleClass().add("client-item");

                // Badge du rang
                StackPane badge = new StackPane();
                badge.getStyleClass().add("rank-badge");
                Label lblRank = new Label(String.valueOf(rank));
                lblRank.getStyleClass().add("rank-number");
                badge.getChildren().add(lblRank);

                // Infos client
                VBox textBox = new VBox(3);
                Label lblCode = new Label(code);
                lblCode.getStyleClass().add("client-code");
                Label lblNom = new Label(nom);
                lblNom.getStyleClass().add("client-name");
                textBox.getChildren().addAll(lblCode, lblNom);
                HBox.setHgrow(textBox, Priority.ALWAYS);

                // Total livraisons
                Label lblTotal = new Label(total + " livraison(s)");
                lblTotal.getStyleClass().add("client-value");

                ligne.getChildren().addAll(badge, textBox, lblTotal);
                clientsContainer.getChildren().add(ligne);

                rank++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------------------- MÉDICAMENTS LES PLUS LIVRÉS ----------------------
    private void chargerMedicamentsPlusLivres() {
        String query = """
        SELECT CONCAT('M', LPAD(M.idMed, 3, '0')) AS idMed, M.nomMed, DATE_FORMAT(M.datePer, '%d/%m/%Y') AS peremption, COUNT(*) AS total
        FROM inclure I
        JOIN medicament M ON I.idMed = M.idMed
        GROUP BY M.idMed
        ORDER BY total DESC
        LIMIT 4
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            medicationsContainer.getChildren().clear();

            // Titre
            Label sectionTitle = new Label("Médicaments les plus livrés");
            sectionTitle.getStyleClass().add("section-title");
            medicationsContainer.getChildren().add(sectionTitle);

            HBox panel = new HBox(40);
            panel.getStyleClass().add("medication-panel");
            panel.setAlignment(javafx.geometry.Pos.CENTER);

            VBox colGauche = new VBox(20);
            colGauche.setAlignment(javafx.geometry.Pos.CENTER);
            VBox colDroite = new VBox(20);
            colDroite.setAlignment(javafx.geometry.Pos.CENTER);

            int index = 0;
            while (rs.next()) {
                String idMed = rs.getString("idMed");
                String nomMed = rs.getString("nomMed");
                String peremption = rs.getString("peremption");
                int rank = index + 1;

                VBox card = new VBox(5);
                card.getStyleClass().add("medication-card");

                // Badge rang
                HBox topRow = new HBox();
                topRow.setAlignment(javafx.geometry.Pos.TOP_RIGHT);
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                StackPane badge = new StackPane();
                badge.getStyleClass().add("medication-rank-badge");
                Label lblRank = new Label(String.valueOf(rank));
                lblRank.getStyleClass().add("medication-rank-number");
                badge.getChildren().add(lblRank);
                topRow.getChildren().addAll(spacer, badge);

                // Infos médicament
                Label lblId = new Label(idMed);
                lblId.getStyleClass().add("medication-code");
                Label lblNom = new Label(nomMed);
                lblNom.getStyleClass().add("medication-name");
                Label lblPeremption = new Label("Péremption: " + peremption);
                lblPeremption.getStyleClass().add("medication-expiry");

                card.getChildren().addAll(topRow, lblId, lblNom, lblPeremption);

                if (rank <= 2) {
                    colGauche.getChildren().add(card);
                } else {
                    colDroite.getChildren().add(card);
                }

                index++;
            }

            // Séparateur vertical
            Region separator = new Region();
            separator.getStyleClass().add("vertical-separator");

            panel.getChildren().addAll(colGauche, separator, colDroite);
            medicationsContainer.getChildren().add(panel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}