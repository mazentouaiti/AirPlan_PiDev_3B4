package com.example.airPlan.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.example.airPlan.models.reclamation;
import com.example.airPlan.Services.reclamationService;

import java.io.IOException;
import java.sql.Date;
import java.util.Objects;

public class UpdateReclamation {
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField ratingField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextField descriptionField;

    private reclamation currentReclamation;
    private final reclamationService reclamationService = new reclamationService();

    @FXML
    public void initialize() {
        // Initialize type combo box
        typeComboBox.setItems(FXCollections.observableArrayList(
                "flights",
                "Service",
                "transport"
        ));

        // Initialize status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
                "flights",
                "Service",
                "transport"
        ));

        // Debug print to verify fields initialization
        System.out.println("Initialize called - Fields status:");
        System.out.println("typeComboBox: " + (typeComboBox != null ? "OK" : "NULL"));
        System.out.println("datePicker: " + (datePicker != null ? "OK" : "NULL"));
        System.out.println("ratingField: " + (ratingField != null ? "OK" : "NULL"));
        System.out.println("statusComboBox: " + (statusComboBox != null ? "OK" : "NULL"));
        System.out.println("descriptionField: " + (descriptionField != null ? "OK" : "NULL"));
    }

    public void setReclamationData(reclamation reclamation) {
        this.currentReclamation = reclamation;

        // Debug print
        System.out.println("setReclamationData called with reclamation: " +
                (reclamation != null ? "ID=" + reclamation.getId() : "NULL"));

        if (reclamation != null) {
            // Add null checks to prevent NPE
            if (typeComboBox != null) {
                typeComboBox.setValue(reclamation.getType());
            } else {
                System.err.println("ERROR: typeComboBox is null");
            }

            if (datePicker != null && reclamation.getDatee() != null) {
                datePicker.setValue(reclamation.getDatee().toLocalDate());
            } else {
                System.err.println("ERROR: datePicker or reclamation date is null");
            }

            if (ratingField != null) {
                ratingField.setText(String.valueOf(reclamation.getNote()));
            } else {
                System.err.println("ERROR: ratingField is null");
            }

            if (statusComboBox != null) {
                statusComboBox.setValue(reclamation.getStatut());
            } else {
                System.err.println("ERROR: statusComboBox is null");
            }

            if (descriptionField != null) {
                descriptionField.setText(reclamation.getDescription());
            } else {
                System.err.println("ERROR: descriptionField is null");
            }
        }
    }

    @FXML
    private void update(ActionEvent event) {
        if (validateInput()) {
            try {
                // Update the current reclamation with form data
                currentReclamation.setType(typeComboBox.getValue());
                currentReclamation.setDatee(Date.valueOf(datePicker.getValue()));
                currentReclamation.setNote(Integer.parseInt(ratingField.getText()));
                currentReclamation.setStatut(statusComboBox.getValue());
                currentReclamation.setDescription(descriptionField.getText());

                // Call service to update
                reclamationService.update(currentReclamation);

                // Show success alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Reclamation updated successfully!");
                alert.showAndWait();

                returnToList();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred: " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void annuler(ActionEvent event) {
        returnToList();
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (typeComboBox.getValue() == null) {
            errors.append("Type is required.\n");
        }

        if (datePicker.getValue() == null) {
            errors.append("Date is required.\n");
        }

        try {
            int rating = Integer.parseInt(ratingField.getText());
            if (rating < 1 || rating > 5) {
                errors.append("Rating must be between 1 and 5.\n");
            }
        } catch (NumberFormatException e) {
            errors.append("Rating must be a valid number.\n");
        }

        if (statusComboBox.getValue() == null) {
            errors.append("Status is required.\n");
        }

        if (descriptionField.getText().trim().isEmpty()) {
            errors.append("Description is required.\n");
        }

        if (errors.length() > 0) {
            showAlert(errors.toString());
            return false;
        }
        return true;
    }

    private void returnToList() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Client/affichage_reclamation.fxml")));
            Stage stage = (Stage) typeComboBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to return to reclamation list: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}