package com.example.airPlan.controllers.Admin;

import com.example.airPlan.models.Hebergement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HotelInfoAdmin {
    @FXML private Label nameinfo;
    @FXML private Label typeinfo;
    @FXML private Label cityinfo;
    @FXML private Label countryinfo;
    @FXML private Label addressinfo ;
    @FXML private Label descriptioninfo;

    @FXML private Label capacityinfo;
    @FXML private Label ratinginfo;
    @FXML private Label dispoinfo;
    @FXML private Label priceinf;
    @FXML private Label priceinfo;
    @FXML private Label optionsinfo;
    @FXML private ImageView photoinfo;
    @FXML private HBox albuminfoo;
    @FXML
    private Button returnButton;

    @FXML
    private void initialize() {
        returnButton.setOnAction(event -> retournerAdmin());
    }






    public void retournerAdmin() {
        try {
            // Charger la vue accommodation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/admin_acc.fxml"));
            Parent root = loader.load();

            // Obtenir la scène depuis le bouton
            Stage stage = (Stage) returnButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Optionnel : ajouter des styles, animations, etc.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setHebergementDetails(Hebergement h) {
        nameinfo.setText(h.getName());
        typeinfo.setText(h.getType());
        cityinfo.setText(h.getCity());
        countryinfo.setText(h.getCountry());
        addressinfo.setText(h.getAddress());
        descriptioninfo.setText(h.getDescription());
        //albuminfo.setText(h.getAlbum()); // ou afficher un lien ou une liste d’images si besoin
        capacityinfo.setText(String.valueOf(h.getCapacity()));
        ratinginfo.setText(String.valueOf(h.getRating()));
        dispoinfo.setText(h.isDisponibility() ? "Available" : "Unavailable");
        priceinfo.setText(String.format("%.2f TND", h.getPricePerNight())); // pour l'affichage stylé
        optionsinfo.setText(h.getOptions());
        String photoPath = h.getPhoto();


        try {
            File file = new File(photoPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                photoinfo.setImage(image);
            } else {
                System.out.println(" Fichier image introuvable : " + photoPath);
                // Optionnel : image par défaut
                photoinfo.setImage(new Image(getClass().getResourceAsStream("/images/default.jpg")));
            }
        } catch (Exception e) {
            System.out.println(" Erreur lors du chargement de l'image : " + e.getMessage());
        }
        System.out.println("Chemin reçu de la base : " + h.getPhoto());
        System.out.println("Fichier existe ? " + new File(h.getPhoto()).exists());


        //albuum

        // Affichage de l'album dans le FlowPane
        albuminfoo.getChildren().clear(); // Nettoyer avant ajout

        if (h.getAlbum() != null && !h.getAlbum().isEmpty()) {
            String[] imagePaths = h.getAlbum().split("\n");
            for (String path : imagePaths) {
                File file = new File(path.trim());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(160);
                    imageView.setFitHeight(160);
                    imageView.setPreserveRatio(true);
                    imageView.getStyleClass().add("image-thumbnail");

                    // Miniature cliquable
                    imageView.setOnMouseClicked(event -> {
                        Stage stage = new Stage();
                        stage.setTitle("Aperçu de l'image");

                        ImageView fullSize = new ImageView(image);
                        fullSize.setPreserveRatio(true);
                        fullSize.setFitWidth(600);

                        StackPane root = new StackPane(fullSize);
                        root.setPadding(new Insets(10));
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.show();

                    });

                    albuminfoo.getChildren().add(imageView);
                } else {
                    System.out.println("Image non trouvée : " + path);
                }
            }
        } else {
            System.out.println("Aucune image dans l’album.");
        }


    }



}
