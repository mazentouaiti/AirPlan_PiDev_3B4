package com.example.airPlan.controllers.Client;

import com.example.airPlan.models.FlightModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class WindowReservationController implements Initializable {

    @FXML private Button cancel_btn;
    @FXML
    private Button confirm_btn;
    @FXML private TextField num_field;
    @FXML private TextField id_field;
    @FXML private TextField dest_field;
    @FXML private TextField status_field;
    @FXML private TextField origin_field;
    @FXML private TextField depart_field;


    private FlightModel selectedFlight;
    @FXML
    private Spinner<Integer> passanger_number;
    @FXML
    private ComboBox<String> classcombo;
    @FXML
    private TextField autoprice;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (classcombo != null) {
            classcombo.getItems().addAll("Economy", "Business", "First Class");
            classcombo.setValue("Economy");
        } else {
            System.err.println("Warning: class_combo is null!");
        }

        cancel_btn.setOnAction(event -> {
            ((Stage) cancel_btn.getScene().getWindow()).close();
        });

        confirm_btn.setOnAction(event -> {
            confirmReservation();
            ((Stage) confirm_btn.getScene().getWindow()).close();
        });
    }

    public void setSelectedFlight(FlightModel flight) {
        this.selectedFlight = flight;
        fillFormWithFlightData();
    }

    private void fillFormWithFlightData() {
        if (selectedFlight != null) {
            try {
                // First verify flight is approved
                if (!"approved".equals(selectedFlight.getAdminStatus())) {
                    showErrorAlert("Unavailable", "This flight is not available for booking");
                    ((Stage) cancel_btn.getScene().getWindow()).close();
                    return;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                num_field.setText(selectedFlight.getFlightNumber());
                id_field.setText(String.valueOf(selectedFlight.getFlight_id()));
                origin_field.setText(selectedFlight.getOrigin());
                dest_field.setText(selectedFlight.getDestination());
                status_field.setText(selectedFlight.getStatus());
                depart_field.setText(dateFormat.format(selectedFlight.getDepartureDate()));
                autoprice.setText(String.format("€%.2f", selectedFlight.getPrice()));

                SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(
                                1,
                                selectedFlight.getCapacity(),
                                1);
                passanger_number.setValueFactory(valueFactory);

                passanger_number.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());
                classcombo.valueProperty().addListener((obs, oldVal, newVal) -> updateTotalPrice());

                updateTotalPrice();
            } catch (Exception e) {
                showErrorAlert("Error", "Failed to load flight data");
            }
        }
    }

    private void updateTotalPrice() {
        if (selectedFlight != null) {
            try {
                String classType = classcombo.getValue();
                int passengerCount = passanger_number.getValue();
                double basePrice = selectedFlight.getPrice();

                double multiplier = switch (classType) {
                    case "Business" -> 1.5;
                    case "First Class" -> 2.0;
                    default -> 1.0;
                };

                double totalPrice = basePrice * multiplier * passengerCount;
                autoprice.setText(String.format("€%.2f", totalPrice));
            } catch (Exception e) {
                showErrorAlert("Calculation Error", "Failed to calculate total price: " + e.getMessage());
            }
        }
    }

    private void cancelReservation() {
        autoprice.getScene().getWindow().hide();
    }

    private void confirmReservation() {
        try {
            // Check if flight is still approved
            if (selectedFlight == null || !"approved".equals(selectedFlight.getAdminStatus())) {
                showErrorAlert("Reservation Error", "This flight is no longer available for booking");
                return;
            }

            // Check capacity
            int passengers = passanger_number.getValue();
            if (passengers > selectedFlight.getCapacity()) {
                showErrorAlert("Capacity Exceeded",
                        "Only " + selectedFlight.getCapacity() + " seats available");
                return;
            }

            // Add reservation logic here
            showInformationAlert("Success", "Reservation confirmed successfully!");
            autoprice.getScene().getWindow().hide();
        } catch (Exception e) {
            showErrorAlert("Reservation Error", "Failed to confirm reservation: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}