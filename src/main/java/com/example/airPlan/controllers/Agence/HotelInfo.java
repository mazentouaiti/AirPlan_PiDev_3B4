package com.example.airPlan.controllers.Agence;

import com.example.airPlan.models.Hebergement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HotelInfo {
    @FXML private Label nameinfo;
    @FXML private Label typeinfo;
    @FXML private Label cityinfo;
    @FXML private Label countryinfo;
    @FXML private Label addressinfo;
    @FXML private Label descriptioninfo;
    @FXML private Label capacityinfo;
    @FXML private Label ratinginfo;
    @FXML private Label dispoinfo;
    @FXML private Label priceinf;
    @FXML private Label priceinfo;
    @FXML private Label optionsinfo;
    @FXML private ImageView photoinfo;
    @FXML private HBox albuminfoo;
    @FXML private Button returnButton;

    @FXML
    private void initialize() {
        returnButton.setOnAction(event -> retournerAccueil());
    }

    private String getStarRating(double rating) {
        int fullStars = (int) rating;
        boolean halfStar = (rating - fullStars) >= 0.5;

        StringBuilder stars = new StringBuilder();

        for (int i = 0; i < fullStars; i++) {
            stars.append("★"); // Full star
        }

        if (halfStar) {
            stars.append("☆"); // Half star (you can use "½" if preferred)
        }

        while (stars.length() < 5) {
            stars.append("☆"); // Fill up to 5 stars
        }

        return stars.toString();
    }

    public void retournerAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Agences/agency_acc.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
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
        capacityinfo.setText(String.valueOf(h.getCapacity()));
        ratinginfo.setText(getStarRating(h.getRating())); // Changed to use star rating
        dispoinfo.setText(h.isDisponibility() ? "Available" : "Unavailable");
        priceinfo.setText(String.format("%.2f TND", h.getPricePerNight()));
        optionsinfo.setText(h.getOptions());
        String photoPath = h.getPhoto();

        try {
            File file = new File(photoPath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                photoinfo.setImage(image);
            } else {
                System.out.println("Fichier image introuvable : " + photoPath);
                photoinfo.setImage(new Image(getClass().getResourceAsStream("/images/default.jpg")));
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de l'image : " + e.getMessage());
        }

        // Album display
        albuminfoo.getChildren().clear();
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

                    imageView.setOnMouseClicked(event -> {
                        Stage mainStage = (Stage) imageView.getScene().getWindow();
                        mainStage.getScene().getRoot().setEffect(new GaussianBlur(10));

                        Pane overlay = new Pane();
                        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
                        overlay.setPrefSize(mainStage.getScene().getWidth(), mainStage.getScene().getHeight());
                        ((Pane) mainStage.getScene().getRoot()).getChildren().add(overlay);

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
                        stage.initOwner(mainStage);

                        stage.setOnHidden(e -> {
                            mainStage.getScene().getRoot().setEffect(null);
                            ((Pane) mainStage.getScene().getRoot()).getChildren().remove(overlay);
                        });

                        stage.show();
                    });
                    albuminfoo.getChildren().add(imageView);
                } else {
                    System.out.println("Image non trouvée : " + path);
                }
            }
        } else {
            System.out.println("Aucune image dans l'album.");
        }
    }
}