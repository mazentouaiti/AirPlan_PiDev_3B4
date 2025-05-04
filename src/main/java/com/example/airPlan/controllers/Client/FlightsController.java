package com.example.airPlan.controllers.Client;

import com.example.airPlan.Services.FlightServices;
import com.example.airPlan.models.FlightModel;
import com.example.airPlan.views.FlightCellFactory;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import javafx.util.converter.LocalDateStringConverter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FlightsController implements Initializable {

    @FXML
    public AnchorPane mainContent;
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
    private FlightModel selectedFlight;

    @FXML
    private TextField resv_dest_field;
    @FXML
    private ComboBox<String> resv_classcombo;
    @FXML
    private Label reservationTitle;
    @FXML
    private Spinner<Integer> resv_passenger_number;
    @FXML
    private TextField resv_num_field;
    @FXML
    private Button resv_cancel_btn;
    @FXML
    private Button resv_confirm_btn;
    @FXML
    private TextField resv_autoprice;
    @FXML
    private AnchorPane reservationPanel;
    @FXML
    private TextField resv_depart_field;
    @FXML
    private Button closeReservationBtn;
    @FXML
    private TextField resv_origin_field;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            flightServices = new FlightServices();
            initializePriceFilter();
            initializeFlightListView();
            setupSearchHandler();
            setupAutoSearch();
            refreshFlightData();
            startStatusUpdateScheduler();
        } catch (Exception e) {
            showErrorAlert("Initialization Error", "Failed to initialize: " + e.getMessage());
        }
        resv_classcombo.getItems().addAll("Economy", "Business", "First Class");
        resv_classcombo.setValue("Economy");
        resv_confirm_btn.setOnAction(event -> confirmReservation());
        closeReservationBtn.setOnAction(event -> hideReservationPanel());
        resv_cancel_btn.setOnAction(event -> hideReservationPanel());

        resv_passenger_number.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        resv_classcombo.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        depart_date.setConverter(new LocalDateStringConverter(DateTimeFormatter.ISO_DATE, DateTimeFormatter.ISO_DATE));
        depart_date.setPromptText("AAAA-MM-JJ");

    }
    private void setupAutoSearch() {
        setupDebounce(depart_field);
        setupDebounce(destin_field);
        depart_date.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d{4}-\\d{2}-\\d{2}") && !newVal.isEmpty()) {
                Platform.runLater(() -> {
                    showErrorAlert("Format invalide", "Le format doit être AAAA-MM-JJ");
                    depart_date.setValue(null);
                });
            }
        });
        depart_date.valueProperty().addListener((obs, oldVal, newVal) -> searchFlights());
        combo_price.valueProperty().addListener((obs, oldVal, newVal) -> searchFlights());
    }

    private void setupDebounce(TextField textField) {
        PauseTransition debounce = new PauseTransition(Duration.millis(500));
        debounce.setOnFinished(e -> searchFlights());
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            debounce.playFromStart();
        });
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
        flights_listview.setCellFactory(listView -> {
            try {
                // Load the cell FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/FlightCell.fxml"));
                Node cell = loader.load();

                // Get the cell's controller
                FlightCellController cellController = loader.getController();

                // Pass this main controller to the cell
                cellController.setMainController(this);

                return new ListCell<FlightModel>() {
                    @Override
                    protected void updateItem(FlightModel flight, boolean empty) {
                        super.updateItem(flight, empty);
                        if (empty || flight == null) {
                            setGraphic(null);
                        } else {
                            cellController.setFlight(flight);
                            setGraphic(cell);
                        }
                    }
                };
            } catch (IOException e) {
                e.printStackTrace();
                return new ListCell<>();
            }
        });
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
            String departure = depart_field.getText().trim().toLowerCase();
            String destination = destin_field.getText().trim().toLowerCase();
            LocalDate departureDate = depart_date.getValue();
            String priceFilter = combo_price.getValue();

            if (flightsList == null) refreshFlightData();

            List<FlightModel> filteredFlights = flightsList.stream()
                    .filter(flight ->
                            (departure.isEmpty() || flight.getOrigin().toLowerCase().contains(departure)) &&
                                    (destination.isEmpty() || flight.getDestination().toLowerCase().contains(destination)) &&
                                    (departureDate == null || isSameDate(flight.getDepartureDate(), departureDate)) && // Modification
                                    checkPriceFilter(flight.getPrice(), priceFilter)
                    )
                    .toList();

            flights_listview.setItems(FXCollections.observableArrayList(filteredFlights));

            if (filteredFlights.isEmpty()) {
                showInformationAlert("No Results", "No matching flights found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Search Error", "Failed to search flights: " + e.getMessage());
        }
    }

    private boolean isSameDate(Date flightDate, LocalDate targetDate) {
        if (flightDate == null || targetDate == null) return false;
        Instant instant = Instant.ofEpochMilli(flightDate.getTime());
        LocalDate convertedDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return convertedDate.equals(targetDate);
    }

    private boolean checkPriceFilter(double price, String priceFilter) {
        switch (priceFilter) {
            case "Under 100€":
                return price < 100;
            case "100–300€":
                return price >= 100 && price <= 300;
            case "Above 300€":
                return price > 300;
            default:
                return true;
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

    @FXML
    public void hideReservationPanel() {
        // Slide down animation
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(300), reservationPanel);
        slideDown.setToY(0);
        slideDown.play();

        // Restore main content
        mainContent.setDisable(false);
        mainContent.setEffect(null);
    }


    public void showReservationPanel(FlightModel flight) {
        this.selectedFlight = flight;

        // Populate form
        resv_num_field.setText(flight.getFlightNumber());
        resv_origin_field.setText(flight.getOrigin());
        resv_dest_field.setText(flight.getDestination());
        resv_depart_field.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(flight.getDepartureDate()));

        // Setup passenger spinner
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, flight.getCapacity(), 1);
        resv_passenger_number.setValueFactory(valueFactory);

        calculateTotalPrice();

        // Slide up animation
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), reservationPanel);
        slideUp.setToY(-reservationPanel.getHeight());
        slideUp.play();

        // Dim main content
        mainContent.setDisable(true);
        mainContent.setEffect(new BoxBlur(3, 3, 2));
    }

    private void calculateTotalPrice() {
        if (selectedFlight != null) {
            double basePrice = selectedFlight.getPrice();
            int passengers = resv_passenger_number.getValue();
            String classType = resv_classcombo.getValue();

            double multiplier = switch (classType) {
                case "Business" -> 1.5;
                case "First Class" -> 2.0;
                default -> 1.0;
            };

            double total = basePrice * multiplier * passengers;
            resv_autoprice.setText(String.format("€%.2f", total));
        }
    }

    private void confirmReservation() {
        try {
            if (selectedFlight == null || !"approved".equals(selectedFlight.getAdminStatus())) {
                showErrorAlert("Reservation Error", "This flight is no longer available for booking");
                return;
            }

            int passengers = resv_passenger_number.getValue();
            if (passengers > selectedFlight.getCapacity()) {
                showErrorAlert("Capacity Exceeded",
                        "Only " + selectedFlight.getCapacity() + " seats available");
                return;
            }

            // Add your reservation logic here
            showInformationAlert("Success", "Reservation confirmed successfully!");
            hideReservationPanel();
        } catch (Exception e) {
            showErrorAlert("Reservation Error", "Failed to confirm reservation: " + e.getMessage());
        }
    }

}