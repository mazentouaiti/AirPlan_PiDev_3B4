package com.example.airPlan.controllers.Agence;

import com.example.airPlan.Services.FlightServices;
import com.example.airPlan.models.FlightModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FlightAgencyController implements Initializable {



    @FXML private Button create_btn;
    @FXML private ListView<FlightModel> listview_flights;

    private final FlightServices flightService = new FlightServices();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupListView();
        loadFlights();
        setupButtonActions();
    }

    private void setupListView() {
        listview_flights.setCellFactory(param -> new ListCell<FlightModel>() {
            private FXMLLoader loader;
            private AnchorPane cell;
            private FlightAgencyCellController controller;

            {
                try {
                    loader = new FXMLLoader(getClass().getResource("/Fxml/Agences/FlightAgencyCell.fxml"));
                    cell = loader.load();
                    controller = loader.getController();
                    controller.setMainController(FlightAgencyController.this); // Add this line
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
        });
    }

    private void setupButtonActions() {
        create_btn.setOnAction(event -> createFlight());

    }

    @FXML
    public void loadFlights() {
        List<FlightModel> flightList = flightService.getAllFlights();
        ObservableList<FlightModel> observableList = FXCollections.observableArrayList(flightList);
        listview_flights.setItems(observableList);
    }

    @FXML
    private void createFlight() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Agences/ManageFlights.fxml"));
            AnchorPane root = loader.load();

            ManageFlightsController controller = loader.getController();
            controller.setMode(true); // Create mode

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Create New Flight");
            stage.showAndWait();

            // After the stage is closed, check if flight was created
            FlightModel newFlight = controller.getFlightData();
            if (newFlight != null) {
                flightService.addFlight(newFlight);
                loadFlights();
                showAlert("Success", "Flight created successfully!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open flight creation window");
        }
    }



    public void updateFlight(FlightModel flight) {
        if (flight != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Agences/ManageFlights.fxml"));
                AnchorPane root = loader.load();

                ManageFlightsController controller = loader.getController();
                controller.setMode(false); // Update mode
                controller.populateFields(flight);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Update Flight");
                stage.showAndWait();

                FlightModel updatedFlight = controller.getFlightData();
                if (updatedFlight != null) {
                    updatedFlight.setFlight_id(flight.getFlight_id());
                    flightService.updateFlight(updatedFlight);
                    loadFlights();
                    showAlert("Success", "Flight updated successfully!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Could not open flight update window");
            }
        }
    }

    public void deleteFlight(FlightModel flight) {
        if (flight != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Deletion");
            confirmation.setHeaderText("Delete Flight");
            confirmation.setContentText("Are you sure you want to delete this flight?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                flightService.deleteFlight(flight.getFlight_id());
                loadFlights();
                showAlert("Success", "Flight deleted successfully!");
            }
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