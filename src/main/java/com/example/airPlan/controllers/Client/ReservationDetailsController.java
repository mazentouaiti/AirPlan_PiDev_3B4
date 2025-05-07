package com.example.airPlan.controllers.Client;

import com.example.airPlan.models.Passenger;
import com.example.airPlan.models.Reservation;
import com.example.airPlan.Services.ReservationService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ReservationDetailsController {
    public Button closeButton;
    @FXML private Label reservationIdLabel;
    @FXML private Label flightNumberLabel;
    @FXML private Label routeLabel;
    @FXML private Label departureLabel;
    @FXML private Label passengerCountLabel;
    @FXML private Label classLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Label statusLabel;
    @FXML private ListView<String> passengerListView;
    @FXML private Button cancelButton;

    private Reservation reservation;
    private ReservationService reservationService = new ReservationService();

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        updateUI();
    }

    private void updateUI() {
        reservationIdLabel.setText(String.valueOf(reservation.getReservationId()));
        flightNumberLabel.setText(reservation.getFlightDetails().getFlightNumber());
        routeLabel.setText(reservation.getFlightDetails().getOrigin() + " → " +
                reservation.getFlightDetails().getDestination());
        departureLabel.setText(reservation.getFlightDetails().getDepartureDate().toString());
        passengerCountLabel.setText(String.valueOf(reservation.getPassengerCount()));
        classLabel.setText(reservation.getClassType());
        totalPriceLabel.setText(String.format("€%.2f", reservation.getTotalPrice()));
        statusLabel.setText(reservation.getStatus());

        // Disable cancel button if already cancelled
        if ("cancelled".equalsIgnoreCase(reservation.getStatus())) {
            cancelButton.setDisable(true);
        }

        // Load passengers
        passengerListView.getItems().clear();
        for (Passenger passenger : reservation.getPassengers()) {
            passengerListView.getItems().add(
                    passenger.getFirstName() + " " + passenger.getLastName() +
                            " (Passport: " + passenger.getPassportNumber() + ")"
            );
        }
    }

    @FXML
    private void handleCancelReservation() {
        if (reservationService.cancelReservation(reservation.getReservationId())) {
            reservation.setStatus("cancelled");
            updateUI();
            showInformationAlert("Success", "Reservation has been cancelled.");
        } else {
            showErrorAlert("Error", "Failed to cancel reservation.");
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    private void showInformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}