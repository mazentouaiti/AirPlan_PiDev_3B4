package com.example.airPlan.controllers.Agence;

import com.example.airPlan.models.Hebergement;
import com.example.airPlan.Services.ServiceHebergement;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HotelAdd {

    @FXML private TextField namefield;
    @FXML private TextField cityfield;
    @FXML private TextField addressfield;
    @FXML private TextField countryfield;
    @FXML private TextField pricefield;
    @FXML private Label albumlabel;
    @FXML private TextArea descriptionfield;
    @FXML private HBox getStarBox;
    @FXML private Spinner<Integer> capacityspinner;
    @FXML private CheckBox wifi, pool, meals, air, parking;
    @FXML private ComboBox<String> typeCombo;
    @FXML private Label fileLabel;
    @FXML private HBox starBox;
    @FXML private Stage stage;
    @FXML private Scene scene;

    // Error Labels
    @FXML private Label errorName;
    @FXML private Label errorType;
    @FXML private Label errorCity;
    @FXML private Label errorAddress;
    @FXML private Label errorCountry;
    @FXML private Label errorPrice;
    @FXML private Label errorDescription;
    @FXML private Label errorPhoto;
    @FXML private Label errorAlbum;
    @FXML private Label errorRating;
    @FXML private Label errorCapacity;

    private File selectedFile;
    private final IntegerProperty rating = new SimpleIntegerProperty(0);
    private List<String> selectedOptions = new ArrayList<>();
    private Hebergement hebergementToEdit;
    boolean disponibility;

    @FXML
    public void initialize() {
        // Initialize spinner
        SpinnerValueFactory<Integer> capacityFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 5000, 0);
        capacityspinner.setValueFactory(capacityFactory);

        // Initialize type combo box
        typeCombo.getItems().addAll("Hotel", "House", "Apartment", "Villa", "Hostel", "Bungalow");

        // Setup option checkboxes
        setupOptionCheckboxes();

        // Setup stars rating
        setupStarRating();

        // Setup real-time validation for text fields
        setupFieldValidations();
    }

    private void setupStarRating() {
        int maxStars = 5;
        Label[] stars = new Label[maxStars];

        for (int i = 0; i < maxStars; i++) {
            final int index = i;
            Label star = new Label("☆");
            star.setStyle("-fx-font-size: 24px; -fx-text-fill: #f39c12; -fx-cursor: hand;");
            star.setOnMouseClicked(e -> {
                rating.set(index + 1);
                validateRating();
            });
            stars[i] = star;
            starBox.getChildren().add(star);
        }

        rating.addListener((obs, oldVal, newVal) -> {
            for (int i = 0; i < maxStars; i++) {
                stars[i].setText(i < newVal.intValue() ? "★" : "☆");
            }
            validateRating();
        });
    }

    private void setupFieldValidations() {
        // Name validation - letters only, min 3 chars
        namefield.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^[A-Za-z\\s]{3,}$") && !newVal.isEmpty()) {
                namefield.setStyle("-fx-border-color: red;");
                errorName.setText("Name must contain only letters and have at least 3 characters");
                errorName.setTextFill(Color.RED);
            } else {
                namefield.setStyle("");
                errorName.setText("");
            }
        });

        // City validation - letters only, min 2 chars
        cityfield.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^[A-Za-z\\s]{2,}$") && !newVal.isEmpty()) {
                cityfield.setStyle("-fx-border-color: red;");
                errorCity.setText("City must contain only letters and have at least 2 characters");
                errorCity.setTextFill(Color.RED);
            } else {
                cityfield.setStyle("");
                errorCity.setText("");
            }
        });

        // Address validation - letters and numbers, min 5 chars
        addressfield.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^[A-Za-z0-9\\s]{5,}$") && !newVal.isEmpty()) {
                addressfield.setStyle("-fx-border-color: red;");
                errorAddress.setText("Address must have at least 5 characters (letters and numbers allowed)");
                errorAddress.setTextFill(Color.RED);
            } else {
                addressfield.setStyle("");
                errorAddress.setText("");
            }
        });

        // Country validation - letters only, min 2 chars
        countryfield.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^[A-Za-z\\s]{2,}$") && !newVal.isEmpty()) {
                countryfield.setStyle("-fx-border-color: red;");
                errorCountry.setText("Country must contain only letters and have at least 2 characters");
                errorCountry.setTextFill(Color.RED);
            } else {
                countryfield.setStyle("");
                errorCountry.setText("");
            }
        });

        // Price validation - valid number
        pricefield.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^\\d+(\\.\\d{1,2})?$") && !newVal.isEmpty()) {
                pricefield.setStyle("-fx-border-color: red;");
                errorPrice.setText("Price must be a valid number");
                errorPrice.setTextFill(Color.RED);
            } else {
                pricefield.setStyle("");
                errorPrice.setText("");
            }
        });

        // Description validation - not empty
        descriptionfield.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                descriptionfield.setStyle("-fx-border-color: red;");
                errorDescription.setText("Description cannot be empty");
                errorDescription.setTextFill(Color.RED);
            } else {
                descriptionfield.setStyle("");
                errorDescription.setText("");
            }
        });

        // Type validation
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                typeCombo.setStyle("-fx-border-color: red;");
                errorType.setText("Please select a type");
                errorType.setTextFill(Color.RED);
            } else {
                typeCombo.setStyle("");
                errorType.setText("");
            }
        });

        // Capacity validation
        capacityspinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal <= 0) {
                capacityspinner.setStyle("-fx-border-color: red;");
                errorCapacity.setText("Capacity must be greater than 0");
                errorCapacity.setTextFill(Color.RED);
            } else {
                capacityspinner.setStyle("");
                errorCapacity.setText("");
            }
        });



    }

    private void validateRating() {
        if (rating.get() <= 0) {
            starBox.setStyle("-fx-border-color: red;");
            errorRating.setText("Please select a rating");
            errorRating.setTextFill(Color.RED);
        } else {
            starBox.setStyle("");
            errorRating.setText("");
        }
    }

    private void validatePhoto() {
        if (fileLabel.getText().isEmpty() || fileLabel.getText().equals("No file selected")) {

            errorPhoto.setText("Please select a photo");
            errorPhoto.setTextFill(Color.RED);
        } else {
            fileLabel.setStyle("");
            errorPhoto.setText("");
        }
    }

    private void validateAlbum() {
        if (albumlabel.getText().isEmpty()) {

            errorAlbum.setText("Please select at least one photo for the album");
            errorAlbum.setTextFill(Color.RED);
        } else {
            albumlabel.setStyle("");
            errorAlbum.setText("");
        }
    }

    @FXML
    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            fileLabel.setText(selectedFile.getAbsolutePath());
            validatePhoto();
        } else {
            fileLabel.setText("No file selected");
            validatePhoto();
        }
    }

    @FXML
    private void handleChooseAlbum() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose images for the album");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg");
        fileChooser.getExtensionFilters().add(imageFilter);

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            List<String> imagePaths = new ArrayList<>();
            for (File file : selectedFiles) {
                imagePaths.add(file.getAbsolutePath());
            }
            String albumPath = String.join("\n", imagePaths);
            albumlabel.setText(albumPath);
            validateAlbum();
        } else {
            albumlabel.setText("");
            validateAlbum();
        }
    }

    private void setupOptionCheckboxes() {
        setupCheckboxListener(wifi, "Wi-Fi");
        setupCheckboxListener(pool, "Pool");
        setupCheckboxListener(meals, "All Meals Included");
        setupCheckboxListener(air, "Air Conditioning");
        setupCheckboxListener(parking, "Parking");
    }

    private void setupCheckboxListener(CheckBox checkbox, String label) {
        checkbox.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                selectedOptions.add(label);
            } else {
                selectedOptions.remove(label);
            }
        });
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        // Validate all fields before submission
        boolean isValid = true;

        // Validate name
        if (namefield.getText().isEmpty() || !namefield.getText().matches("^[A-Za-z\\s]{3,}$")) {
            namefield.setStyle("-fx-border-color: red;");
            errorName.setText("Name must contain only letters ");
            errorName.setTextFill(Color.RED);
            isValid = false;
        }

        // Validate type
        if (typeCombo.getValue() == null) {
            typeCombo.setStyle("-fx-border-color: red;");
            errorType.setText("Please select a type");
            errorType.setTextFill(Color.RED);
            isValid = false;
        }

        // Validate city
        if (cityfield.getText().isEmpty() || !cityfield.getText().matches("^[A-Za-z\\s]{2,}$")) {
            cityfield.setStyle("-fx-border-color: red;");
            errorCity.setText("City must contain only letters ");
            errorCity.setTextFill(Color.RED);
            isValid = false;
        }

        // Validate address
        if (addressfield.getText().isEmpty() || !addressfield.getText().matches("^[A-Za-z0-9\\s]{5,}$")) {
            addressfield.setStyle("-fx-border-color: red;");
            errorAddress.setText("Address must have at least 5 characters ");
            errorAddress.setTextFill(Color.RED);
            isValid = false;
        }

        // Validate country
        if (countryfield.getText().isEmpty() || !countryfield.getText().matches("^[A-Za-z\\s]{2,}$")) {
            countryfield.setStyle("-fx-border-color: red;");
            errorCountry.setText("Country must contain only letters ");
            errorCountry.setTextFill(Color.RED);
            isValid = false;
        }

        // Validate price
        if (pricefield.getText().isEmpty() || !pricefield.getText().matches("^\\d+(\\.\\d{1,2})?$")) {
            pricefield.setStyle("-fx-border-color: red;");
            errorPrice.setText("Price must be a valid number ");
            errorPrice.setTextFill(Color.RED);
            isValid = false;
        }

        // Validate description
        if (descriptionfield.getText().isEmpty()) {
            descriptionfield.setStyle("-fx-border-color: red;");
            errorDescription.setText("Description cannot be empty");
            errorDescription.setTextFill(Color.RED);
            isValid = false;
        }

        // Validate photo
        if (fileLabel.getText().isEmpty() || fileLabel.getText().equals("No file selected")) {

            errorPhoto.setText("Please select a photo");
            errorPhoto.setTextFill(Color.RED);
            isValid = false;
        }

        // Validate album
        if (albumlabel.getText().isEmpty()) {

            errorAlbum.setText("Please select at least one photo for the album");
            errorAlbum.setTextFill(Color.RED);
            isValid = false;
        }

        // Validate rating
        if (rating.get() <= 0) {
           // starBox.setStyle("-fx-border-color: red;");
            errorRating.setText("Please select a rating");
            errorRating.setTextFill(Color.RED);
            isValid = false;
        }

        // Validate capacity
        if (capacityspinner.getValue() == null || capacityspinner.getValue() <= 0) {
            capacityspinner.setStyle("-fx-border-color: red;");
            errorCapacity.setText("Capacity must be greater than 0");
            errorCapacity.setTextFill(Color.RED);
            isValid = false;
        }

        if (!isValid) {
            showAlert("Please correct the errors in the form before submitting.", Alert.AlertType.WARNING);
            return;
        }

        try {
            String name = namefield.getText();
            String type = typeCombo.getValue();
            String city = cityfield.getText();
            String address = addressfield.getText();
            String country = countryfield.getText();
            double price = Double.parseDouble(pricefield.getText());
            String photo = fileLabel.getText();
            String album = albumlabel.getText();
            String description = descriptionfield.getText();
            int ratingValue = rating.get();
            int capacity = capacityspinner.getValue();
            disponibility = capacity != 0;
            String options = String.join(", ", selectedOptions);
            String status = "waiting";

            ServiceHebergement sh = new ServiceHebergement();

            if (hebergementToEdit == null) {
                // Adding new accommodation
                Hebergement h = new Hebergement(name, type, city, address, country, price, disponibility,
                        photo, album, description, options, ratingValue, capacity, status);
                sh.ajouter(h);
                showAlert("Accommodation added successfully!", Alert.AlertType.INFORMATION);
                clearForm();
            } else {
                // Editing existing accommodation
                hebergementToEdit.setName(name);
                hebergementToEdit.setType(type);
                hebergementToEdit.setCity(city);
                hebergementToEdit.setAddress(address);
                hebergementToEdit.setCountry(country);
                hebergementToEdit.setPricePerNight(price);
                hebergementToEdit.setPhoto(photo);
                hebergementToEdit.setAlbum(album);
                hebergementToEdit.setDescription(description);
                hebergementToEdit.setOptions(options);
                hebergementToEdit.setRating(ratingValue);
                hebergementToEdit.setCapacity(capacity);
                hebergementToEdit.setDisponibility(disponibility);

                sh.modifier(hebergementToEdit);
                showAlert("Accommodation updated successfully!", Alert.AlertType.INFORMATION);
                hebergementToEdit = null;
            }

        } catch (NumberFormatException e) {
            showAlert("Price must be a valid number.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("An unexpected error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        namefield.clear();
        typeCombo.setValue(null);
        cityfield.clear();
        addressfield.clear();
        countryfield.clear();
        pricefield.clear();
        fileLabel.setText("");
        albumlabel.setText("");
        descriptionfield.clear();

        // Reset checkboxes
        wifi.setSelected(false);
        pool.setSelected(false);
        meals.setSelected(false);
        air.setSelected(false);
        parking.setSelected(false);
        selectedOptions.clear();

        // Reset spinner and rating
        capacityspinner.getValueFactory().setValue(0);
        rating.set(0);
        disponibility = false;

        // Clear all error messages
        errorName.setText("");
        errorType.setText("");
        errorCity.setText("");
        errorAddress.setText("");
        errorCountry.setText("");
        errorPrice.setText("");
        errorDescription.setText("");
        errorPhoto.setText("");
        errorAlbum.setText("");
        errorRating.setText("");
        errorCapacity.setText("");

        // Reset field styles
        namefield.setStyle("");
        typeCombo.setStyle("");
        cityfield.setStyle("");
        addressfield.setStyle("");
        countryfield.setStyle("");
        pricefield.setStyle("");
        descriptionfield.setStyle("");
        fileLabel.setStyle("");
        albumlabel.setStyle("");
        starBox.setStyle("");
        capacityspinner.setStyle("");
    }

    public void initData(Hebergement h) {
        this.hebergementToEdit = h;
        namefield.setText(h.getName());
        typeCombo.setValue(h.getType());
        cityfield.setText(h.getCity());
        addressfield.setText(h.getAddress());
        countryfield.setText(h.getCountry());
        pricefield.setText(String.valueOf(h.getPricePerNight()));
        fileLabel.setText(h.getPhoto());
        albumlabel.setText(h.getAlbum());
        descriptionfield.setText(h.getDescription());
        rating.set(h.getRating());
        capacityspinner.getValueFactory().setValue(h.getCapacity());
        disponibility = (h.getCapacity() != 0);

        // Set checkboxes based on options
        String options = h.getOptions();
        if (options != null) {
            wifi.setSelected(options.contains("Wi-Fi"));
            pool.setSelected(options.contains("Pool"));
            parking.setSelected(options.contains("Parking"));
            meals.setSelected(options.contains("All Meals Included"));
            air.setSelected(options.contains("Air Conditioning"));
        }
    }

    public void switch_admin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Agences/agency_acc.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}