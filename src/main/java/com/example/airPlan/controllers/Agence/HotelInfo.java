package com.example.airPlan.controllers.Agence;

import com.example.airPlan.models.Hebergement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            Parent agencyAccView = loader.load();

            // Get the parent BorderPane from the current scene
            BorderPane parent = (BorderPane) returnButton.getScene().getRoot();
            parent.setCenter(agencyAccView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private Stage imagePreviewStage = null;
    private int currentImageIndex = 0;
    private List<Image> imageList = new ArrayList<>();

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


// In your method
        albuminfoo.getChildren().clear();
        imageList.clear(); // Clear previous images

        if (h.getAlbum() != null && !h.getAlbum().isEmpty()) {
            String[] imagePaths = h.getAlbum().split("\n");

            for (String path : imagePaths) {
                File file = new File(path.trim());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageList.add(image); // Store all images

                    ImageView thumbnail = new ImageView(image);
                    thumbnail.setFitWidth(160);
                    thumbnail.setFitHeight(160);
                    thumbnail.setPreserveRatio(true);
                    thumbnail.getStyleClass().add("image-thumbnail");

                    int index = imageList.size() - 1;
                    thumbnail.setOnMouseClicked(event -> openImageViewer(index));

                    albuminfoo.getChildren().add(thumbnail);
                } else {
                    System.out.println("Image non trouvée : " + path);
                }
            }
        }
    }


    private void openImageViewer(int startIndex) {
        if (imagePreviewStage != null && imagePreviewStage.isShowing()) return;

        currentImageIndex = startIndex;

        Stage mainStage = (Stage) albuminfoo.getScene().getWindow();
        mainStage.getScene().getRoot().setEffect(new GaussianBlur(10));

        imagePreviewStage = new Stage(StageStyle.TRANSPARENT);
        imagePreviewStage.initModality(Modality.APPLICATION_MODAL);
        imagePreviewStage.initOwner(mainStage);

        // Main image
        ImageView fullSize = new ImageView();
        fullSize.setPreserveRatio(true);
        fullSize.setFitWidth(600);
        fullSize.setImage(imageList.get(currentImageIndex));

        // "X" close button
        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
                "-fx-background-color: rgba(0,0,0,0.5);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 18px;" +
                        "-fx-background-radius: 20;" +
                        "-fx-min-width: 35px;" +
                        "-fx-min-height: 35px;" +
                        "-fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> imagePreviewStage.close());
        StackPane.setAlignment(closeBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(closeBtn, new Insets(20));

        // Navigation buttons
        Button leftBtn = new Button("◀");
        Button rightBtn = new Button("▶");

        String navStyle = "-fx-background-color: rgba(0,0,0,0.4); -fx-text-fill: white; -fx-font-size: 24px; -fx-background-radius: 20; -fx-cursor: hand;";
        leftBtn.setStyle(navStyle);
        rightBtn.setStyle(navStyle);

        leftBtn.setOnAction(e -> navigateImage(fullSize, -1));
        rightBtn.setOnAction(e -> navigateImage(fullSize, 1));

        VBox leftWrapper = new VBox(leftBtn);
        VBox rightWrapper = new VBox(rightBtn);
        leftWrapper.setAlignment(Pos.CENTER_LEFT);
        rightWrapper.setAlignment(Pos.CENTER_RIGHT);
        HBox.setMargin(leftWrapper, new Insets(0, 0, 0, 20));
        HBox.setMargin(rightWrapper, new Insets(0, 20, 0, 0));

        // Center image
        StackPane imagePane = new StackPane(fullSize, closeBtn);
        imagePane.setStyle("-fx-background-color: transparent;");

        HBox content = new HBox(leftWrapper, imagePane, rightWrapper);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(content, Color.TRANSPARENT);

        // Enable arrow key navigation
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT -> navigateImage(fullSize, -1);
                case RIGHT -> navigateImage(fullSize, 1);
                case ESCAPE -> imagePreviewStage.close();
            }
        });

        imagePreviewStage.setScene(scene);
        imagePreviewStage.setResizable(false);
        imagePreviewStage.show();

        // Automatically request focus for keyboard events
        scene.getRoot().requestFocus();

        imagePreviewStage.setOnHidden(e -> {
            mainStage.getScene().getRoot().setEffect(null);
            imagePreviewStage = null;
        });
    }

    private void navigateImage(ImageView imageView, int direction) {
        int newIndex = currentImageIndex + direction;
        if (newIndex >= 0 && newIndex < imageList.size()) {
            currentImageIndex = newIndex;
            imageView.setImage(imageList.get(currentImageIndex));
        }
    }

}