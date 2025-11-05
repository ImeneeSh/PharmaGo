package com.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML private ToggleButton btnDashboard;
    @FXML private ToggleButton btnConsultation;
    @FXML private ToggleButton btnClients;
    @FXML private ToggleButton btnMedicaments;
    @FXML private ToggleButton btnLivraisons;

    @FXML private ImageView iconDashboard;
    @FXML private ImageView iconConsultation;
    @FXML private ImageView iconClients;
    @FXML private ImageView iconMedicaments;
    @FXML private ImageView iconLivraisons;

    private final ToggleGroup menuGroup = new ToggleGroup();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Groupe de boutons
        btnDashboard.setToggleGroup(menuGroup);
        btnConsultation.setToggleGroup(menuGroup);
        btnClients.setToggleGroup(menuGroup);
        btnMedicaments.setToggleGroup(menuGroup);
        btnLivraisons.setToggleGroup(menuGroup);

        // Initialisation
        btnDashboard.setSelected(true);
        updateIcons();

        // Ajout d’un listener pour mettre à jour le style et les icônes
        menuGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            updateActiveButton();
            updateIcons();
        });

        // Navigation temporaire (à personnaliser)
        btnConsultation.setOnAction(e -> System.out.println("Aller vers Consultation globale"));
        btnClients.setOnAction(e -> System.out.println("Aller vers Gestion des clients"));
        btnMedicaments.setOnAction(e -> System.out.println("Aller vers Gestion des médicaments"));
        btnLivraisons.setOnAction(e -> System.out.println("Aller vers Gestion des livraisons"));
    }

    private void updateActiveButton() {
        btnDashboard.getStyleClass().removeAll("menu-item-active");
        btnConsultation.getStyleClass().removeAll("menu-item-active");
        btnClients.getStyleClass().removeAll("menu-item-active");
        btnMedicaments.getStyleClass().removeAll("menu-item-active");
        btnLivraisons.getStyleClass().removeAll("menu-item-active");

        ToggleButton selected = (ToggleButton) menuGroup.getSelectedToggle();
        if (selected != null) {
            selected.getStyleClass().add("menu-item-active");
        }
    }

    private void updateIcons() {
        setIcon(iconDashboard, "/assets/tableau-de-bord" + (btnDashboard.isSelected() ? "" : " (1)") + ".png");
        setIcon(iconConsultation, "/assets/graphique-a-barres" + (btnConsultation.isSelected() ? "" : " (1)") + ".png");
        setIcon(iconClients, btnClients.isSelected()
                ? "/assets/silhouette-dutilisateurs-multiples.png"
                : "/assets/liste-des-utilisateurs.png");
        setIcon(iconMedicaments, "/assets/pilule" + (btnMedicaments.isSelected() ? "" : " (1)") + ".png");
        setIcon(iconLivraisons, btnLivraisons.isSelected()
                ? "/assets/livraison (3).png"
                : "/assets/livraison (1).png");
    }

    private void setIcon(ImageView view, String path) {
        URL resource = getClass().getResource(path);
        if (resource == null) {
            System.err.println("Image introuvable : " + path);
            return;
        }
        view.setImage(new Image(resource.toExternalForm()));
    }



}