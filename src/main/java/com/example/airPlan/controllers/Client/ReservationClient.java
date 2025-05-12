package com.example.airPlan.controllers.Client;

import com.example.airPlan.Utiles.DBConnection;
import com.example.airPlan.models.Hebergement;
import com.example.airPlan.models.Reservation;
import com.example.airPlan.Services.ServiceReservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javafx.embed.swing.SwingFXUtils;
import java.io.File;


public class ReservationClient {
    @FXML
    private ImageView imagehebergement;
    @FXML
    private Label CountryCity;
    @FXML
    private Label nameheber;
    @FXML
    private Label optionheber;
    @FXML
    private Label priceheber;
    @FXML
    private Label typeheber;
    @FXML
    private Label ratingheber;
    @FXML
    private DatePicker departuredate;
    @FXML
    private TextArea requestarea;
    @FXML
    private Spinner childrenspinner;
    @FXML
    private DatePicker arrivaldate;
    @FXML
    private Spinner adultspinner;
    @FXML
    private Spinner roomspinner;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnReserve;
    @FXML
    private TextField nameresField;
    @FXML
    private TextField emailresField;
    private Hebergement currentHebergement;
    private ServiceReservation reservationService;
    private int currentUserId;
    private Node previousView;
    private BorderPane parentContainer;
    public void setPreviousView(Node previousView, BorderPane parentContainer) {
        this.previousView = previousView;
        this.parentContainer = parentContainer;
    }

    @FXML
    public void initialize() {
        reservationService = new ServiceReservation();
        // Adult spinner (minimum 1 adulte)
        SpinnerValueFactory<Integer> adultFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1);
        adultspinner.setValueFactory(adultFactory);

        // Children spinner (minimum 0 enfant)
        SpinnerValueFactory<Integer> childrenFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0);
        childrenspinner.setValueFactory(childrenFactory);

        // Room spinner (minimum 1 chambre)
        SpinnerValueFactory<Integer> roomFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        roomspinner.setValueFactory(roomFactory);
    }
    public void setHebergementData(Hebergement hebergement) {
        this.currentHebergement = hebergement;
        if (hebergement != null) {
            Image img = new Image(hebergement.getPhoto(), 200, 150, true, true);
            imagehebergement.setImage(img);
            CountryCity.setText(hebergement.getCountry() + " , " + hebergement.getCity());
            nameheber.setText(hebergement.getName());
            optionheber.setText(hebergement.getOptions()); // (ou autre champ selon ton modèle)
            priceheber.setText(String.format("%.2f TND", hebergement.getPricePerNight()));
            typeheber.setText(hebergement.getType());
            ratingheber.setText(getStarRating(hebergement.getRating()));

        }
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
    private String getStarRating(double rating) {
        int fullStars = (int) rating;
        boolean halfStar = (rating - fullStars) >= 0.5;

        StringBuilder stars = new StringBuilder();

        for (int i = 0; i < fullStars; i++) {
            stars.append("★"); // étoile pleine
        }

        if (halfStar) {
            stars.append("☆"); // étoile vide pour demi-étoile (ou utilise autre chose si tu veux)
        }

        while (stars.length() < 5) {
            stars.append("☆"); // compléter jusqu'à 5 étoiles
        }

        return stars.toString();

    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        if (parentContainer != null && previousView != null) {
            parentContainer.setCenter(previousView);
        } else {
            // Fallback to window replacement
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Client/client_acc.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnCancel.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    @FXML
    private void handleSubmit(ActionEvent event) throws SQLException {
        if (departuredate.getValue() == null || arrivaldate.getValue() == null) {
            showAlert("Please fill in all required fields.", Alert.AlertType.WARNING);
            return;
        }

        if (!validateForm()) {
            return;
        }

        ajouterReservation();

        //pdf
        String countryCity = CountryCity.getText();
        String hebergementName = nameheber.getText();
        String options = optionheber.getText();
        String price = priceheber.getText();
        String type = typeheber.getText();
        String rating = ratingheber.getText();
        String dateArrival = arrivaldate.getValue().toString();
        String dateDeparture = departuredate.getValue().toString();
        String request = requestarea.getText();
        int nbAdults = (int) adultspinner.getValue();
        int nbChildren = (int) childrenspinner.getValue();
        int nbRooms = (int) roomspinner.getValue();

// Récupérer l’image (enregistrée temporairement pour le PDF si tu veux l’y inclure)
        Image fxImage = imagehebergement.getImage();

        String imagePath = "C:\\Users\\jmaae\\IdeaProjects\\Hotels\\src\\main\\resources\\com\\example\\hotels\\images\\passeport.png";


        StyledPdfGenerator generator = new StyledPdfGenerator();
        generator.generatePdf(
                "ReservationConfirmation.pdf",
                countryCity,
                hebergementName,
                type,
                options,
                price,
                rating,
                dateArrival,
                dateDeparture,
                nbAdults,
                nbChildren,
                nbRooms,
                request,
                imagePath
        );
        if (parentContainer != null && previousView != null) {
            parentContainer.setCenter(previousView);
        }

        //emailll
    /*
        String clientEmail = emailresField.getText();  // champ pour l'email du client
        String clientName = nameresField.getText();  // champ pour le nom du client

        // Appelle la méthode d'envoi d'email pour envoyer une confirmation
        BrevoEmailSender.sendEmail(clientEmail, clientName);

        // Affiche une alerte pour indiquer que la réservation a été effectuée avec succès
        showAlert("Reservation successful! A confirmation email has been sent.", Alert.AlertType.INFORMATION);

    */

    }



    private boolean validateCapacity(int hebergementId, int roomsToReserve) throws SQLException {
        String sql = "SELECT capacity FROM hebergement WHERE acc_id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, hebergementId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int currentCapacity = rs.getInt("capacity");
                return currentCapacity >= roomsToReserve;
            }
            return false;
        }
    }


    private void ajouterReservation() {
        try {
            Reservation reservation = new Reservation();

            reservation.setIdUser(1); // à adapter
            reservation.setIdAcc(currentHebergement.getId());
            reservation.setTypeReservation(currentHebergement.getType());
            reservation.setDestination(currentHebergement.getCountry()+", "+currentHebergement.getCity());
            reservation.setNumberOfRooms((Integer) roomspinner.getValue());
            reservation.setNumberOfAdults((Integer) adultspinner.getValue());
            reservation.setNumberOfChildren((Integer) childrenspinner.getValue());
            reservation.setRequests(requestarea.getText().trim());
            reservation.setArrivalDate(Date.valueOf(arrivaldate.getValue()));
            reservation.setDepartureDate(Date.valueOf(departuredate.getValue()));

            // Calculate prices
            long nights = ChronoUnit.DAYS.between(arrivaldate.getValue(), departuredate.getValue());
            double adultPrice = nights * currentHebergement.getPricePerNight() * reservation.getNumberOfAdults();
            double childPrice = nights * currentHebergement.getPricePerNight() * reservation.getNumberOfChildren() * 0.5; // 50% for children
            double totalPrice = adultPrice + childPrice;

            reservation.setPriceAdultsAcc(adultPrice);
            reservation.setPriceChildrenAcc(childPrice);
            reservation.setTotalPriceAcc(totalPrice);
            reservation.setNameOfReservatedAccommodation(currentHebergement.getName());

            ServiceReservation service = new ServiceReservation();
            service.addReservation(reservation);

            showAlert("Réservation enregistrée avec succès.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur lors de la réservation : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }





    private boolean validateForm() throws SQLException {

        if (currentHebergement == null) {
            showAlert("No accommodation selected.", Alert.AlertType.WARNING);
            return false;
        }
        if (!validateCapacity(currentHebergement.getId(), (Integer) roomspinner.getValue())) {
            showAlert("Not enough capacity available for the selected number of rooms.", Alert.AlertType.WARNING);
            return false;
        }


        String requestText = requestarea.getText().trim();

        // Validate request text with regex
        if (!requestarea.getText().matches("^[A-Za-z0-9\\s.,;:!?()'-]*$")) {
            showAlert("Request must contain only valid characters.", Alert.AlertType.WARNING);
            return false;
        }

        // Validate dates
        if (arrivaldate.getValue() == null || departuredate.getValue() == null) {
            showAlert("Please select both arrival and departure dates.", Alert.AlertType.WARNING);
            return false;
        }

        // int arrivalYear = arrivaldate.getValue().getYear();
        // Get current date
        LocalDate currentDate = LocalDate.now();

// Get arrival and departure dates from your date pickers
        LocalDate arrivalDate = arrivaldate.getValue();
        LocalDate departureDate = departuredate.getValue();

// Validate years (2025 or 2026 only)
        int arrivalYear = arrivalDate.getYear();
        int departureYear = departureDate.getYear();
        if (!((arrivalYear == 2025 || arrivalYear == 2026) && (departureYear == 2025 || departureYear == 2026))) {
            showAlert("Arrival and Departure dates must be in 2025 or 2026.", Alert.AlertType.WARNING);
            return false;
        }

// Validate arrival date is not before current date
        if (arrivalDate.isBefore(currentDate)) {
            showAlert("Arrival date cannot be before today's date.", Alert.AlertType.WARNING);
            return false;
        }

// Validate departure date is after arrival date
        if (departureDate.isBefore(arrivalDate)) {
            showAlert("Departure date must be after arrival date.", Alert.AlertType.WARNING);
            return false;
        }




        // Validate spinners
        if (adultspinner.getValue() == null ) {
            showAlert("At least one adult is required.", Alert.AlertType.WARNING);
            return false;
        }

        if (roomspinner.getValue() == null ) {
            showAlert("At least one room must be selected.", Alert.AlertType.WARNING);
            return false;
        }



        return true; // All validations passed
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
