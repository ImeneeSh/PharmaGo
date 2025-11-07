package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
 * ============================================================
 * Contrôleur de la page ConsultationGlobale.fxml
 * Gère la logique liée aux statistiques et à l’interaction
 * des barres de progression "Livraisons par type".
 * ============================================================
 */
public class ConsultationGlobaleController {

    /* ----------------------------------------------------------------
       === Références FXML (liées via fx:id dans le fichier FXML) ===
       ---------------------------------------------------------------- */
    @FXML private ProgressBar coldChainBar;  // Barre : chaîne du froid
    @FXML private ProgressBar frozenBar;     // Barre : sous congélation
    @FXML private ProgressBar dangerBar;     // Barre : dangereuses / cytotoxiques

    @FXML private Label coldChainLabel;  // Texte affichant "x livraison(s)" pour chaîne du froid
    @FXML private Label frozenLabel;     // Texte affichant "x livraison(s)" pour congélation
    @FXML private Label dangerLabel;     // Texte affichant "x livraison(s)" pour dangereuses


    /* ----------------------------------------------------------------
       === Variables internes : valeurs numériques de chaque type ===
       ---------------------------------------------------------------- */
    private int coldChainCount = 5;   // Valeur initiale (affichée au lancement)
    private int frozenCount = 2;
    private int dangerCount = 3;

    // Chaque progression correspond à une échelle de 0.0 → 1.0
    // (tu peux ajuster le pas de progression ici)
    private final double STEP = 0.1;


    /* ----------------------------------------------------------------
       === MÉTHODES D’INCRÉMENTATION / DÉCRÉMENTATION ===
       Appelées depuis les boutons "+" et "−" du FXML
       ---------------------------------------------------------------- */

    // === Chaîne du froid ===
    @FXML
    private void increaseColdChain() {
        coldChainCount++;
        updateProgress(coldChainBar, coldChainLabel, coldChainCount);
    }

    @FXML
    private void decreaseColdChain() {
        if (coldChainCount > 0) {
            coldChainCount--;
            updateProgress(coldChainBar, coldChainLabel, coldChainCount);
        }
    }

    // === Sous congélation ===
    @FXML
    private void increaseFrozen() {
        frozenCount++;
        updateProgress(frozenBar, frozenLabel, frozenCount);
    }

    @FXML
    private void decreaseFrozen() {
        if (frozenCount > 0) {
            frozenCount--;
            updateProgress(frozenBar, frozenLabel, frozenCount);
        }
    }

    // === Dangereuses / cytotoxiques ===
    @FXML
    private void increaseDanger() {
        dangerCount++;
        updateProgress(dangerBar, dangerLabel, dangerCount);
    }

    @FXML
    private void decreaseDanger() {
        if (dangerCount > 0) {
            dangerCount--;
            updateProgress(dangerBar, dangerLabel, dangerCount);
        }
    }


    /* ----------------------------------------------------------------
       === MÉTHODE GÉNÉRIQUE DE MISE À JOUR DE LA BARRE ET DU TEXTE ===
       ---------------------------------------------------------------- */
    private void updateProgress(ProgressBar bar, Label label, int count) {
        // Convertir le nombre de livraisons en une progression (max arbitraire à 10)
        double progress = Math.min(1.0, count / 10.0);

        // Mise à jour visuelle
        bar.setProgress(progress);

        // Mise à jour du texte affiché
        label.setText(count + (count > 1 ? " livraisons" : " livraison"));
    }

    /* ----------------------------------------------------------------
       === INITIALISATION (optionnelle)
       Appelée automatiquement au chargement du FXML
       ---------------------------------------------------------------- */
    @FXML
    public void initialize() {
        // On initialise l'affichage des valeurs au démarrage
        updateProgress(coldChainBar, coldChainLabel, coldChainCount);
        updateProgress(frozenBar, frozenLabel, frozenCount);
        updateProgress(dangerBar, dangerLabel, dangerCount);
    }
}
