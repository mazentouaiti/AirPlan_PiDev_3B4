package com.example.airPlan.controllers.Admin;

import com.example.airPlan.Services.FlightServices;
import com.example.airPlan.models.FlightModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class FlightAdminCellController implements Initializable {

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
    private Button accept_btn;
    @FXML
    private Button refuse_btn;
    @FXML
    private Label admin_status;

    private FlightModel currentFlight;
    private FlightAdminController mainController;

    public void setMainController(FlightAdminController mainController) {
        this.mainController = mainController;
    }

    public void setFlightData(FlightModel flight) {
        this.currentFlight = flight;
        if (flight != null) {
            number_lbl.setText(flight.getFlightNumber() != null ? flight.getFlightNumber() : "");
            origin_lbl.setText(flight.getOrigin() != null ? flight.getOrigin() : "");
            des_lbl.setText(flight.getDestination() != null ? flight.getDestination() : "");
            airline_lbl.setText(flight.getAirline() != null ? flight.getAirline() : "");
            status_lbl.setText(flight.getStatus() != null ? flight.getStatus() : "");
            price_lbl.setText(String.format("$%.2f", flight.getPrice()));
            capacity_lbl.setText(String.valueOf(flight.getCapacity()));
            admin_status.setText(flight.getAdminStatus() != null ? flight.getAdminStatus() : "pending");
            if ("approved".equals(flight.getAdminStatus()) || "rejected".equals(flight.getAdminStatus())) {
                accept_btn.setVisible(false);
                refuse_btn.setVisible(false);
            } else {
                accept_btn.setVisible(true);
                refuse_btn.setVisible(true);
            }


            if (flight.getDepartureDate() != null) {
                depart_lbl.setText(dateFormat.format(flight.getDepartureDate()));
            }

            if (flight.getReturnDate() != null) {
                arrival_lbl.setText(dateFormat.format(flight.getReturnDate()));
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization code if needed
    }
    @FXML
    private void onAcceptClicked() {
        if (currentFlight != null) {
            FlightServices flightService = new FlightServices();
            flightService.approveFlight(currentFlight.getFlight_id());
            admin_status.setText("approved");
            accept_btn.setVisible(false);
            refuse_btn.setVisible(false);
            mainController.loadFlights();
        }
    }

    @FXML
    private void onRefuseClicked() {
        if (currentFlight != null) {
            FlightServices flightService = new FlightServices();
            flightService.rejectFlight(currentFlight.getFlight_id());
            admin_status.setText("rejected");
            accept_btn.setVisible(false);
            refuse_btn.setVisible(false);
            mainController.loadFlights();
        }
    }
}