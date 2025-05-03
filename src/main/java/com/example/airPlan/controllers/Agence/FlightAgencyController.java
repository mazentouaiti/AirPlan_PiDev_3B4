package com.example.airPlan.controllers.Agence;

import com.example.airPlan.Services.FlightServices;
import com.example.airPlan.models.FlightModel;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FlightAgencyController implements Initializable {



    @FXML private Button create_btn;
    @FXML private ListView<FlightModel> listview_flights;

    private final FlightServices flightService = new FlightServices();
    private boolean isCreateMode;
    private FlightModel currentFlight;


    @FXML
    private TextField search_field;
    @FXML
    private TextField airlineField;
    @FXML
    private TextField originField;
    @FXML
    private DatePicker arrivalDatePicker;
    @FXML
    private Button closePanelBtn;
    @FXML
    private TextField capacityField;
    @FXML
    private Button confirmBtn;
    @FXML
    private TextField flightNumberField;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private AnchorPane mainContent;
    @FXML
    private Label panelTitle;
    @FXML
    private DatePicker departureDatePicker;
    @FXML
    private AnchorPane managementPanel;
    @FXML
    private TextField destinationField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setupButtonActions();
        setupSearchListener();
        managementPanel.setTranslateX(managementPanel.getWidth());
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Scheduled", "Delayed", "Cancelled", "Boarding", "Landed"
        ));
        setupListView();
        loadFlights();
    }
    @FXML
    private void handleCreateFlight() {
        isCreateMode = true;
        panelTitle.setText("Create New Flight");
        clearForm();
        showManagementPanel();
    }
    public void handleUpdateFlight(FlightModel flight) {
        isCreateMode = false;
        panelTitle.setText("Update Flight");
        currentFlight = flight;
        populateForm(flight);
        showManagementPanel();
    }
    @FXML
    private void handleConfirmAction() {
        if (validateInputs()) {
            FlightModel flight = isCreateMode ? new FlightModel() : currentFlight;

            // Set flight properties from form
            flight.setFlightNumber(flightNumberField.getText());
            flight.setOrigin(originField.getText());
            flight.setDestination(destinationField.getText());
            flight.setAirline(airlineField.getText());
            flight.setStatus(statusComboBox.getValue());
            flight.setPrice(Double.parseDouble(priceField.getText()));
            flight.setCapacity(Integer.parseInt(capacityField.getText()));
            flight.setDepartureDate(Date.valueOf(departureDatePicker.getValue()));
            flight.setReturnDate(Date.valueOf(arrivalDatePicker.getValue()));

            if (isCreateMode) {
                flightService.addFlight(flight);
                showAlert("Success", "Flight created successfully!");
            } else {
                flightService.updateFlight(flight);
                showAlert("Success", "Flight updated successfully!");
            }

            loadFlights();
            hideManagementPanel();
        }
    }
    private void showManagementPanel() {
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), managementPanel);
        slideIn.setToX(-managementPanel.getWidth());
        slideIn.play();

        // Dim main content
        mainContent.setDisable(true);
        mainContent.setEffect(new BoxBlur(3, 3, 2));
    }

    @FXML
    private void hideManagementPanel() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), managementPanel);
        slideOut.setToX(managementPanel.getWidth());
        slideOut.play();
        mainContent.setDisable(false);
        mainContent.setEffect(null);
    }
    private void populateForm(FlightModel flight) {
        flightNumberField.setText(flight.getFlightNumber());
        originField.setText(flight.getOrigin());
        destinationField.setText(flight.getDestination());
        airlineField.setText(flight.getAirline());
        statusComboBox.setValue(flight.getStatus());
        priceField.setText(String.valueOf(flight.getPrice()));
        capacityField.setText(String.valueOf(flight.getCapacity()));
        departureDatePicker.setValue(flight.getDepartureDate().toLocalDate());
        arrivalDatePicker.setValue(flight.getReturnDate().toLocalDate());
    }
    private void clearForm() {
        flightNumberField.clear();
        originField.clear();
        destinationField.clear();
        airlineField.clear();
        statusComboBox.getSelectionModel().clearSelection();
        priceField.clear();
        capacityField.clear();
        departureDatePicker.setValue(null);
        arrivalDatePicker.setValue(null);
        currentFlight = null;
    }

    private boolean validateInputs() {
        // Add your validation logic here
        return true;
    }
    private void setupSearchListener() {
        search_field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                loadFlights(); // Reset to all flights when search is empty
            } else {
                searchFlights(newValue.trim());
            }
        });
    }
    private void searchFlights(String searchTerm) {
        List<FlightModel> allFlights = flightService.getAllFlights();
        ObservableList<FlightModel> filteredFlights = FXCollections.observableArrayList();
        String lowerSearchTerm = searchTerm.toLowerCase();
        for (FlightModel flight : allFlights) {
            if (flight.getFlightNumber() != null && flight.getFlightNumber().toLowerCase().contains(lowerSearchTerm) ||
                    flight.getAirline() != null && flight.getAirline().toLowerCase().contains(lowerSearchTerm) ||
                    flight.getOrigin() != null && flight.getOrigin().toLowerCase().contains(lowerSearchTerm) ||
                    flight.getDestination() != null && flight.getDestination().toLowerCase().contains(lowerSearchTerm) ||
                    String.format("$%.2f", flight.getPrice()).toLowerCase().contains(lowerSearchTerm) ||
                    String.valueOf(flight.getCapacity()).contains(searchTerm)) { // No toLowerCase for numbers

                filteredFlights.add(flight);
            }
        }
        listview_flights.setItems(filteredFlights);
        if (filteredFlights.isEmpty()) {
            listview_flights.setPlaceholder(new Label("No flights found matching: " + searchTerm));
        } else {
            listview_flights.setPlaceholder(null);
        }
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
        create_btn.setOnAction(event -> handleCreateFlight());

    }

    @FXML
    public void loadFlights() {
        List<FlightModel> flightList = flightService.getAllFlights();
        ObservableList<FlightModel> observableList = FXCollections.observableArrayList(flightList);
        listview_flights.setItems(observableList);
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