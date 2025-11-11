// ce fichier est responsable des actions / animations

package com.example.controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AccueilController implements Initializable {

    @FXML
    private Button boutonCommencer;

    @FXML
    private GridPane gridPane;

    @FXML // pour le défilement vertical
    private ScrollPane scrollPane;

    @FXML
    private Hyperlink linkFonctionnalites, linkApropos, linkAide;

    @FXML
    private VBox sectionFonctionnalites, sectionApropos, sectionAide;

    private boolean animationPlayed = false;

    // action sur le bouton commencer
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (boutonCommencer != null) {
            boutonCommencer.setOnAction(event -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/Authentification.fxml"));
                    Parent root = loader.load();

                    // On récupère le stage courant
                    Stage stage = (Stage) boutonCommencer.getScene().getWindow();

                    // Nouvelle scène avec CSS
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/styles/Authentification.css").toExternalForm());

                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        linkFonctionnalites.setOnAction(e -> scrollTo(sectionFonctionnalites));
        linkApropos.setOnAction(e -> scrollTo(sectionApropos));
        linkAide.setOnAction(e -> scrollTo(sectionAide));

        if (gridPane != null) {
            for (Node node : gridPane.getChildren()) {
                node.setOpacity(0);
                node.setTranslateY(40);
                node.setScaleX(0.97);
                node.setScaleY(0.97);
            }

            gridPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    Platform.runLater(this::playStartupAnimations);
                }
            });
        }
    }

    //scroll lorsque je clique sur l'un des liens de la barre de navigation
    private void scrollTo(Node section) {
        if (section == null || scrollPane == null || scrollPane.getContent() == null) return;

        Platform.runLater(() -> {
            double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
            double viewportHeight = scrollPane.getViewportBounds().getHeight();

            if (contentHeight <= viewportHeight) {
                scrollPane.setVvalue(0);
                return;
            }

            double sectionY = section.getBoundsInParent().getMinY();

            double v = sectionY / (contentHeight - viewportHeight);

            if (v < 0) v = 0;
            if (v > 1) v = 1;

            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(scrollPane.vvalueProperty(), v, Interpolator.EASE_BOTH);
            KeyFrame kf = new KeyFrame(Duration.millis(600), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
        });
    }

    private void playStartupAnimations() {
        if (animationPlayed) return;
        animationPlayed = true;

        int delay = 0;
        for (Node node : gridPane.getChildren()) {
            animateCard(node, delay);
            delay += 150;
        }
    }

    // Pour les animations des cartes
    private void animateCard(Node node, int delay) {
        FadeTransition fade = new FadeTransition(Duration.millis(600), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delay));

        TranslateTransition translate = new TranslateTransition(Duration.millis(600), node);
        translate.setFromY(30);
        translate.setToY(0);
        translate.setDelay(Duration.millis(delay));

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), node);
        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), node);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        SequentialTransition bounce = new SequentialTransition(scaleUp, scaleDown);
        ParallelTransition appear = new ParallelTransition(fade, translate);
        SequentialTransition fullAnimation = new SequentialTransition(appear, bounce);

        fullAnimation.setDelay(Duration.millis(delay));
        fullAnimation.setInterpolator(Interpolator.EASE_OUT);
        fullAnimation.play();
    }
}