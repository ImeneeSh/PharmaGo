package com.example.controllers;

import javafx.application.Platform;
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
    private static final String BASE_STYLE = "-fx-focus-color: transparent; " +
            "-fx-faint-focus-color: transparent; " +
            "-fx-background-insets: 0; " +
            "-fx-padding: 8 15;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Groupe de boutons
        btnDashboard.setToggleGroup(menuGroup);
        btnConsultation.setToggleGroup(menuGroup);
        btnClients.setToggleGroup(menuGroup);
        btnMedicaments.setToggleGroup(menuGroup);
        btnLivraisons.setToggleGroup(menuGroup);

        // Désactiver les effets visuels par défaut pour tous les boutons
        disableDefaultButtonEffects(btnDashboard);
        disableDefaultButtonEffects(btnConsultation);
        disableDefaultButtonEffects(btnClients);
        disableDefaultButtonEffects(btnMedicaments);
        disableDefaultButtonEffects(btnLivraisons);

        // Initialisation
        btnDashboard.setSelected(true);
        updateIcons();

        // Ajout d'un listener pour mettre à jour le style et les icônes
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

    private void disableDefaultButtonEffects(ToggleButton button) {
        // Désactiver le focus traversable pour éviter les effets de focus
        button.setFocusTraversable(false);

        // Forcer le style à rester constant lors des interactions
        button.pressedProperty().addListener((obs, wasPressed, isPressed) -> {
            Platform.runLater(() -> {
                applyCorrectStyle(button);
            });
        });

        // S'assurer que le style reste constant même après le clic
        button.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            Platform.runLater(() -> {
                applyCorrectStyle(button);
            });
        });

        // Forcer le style lors de la sélection
        button.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            Platform.runLater(() -> {
                applyCorrectStyle(button);
            });
        });

        // Appliquer le style initial
        applyCorrectStyle(button);
    }

    private void applyCorrectStyle(ToggleButton button) {
        if (button.isSelected()) {
            // Style pour le bouton actif : barre verte à gauche, fond transparent
            button.getStyleClass().removeAll("menu-item");
            if (!button.getStyleClass().contains("menu-item-active")) {
                button.getStyleClass().add("menu-item-active");
            }
            // Forcer le style CSS pour éviter les effets par défaut
            // Les styles inline ont la priorité sur les styles par défaut de JavaFX
            button.setStyle(BASE_STYLE + " -fx-background-color: transparent; " +
                    "-fx-border-width: 0 0 0 5; " +
                    "-fx-border-color: #00551D; " +
                    "-fx-border-radius: 0;");
        } else {
            // Style pour le bouton inactif : fond transparent
            button.getStyleClass().removeAll("menu-item-active");
            if (!button.getStyleClass().contains("menu-item")) {
                button.getStyleClass().add("menu-item");
            }
            // Forcer le style CSS pour éviter les effets par défaut
            button.setStyle(BASE_STYLE + " -fx-background-color: transparent; " +
                    "-fx-border-width: 0;");
        }
    }

    private void updateActiveButton() {
        // Appliquer les styles corrects pour tous les boutons
        // Les listeners sur selectedProperty() s'occuperont aussi de cela,
        // mais on s'assure ici que les styles sont bien appliqués
        Platform.runLater(() -> {
            applyCorrectStyle(btnDashboard);
            applyCorrectStyle(btnConsultation);
            applyCorrectStyle(btnClients);
            applyCorrectStyle(btnMedicaments);
            applyCorrectStyle(btnLivraisons);
        });
    }

    private void updateIcons() {
        // L'icône du Tableau de bord reste toujours la même (forme active)
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