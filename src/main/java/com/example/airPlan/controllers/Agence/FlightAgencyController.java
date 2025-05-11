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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
    @FXML
    private Label statusError;
    @FXML
    private Label flightNumberError;
    @FXML
    private Label capacityError;
    @FXML
    private Label departureDateError;
    @FXML
    private Label priceError;
    @FXML
    private Label arrivalDateError;
    @FXML
    private Label originError;
    @FXML
    private Label destinationError;
    @FXML
    private Label airlineError;

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
        setupValidationListeners();
        flightNumberError.getProperties().put("field", flightNumberField);
        airlineError.getProperties().put("field", airlineField);
        originError.getProperties().put("field", originField);
        destinationError.getProperties().put("field", destinationField);
        departureDateError.getProperties().put("field", departureDatePicker);
        arrivalDateError.getProperties().put("field", arrivalDatePicker);
        priceError.getProperties().put("field", priceField);
        capacityError.getProperties().put("field", capacityField);
        statusError.getProperties().put("field", statusComboBox);

        clearAllErrors();
    }
    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private void setupValidationListeners() {
        // Flight Number validation
        flightNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("[A-Za-z]{0,2}\\d{0,4}")) {
                flightNumberField.setText(oldVal);
            }
            clearError(flightNumberError, flightNumberField);
        });

        // Airline validation - prevent numbers
        airlineField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isNumeric(newVal)) {
                airlineField.setText(oldVal);
            }
            clearError(airlineError, airlineField);
        });

        // Origin validation - prevent numbers
        originField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isNumeric(newVal)) {
                originField.setText(oldVal);
            }
            clearError(originError, originField);
        });

        // Destination validation - prevent numbers
        destinationField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isNumeric(newVal)) {
                destinationField.setText(oldVal);
            }
            clearError(destinationError, destinationField);
        });


        // Price validation
        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                priceField.setText(oldVal);
            }
            clearError(priceError, priceField);
        });

        // Capacity validation
        capacityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                capacityField.setText(oldVal);
            }
            clearError(capacityError, capacityField);
        });

        // Departure Date validation
        departureDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            clearError(departureDateError, departureDatePicker);
            if (newVal != null && arrivalDatePicker.getValue() != null &&
                    newVal.isAfter(arrivalDatePicker.getValue())) {
                showError(arrivalDateError, "Must be after departure", arrivalDatePicker);
            } else {
                clearError(arrivalDateError, arrivalDatePicker);
            }
        });

        // Arrival Date validation
        arrivalDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            clearError(arrivalDateError, arrivalDatePicker);
            if (newVal != null && departureDatePicker.getValue() != null &&
                    newVal.isBefore(departureDatePicker.getValue())) {
                showError(arrivalDateError, "Must be after departure", arrivalDatePicker);
            } else {
                clearError(arrivalDateError, arrivalDatePicker);
            }
        });

        // Status validation
        statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            clearError(statusError, statusComboBox);
        });

        // Add focus listeners to validate when focus is lost
        addFocusValidation(flightNumberField, () -> validateFlightNumber());
        addFocusValidation(airlineField, () -> validateAirline());
        addFocusValidation(originField, () -> validateOrigin());
        addFocusValidation(destinationField, () -> validateDestination());
        addFocusValidation(priceField, () -> validatePrice());
        addFocusValidation(capacityField, () -> validateCapacity());
    }

    private void addFocusValidation(Control field, Runnable validation) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // When focus is lost
                validation.run();
            }
        });
    }

    private boolean validateFlightNumber() {
        if (flightNumberField.getText().isEmpty()) {
            showError(flightNumberError, "Flight number is required", flightNumberField);
            return false;
        } else if (!flightNumberField.getText().matches("[A-Za-z]{2}\\d{3,4}")) {
            showError(flightNumberError, "Format: 2 letters + 3-4 numbers", flightNumberField);
            return false;
        }
        return true;
    }

    private boolean validateAirline() {
        if (airlineField.getText().isEmpty()) {
            showError(airlineError, "Airline is required", airlineField);
            return false;
        }
        return true;
    }

    private boolean validateOrigin() {
        if (originField.getText().isEmpty()) {
            showError(originError, "Origin is required", originField);
            return false;
        } else if (originField.getText().length() < 3) {
            showError(originError, "Minimum 3 characters", originField);
            return false;
        }
        return true;
    }

    private boolean validateDestination() {
        if (destinationField.getText().isEmpty()) {
            showError(destinationError, "Destination is required", destinationField);
            return false;
        } else if (destinationField.getText().length() < 3) {
            showError(destinationError, "Minimum 3 characters", destinationField);
            return false;
        }
        return true;
    }

    private boolean validatePrice() {
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                showError(priceError, "Must be positive", priceField);
                return false;
            }
        } catch (NumberFormatException e) {
            showError(priceError, "Invalid number", priceField);
            return false;
        }
        return true;
    }

    private boolean validateCapacity() {
        try {
            int capacity = Integer.parseInt(capacityField.getText());
            if (capacity <= 0) {
                showError(capacityError, "Must be positive", capacityField);
                return false;
            }
        } catch (NumberFormatException e) {
            showError(capacityError, "Invalid number", capacityField);
            return false;
        }
        return true;
    }

    // Helper methods
    private void showError(Label errorLabel, String message, Node field) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        field.getStyleClass().add("error-field");
    }

    private void clearError(Label errorLabel, Node field) {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        field.getStyleClass().remove("error-field");
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
        boolean isValid = true;

        // Clear all previous errors
        clearAllErrors();

        // Flight Number validation
        if (flightNumberField.getText().isEmpty()) {
            showError(flightNumberError, "Flight number is required");
            isValid = false;
        } else if (!flightNumberField.getText().matches("[A-Za-z]{2}\\d{3,4}")) {
            showError(flightNumberError, "Format: 2 letters + 3-4 numbers (e.g., AB123)");
            isValid = false;
        }

        // Airline validation
        if (airlineField.getText().isEmpty()) {
            showError(airlineError, "Airline is required");
            isValid = false;
        } else if (isNumeric(airlineField.getText())) {
            showError(airlineError, "Airline cannot be a number");
            isValid = false;
        }

        // Origin validation
        if (originField.getText().isEmpty()) {
            showError(originError, "Origin is required");
            isValid = false;
        } else if (originField.getText().length() < 3) {
            showError(originError, "Minimum 3 characters");
            isValid = false;
        } else if (isNumeric(originField.getText())) {
            showError(originError, "Origin cannot be a number");
            isValid = false;
        }

        // Destination validation
        if (destinationField.getText().isEmpty()) {
            showError(destinationError, "Destination is required");
            isValid = false;
        } else if (destinationField.getText().length() < 3) {
            showError(destinationError, "Minimum 3 characters");
            isValid = false;
        } else if (isNumeric(destinationField.getText())) {
            showError(destinationError, "Destination cannot be a number");
            isValid = false;
        }

        // Date validation
        if (departureDatePicker.getValue() == null) {
            showError(departureDateError, "Departure date is required");
            isValid = false;
        }

        if (arrivalDatePicker.getValue() == null) {
            showError(arrivalDateError, "Arrival date is required");
            isValid = false;
        } else if (departureDatePicker.getValue() != null &&
                arrivalDatePicker.getValue().isBefore(departureDatePicker.getValue())) {
            showError(arrivalDateError, "Must be after departure");
            isValid = false;
        }

        // Price validation
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                showError(priceError, "Must be positive");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            showError(priceError, "Invalid number");
            isValid = false;
        }

        // Capacity validation
        try {
            int capacity = Integer.parseInt(capacityField.getText());
            if (capacity <= 0) {
                showError(capacityError, "Must be positive");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            showError(capacityError, "Invalid number");
            isValid = false;
        }

        // Status validation
        if (statusComboBox.getValue() == null) {
            showError(statusError, "Status is required");
            isValid = false;
        }

        return isValid;
    }

    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        // Get the parent node to apply error style (assuming it's a Pane)
        Node field = (Node) errorLabel.getProperties().get("field");
        if (field != null) {
            field.getStyleClass().add("error-field");
        }
    }

    private void clearAllErrors() {
        flightNumberError.setText("");
        flightNumberError.setVisible(false);
        airlineError.setText("");
        airlineError.setVisible(false);
        originError.setText("");
        originError.setVisible(false);
        destinationError.setText("");
        destinationError.setVisible(false);
        departureDateError.setText("");
        departureDateError.setVisible(false);
        arrivalDateError.setText("");
        arrivalDateError.setVisible(false);
        priceError.setText("");
        priceError.setVisible(false);
        capacityError.setText("");
        capacityError.setVisible(false);
        statusError.setText("");
        statusError.setVisible(false);

        // Remove error styling from all fields
        flightNumberField.getStyleClass().remove("error-field");
        airlineField.getStyleClass().remove("error-field");
        originField.getStyleClass().remove("error-field");
        destinationField.getStyleClass().remove("error-field");
        departureDatePicker.getStyleClass().remove("error-field");
        arrivalDatePicker.getStyleClass().remove("error-field");
        priceField.getStyleClass().remove("error-field");
        capacityField.getStyleClass().remove("error-field");
        statusComboBox.getStyleClass().remove("error-field");
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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);

        // Create a TextArea for better error message display
        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setContent(expContent);
        alert.showAndWait();
    }


}