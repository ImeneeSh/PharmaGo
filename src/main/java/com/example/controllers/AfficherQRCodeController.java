package com.example.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AfficherQRCodeController {

    @FXML private ImageView qrCodeImageView;
    @FXML private Label numeroLabel;
    @FXML private Label clientLabel;
    @FXML private Label dateLabel;
    @FXML private Label quantiteLabel;
    @FXML private Label statutLabel;
    @FXML private Label typeLabel;
    @FXML private Label taxeLabel;
    @FXML private Label coutLabel;
    @FXML private Button btnFermer;

    private GestionLivraisonsController.Livraison livraison;

    @FXML
    public void initialize() {
        btnFermer.setOnAction(e -> fermer());
    }

    /**
     * Initialise la fenêtre avec les données de la livraison
     * @param livraison La livraison à afficher
     */
    public void setLivraison(GestionLivraisonsController.Livraison livraison) {
        this.livraison = livraison;
        
        // Afficher les informations de la livraison
        numeroLabel.setText(String.valueOf(livraison.getNumLiv()));
        clientLabel.setText(livraison.getClient());
        dateLabel.setText(livraison.getDateFormatee());
        quantiteLabel.setText(String.valueOf(livraison.getNombreMedicaments()) + " médicament(s)");
        statutLabel.setText(livraison.getStatut());
        typeLabel.setText(livraison.getType());
        taxeLabel.setText(livraison.getTaxe() + " DA");
        coutLabel.setText(livraison.getCout() + " DA");
        
        // Générer et afficher le QR code
        genererQRCode();
    }

    /**
     * Génère un QR code contenant les informations de la livraison
     */
    private void genererQRCode() {
        try {
            String qrText = String.format(
                "Livraison: %s\nClient: %s\nDate: %s\nQuantité: %d\nStatut: %s\nType: %s",
                livraison.getNumLiv(),
                livraison.getClient(),
                livraison.getDateFormatee(),
                livraison.getNombreMedicaments(),
                livraison.getStatut(),
                livraison.getType()
            );

            // Configuration du QR code
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            // Générer le QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrText, BarcodeFormat.QR_CODE, 200, 200, hints);

            // Convertir BitMatrix en Image JavaFX
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            WritableImage writableImage = new WritableImage(width, height);
            PixelWriter pixelWriter = writableImage.getPixelWriter();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixelWriter.setArgb(x, y, 0xFF000000); // Noir
                    } else {
                        pixelWriter.setArgb(x, y, 0xFFFFFFFF); // Blanc
                    }
                }
            }

            // Afficher l'image
            qrCodeImageView.setImage(writableImage);

        } catch (WriterException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la génération du QR code : " + e.getMessage());
        }
    }

    private void fermer() {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }
}

