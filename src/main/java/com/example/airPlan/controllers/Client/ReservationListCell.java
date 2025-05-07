package com.example.airPlan.controllers.Client;

import com.example.airPlan.models.Reservation;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ReservationListCell extends ListCell<Reservation> {
    @Override
    protected void updateItem(Reservation reservation, boolean empty) {
        super.updateItem(reservation, empty);

        if (empty || reservation == null) {
            setText(null);
            setGraphic(null);
        } else {
            HBox hbox = new HBox(10);

            Text idText = new Text("#" + reservation.getReservationId());
            Text flightText = new Text(reservation.getFlightDetails().getFlightNumber());
            Text routeText = new Text(reservation.getFlightDetails().getOrigin() + " → " +
                    reservation.getFlightDetails().getDestination());
            Text dateText = new Text(reservation.getFlightDetails().getDepartureDate().toString());
            Text passengersText = new Text(reservation.getPassengerCount() + " passengers");
            Text priceText = new Text(String.format("€%.2f", reservation.getTotalPrice()));
            Text statusText = new Text(reservation.getStatus());

            // Style based on status
            switch (reservation.getStatus().toLowerCase()) {
                case "confirmed":
                    statusText.setStyle("-fx-fill: green; -fx-font-weight: bold;");
                    break;
                case "cancelled":
                    statusText.setStyle("-fx-fill: red; -fx-strikethrough: true;");
                    break;
                case "pending":
                    statusText.setStyle("-fx-fill: orange;");
                    break;
            }

            hbox.getChildren().addAll(idText, flightText, routeText, dateText,
                    passengersText, priceText, statusText);
            setGraphic(hbox);
        }
    }
}