package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/views/Accueil.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 1024, 768);


            URL cssURL = getClass().getResource("/styles/styles.css");
            if (cssURL != null) {
                System.out.println("CSS trouvé : " + cssURL);
                scene.getStylesheets().add(cssURL.toExternalForm());
            } else {
                System.err.println("Fichier CSS introuvable !");
            }

            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            primaryStage.setTitle("PharmaGo");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.initStyle(javafx.stage.StageStyle.DECORATED);
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(720);
            primaryStage.centerOnScreen();

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
