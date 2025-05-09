package com.example.airPlan.controllers.Client;

import com.example.airPlan.models.Hebergement;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class FavorisCellController {
    @FXML private Label price_lbl;
    @FXML private Label rating_lbl;
    @FXML private Label type_lbl;
    @FXML private Label name_lbl;
    @FXML private Label city_lbl;
    @FXML private Label country_lbl;
    @FXML private Label dispo_lbl;


    public void setHebergementData(Hebergement hebergement) {
        name_lbl.setText(hebergement.getName());
        country_lbl.setText(hebergement.getCountry());
        city_lbl.setText(hebergement.getCity());
        type_lbl.setText(hebergement.getType());
        price_lbl.setText(String.format("%.2f TND", hebergement.getPricePerNight()));
        rating_lbl.setText(getStarRating(hebergement.getRating()));
        dispo_lbl.setText(hebergement.isDisponibility() ? "Available" : "Booked");


    }
    private String getStarRating(double rating) {
        int fullStars = (int) rating;
        boolean halfStar = (rating - fullStars) >= 0.5;

        StringBuilder stars = new StringBuilder();

        for (int i = 0; i < fullStars; i++) {
            stars.append("★"); // étoile pleine
        }

        if (halfStar) {
            stars.append("☆"); // étoile vide pour demi-étoile (ou utilise autre chose si tu veux)
        }

        while (stars.length() < 5) {
            stars.append("☆"); // compléter jusqu'à 5 étoiles
        }

        return stars.toString();

    }


}