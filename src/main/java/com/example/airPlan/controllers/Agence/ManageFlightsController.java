package com.example.airPlan.controllers.Agence;

import com.example.airPlan.models.FlightModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class ManageFlightsController implements Initializable {

    @FXML private TextField flight_number;
    @FXML private TextField price;
    @FXML private TextField origin;
    @FXML private TextField destination;
    @FXML private TextField id;
    @FXML private TextField airline;
    @FXML private TextField capacity;
    @FXML private ComboBox<String> status;
    @FXML private DatePicker arrive;
    @FXML private DatePicker depart;
    @FXML private Button confirm;
    @FXML private Button cancel;

    private boolean createMode;
    private FlightModel flightData = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        status.getItems().addAll("Scheduled", "Delayed", "Cancelled", "Boarding", "Landed");

        confirm.setOnAction(event -> {
            if (validateInputs()) {
                flightData = createFlightModel();
                closeWindow();
            }
        });

        cancel.setOnAction(event -> {
            flightData = null;
            closeWindow();
        });
    }

    public void setMode(boolean createMode) {
        this.createMode = createMode;
        id.setDisable(createMode);
    }

    public void populateFields(FlightModel flight) {
        if (flight != null) {
            id.setText(String.valueOf(flight.getFlight_id()));
            flight_number.setText(flight.getFlightNumber());
            origin.setText(flight.getOrigin());
            destination.setText(flight.getDestination());
            airline.setText(flight.getAirline());
            status.setValue(flight.getStatus());
            price.setText(String.valueOf(flight.getPrice()));
            depart.setValue(flight.getDepartureDate().toLocalDate());
            arrive.setValue(flight.getReturnDate().toLocalDate());
            capacity.setText(String.valueOf(flight.getCapacity()));
        }
    }

    public FlightModel getFlightData() {
        return flightData;
    }

    private FlightModel createFlightModel() {
        FlightModel flight = new FlightModel();
        if (!id.getText().isEmpty() && !createMode) {
            flight.setFlight_id(Integer.parseInt(id.getText()));
        }
        flight.setFlightNumber(flight_number.getText());
        flight.setOrigin(origin.getText());
        flight.setDestination(destination.getText());
        flight.setAirline(airline.getText());
        flight.setStatus(status.getValue());
        flight.setPrice(Double.parseDouble(price.getText()));
        flight.setDepartureDate(Date.valueOf(depart.getValue()));
        flight.setReturnDate(Date.valueOf(arrive.getValue()));
        flight.setCapacity(Integer.parseInt(capacity.getText()));
        flight.setClassType("Economy");
        return flight;
    }

    private boolean validateInputs() {
        if (flight_number.getText().isEmpty() || origin.getText().isEmpty() ||
                destination.getText().isEmpty() || airline.getText().isEmpty() ||
                status.getValue() == null || price.getText().isEmpty() ||
                depart.getValue() == null || arrive.getValue() == null ||
                capacity.getText().isEmpty()) {

            showAlert("Validation Error", "Please fill in all fields");
            return false;
        }

        try {
            Double.parseDouble(price.getText());
            Integer.parseInt(capacity.getText());
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Price and Capacity must be valid numbers");
            return false;
        }

        if (arrive.getValue().isBefore(depart.getValue())) {
            showAlert("Validation Error", "Return date must be after departure date");
            return false;
        }

        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) confirm.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}