package com.example.airPlan.controllers.Client;

import com.example.airPlan.models.FlightModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class FlightCellController implements Initializable {

    @FXML private Label des_lbl;
    @FXML private Label price_lbl;
    @FXML private Label status_lbl;
    @FXML private Label airline_lbl;
    @FXML private Label origin_lbl;
    @FXML private Label depart_lbl;
    @FXML private Button view_btn;

    private FlightModel flightModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        view_btn.setOnAction(event -> {
            if (flightModel != null) {
                openReservationWindow();
            }
        });
    }

    private void openReservationWindow() {
        try {
            // 1. Load the FXML file
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Fxml/Client/WindowReservation.fxml"));
            Parent root = loader.load();

            // 2. Get controller and set flight
            WindowReservationController controller = loader.getController();
            controller.setSelectedFlight(flightModel);

            // 3. Create and configure stage
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Flight Reservation");
            stage.initModality(Modality.APPLICATION_MODAL);

            // 4. Show window
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading WindowReservation.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Window Error",
                    "Failed to open reservation window: " + e.getMessage());
        }
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void setFlight(FlightModel flightModel) {
        this.flightModel = flightModel;
        updateFlightData();

        // Disable view button if flight isn't approved
        if (flightModel != null && !"approved".equals(flightModel.getAdminStatus())) {
            view_btn.setDisable(true);
            view_btn.setTooltip(new Tooltip("This flight is not available for booking"));
        } else {
            view_btn.setDisable(false);
            view_btn.setTooltip(null);
        }
    }

    private void updateFlightData() {
        if (flightModel != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            des_lbl.setText(flightModel.getDestination());
            origin_lbl.setText(flightModel.getOrigin());
            price_lbl.setText(String.format("€%.2f", flightModel.getPrice()));
            status_lbl.setText(flightModel.getStatus());
            depart_lbl.setText(dateFormat.format(flightModel.getDepartureDate()));
            airline_lbl.setText(flightModel.getAirline());

            // Add visual indicator for flight status
            if (!"approved".equals(flightModel.getAdminStatus())) {
                status_lbl.setStyle("-fx-text-fill: red;");
                status_lbl.setText(status_lbl.getText() + " (Pending Approval)");
            } else {
                status_lbl.setStyle("-fx-text-fill: green;");
            }
            switch (flightModel.getStatus().toLowerCase()) {
                case "landed":
                    status_lbl.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    break;
                case "boarding":
                    status_lbl.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    break;
                case "delayed":
                    status_lbl.setStyle("-fx-text-fill: #FF5733; -fx-font-style: italic;");
                    break;
                case "cancelled":
                    status_lbl.setStyle("-fx-text-fill: red; -fx-strikethrough: true;");
                    break;
                case "scheduled":
                    status_lbl.setStyle("-fx-text-fill: blue;");
                    break;
                default:
                    status_lbl.setStyle("");
            }
        }
    }

    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}