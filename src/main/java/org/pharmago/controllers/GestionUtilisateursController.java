package org.pharmago.controllers;

import org.pharmago.bdd.DatabaseConnection;
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
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Contrôleur pour l'interface de gestion des utilisateurs
 */
public class GestionUtilisateursController implements Initializable {

    @FXML private TextField searchField;
    @FXML private Button btnAjouterUtilisateur;
    @FXML private GridPane utilisateursGrid;

    private List<Utilisateur> utilisateurs = new ArrayList<>();
    private List<Utilisateur> utilisateursFiltres = new ArrayList<>();

    // Regex pour validation nom/prenom/email
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ]+(?:[-' ][A-Za-zÀ-ÖØ-öø-ÿ]+)*$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chargerUtilisateurs();
        configurerRecherche();
        configurerBoutonAjouter();
        searchField.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(
                        getClass().getResource("/styles/GestionUtilisateurs.css").toExternalForm()
                );
            }
        });

    }

    /**
     * Charge les utilisateurs depuis la DB (rôle = 'personnel')
     */
    private void chargerUtilisateurs() {
        utilisateurs.clear();
        String query = "SELECT idUser, nom, prenom, mail FROM utilisateur WHERE role = 'personnel'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("idUser");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String mail = rs.getString("mail");
                utilisateurs.add(new Utilisateur(id, nom, prenom, mail));
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur DB", "Impossible de charger les utilisateurs : " + e.getMessage());
            e.printStackTrace();
        }

        utilisateursFiltres = new ArrayList<>(utilisateurs);
        afficherUtilisateurs();
    }

    /**
     * Configuration de la recherche
     */
    private void configurerRecherche() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filtrerUtilisateurs(newVal));
    }

    private void filtrerUtilisateurs(String critere) {
        if (critere == null || critere.trim().isEmpty()) {
            utilisateursFiltres = new ArrayList<>(utilisateurs);
        } else {
            String critereLower = critere.toLowerCase().trim();
            utilisateursFiltres = utilisateurs.stream()
                    .filter(u -> u.getNom().toLowerCase().contains(critereLower) ||
                            u.getPrenom().toLowerCase().contains(critereLower) ||
                            String.valueOf(u.getIdUser()).contains(critereLower))
                    .toList();
        }
        afficherUtilisateurs();
    }

    /**
     * Configuration du bouton Ajouter
     */
    private void configurerBoutonAjouter() {
        btnAjouterUtilisateur.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterUtilisateur.fxml"));
                Parent root = loader.load();
                AjouterUtilisateurController controller = loader.getController();

                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle("Ajouter un utilisateur");
                dialog.setResizable(false);
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles/GestionUtilisateurs.css").toExternalForm());
                dialog.setScene(scene);
                dialog.showAndWait();

                if (controller.isConfirme()) {
                    Utilisateur nouvel = controller.getNouvelUtilisateur();

                    // Validation et ajout dans DB
                    String mail = nouvel.getMail().trim().toLowerCase();
                    String nom = nouvel.getNom().trim();
                    String prenom = nouvel.getPrenom().trim();
                    String mdp = nouvel.getMotDePasse();

                    if (!validateUtilisateur(nom, prenom, mail, mdp)) return;

                    String hashed = BCrypt.hashpw(mdp, BCrypt.gensalt(12));

                    String insert = "INSERT INTO utilisateur (nom, prenom, mail, mdp, role) VALUES (?, ?, ?, ?, 'personnel')";
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement(insert, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, nom);
                        stmt.setString(2, prenom);
                        stmt.setString(3, mail);
                        stmt.setString(4, hashed);

                        int affected = stmt.executeUpdate();
                        if (affected > 0) {
                            try (ResultSet keys = stmt.getGeneratedKeys()) {
                                if (keys.next()) {
                                    int id = keys.getInt(1);
                                    utilisateurs.add(new Utilisateur(id, nom, prenom, mail));
                                    filtrerUtilisateurs(searchField.getText());
                                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur ajouté avec succès !");
                                }
                            }
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ajouter l'utilisateur.");
                        }
                    }

                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Valide les champs d'un utilisateur (nom, prénom, mail, mot de passe).
     * Cette version est utilisée pour l'ajout (modeModification = false).
     *
     * @param nom Nom
     * @param prenom Prénom
     * @param mail Email
     * @param mdp Mot de passe
     * @return true si valide, false sinon
     */
    private boolean validateUtilisateur(String nom, String prenom, String mail, String mdp) {
        // Par défaut, false = ajout (nouvel utilisateur)
        return validateUtilisateur(nom, prenom, mail, mdp, false);
    }

    /**
     * Valide les champs d'un utilisateur (nom, prénom, mail, mot de passe).
     *
     * @param nom Nom
     * @param prenom Prénom
     * @param mail Email
     * @param mdp Mot de passe (peut être vide en modification)
     * @param modeModification true si on modifie un utilisateur
     * @return true si valide, false sinon
     */
    private boolean validateUtilisateur(String nom, String prenom, String mail, String mdp, boolean modeModification) {
        // Vérification des champs obligatoires
        if (nom == null || nom.isEmpty() ||
                prenom == null || prenom.isEmpty() ||
                mail == null || mail.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs obligatoires.");
            return false;
        }

        // Vérification nom/prénom avec regex
        if (!NAME_PATTERN.matcher(nom).matches()) {
            showAlert(Alert.AlertType.ERROR, "Nom invalide", "Le nom ne doit contenir que des lettres.");
            return false;
        }
        if (!NAME_PATTERN.matcher(prenom).matches()) {
            showAlert(Alert.AlertType.ERROR, "Prénom invalide", "Le prénom ne doit contenir que des lettres.");
            return false;
        }

        // Vérification email
        if (!EMAIL_PATTERN.matcher(mail).matches()) {
            showAlert(Alert.AlertType.ERROR, "Email invalide", "Veuillez entrer une adresse e-mail valide.");
            return false;
        }

        // Vérification mot de passe
        if (!modeModification) {
            // Ajout : mot de passe obligatoire
            if (mdp == null || mdp.length() < 10) {
                showAlert(Alert.AlertType.ERROR, "Mot de passe trop court", "Le mot de passe doit contenir au minimum 10 caractères.");
                return false;
            }
        } else {
            // Modification : mot de passe facultatif mais s'il est saisi, minimum 10 caractères
            if (mdp != null && !mdp.isEmpty() && mdp.length() < 10) {
                showAlert(Alert.AlertType.ERROR, "Mot de passe trop court", "Le mot de passe doit contenir au minimum 10 caractères.");
                return false;
            }
        }

        return true; // Tout est valide
    }

    /**
     * Affiche les utilisateurs dans la grille
     */
    private void afficherUtilisateurs() {
        utilisateursGrid.getChildren().clear();
        int colonnes = 3;

        for (int i = 0; i < utilisateursFiltres.size(); i++) {
            Utilisateur u = utilisateursFiltres.get(i);
            int colonne = i % colonnes;
            int ligne = i / colonnes;
            utilisateursGrid.add(creerCarteUtilisateur(u), colonne, ligne);
        }
    }

    private VBox creerCarteUtilisateur(Utilisateur u) {
        VBox carte = new VBox(10);
        carte.setPadding(new Insets(20));
        carte.getStyleClass().add("utilisateur-card");
        carte.setPrefWidth(300);
        carte.setPrefHeight(180);

        HBox enTete = new HBox(10);
        enTete.setAlignment(Pos.CENTER_LEFT);

        Label codeLabel = new Label("U" + String.format("%03d", u.getIdUser()));
        codeLabel.getStyleClass().add("utilisateur-code");

        Button btnModifier = new Button();
        btnModifier.getStyleClass().add("btn-icon");
        btnModifier.setGraphic(new ImageView(new Image("/assets/bouton-modifier.png", 18, 18, true, true)));
        btnModifier.setOnAction(e -> modifierUtilisateur(u));

        Button btnSupprimer = new Button();
        btnSupprimer.getStyleClass().add("btn-icon");
        btnSupprimer.setGraphic(new ImageView(new Image("/assets/supprimer.png", 18, 18, true, true)));
        btnSupprimer.setOnAction(e -> supprimerUtilisateur(u));

        enTete.getChildren().addAll(codeLabel, btnModifier, btnSupprimer);

        Label nomLabel = new Label(u.getNom() +" "+ u.getPrenom());
        nomLabel.getStyleClass().add("utilisateur-nom");

        carte.getChildren().addAll(enTete, nomLabel);
        return carte;
    }

    private void modifierUtilisateur(Utilisateur u) {
        try {
            // Charger le FXML du formulaire
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/views/AjouterUtilisateur.fxml"));
            Parent root = loader.load();
            AjouterUtilisateurController controller = loader.getController();
            controller.preparerModification(u);

            // Créer la fenêtre modale
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Modifier un utilisateur");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionUtilisateurs.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();

            // Si l'utilisateur confirme la modification
            if (controller.isConfirme()) {
                Utilisateur modif = controller.getNouvelUtilisateur();

                // Validation des champs (mot de passe facultatif)
                if (!validateUtilisateur(modif.getNom(), modif.getPrenom(), modif.getMail(),
                        modif.getMotDePasse(), true)) return; // true = modeModification

                // Préparer la requête SQL
                String update;
                boolean changerMotDePasse = modif.getMotDePasse() != null && !modif.getMotDePasse().isEmpty();
                if (changerMotDePasse) {
                    update = "UPDATE utilisateur SET nom=?, prenom=?, mail=?, mdp=? WHERE idUser=?";
                } else {
                    update = "UPDATE utilisateur SET nom=?, prenom=?, mail=? WHERE idUser=?";
                }

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(update)) {

                    stmt.setString(1, modif.getNom());
                    stmt.setString(2, modif.getPrenom());
                    stmt.setString(3, modif.getMail());

                    if (changerMotDePasse) {
                        String hashed = BCrypt.hashpw(modif.getMotDePasse(), BCrypt.gensalt(12));
                        stmt.setString(4, hashed);
                        stmt.setInt(5, u.getIdUser());
                    } else {
                        stmt.setInt(4, u.getIdUser());
                    }

                    stmt.executeUpdate();

                    // Mise à jour locale
                    u.setNom(modif.getNom());
                    u.setPrenom(modif.getPrenom());
                    u.setMail(modif.getMail());

                    filtrerUtilisateurs(searchField.getText());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur modifié avec succès !");
                }
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void supprimerUtilisateur(Utilisateur u) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/pharmago/views/ConfirmerSuppression.fxml"));
            Parent root = loader.load();
            ConfirmerSuppressionController controller = loader.getController();
            controller.setUtilisateur(u);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Confirmation de suppression");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/GestionUtilisateurs.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();

            if (controller.isConfirmation()) {
                String delete = "DELETE FROM utilisateur WHERE idUser=?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(delete)) {
                    stmt.setInt(1, u.getIdUser());
                    stmt.executeUpdate();
                    utilisateurs.remove(u);
                    filtrerUtilisateurs(searchField.getText());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Utilisateur supprimé avec succès !");
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

    /**
     * Classe Utilisateur
     */
    public static class Utilisateur {
        private int idUser;
        private String nom;
        private String prenom;
        private String mail;
        private String motDePasse;

        public Utilisateur(int idUser, String nom, String prenom, String mail) {
            this.idUser = idUser;
            this.nom = nom;
            this.prenom = prenom;
            this.mail = mail;
        }

        public int getIdUser() { return idUser; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }
        public String getMail() { return mail; }
        public String getMotDePasse() { return motDePasse; }

        public void setNom(String nom) { this.nom = nom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        public void setMail(String mail) { this.mail = mail; }
        public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    }
}
