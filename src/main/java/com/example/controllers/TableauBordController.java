package com.example.controllers;

import com.example.bdd.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class TableauBordController implements Initializable {

    @FXML private Label lblTotalClients;
    @FXML private Label lblNouveauxClients;

    @FXML private Label lblTotalMedicaments;
    @FXML private Label lblTotalLivraisons;
    @FXML private Label lblLivraisonsAttente;

    @FXML private VBox boxLivraisonsRecentes;
    @FXML private VBox boxMedicamentsSurveiller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerStatsClients();
        chargerStatsMedicaments();
        chargerStatsLivraisons();
        chargerLivraisonsRecentes();
        chargerMedicamentsSurveiller();
    }


    private void chargerStatsClients() {
        String queryTotal = "SELECT COUNT(*) FROM client";
        String queryNouveaux = "SELECT COUNT(*) FROM client WHERE MONTH(dateCreation) = MONTH(CURRENT_DATE())";

        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();

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

        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(queryTotal);
            if (rs.next()) lblTotalMedicaments.setText(String.valueOf(rs.getInt(1)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void chargerStatsLivraisons() {
        String queryTotal = "SELECT COUNT(*) FROM livraison";
        String queryAttente = "SELECT COUNT(*) FROM livraison WHERE statut = 'en_attente'";

        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();

            ResultSet rs1 = stmt.executeQuery(queryTotal);
            if (rs1.next()) lblTotalLivraisons.setText(String.valueOf(rs1.getInt(1)));

            ResultSet rs2 = stmt.executeQuery(queryAttente);
            if (rs2.next()) lblLivraisonsAttente.setText(String.valueOf(rs2.getInt(1)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chargerLivraisonsRecentes() {
        String query = """
        SELECT CONCAT('L', LPAD(L.numLiv, 3, '0')) AS numLiv, C.nom, C.prenom, L.statut
        FROM livraison L
        JOIN client C ON L.codeClt = C.codeClt
        ORDER BY L.dateLiv DESC
        LIMIT 3
        """;


        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            boxLivraisonsRecentes.getChildren().clear();

            while (rs.next()) {
                String code = rs.getString("numLiv");
                String nom = rs.getString("nom") + " " + rs.getString("prenom");
                String statut = rs.getString("statut");

                HBox ligne = new HBox(10);
                ligne.setPadding(new Insets(10));
                ligne.getStyleClass().add("recent-item");

                VBox textBox = new VBox();
                Label lblCode = new Label(code);
                lblCode.getStyleClass().add("recent-code");
                Label lblNom = new Label(nom);
                lblNom.getStyleClass().add("recent-name");
                textBox.getChildren().addAll(lblCode, lblNom);
                HBox.setHgrow(textBox, Priority.ALWAYS);

                Label lblStatut = new Label(statut);
                lblStatut.getStyleClass().add(getStyleClassForStatut(statut));

                ligne.getChildren().addAll(textBox, lblStatut);
                boxLivraisonsRecentes.getChildren().add(ligne);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStyleClassForStatut(String statut) {
        return switch (statut) {
            case "livrée" -> "status-delivered";
            case "en_attente" -> "status-pending";
            case "annulée" -> "status-cancelled";
            case "en_cours" -> "status-inprogress";
            default -> "status-alert";
        };
    }

    private void chargerMedicamentsSurveiller() {
        String query = """
        SELECT M.nomMed, L.dateLiv
        FROM medicament M
        JOIN inclure I ON M.idMed = I.idMed
        JOIN livraison L ON L.numLiv = I.numLiv
        WHERE L.type_liv = 'dangereuse'
        ORDER BY L.dateLiv DESC
    """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            boxMedicamentsSurveiller.getChildren().clear();

            while (rs.next()) {
                String nomMed = rs.getString("nomMed");
                String date = rs.getString("dateLiv");

                HBox ligne = new HBox(10);
                ligne.setPadding(new Insets(10));
                ligne.getStyleClass().add("recent-item");

                VBox textBox = new VBox();
                Label lblMed = new Label(nomMed);
                lblMed.getStyleClass().add("recent-code");
                Label lblDate = new Label("Livraison prévu pour le : " + date);
                lblDate.getStyleClass().add("recent-name");
                textBox.getChildren().addAll(lblMed, lblDate);
                HBox.setHgrow(textBox, Priority.ALWAYS);


                Label alert = new Label("Dangereux");
                alert.getStyleClass().add("status-alert");

                ligne.getChildren().addAll(textBox, alert);
                boxMedicamentsSurveiller.getChildren().add(ligne);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
