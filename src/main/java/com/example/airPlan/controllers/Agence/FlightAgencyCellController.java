package com.example.airPlan.controllers.Agence;

import com.example.airPlan.models.FlightModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class FlightAgencyCellController implements Initializable {

    @FXML private Label airline_lbl;
    @FXML private Label origin_lbl;
    @FXML private Label des_lbl;
    @FXML private Label depart_lbl;
    @FXML private Label price_lbl;
    @FXML private Label status_lbl;
    @FXML private Label number_lbl;
    @FXML private Label capacity_lbl;
    @FXML private Label arrival_lbl;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @FXML
    private Button update_btn;
    @FXML
    private Button delete_btn;

    private FlightModel currentFlight;
    private FlightAgencyController mainController;

    public void setMainController(FlightAgencyController mainController) {
        this.mainController = mainController;
    }

    public void setFlightData(FlightModel flight) {
        this.currentFlight = flight; // Add this line to store the flight
        if (flight != null) {
            number_lbl.setText(flight.getFlightNumber() != null ? flight.getFlightNumber() : "");
            origin_lbl.setText(flight.getOrigin() != null ? flight.getOrigin() : "");
            des_lbl.setText(flight.getDestination() != null ? flight.getDestination() : "");
            airline_lbl.setText(flight.getAirline() != null ? flight.getAirline() : "");
            status_lbl.setText(flight.getStatus() != null ? flight.getStatus() : "");
            price_lbl.setText(String.format("$%.2f", flight.getPrice()));
            capacity_lbl.setText(String.valueOf(flight.getCapacity()));

            if (flight.getDepartureDate() != null) {
                depart_lbl.setText(dateFormat.format(flight.getDepartureDate()));
            }

            if (flight.getReturnDate() != null) {
                arrival_lbl.setText(dateFormat.format(flight.getReturnDate()));
            }
        }
    }
    @FXML
    private void onDeleteClicked() {
        if (currentFlight != null && mainController != null) {
            mainController.deleteFlight(currentFlight);
        }
    }

    @FXML
    private void onUpdateClicked() {
        if (currentFlight != null && mainController != null) {
            mainController.handleUpdateFlight(currentFlight);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization code if needed
    }
}