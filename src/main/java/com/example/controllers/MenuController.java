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
    private static String currentPage = "TableauBord"; // ou n'importe quelle valeur par défaut


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

        // Groupe de boutons
        btnDashboard.setToggleGroup(menuGroup);
        btnConsultation.setToggleGroup(menuGroup);
        btnClients.setToggleGroup(menuGroup);
        btnMedicaments.setToggleGroup(menuGroup);
        btnLivraisons.setToggleGroup(menuGroup);
        btnUtilisateurs.setToggleGroup(menuGroup);

        // Désactiver les effets visuels par défaut pour tous les boutons
        disableDefaultButtonEffects(btnDashboard);
        disableDefaultButtonEffects(btnConsultation);
        disableDefaultButtonEffects(btnClients);
        disableDefaultButtonEffects(btnMedicaments);
        disableDefaultButtonEffects(btnLivraisons);
        disableDefaultButtonEffects(btnUtilisateurs);

        // Sélectionner le bouton correspondant à la page actuelle
        selectButtonForCurrentPage();

        // Ajout d'un listener pour mettre à jour le style et les icônes
        menuGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            updateActiveButton();
            updateIcons();
        });

        // Navigation : connexion du bouton Tableau de bord
        btnDashboard.setOnAction(e -> {
            System.out.println("Clic sur Tableau de bord");
            setCurrentPage("TableauBord");
            navigateToTableauBord();
        });

        // Navigation : connexion du bouton Consultation Globale
        btnConsultation.setOnAction(e -> {
            System.out.println("Clic sur Consultation Globale");
            setCurrentPage("ConsultationGlobale");
            navigateToConsultationGlobale();
        });

        // Navigation : connexion du bouton Gestion des clients
        btnClients.setOnAction(e -> {
            System.out.println("Clic sur Gestion des clients");
            setCurrentPage("GestionClients");
            ouvrirGestionClients();
        });

        // Navigation : connexion du bouton Gestion des médicaments
        btnMedicaments.setOnAction(e -> {
            System.out.println("Clic sur Gestion des médicaments");
            setCurrentPage("GestionMedicaments");
            ouvrirGestionMedicaments();
        });

        // Navigation : connexion du bouton Gestion des livraisons
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
            applyCorrectStyle(btnUtilisateurs);
        });
    }

    /**
     * Sélectionne le bouton correspondant à la page actuelle
     */
    private void selectButtonForCurrentPage() {
        // Désélectionner tous les boutons d'abord
        menuGroup.selectToggle(null);
        
        // Sélectionner le bouton correspondant à la page actuelle
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
        
        // Mettre à jour les styles et icônes
        updateActiveButton();
        updateIcons();
    }

    /**
     * Définit la page actuelle (méthode publique pour être appelée depuis l'extérieur)
     */
    public static void setCurrentPage(String pageName) {
        currentPage = pageName;
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

    /**
     * Navigation vers la page Tableau de bord
     * Charge le fichier FXML TableauBord.fxml et change la scène
     */
    private void navigateToTableauBord() {
        try {
            // Charger le fichier FXML de la page Tableau de bord
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/views/TableauBord.fxml"));
            Parent root = fxmlLoader.load();

            // Récupérer la scène actuelle et la fenêtre (Stage)
            Scene currentScene = null;
            Stage stage = null;

            // Essayer plusieurs méthodes pour obtenir la scène
            if (btnDashboard != null) {
                currentScene = btnDashboard.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            // Méthode alternative : depuis n'importe quel autre bouton
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

            // Créer une nouvelle scène avec le contenu chargé
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            Scene scene = new Scene(root, width, height);

            // Charger les fichiers CSS nécessaires
            URL cssMenu = getClass().getResource("/styles/menu.css");
            URL cssTableau = getClass().getResource("/styles/tableauBord.css");

            if (cssMenu != null) {
                scene.getStylesheets().add(cssMenu.toExternalForm());
            }
            if (cssTableau != null) {
                scene.getStylesheets().add(cssTableau.toExternalForm());
            }

            // Appliquer la police par défaut
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            // Définir la nouvelle scène et afficher
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

    /**
     * Navigation vers la page Consultation Globale
     * Charge le fichier FXML ConsultationGlobale.fxml et change la scène
     */
    private void navigateToConsultationGlobale() {
        try {
            // Charger le fichier FXML de la page Consultation Globale
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/views/ConsultationGlobale.fxml"));
            Parent root = fxmlLoader.load();

            // Récupérer la scène actuelle et la fenêtre (Stage)
            Scene currentScene = btnConsultation.getScene();
            Stage stage = (Stage) currentScene.getWindow();

            // Créer une nouvelle scène avec le contenu chargé
            Scene scene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

            // Charger les fichiers CSS nécessaires
            URL cssMenu = getClass().getResource("/styles/menu.css");
            URL cssConsultation = getClass().getResource("/styles/ConsultationGloable.css");

            if (cssMenu != null) {
                scene.getStylesheets().add(cssMenu.toExternalForm());
            }
            if (cssConsultation != null) {
                scene.getStylesheets().add(cssConsultation.toExternalForm());
            }

            // Appliquer la police par défaut
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            // Définir la nouvelle scène et afficher
            stage.setScene(scene);
            setCurrentPage("ConsultationGlobale");

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page Consultation Globale : " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Navigation vers la page Gestion des clients
     * Charge le fichier FXML GestionClients.fxml et change la scène
     */
    private void ouvrirGestionClients() {
        try {
            System.out.println("Début du chargement de GestionClients...");

            // Charger le fichier FXML de la page Gestion des clients
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

            // Récupérer la scène actuelle et la fenêtre (Stage)
            // Essayer plusieurs méthodes pour obtenir la scène
            Scene currentScene = null;
            Stage stage = null;

            // Méthode 1: depuis le bouton
            if (btnClients != null) {
                currentScene = btnClients.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            // Méthode 2: depuis le parent du bouton
            if (currentScene == null && btnClients != null && btnClients.getParent() != null) {
                currentScene = btnClients.getParent().getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            // Méthode 3: depuis n'importe quel nœud de la scène
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

            // Créer une nouvelle scène avec le contenu chargé
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            Scene scene = new Scene(root, width, height);
            System.out.println("Nouvelle scène créée: " + width + "x" + height);

            // Charger les fichiers CSS nécessaires
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

            // Appliquer la police par défaut
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            // Définir la nouvelle scène et afficher
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

    /**
     * Navigation vers la page Gestion des médicaments
     * Charge le fichier FXML GestionMedicaments.fxml et change la scène
     */
    private void ouvrirGestionMedicaments() {
        try {
            System.out.println("Début du chargement de GestionMedicaments...");

            // Charger le fichier FXML de la page Gestion des médicaments
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

            // Récupérer la scène actuelle et la fenêtre (Stage)
            Scene currentScene = null;
            Stage stage = null;

            // Méthode 1: depuis le bouton
            if (btnMedicaments != null) {
                currentScene = btnMedicaments.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            // Méthode 2: depuis le parent du bouton
            if (currentScene == null && btnMedicaments != null && btnMedicaments.getParent() != null) {
                currentScene = btnMedicaments.getParent().getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            // Méthode 3: depuis n'importe quel autre bouton
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

            // Créer une nouvelle scène avec le contenu chargé
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            Scene scene = new Scene(root, width, height);
            System.out.println("Nouvelle scène créée: " + width + "x" + height);

            // Charger les fichiers CSS nécessaires
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

            // Appliquer la police par défaut
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            // Définir la nouvelle scène et afficher
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

    /**
     * Navigation vers la page Gestion des livraisons
     * Charge le fichier FXML GestionLivraisons.fxml et change la scène
     */
    private void ouvrirGestionLivraisons() {
        try {
            System.out.println("Début du chargement de GestionLivraisons...");

            // Charger le fichier FXML de la page Gestion des livraisons
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

            // Récupérer la scène actuelle et la fenêtre (Stage)
            Scene currentScene = null;
            Stage stage = null;

            // Méthode 1: depuis le bouton
            if (btnLivraisons != null) {
                currentScene = btnLivraisons.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            // Méthode 2: depuis le parent du bouton
            if (currentScene == null && btnLivraisons != null && btnLivraisons.getParent() != null) {
                currentScene = btnLivraisons.getParent().getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            // Méthode 3: depuis n'importe quel autre bouton
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

            // Créer une nouvelle scène avec le contenu chargé
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            Scene scene = new Scene(root, width, height);
            System.out.println("Nouvelle scène créée: " + width + "x" + height);

            // Charger les fichiers CSS nécessaires
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

            // Appliquer la police par défaut
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            // Définir la nouvelle scène et afficher
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

            // Charger le fichier FXML de la page Gestion des clients
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

            // Récupérer la scène actuelle et la fenêtre (Stage)
            // Essayer plusieurs méthodes pour obtenir la scène
            Scene currentScene = null;
            Stage stage = null;

            // Méthode 1: depuis le bouton
            if (btnUtilisateurs != null) {
                currentScene = btnUtilisateurs.getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            // Méthode 2: depuis le parent du bouton
            if (currentScene == null && btnUtilisateurs != null && btnUtilisateurs.getParent() != null) {
                currentScene = btnUtilisateurs.getParent().getScene();
                if (currentScene != null) {
                    stage = (Stage) currentScene.getWindow();
                }
            }

            // Méthode 3: depuis n'importe quel nœud de la scène
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

            // Créer une nouvelle scène avec le contenu chargé
            double width = currentScene.getWidth() > 0 ? currentScene.getWidth() : 1024;
            double height = currentScene.getHeight() > 0 ? currentScene.getHeight() : 768;
            Scene scene = new Scene(root, width, height);
            System.out.println("Nouvelle scène créée: " + width + "x" + height);

            // Charger les fichiers CSS nécessaires
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

            // Appliquer la police par défaut
            scene.getRoot().setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");

            // Définir la nouvelle scène et afficher
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