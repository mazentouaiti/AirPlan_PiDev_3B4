package com.example.airPlan.controllers.Client;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import com.example.airPlan.models.feedback;
import com.example.airPlan.Services.feedbackService;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

public class AjouteFeed {

    @FXML
    private TextField titleField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea responseArea;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Label errorLabel;

    private feedbackService feedbackService = new feedbackService();

    // For edit mode
    private boolean editMode = false;
    private int feedbackId = -1;

    @FXML
    public void initialize() {
        // Initialize status combo box
        statusComboBox.setItems(FXCollections.observableArrayList(
                "nouveau",
                "en cours",
                "traité"
        ));

        // Set default value for status
        statusComboBox.setValue("nouveau");

        // Set default date to today
        datePicker.setValue(LocalDate.now());
    }

    public void setFeedbackData(feedback fb) {
        if (fb != null) {
            editMode = true;
            feedbackId = fb.getIdfeed();

            // Fill form fields with feedback data
            titleField.setText(fb.getTitlefeed());

            // Convert SQL date to LocalDate
            if (fb.getDatefeed() != null) {
                datePicker.setValue(fb.getDatefeed().toLocalDate());
            }

            responseArea.setText(fb.getReponsefeed());
            statusComboBox.setValue(fb.getStatutfeed());
        }
    }

    @FXML
    public void annuler(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Fxml/Admin/affichage_feed.fxml")));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page d'affichage des feedbacks");
        }
    }

    @FXML
    public void save(ActionEvent actionEvent) {
        // Check if the required fields are filled
        if (titleField.getText().trim().isEmpty() || responseArea.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs obligatoires");
            return;
        }

        try {
            // Convert LocalDate to SQL Date
            Date sqlDate = null;
            if (datePicker.getValue() != null) {
                sqlDate = Date.valueOf(datePicker.getValue());
            } else {
                sqlDate = Date.valueOf(LocalDate.now());
            }

            // Create feedback object
            feedback fb = new feedback(
                    editMode ? feedbackId : 0,
                    titleField.getText().trim(),
                    sqlDate,
                    responseArea.getText().trim(),
                    statusComboBox.getValue(),
                    1 // Default user ID - you might want to get this from session or other source
            );

            // Save or update feedback
            if (editMode) {
                feedbackService.update(fb);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Feedback mis à jour avec succès !");
            } else {
                feedbackService.add(fb);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Feedback ajouté avec succès !");
            }

            // Return to the feedback list
            annuler(actionEvent);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}