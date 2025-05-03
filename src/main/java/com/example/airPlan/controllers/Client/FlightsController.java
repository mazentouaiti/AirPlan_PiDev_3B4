package com.example.airPlan.controllers.Client;

import com.example.airPlan.Services.FlightServices;
import com.example.airPlan.models.FlightModel;
import com.example.airPlan.views.FlightCellFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FlightsController implements Initializable {

    // UI Components
    @FXML private TextField depart_field;
    @FXML private TextField destin_field;
    @FXML private DatePicker depart_date;
    @FXML private ComboBox<String> combo_price;
    @FXML private Button search_btn;
    @FXML private ListView<FlightModel> flights_listview;


    // Business Logic Components
    private FlightServices flightServices;
    private ObservableList<FlightModel> flightsList;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize services
            flightServices = new FlightServices();

            // Setup UI components
            initializePriceFilter();
            initializeFlightListView();
            setupSearchHandler();

            // Load initial data
            refreshFlightData();
            startStatusUpdateScheduler();
        } catch (Exception e) {
            showErrorAlert("Initialization Error", "Failed to initialize: " + e.getMessage());
        }
    }

    private void startStatusUpdateScheduler() {
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                try {
                    flightServices.updateAllFlightStatuses();
                    refreshFlightData(); // Refresh the view
                } catch (Exception e) {
                    System.err.println("Status update failed: " + e.getMessage());
                }
            });
        }, 0, 1, TimeUnit.HOURS); // Check every hour
    }

    private void initializePriceFilter() {
        combo_price.getItems().addAll("All", "Under 100€", "100–300€", "Above 300€");
        combo_price.setValue("All");
    }

    private void initializeFlightListView() {
        flights_listview.setCellFactory(listView -> new FlightCellFactory());
    }

    private void setupSearchHandler() {
        search_btn.setOnAction(event -> searchFlights());
    }


    private void refreshFlightData() {
        try {
            List<FlightModel> approvedFlights = flightServices.getAllFlights().stream()
                    .filter(flight -> "approved".equals(flight.getAdminStatus()))
                    .toList();

            flightsList = FXCollections.observableArrayList(approvedFlights);
            flights_listview.setItems(flightsList);
        } catch (Exception e) {
            showErrorAlert("Data Load Error", "Failed to load flights: " + e.getMessage());
        }
    }

    private void searchFlights() {
        try {
            String departure = depart_field.getText().trim();
            String destination = destin_field.getText().trim();
            LocalDate departureDate = depart_date.getValue();
            String priceFilter = combo_price.getValue();

            List<FlightModel> filteredFlights = flightServices.searchFlights(
                            departure.isEmpty() ? null : departure,
                            destination.isEmpty() ? null : destination,
                            departureDate != null ? Date.valueOf(departureDate) : null,
                            priceFilter
                    ).stream()
                    .filter(flight -> "approved".equals(flight.getAdminStatus()))
                    .toList();

            if (filteredFlights.isEmpty()) {
                showInformationAlert("No Results", "No matching flights found");
            }

            flightsList = FXCollections.observableArrayList(filteredFlights);
            flights_listview.setItems(flightsList);
        } catch (Exception e) {
            showErrorAlert("Search Error", "Failed to search flights: " + e.getMessage());
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