package com.example.airPlan.controllers.Client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.example.airPlan.models.feedback;
import com.example.airPlan.Services.feedbackService;

import java.io.IOException;
import java.sql.Date;
import java.util.Objects;

public class UpdateFeed {
    @FXML
    public TextArea responseArea;
    @FXML
    private TextField titleField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> statusComboBox;

    private feedback currentFeedback;
    private final feedbackService feedbackService = new feedbackService();

    @FXML
    public void initialize() {
        // Initialize status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
                "Pending",
                "In Progress",
                "Resolved",
                "Rejected",
                "Under Review"
        ));

        // Debug print to verify fields initialization
        System.out.println("Initialize called - Fields status:");
        System.out.println("titleField: " + (titleField != null ? "OK" : "NULL"));
        System.out.println("datePicker: " + (datePicker != null ? "OK" : "NULL"));
        System.out.println("responseArea: " + (responseArea != null ? "OK" : "NULL"));
        System.out.println("statusComboBox: " + (statusComboBox != null ? "OK" : "NULL"));
    }

    public void setFeedbackData(feedback feedback) {
        this.currentFeedback = feedback;

        // Debug print
        System.out.println("setFeedbackData called with feedback: " +
                (feedback != null ? "ID=" + feedback.getIdfeed() : "NULL"));

        if (feedback != null) {
            // Add null checks to prevent NPE
            if (titleField != null) {
                titleField.setText(feedback.getTitlefeed());
            } else {
                System.err.println("ERROR: titleField is null");
            }

            if (datePicker != null && feedback.getDatefeed() != null) {
                datePicker.setValue(feedback.getDatefeed().toLocalDate());
            } else {
                System.err.println("ERROR: datePicker or feedback date is null");
            }

            if (responseArea != null) {
                responseArea.setText(feedback.getReponsefeed());
            } else {
                System.err.println("ERROR: responseArea is null");
            }

            if (statusComboBox != null) {
                statusComboBox.setValue(feedback.getStatutfeed());
            } else {
                System.err.println("ERROR: statusComboBox is null");
            }
        }
    }

    @FXML
    private void update(ActionEvent event) {
        if (validate_feedback()) {
            try {
                // Update the current feedback with form data
                currentFeedback.setTitlefeed(titleField.getText());
                currentFeedback.setDatefeed(Date.valueOf(datePicker.getValue()));
                currentFeedback.setReponsefeed(responseArea.getText());
                currentFeedback.setStatutfeed(statusComboBox.getValue());

                // Set user ID to null/0 as required
                currentFeedback.setId_user(0); // Use 0 or another appropriate default value

                // Call service to update
                feedbackService.update(currentFeedback);

                // Show success alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Feedback updated successfully!");
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

    private boolean validate_feedback() {
        StringBuilder errors = new StringBuilder();

        if (titleField.getText().trim().isEmpty()) {
            errors.append("Title is required.\n");
        }

        if (datePicker.getValue() == null) {
            errors.append("Date is required.\n");
        }

        if (responseArea.getText().trim().isEmpty()) {
            errors.append("Response is required.\n");
        }

        if (statusComboBox.getValue() == null) {
            errors.append("Status is required.\n");
        }

        if (errors.length() > 0) {
            showAlert(errors.toString());
            return false;
        }
        return true;
    }

    private void returnToList() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/Affichage_feed.fxml")));
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to return to feedback list: " + e.getMessage());
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