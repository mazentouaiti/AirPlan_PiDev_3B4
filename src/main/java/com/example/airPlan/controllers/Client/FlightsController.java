package com.example.airPlan.controllers.Client;

import com.example.airPlan.App;
import com.example.airPlan.Services.FlightServices;
import com.example.airPlan.Services.InvoiceService;
import com.example.airPlan.models.FlightModel;
import com.example.airPlan.controllers.Client.ReservedFlight;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.LocalDateStringConverter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.web.WebEngine;
import java.awt.Desktop;

public class FlightsController implements Initializable {

    @FXML
    public AnchorPane mainContent;
    @FXML private TextField depart_field;
    @FXML private TextField destin_field;
    @FXML private DatePicker depart_date;
    @FXML private ComboBox<String> combo_price;
    @FXML private Button search_btn;
    @FXML private ListView<FlightModel> flights_listview;
    @FXML private WebView mapWebView;


    private FlightServices flightServices;
    private ObservableList<FlightModel> flightsList;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private FlightModel selectedFlight;
    private WebEngine webEngine;
    private ObservableList<ReservedFlight> reservedFlightsListItems = FXCollections.observableArrayList();


    @FXML    private TextField resv_dest_field;
    @FXML    private ComboBox<String> resv_classcombo;
    @FXML    private Label reservationTitle;
    @FXML    private Spinner<Integer> resv_passenger_number;
    @FXML    private TextField resv_num_field;
    @FXML    private Button resv_cancel_btn;
    @FXML    private Button resv_confirm_btn;
    @FXML    private TextField resv_autoprice;
    @FXML    private AnchorPane reservationPanel;
    @FXML    private TextField resv_depart_field;
    @FXML    private Button closeReservationBtn;
    @FXML    private TextField resv_origin_field;
    @FXML
    private Button reservedFlights_btn;
    @FXML
    private ListView reservedFlightsList;
    @FXML
    private AnchorPane reservedFlights;
    @FXML
    private Label counter;
    @FXML
    private Label priceLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        webEngine = mapWebView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

        // Load empty map initially
        webEngine.load("https://www.google.com/maps");
        //loadMap();
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
        reservedFlightsList.setItems(reservedFlightsListItems);
        initializeReservedFlightsListView();
        reservedFlights_btn.setOnAction(event -> toggleReservedFlightsPanel());
        reservedFlights.setTranslateX(600);
        counter.setText("0");
        counter.setStyle("-fx-background-color: red; -fx-background-radius: 50; -fx-pref-width: 25; -fx-pref-height: 25; -fx-alignment: center;");
        reservedFlightsListItems.addListener((ListChangeListener<ReservedFlight>) change -> {
            Platform.runLater(() -> {
                updatePassengerCounter();
                updateTotalPriceDisplay();
            });
        });
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
        Label loadingLabel = new Label("Loading flights...");
        flights_listview.setPlaceholder(loadingLabel);
        search_btn.setDisable(true);

        try {
            List<FlightModel> flights = flightServices.getAllFlights();
            flightsList = FXCollections.observableArrayList(
                    flights.stream()
                            .filter(flight -> "approved".equals(flight.getAdminStatus()))
                            .toList()
            );
            flights_listview.setItems(flightsList);

        } catch (Exception e) {
            Button retryButton = new Button("Retry");
            retryButton.setOnAction(event -> refreshFlightData());
            VBox errorBox = new VBox(new Label("Failed to load flights: " + e.getMessage()), retryButton);
            errorBox.setSpacing(10);
            errorBox.setAlignment(Pos.CENTER);
            flights_listview.setPlaceholder(errorBox);
            showErrorAlert("Database Error", "Failed to load flights: " + e.getMessage());
        } finally {
            search_btn.setDisable(false);
        }
    }
    private boolean isValidAirportCode(String input) {
        return true;
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
                                    (departureDate == null || isSameDate(flight.getDepartureDate(), departureDate)) &&
                                    checkPriceFilter(flight.getPrice(), priceFilter) &&
                                    "approved".equals(flight.getAdminStatus())
                    )
                    .toList();

            flights_listview.setItems(FXCollections.observableArrayList(filteredFlights));

            // Always update map with destination (even if empty)
            updateMap(destination);

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
    private double calculateTotalPrice() {
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
            resv_autoprice.setText(String.format("€%.2f", total)); // Update UI
            return total; // Return the calculated value
        }
        return 0.0; // Default if no flight selected
    }
    private void confirmReservation() {
        if (selectedFlight == null) return;

        // Get the calculated total price
        double total = calculateTotalPrice();
        int passengers = resv_passenger_number.getValue();
        String classType = resv_classcombo.getValue();

        // Create reservation without payment
        ReservedFlight reservedFlight = new ReservedFlight(selectedFlight, passengers, classType, total);
        reservedFlightsListItems.add(reservedFlight);

        Alert invoiceConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        invoiceConfirmation.setTitle("Reservation Added");
        invoiceConfirmation.setContentText("Reservation added to your cart. Do you want to view the invoice now?");
        invoiceConfirmation.setHeaderText("Note: Payment is pending until you complete the reservation");

        Optional<ButtonType> result = invoiceConfirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showInvoiceInWebView(reservedFlight);
        }

        hideReservationPanel();
        updateTotalPriceDisplay();
    }

    private void loadInExternalBrowser(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            showErrorAlert("Browser Error", "Failed to open browser: " + e.getMessage());
        }
    }
    @FXML
    public void loadMap() {
        // Load empty Google Maps in WebView
        webEngine.load("https://www.google.com/maps");
    }
    private void updateMap(String destination) {
        try {
            if (!destination.isEmpty()) {
                // Proper URL encoding and Google Maps search format
                String encodedDest = java.net.URLEncoder.encode(destination, "UTF-8");
                String url = "https://www.google.com/maps/search/?api=1&query=" + encodedDest;
                webEngine.load(url);
            } else {
                // Load empty map if no destination
                webEngine.load("https://www.google.com/maps");
            }
        } catch (Exception e) {
            showErrorAlert("Map Error", "Failed to search destination: " + e.getMessage());
        }
    }
    private String encodeURIComponent(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s.replace(" ", "+");
        }
    }
    private void initializeReservedFlightsListView() {
        reservedFlightsList.setCellFactory(listView -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/FlightCell.fxml"));
                Node cell = loader.load();
                FlightCellController cellController = loader.getController();
                cellController.setMainController(this);
                return new ListCell<ReservedFlight>() {
                    @Override
                    protected void updateItem(ReservedFlight reservedFlight, boolean empty) {
                        super.updateItem(reservedFlight, empty);
                        if (empty || reservedFlight == null) {
                            setGraphic(null);
                        } else {
                            // Set the flight
                            cellController.setFlight(reservedFlight.getFlight());
                            // Update the priceLabel directly
                            priceLabel.setText(String.format("€%.2f", reservedFlight.getTotalPrice()));
                            cellController.setCancelButtonBehavior(() -> {
                                reservedFlightsListItems.remove(reservedFlight);
                                // Update the counter when a reservation is canceled
                                updatePassengerCounter();
                                // Clear or update priceLabel when canceled
                                updateTotalPriceDisplay();
                            });
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
    private void updatePassengerCounter() {
        int totalPassengers = reservedFlightsListItems.stream()
                .mapToInt(ReservedFlight::getPassengers)
                .sum();
        counter.setText(String.valueOf(totalPassengers));
    }
    private void updateTotalPriceDisplay() {
        if (reservedFlightsListItems.isEmpty()) {
            priceLabel.setText("€0.00"); // Reset when no reservations
        } else {
            // Calculate and display the sum of all reserved flight prices
            double total = reservedFlightsListItems.stream()
                    .mapToDouble(ReservedFlight::getTotalPrice)
                    .sum();
            priceLabel.setText(String.format("€%.2f", total));
        }
    }
    public void setPrice(double price) {
        // Assuming you have a priceLabel in your FlightCellController
        priceLabel.setText(String.format("€%.2f", price));
    }
    @FXML
    private void toggleReservedFlightsPanel() {
        if (reservedFlights.getTranslateX() == -600) {
            hideReservedFlightsPanel();
        } else {
            showReservedFlightsPanel();
        }
    }
    private void showReservedFlightsPanel() {
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), reservedFlights);
        slideIn.setToX(-600); // Adjust based on layout
        slideIn.play();
    }
    @FXML
    private void hideReservedFlightsPanel() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), reservedFlights);
        slideOut.setToX(600);
        slideOut.play();
    }
    // Update the method signature to accept the parameter
    private void generateAndShowInvoice(ReservedFlight reservedFlight) {
        showInvoiceInWebView(reservedFlight);
    }
    private void displayInvoice(String htmlContent) throws Exception {
        // Validate HTML content
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            throw new Exception("Generated invoice content is empty");
        }

        // Create temp file
        File tempFile;
        try {
            tempFile = File.createTempFile("invoice_", ".html");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new Exception("Could not create temporary file: " + e.getMessage(), e);
        }

        // Write content
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(htmlContent);
        } catch (IOException e) {
            throw new Exception("Could not write invoice to file: " + e.getMessage(), e);
        }

        // Display in browser
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(tempFile.toURI());
            } else {
                HostServices hostServices = App.getHostServicesInstance();
                if (hostServices != null) {
                    hostServices.showDocument(tempFile.toURI().toString());
                } else {
                    throw new Exception("No available method to display the invoice");
                }
            }
        } catch (Exception e) {
            throw new Exception("Could not display invoice: " + e.getMessage(), e);
        }
    }

    private String generateInvoiceContent(ReservedFlight reservedFlight) throws Exception {
        InvoiceService invoiceService = new InvoiceService();
        String clientName = getClientName();

        try {
            if (reservedFlight != null) {
                return invoiceService.generateFlightInvoice(
                        Objects.requireNonNull(reservedFlight, "Reservation cannot be null"),
                        Objects.requireNonNull(clientName, "Client name cannot be null")
                );
            } else {
                if (reservedFlightsListItems == null || reservedFlightsListItems.isEmpty()) {
                    throw new IllegalStateException("No flights reserved");
                }
                return invoiceService.generateMultiFlightInvoice(
                        new ArrayList<>(reservedFlightsListItems),
                        Objects.requireNonNull(clientName, "Client name cannot be null")
                );
            }
        } catch (Exception e) {
            throw new Exception("Failed to generate invoice content: " + e.getMessage(), e);
        }
    }
    private String getClientName() {
        // Implement proper client name lookup
        return "Client Name"; // Replace with actual implementation
    }

    @FXML
    private void handleReserveAll() {
        if (reservedFlightsListItems.isEmpty()) {
            showErrorAlert("No Flights", "Please add flights to reserve first.");
            return;
        }

        double totalPrice = reservedFlightsListItems.stream()
                .mapToDouble(ReservedFlight::getTotalPrice)
                .sum();

        // Ask for payment first
        boolean paymentSuccess = showPaymentDialog(totalPrice);
        if (!paymentSuccess) {
            showErrorAlert("Payment Failed", "Reservation canceled due to payment issue.");
            return;
        }

        // Mark all reservations as paid
        reservedFlightsListItems.forEach(ReservedFlight::markAsPaid);

        Alert invoiceConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
        invoiceConfirmation.setTitle("Reservation Complete");
        invoiceConfirmation.setHeaderText("Payment successful! All flights reserved.");
        invoiceConfirmation.setContentText("Do you want to view the invoice now?");

        Optional<ButtonType> result = invoiceConfirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showInvoiceInWebView(null); // Show multi-flight invoice
        }
    }    // New method for handling all reservations
    private void generateAndShowInvoiceForAll() {
        showInvoiceInWebView(null); // Null indicates multi-flight invoice
    }




    private void showHtmlInvoice(String htmlContent) throws Exception {
        // Create temp file with proper permissions
        File tempFile = File.createTempFile("invoice_template", ".html");
        tempFile.setReadable(true, false);
        tempFile.setWritable(true, false);

        // Write content with proper encoding
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
            writer.write(htmlContent);
            writer.flush();
        }

        // Try desktop browser first
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(tempFile.toURI());
        }
        // Fallback to HostServices
        else {
            HostServices hostServices = App.getHostServicesInstance();
            if (hostServices != null) {
                hostServices.showDocument(tempFile.toURI().toString());
            } else {
                // Final fallback - show in WebView
                Platform.runLater(() -> {
                    WebView webView = new WebView();
                    webView.getEngine().loadContent(htmlContent);

                    Stage stage = new Stage();
                    stage.setScene(new Scene(webView));
                    stage.setTitle("Flight Invoice");
                    stage.show();
                });
            }
        }

        // Schedule file deletion when JVM exits
        tempFile.deleteOnExit();
    }
    private void showInvoiceInWebView(ReservedFlight reservedFlight) {
        try {
            // Validate input
            if (reservedFlight == null && (reservedFlightsListItems == null || reservedFlightsListItems.isEmpty())) {
                throw new IllegalArgumentException("No flight data available for invoice");
            }

            // Generate HTML content
            String htmlContent = generateInvoiceContent(reservedFlight);

            // Verify HTML content was generated
            if (htmlContent == null || htmlContent.trim().isEmpty()) {
                throw new IllegalStateException("Generated invoice content is empty");
            }

            // Create and show invoice window
            Platform.runLater(() -> {
                try {
                    createInvoiceWindow(htmlContent, getWindowTitle(reservedFlight));
                } catch (Exception e) {
                    showErrorAlert("Display Error", "Failed to show invoice: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            Platform.runLater(() ->
                    showErrorAlert("Invoice Error", "Failed to generate invoice: " + e.getMessage()));
        }
    }
    private String getWindowTitle(ReservedFlight reservedFlight) {
        if (reservedFlight != null) {
            return "Invoice - Flight " + reservedFlight.getFlight().getFlightNumber();
        } else if (reservedFlightsListItems != null && !reservedFlightsListItems.isEmpty()) {
            return "Invoice - " + reservedFlightsListItems.size() + " Flights";
        }
        return "Flight Invoice";
    }
    private void createInvoiceWindow(String htmlContent, String title) {
        // Create WebView and configure it
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Set preferred size
        webView.setPrefSize(900, 700);
        webView.setContextMenuEnabled(true);

        // Create the window
        Stage invoiceWindow = new Stage();
        invoiceWindow.setTitle(title);

        // Add print button
        Button printBtn = new Button("Print Invoice");
        printBtn.setStyle("-fx-font-weight: bold; -fx-padding: 8 15;");
        printBtn.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(invoiceWindow)) {
                webEngine.print(job);
                job.endJob();
            }
        });

        // Create layout
        BorderPane root = new BorderPane();
        root.setCenter(webView);

        ToolBar toolbar = new ToolBar(printBtn);
        toolbar.setStyle("-fx-padding: 10; -fx-alignment: center-right;");
        root.setBottom(toolbar);

        // Load content and show window
        webEngine.loadContent(htmlContent);
        invoiceWindow.setScene(new Scene(root));
        invoiceWindow.show();
    }

    private boolean showPaymentDialog(double amount) {
        // Create a custom payment dialog
        Dialog<ButtonType> paymentDialog = new Dialog<>();
        paymentDialog.setTitle("Payment Required");
        paymentDialog.setHeaderText("Complete Payment to Confirm Reservation");
        paymentDialog.setContentText(String.format("Total Amount: €%.2f", amount));

        // Payment options
        ButtonType creditCardBtn = new ButtonType("Credit Card", ButtonBar.ButtonData.OK_DONE);
        ButtonType paypalBtn = new ButtonType("PayPal", ButtonBar.ButtonData.OTHER);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        paymentDialog.getDialogPane().getButtonTypes().addAll(creditCardBtn, paypalBtn, cancelBtn);

        // Simulate payment processing (replace with real API calls if needed)
        Optional<ButtonType> result = paymentDialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == creditCardBtn || result.get() == paypalBtn) {
                // Simulate payment success (replace with actual payment logic)
                showInformationAlert("Payment Success", "Payment processed successfully!");
                return true;
            }
        }
        return false; // Payment failed or was canceled
    }



}

