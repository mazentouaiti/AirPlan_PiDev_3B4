package com.example.airPlan.controllers.Client;

import com.example.airPlan.Services.ReservationService;
import com.example.airPlan.models.Reservation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MyReservationsController {
    public Button refreshButton;
    public Button closeButton;
    @FXML private ListView<Reservation> reservationsListView;

    private ReservationService reservationService = new ReservationService();

    @FXML
    public void initialize() {
        loadReservations();

        reservationsListView.setCellFactory(listView -> new ReservationListCell());

        reservationsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Reservation selected = reservationsListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showReservationDetails(selected);
                }
            }
        });
    }

    private void loadReservations() {
        reservationsListView.getItems().clear();
        reservationsListView.getItems().addAll(
                reservationService.getUserReservations(getCurrentUserId())
        );
    }

    private void showReservationDetails(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/ReservationDetails.fxml"));
            Parent root = loader.load();

            ReservationDetailsController controller = loader.getController();
            controller.setReservation(reservation);

            Stage stage = new Stage();
            stage.setTitle("Reservation Details #" + reservation.getReservationId());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after details window closes
            loadReservations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadReservations();
    }

    @FXML
    private void handleClose() {
        ((Stage) reservationsListView.getScene().getWindow()).close();
    }

    private int getCurrentUserId() {
        // Implement based on your authentication system
        return 1; // Example - replace with actual user ID
    }
}