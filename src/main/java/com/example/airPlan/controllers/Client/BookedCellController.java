package com.example.airPlan.controllers.Client;

import com.example.airPlan.models.Reservation;
import com.example.airPlan.Services.ServiceReservation;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class BookedCellController {
    @javafx.fxml.FXML
    private Button cancel_btn;
    @javafx.fxml.FXML
    private Label arrival_lbl;
    @javafx.fxml.FXML
    private Label total_price_lbl;
    @javafx.fxml.FXML
    private Label name_lbl;
    @javafx.fxml.FXML
    private Label city_lbl;
    @javafx.fxml.FXML
    private Label depart_lbl;
    @javafx.fxml.FXML
    private Label country_lbl;

    private Reservation reservation;
    private Runnable refreshCallback;

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        name_lbl.setText(reservation.getNameOfReservatedAccommodation());
        country_lbl.setText(reservation.getTypeReservation());
        city_lbl.setText(reservation.getDestination());
        depart_lbl.setText(reservation.getDepartureDate().toString());
        arrival_lbl.setText(reservation.getArrivalDate().toString());
        total_price_lbl.setText(String.format("%.2f TND", reservation.getTotalPriceAcc()));
    }

    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @javafx.fxml.FXML
    public void onViewClicked(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Reservation");
        alert.setHeaderText("Are you sure you want to cancel this reservation?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ServiceReservation service = new ServiceReservation();
                    service.deleteReservation(reservation.getIdReservation());
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("Failed to cancel reservation");
                    errorAlert.setContentText("An error occurred while trying to cancel the reservation.");
                    errorAlert.showAndWait();
                }
            }
        });
    }
}