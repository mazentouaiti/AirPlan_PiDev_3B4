package com.example.airPlan.controllers.Admin;

import com.example.airPlan.Services.FlightServices;
import com.example.airPlan.models.FlightModel;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FlightAdminController implements Initializable {



    private final FlightServices flightService = new FlightServices();
    @FXML
    private ChoiceBox<String> flight_status;
    @FXML
    private ListView FlightListView;
    @FXML
    private TextField search_field;
    @FXML
    private Button resetAll_to_pending;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        flight_status.getItems().addAll("All", "Pending", "Approved", "Rejected");
        flight_status.setValue("All");
        flight_status.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            filterFlightsByStatus(newVal);
        });

        // Add search field listener
        search_field.textProperty().addListener((observable, oldValue, newValue) -> {
            searchFlights(newValue);
        });

        // Activate reset button
        resetAll_to_pending.setOnAction(event -> onResetAllClicked());

        setupListView();
        loadFlights();
    }
    private void searchFlights(String searchText) {
        List<FlightModel> allFlights = flightService.getAllFlights();
        ObservableList<FlightModel> filteredFlights = FXCollections.observableArrayList();

        if (searchText == null || searchText.isEmpty()) {
            filterFlightsByStatus(flight_status.getValue());
            return;
        }

        String lowerCaseSearch = searchText.toLowerCase();

        for (FlightModel flight : allFlights) {
            if ((flight.getFlightNumber() != null && flight.getFlightNumber().toLowerCase().contains(lowerCaseSearch)) ||
                    (flight.getOrigin() != null && flight.getOrigin().toLowerCase().contains(lowerCaseSearch)) ||
                    (flight.getDestination() != null && flight.getDestination().toLowerCase().contains(lowerCaseSearch)) ||
                    (flight.getAirline() != null && flight.getAirline().toLowerCase().contains(lowerCaseSearch))) {
                filteredFlights.add(flight);
            }
        }

        FlightListView.setItems(filteredFlights);
    }
    private void filterFlightsByStatus(String status) {
        List<FlightModel> allFlights = flightService.getAllFlights();
        ObservableList<FlightModel> filteredFlights = FXCollections.observableArrayList();

        for (FlightModel flight : allFlights) {
            if ("All".equals(status)) {
                filteredFlights.add(flight);
            } else if ("Pending".equals(status) && ("pending".equals(flight.getAdminStatus()) || flight.getAdminStatus() == null)) {
                filteredFlights.add(flight);
            } else if ("Approved".equals(status) && "approved".equals(flight.getAdminStatus())) {
                filteredFlights.add(flight);
            } else if ("Rejected".equals(status) && "rejected".equals(flight.getAdminStatus())) {
                filteredFlights.add(flight);
            }
        }

        FlightListView.setItems(filteredFlights);
    }

    private FadeTransition createFadeAnimation(Button button) {
        FadeTransition fade = new FadeTransition(Duration.millis(200), button);
        fade.setFromValue(0);
        fade.setToValue(1);
        return fade;
    }


    private FadeTransition createFadeOutAnimation(Button button) {
        FadeTransition fade = new FadeTransition(Duration.millis(150), button);
        fade.setToValue(0);
        return fade;
    }


    private void setupListView() {
        FlightListView.setCellFactory(param -> createFlightCell());
    }
    private ListCell<FlightModel> createFlightCell() {
        return new ListCell<FlightModel>() {
            private FXMLLoader loader;
            private AnchorPane cell;
            private FlightAdminCellController controller;

            {
                try {
                    loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/FlightAdminCell.fxml"));
                    cell = loader.load();
                    controller = loader.getController();
                    controller.setMainController(FlightAdminController.this);
                } catch (IOException e) {
                    e.printStackTrace();
                    setText("Error loading cell template");
                }
            }

            @Override
            protected void updateItem(FlightModel flight, boolean empty) {
                super.updateItem(flight, empty);
                if (empty || flight == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    controller.setFlightData(flight);
                    setGraphic(cell);
                }
            }
        };
    }



    @FXML
    public void loadFlights() {
        filterFlightsByStatus(flight_status.getValue());
    }

    @FXML
    private void onAcceptAllClicked() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Approval");
        confirmation.setHeaderText("Approve all pending flights");
        confirmation.setContentText("Are you sure you want to approve all pending flights?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            flightService.approveAllPendingFlights();
            showAlert("Success", "All pending flights have been approved.");
            loadFlights(); // Refresh the list
        }
    }

    @FXML
    private void onRejectAllClicked() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Rejection");
        confirmation.setHeaderText("Reject all pending flights");
        confirmation.setContentText("Are you sure you want to reject all pending flights?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            flightService.rejectAllPendingFlights();
            showAlert("Success", "All pending flights have been rejected.");
            loadFlights(); // Refresh the list
        }
    }
    @FXML
    private void onResetAllClicked() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Reset All");
        confirmation.setHeaderText("Reset all flight statuses");
        confirmation.setContentText("Are you sure you want to reset ALL approved/rejected flights back to pending?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            flightService.resetAllFlightStatuses();
            showAlert("Success", "All flight statuses have been reset to pending.");
            loadFlights(); // Refresh the list
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}