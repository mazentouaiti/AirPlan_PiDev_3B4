package com.example.airPlan.controllers.Client;

import com.example.airPlan.models.Hebergement;
import com.example.airPlan.Services.ServiceHebergement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClientAcc {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private FlowPane flowPane;

    ServiceHebergement service = new ServiceHebergement();
    @FXML
    private ComboBox pricecombo;
    @FXML
    private TextField destinationfiled;

    private List<Hebergement> hotelList = new ArrayList<>();
    private List<Hebergement> displayedHotels = new ArrayList<>();


    @FXML
    public void initialize() {
          // Marge autour du FlowPane
        scrollPane.setBackground(null);
        flowPane.setBackground(null);

        flowPane.setHgap(20);
        flowPane.setVgap(20);
        flowPane.setPadding(new Insets(20));

        pricecombo.getItems().addAll("Ascending Price", "Descending Price", "Rating");
        pricecombo.setOnAction(event -> sortHotels());
        destinationfiled.textProperty().addListener((observable, oldValue, newValue) -> filterHotels());


        hotelList = service.getHebergementsMisenAvant();
        displayedHotels = new ArrayList<>(hotelList);
        updateHotelCards(displayedHotels);


    }
    private void filterHotels() {
        String keyword = destinationfiled.getText().toLowerCase();
        displayedHotels = hotelList.stream()
                .filter(hotel -> hotel.getName().toLowerCase().contains(keyword)
                        || hotel.getCountry().toLowerCase().contains(keyword)
                        || hotel.getCity().toLowerCase().contains(keyword))

                .collect(Collectors.toList());
        updateHotelCards(displayedHotels);
    }


    private void sortHotels() {
        String selected = (String) pricecombo.getSelectionModel().getSelectedItem();

        if (selected == null) return;

        switch (selected) {
            case "Ascending Price":
                displayedHotels.sort(Comparator.comparingDouble(Hebergement::getPricePerNight));
                break;
            case "Descending Price":
                displayedHotels.sort(Comparator.comparingDouble(Hebergement::getPricePerNight).reversed());
                break;
            case "Rating":
                displayedHotels.sort(Comparator.comparingDouble(Hebergement::getRating).reversed());
                break;
        }

        updateHotelCards(displayedHotels);
    }


    private void updateHotelCards(List<Hebergement> hebergements) {
        flowPane.getChildren().clear();
        for (Hebergement hebergement : hebergements) {
            VBox card = createCard(hebergement);
            flowPane.getChildren().add(card);
        }
    }


    private void loadHebergements() {

        List<Hebergement> hebergements = service.getHebergementsMisenAvant();


        for (Hebergement h : hebergements) {
            VBox card = createCard(h);
            flowPane.getChildren().add(card);
        }
    }

    private VBox createCard(Hebergement hebergement) {
        VBox carte = new VBox();
        carte.setPrefWidth(260);
        carte.setPadding(new Insets(12));
        carte.setSpacing(10);
        carte.setStyle("""
        -fx-background-color: #ffffff;
        -fx-background-radius: 15;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);
    """);

        // Image
        ImageView imageView = new ImageView();
        try {
            Image img = new Image(hebergement.getPhoto(), 240, 160, true, true);
            imageView.setImage(img);
            imageView.setFitWidth(240);
            imageView.setFitHeight(160);
            imageView.setSmooth(true);
            imageView.setPreserveRatio(false);
            imageView.setStyle("-fx-border-radius: 15; -fx-background-radius: 15;");
        } catch (Exception e) {
            System.out.println("Erreur chargement image: " + hebergement.getPhoto());
        }

        // Titre
        Label labelTitre = new Label(hebergement.getName());
        labelTitre.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        labelTitre.setTextFill(Color.web("#2e2e2e"));

        // Localisation
        Label labelLocation = new Label("📍  " + hebergement.getCountry()+", "+hebergement.getCity());
        labelLocation.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        labelLocation.setTextFill(Color.web("#555"));

        // Description
        Label labelDescription = new Label(hebergement.getDescription());
        labelDescription.setFont(Font.font("Arial", 12));
        labelDescription.setWrapText(true);
        labelDescription.setTextFill(Color.GRAY);

        // Prix
        Label labelPrix = new Label(String.format("%.2f TND / Night", hebergement.getPricePerNight()));
        labelPrix.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        labelPrix.setTextFill(Color.web("#009688"));

        // Étoiles
        HBox starsBox = new HBox(2);
        for (int i = 0; i < hebergement.getRating(); i++) {
            Label star = new Label("★");
            star.setStyle("-fx-text-fill: gold; -fx-font-size: 14px;");
            starsBox.getChildren().add(star);
        }

        // Cœur favoris
        Image imgHeartEmpty = new Image(getClass().getResource("/images/heart_empty.png").toExternalForm());
        Image imgHeartFull = new Image(getClass().getResource("/images/heart_full.png").toExternalForm());

        ImageView heartView = new ImageView(imgHeartEmpty);
        heartView.setFitWidth(24);
        heartView.setFitHeight(24);

        Button btnFavori = new Button();
        btnFavori.setGraphic(heartView);
        btnFavori.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        final boolean[] isFavori = {false};
        btnFavori.setOnAction(e -> {
            isFavori[0] = !isFavori[0];
            heartView.setImage(isFavori[0] ? imgHeartFull : imgHeartEmpty);
        });

        // Bouton Réserver
        Button btnReserver = new Button("Réserver");
        btnReserver.setStyle("""
        -fx-background-color: #588b8b;
        -fx-text-fill: white;
        -fx-background-radius: 20;
        -fx-padding: 6 12;
        -fx-font-weight: bold;
    """);
        btnReserver.setVisible(false);

        carte.setOnMouseEntered(e -> {
            btnReserver.setVisible(true); // Cacher/montrer juste le bouton
            carte.setStyle("-fx-background-color: #f1f1f1; -fx-background-radius: 10; "
                    + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        });
        carte.setOnMouseExited(e -> {
            btnReserver.setVisible(false);
            carte.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; "
                    + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
        });

        btnReserver.setOnAction(event -> openReservation(hebergement, event));

        HBox footerBox = new HBox(btnFavori, new Region(), btnReserver);
        HBox.setHgrow(footerBox.getChildren().get(1), Priority.ALWAYS);
        footerBox.setAlignment(Pos.CENTER_LEFT);

        // Survol
        carte.setOnMouseEntered(e -> {
            btnReserver.setVisible(true);
            carte.setStyle("""
            -fx-background-color: #f5f5f5;
            -fx-background-radius: 15;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 4);
        """);
        });

        carte.setOnMouseExited(e -> {
            btnReserver.setVisible(false);
            carte.setStyle("""
            -fx-background-color: #ffffff;
            -fx-background-radius: 15;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 4);
        """);
        });

        // Double-clic pour détails
        carte.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openHotelInfo(hebergement);
            }
        });

        carte.getChildren().addAll(
                imageView,
                labelTitre,
                labelLocation,
                labelDescription,
                labelPrix,
                starsBox,
                footerBox
        );

        return carte;
    }




    private void openHotelInfo(Hebergement hebergement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/hotel_info_client.fxml"));
            Parent root = loader.load();

            HotelInfoClient hotelInfoController = loader.getController();

            hotelInfoController.setHebergementDetails(hebergement);

            Scene currentScene = scrollPane.getScene();
            currentScene.setRoot(root);  // Remplacer le contenu de la scène avec la nouvelle vue




        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openReservation(Hebergement hebergement, ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/reservationclient.fxml"));
            Parent root = loader.load();


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


            ReservationClient controller = loader.getController();


            controller.setHebergementData(hebergement);


            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }





}
