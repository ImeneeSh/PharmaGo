package com.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    private static String currentPage = "TableauBord";


    @FXML private ToggleButton btnDashboard;
    @FXML private ToggleButton btnConsultation;
    @FXML private ToggleButton btnClients;
    @FXML private ToggleButton btnMedicaments;
    @FXML private ToggleButton btnLivraisons;
    @FXML private ToggleButton btnUtilisateurs;

    @FXML private ImageView iconDashboard;
    @FXML private ImageView iconConsultation;
    @FXML private ImageView iconClients;
    @FXML private ImageView iconMedicaments;
    @FXML private ImageView iconLivraisons;
    @FXML private ImageView iconUtilisateurs;

    private final ToggleGroup menuGroup = new ToggleGroup();
    private static final String BASE_STYLE = "-fx-focus-color: transparent; " +
            "-fx-faint-focus-color: transparent; " +
            "-fx-background-insets: 0; " +
            "-fx-padding: 8 15;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btnDashboard.setToggleGroup(menuGroup);
        btnConsultation.setToggleGroup(menuGroup);
        btnClients.setToggleGroup(menuGroup);
        btnMedicaments.setToggleGroup(menuGroup);
        btnLivraisons.setToggleGroup(menuGroup);
        btnUtilisateurs.setToggleGroup(menuGroup);

        if (!com.example.utils.Session.isAdmin()) {
            btnUtilisateurs.setVisible(false);
        } else {
            btnUtilisateurs.setVisible(true);
        }

        disableDefaultButtonEffects(btnDashboard);
        disableDefaultButtonEffects(btnConsultation);
        disableDefaultButtonEffects(btnClients);
        disableDefaultButtonEffects(btnMedicaments);
        disableDefaultButtonEffects(btnLivraisons);
        disableDefaultButtonEffects(btnUtilisateurs);

        selectButtonForCurrentPage();

        menuGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            updateActiveButton();
            updateIcons();
        });

        btnDashboard.setOnAction(e -> {
            System.out.println("Clic sur Tableau de bord");
            setCurrentPage("TableauBord");
            navigateToTableauBord();
        });

        btnConsultation.setOnAction(e -> {
            System.out.println("Clic sur Consultation Globale");
            setCurrentPage("ConsultationGlobale");
            navigateToConsultationGlobale();
        });

        btnClients.setOnAction(e -> {
            System.out.println("Clic sur Gestion des clients");
            setCurrentPage("GestionClients");
            ouvrirGestionClients();
        });

        btnMedicaments.setOnAction(e -> {
            System.out.println("Clic sur Gestion des médicaments");
            setCurrentPage("GestionMedicaments");
            ouvrirGestionMedicaments();
        });

        btnLivraisons.setOnAction(e -> {
            System.out.println("Clic sur Gestion des livraisons");
            setCurrentPage("GestionLivraisons");
            ouvrirGestionLivraisons();
        });

        btnUtilisateurs.setOnAction(e -> {
            System.out.println("Clic sur Gestion des utilisateurs");
            setCurrentPage("GestionUtilisateurs");
            ouvrirGestionUtilisateurs();
        });
    }

    private void disableDefaultButtonEffects(ToggleButton button) {
        button.setFocusTraversable(false);

        button.pressedProperty().addListener((obs, wasPressed, isPressed) -> {
            Platform.runLater(() -> {
                applyCorrectStyle(button);
            });
        });

        button.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            Platform.runLater(() -> {
                applyCorrectStyle(button);
            });
        });

        button.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            Platform.runLater(() -> {
                applyCorrectStyle(button);
            });
        });

        applyCorrectStyle(button);
    }

    private void applyCorrectStyle(ToggleButton button) {
        if (button.isSelected()) {
            button.getStyleClass().removeAll("menu-item");
            if (!button.getStyleClass().contains("menu-item-active")) {
                button.getStyleClass().add("menu-item-active");
            }
            button.setStyle(BASE_STYLE + " -fx-background-color: transparent; " +
                    "-fx-border-width: 0 0 0 5; " +
                    "-fx-border-color: #00551D; " +
                    "-fx-border-radius: 0;");
        } else {
            button.getStyleClass().removeAll("menu-item-active");
            if (!button.getStyleClass().contains("menu-item")) {
                button.getStyleClass().add("menu-item");
            }
            button.setStyle(BASE_STYLE + " -fx-background-color: transparent; " +
                    "-fx-border-width: 0;");
        }
    }

    private void updateActiveButton() {
        Platform.runLater(() -> {
            applyCorrectStyle(btnDashboard);
            applyCorrectStyle(btnConsultation);
            applyCorrectStyle(btnClients);
            applyCorrectStyle(btnMedicaments);
            applyCorrectStyle(btnLivraisons);
            applyCorrectStyle(btnUtilisateurs);
        });
    }

    private void selectButtonForCurrentPage() {
        menuGroup.selectToggle(null);
        
        switch (currentPage) {
            case "TableauBord":
                btnDashboard.setSelected(true);
                break;
            case "ConsultationGlobale":
                btnConsultation.setSelected(true);
                break;
            case "GestionClients":
                btnClients.setSelected(true);
                break;
            case "GestionMedicaments":
                btnMedicaments.setSelected(true);
                break;
            case "GestionLivraisons":
                btnLivraisons.setSelected(true);
                break;
            case "GestionUtilisateurs":
                btnUtilisateurs.setSelected(true);
                break;
            default:
                btnDashboard.setSelected(true);
                break;
        }
        
        updateActiveButton();
        updateIcons();
    }

    public static void setCurrentPage(String pageName) {
        currentPage = pageName;
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
        setIcon(iconUtilisateurs, btnUtilisateurs.isSelected()
                ? "/assets/silhouette-dutilisateurs-multiples.png"
                : "/assets/liste-des-utilisateurs.png");
    }

    private void setIcon(ImageView view, String path) {
        URL resource = getClass().getResource(path);
        if (resource == null) {
            System.err.println("Image introuvable : " + path);
            return;
        }
        view.setImage(new Image(resource.toExternalForm()));
    }

    private void navigateToTableauBord() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/views/TableauBord.fxml"));
            Parent root = fxmlLoader.load();

            Scene currentScene = null;
            Stage stage = null;

            if (btnDashboard != null) {
                currentScene = btnDashboard.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            if (currentScene == null && btnConsultation != null) {
                currentScene = btnConsultation.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            if (currentScene == null || stage == null) {
                System.err.println("ERREUR: Impossible de récupérer la scène ou le Stage !");
                return;
            }

            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            Scene scene = new Scene(root, width, height);

            URL cssMenu = getClass().getResource("/styles/menu.css");
            URL cssTableau = getClass().getResource("/styles/tableauBord.css");

            if (cssMenu != null) {
                scene.getStylesheets().add(cssMenu.toExternalForm());
            }
            if (cssTableau != null) {
                scene.getStylesheets().add(cssTableau.toExternalForm());
            }

            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            stage.setScene(scene);
            setCurrentPage("TableauBord");
            System.out.println("Page Tableau de bord chargée avec succès !");

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page Tableau de bord : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERREUR inattendue lors du chargement de la page Tableau de bord : " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private void navigateToConsultationGlobale() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/views/ConsultationGlobale.fxml"));
            Parent root = fxmlLoader.load();

            Scene currentScene = btnConsultation.getScene();
            Stage stage = (Stage) currentScene.getWindow();

            Scene scene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

            URL cssMenu = getClass().getResource("/styles/menu.css");
            URL cssConsultation = getClass().getResource("/styles/ConsultationGloable.css");

            if (cssMenu != null) {
                scene.getStylesheets().add(cssMenu.toExternalForm());
            }
            if (cssConsultation != null) {
                scene.getStylesheets().add(cssConsultation.toExternalForm());
            }

            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            stage.setScene(scene);
            setCurrentPage("ConsultationGlobale");

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page Consultation Globale : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void ouvrirGestionClients() {
        try {
            System.out.println("Début du chargement de GestionClients...");

            URL fxmlUrl = getClass().getResource("/com/example/views/GestionClients.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERREUR: Fichier FXML GestionClients.fxml introuvable !");
                System.err.println("Chemin recherché: /com/example/views/GestionClients.fxml");
                return;
            }
            System.out.println("Fichier FXML trouvé: " + fxmlUrl);

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Parent root = fxmlLoader.load();
            System.out.println("FXML chargé avec succès");

            Scene currentScene = null;
            Stage stage = null;

            if (btnClients != null) {
                currentScene = btnClients.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            if (currentScene == null && btnClients != null && btnClients.getParent() != null) {
                currentScene = btnClients.getParent().getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            if (currentScene == null && btnDashboard != null) {
                currentScene = btnDashboard.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            if (currentScene == null || stage == null) {
                System.err.println("ERREUR: Impossible de récupérer la scène ou le Stage !");
                System.err.println("btnClients.getScene() = " + (btnClients != null ? btnClients.getScene() : "btnClients est null"));
                return;
            }

            System.out.println("Scène récupérée: " + currentScene);
            System.out.println("Stage récupéré: " + stage);

            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            Scene scene = new Scene(root, width, height);
            System.out.println("Nouvelle scène créée: " + width + "x" + height);

            URL cssMenu = getClass().getResource("/styles/menu.css");
            URL cssGestionClients = getClass().getResource("/styles/GestionClients.css");

            if (cssMenu != null) {
                scene.getStylesheets().add(cssMenu.toExternalForm());
                System.out.println("CSS menu.css chargé");
            } else {
                System.err.println("ATTENTION: Fichier CSS menu.css introuvable !");
            }

            if (cssGestionClients != null) {
                scene.getStylesheets().add(cssGestionClients.toExternalForm());
                System.out.println("CSS GestionClients.css chargé");
            } else {
                System.err.println("ATTENTION: Fichier CSS GestionClients.css introuvable !");
            }

            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            stage.setScene(scene);
            setCurrentPage("GestionClients");
            System.out.println("Page Gestion des clients chargée avec succès !");

        } catch (IOException e) {
            System.err.println("ERREUR lors du chargement de la page Gestion des clients : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERREUR inattendue lors du chargement de la page Gestion des clients : " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    private void ouvrirGestionMedicaments() {
        try {
            System.out.println("Début du chargement de GestionMedicaments...");

            URL fxmlUrl = getClass().getResource("/com/example/views/GestionMedicaments.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERREUR: Fichier FXML GestionMedicaments.fxml introuvable !");
                System.err.println("Chemin recherché: /com/example/views/GestionMedicaments.fxml");
                return;
            }
            System.out.println("Fichier FXML trouvé: " + fxmlUrl);

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Parent root = fxmlLoader.load();
            System.out.println("FXML chargé avec succès");

            Scene currentScene = null;
            Stage stage = null;

            if (btnMedicaments != null) {
                currentScene = btnMedicaments.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            if (currentScene == null && btnMedicaments != null && btnMedicaments.getParent() != null) {
                currentScene = btnMedicaments.getParent().getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }
            
            if (currentScene == null && btnDashboard != null) {
                currentScene = btnDashboard.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            if (currentScene == null || stage == null) {
                System.err.println("ERREUR: Impossible de récupérer la scène ou le Stage !");
                return;
            }

            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            Scene scene = new Scene(root, width, height);
            System.out.println("Nouvelle scène créée: " + width + "x" + height);

            URL cssMenu = getClass().getResource("/styles/menu.css");
            URL cssGestionMedicaments = getClass().getResource("/styles/GestionMedicaments.css");

            if (cssMenu != null) {
                scene.getStylesheets().add(cssMenu.toExternalForm());
                System.out.println("CSS menu.css chargé");
            } else {
                System.err.println("ATTENTION: Fichier CSS menu.css introuvable !");
            }

            if (cssGestionMedicaments != null) {
                scene.getStylesheets().add(cssGestionMedicaments.toExternalForm());
                System.out.println("CSS GestionMedicaments.css chargé");
            } else {
                System.err.println("ATTENTION: Fichier CSS GestionMedicaments.css introuvable !");
            }

            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            stage.setScene(scene);
            setCurrentPage("GestionMedicaments");
            System.out.println("Page Gestion des médicaments chargée avec succès !");

        } catch (IOException e) {
            System.err.println("ERREUR lors du chargement de la page Gestion des médicaments : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERREUR inattendue lors du chargement de la page Gestion des médicaments : " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    private void ouvrirGestionLivraisons() {
        try {
            System.out.println("Début du chargement de GestionLivraisons...");

            URL fxmlUrl = getClass().getResource("/com/example/views/GestionLivraisons.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERREUR: Fichier FXML GestionLivraisons.fxml introuvable !");
                System.err.println("Chemin recherché: /com/example/views/GestionLivraisons.fxml");
                return;
            }
            System.out.println("Fichier FXML trouvé: " + fxmlUrl);

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Parent root = fxmlLoader.load();
            System.out.println("FXML chargé avec succès");

           
            Scene currentScene = null;
            Stage stage = null;

           
            if (btnLivraisons != null) {
                currentScene = btnLivraisons.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

           
            if (currentScene == null && btnLivraisons != null && btnLivraisons.getParent() != null) {
                currentScene = btnLivraisons.getParent().getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            
            if (currentScene == null && btnDashboard != null) {
                currentScene = btnDashboard.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            if (currentScene == null || stage == null) {
                System.err.println("ERREUR: Impossible de récupérer la scène ou le Stage !");
                return;
            }

            
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            Scene scene = new Scene(root, width, height);
            System.out.println("Nouvelle scène créée: " + width + "x" + height);

           
            URL cssMenu = getClass().getResource("/styles/menu.css");
            URL cssGestionLivraisons = getClass().getResource("/styles/GestionLivraisons.css");

            if (cssMenu != null) {
                scene.getStylesheets().add(cssMenu.toExternalForm());
                System.out.println("CSS menu.css chargé");
            } else {
                System.err.println("ATTENTION: Fichier CSS menu.css introuvable !");
            }

            if (cssGestionLivraisons != null) {
                scene.getStylesheets().add(cssGestionLivraisons.toExternalForm());
                System.out.println("CSS GestionLivraisons.css chargé");
            } else {
                System.err.println("ATTENTION: Fichier CSS GestionLivraisons.css introuvable !");
            }

          
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

           
            stage.setScene(scene);
            setCurrentPage("GestionLivraisons");
            System.out.println("Page Gestion des livraisons chargée avec succès !");

        } catch (IOException e) {
            System.err.println("ERREUR lors du chargement de la page Gestion des livraisons : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERREUR inattendue lors du chargement de la page Gestion des livraisons : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ouvrirGestionUtilisateurs() {
        try {
            System.out.println("Début du chargement de GestionUtilisateurs...");

           
            URL fxmlUrl = getClass().getResource("/com/example/views/GestionUtilisateurs.fxml");
            if (fxmlUrl == null) {
                System.err.println("ERREUR: Fichier FXML GestionUtilisateurs.fxml introuvable !");
                System.err.println("Chemin recherché: /com/example/views/GestionUtilisateurs.fxml");
                return;
            }
            System.out.println("Fichier FXML trouvé: " + fxmlUrl);

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Parent root = fxmlLoader.load();
            System.out.println("FXML chargé avec succès");

           
            Scene currentScene = null;
            Stage stage = null;

           
            if (btnUtilisateurs != null) {
                currentScene = btnUtilisateurs.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

           
            if (currentScene == null && btnUtilisateurs != null && btnUtilisateurs.getParent() != null) {
                currentScene = btnUtilisateurs.getParent().getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

           
            if (currentScene == null && btnDashboard != null) {
                currentScene = btnDashboard.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            if (currentScene == null || stage == null) {
                System.err.println("ERREUR: Impossible de récupérer la scène ou le Stage !");
                System.err.println("btnClients.getScene() = " + (btnUtilisateurs != null ? btnUtilisateurs.getScene() : "btnUtilisateurs est null"));
                return;
            }

            System.out.println("Scène récupérée: " + currentScene);
            System.out.println("Stage récupéré: " + stage);

           
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            Scene scene = new Scene(root, width, height);
            System.out.println("Nouvelle scène créée: " + width + "x" + height);

            
            URL cssMenu = getClass().getResource("/styles/menu.css");
            URL cssGestionUtilisateurs = getClass().getResource("/styles/GestionUtilisateurs.css");

            if (cssMenu != null) {
                scene.getStylesheets().add(cssMenu.toExternalForm());
                System.out.println("CSS menu.css chargé");
            } else {
                System.err.println("ATTENTION: Fichier CSS menu.css introuvable !");
            }

            if (cssGestionUtilisateurs != null) {
                scene.getStylesheets().add(cssGestionUtilisateurs.toExternalForm());
                System.out.println("CSS GestionUtilisateurs.css chargé");
            } else {
                System.err.println("ATTENTION: Fichier CSS GestionUtilisateurs.css introuvable !");
            }

           
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

           
            stage.setScene(scene);
            setCurrentPage("GestionUtilisateurs");
            System.out.println("Page Gestion des utilisateurs chargée avec succès !");

        } catch (IOException e) {
            System.err.println("ERREUR lors du chargement de la page Gestion des utilisateurs : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("ERREUR inattendue lors du chargement de la page Gestion des utilisateurs : " + e.getMessage());
            e.printStackTrace();
        }
    }


}
