package com.example.airPlan.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.airPlan.models.feedback;
import com.example.airPlan.Services.feedbackService;

import java.io.IOException;
import java.util.List;

public class AffichageFeed {
 
        @FXML
        private ListView<feedback> listViewFeed;

        private final com.example.airPlan.Services.feedbackService feedbackService = new feedbackService();

        @FXML
        public void initialize() {
            configureCellFactory();
            loadfeedbacks();
        }

        private void loadfeedbacks() {
            try {
                List<feedback> feedbacks = feedbackService.display();
                listViewFeed.getItems().setAll(feedbacks);
            } catch (Exception e) {
                showAlert("Error", "Failed to load feedbacks: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void configureCellFactory() {
            listViewFeed.setCellFactory(param -> new ListCell<feedback>() {
                @Override
                protected void updateItem(feedback feedback, boolean empty) {
                    super.updateItem(feedback, empty);
                    if (empty || feedback == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        // Create custom cell layout
                        VBox container = new VBox(5);
                        HBox firstLine = new HBox(10);
                        HBox secondLine = new HBox(10);
                        HBox buttonBox = new HBox(10);

                        // Create labels
                        Label idLabel = new Label("ID: " + feedback.getIdfeed());
                        Label titelLabel = new Label("Titel_feed: " + feedback.getTitlefeed());
                        Label dateLabel = new Label("Date: " + feedback.getDatefeed());
                        Label ReponsefeedLabel = new Label("Reponsefeed: " + feedback.getReponsefeed());
                        Label statusLabel = new Label("Status: " + feedback.getStatutfeed());

                        // Style the labels
                        ReponsefeedLabel.setStyle("-fx-font-weight: bold;");
                        statusLabel.setStyle("-fx-text-fill: " + (feedback.getStatutfeed().equalsIgnoreCase("resolved") ? "green" : "red") + ";");

                        // Create buttons
                        Button editButton = new Button("Edit");
                        Button deleteButton = new Button("Delete");

                        // Style buttons
                        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                        // Set button actions
                        editButton.setOnAction(event -> openEditForm(feedback));
                        deleteButton.setOnAction(event -> deletefeedback(feedback));

                        // Add components to containers
                        firstLine.getChildren().addAll(idLabel, titelLabel);
                        secondLine.getChildren().addAll(dateLabel, ReponsefeedLabel);
                        buttonBox.getChildren().addAll( statusLabel, editButton, deleteButton);
                        container.getChildren().addAll(firstLine, secondLine, buttonBox);

                        setGraphic(container);
                    }
                }
            });
        }

    private void openEditForm(feedback feedback) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/UpdateFeed.fxml"));
            Parent root = loader.load();

            UpdateFeed controller = loader.getController();
            controller.setFeedbackData(feedback); // This line is causing the error

            Stage stage = (Stage) listViewFeed.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            // Better error handling like in your event code
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to load edit form: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

        private void deletefeedback(feedback feedback) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete feedback ID: " + feedback.getIdfeed());
            alert.setContentText("Are you sure you want to delete this feedback?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        feedbackService.delete(feedback.getIdfeed());
                        loadfeedbacks();
                        showAlert("Success", "feedback ID: " + feedback.getIdfeed() + " deleted successfully");
                    } catch (Exception e) {
                        showAlert("Error", "Failed to delete feedback: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        }

        @FXML
        private void handleAddButton() {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Admin/AjouteFeed.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Add New feedback");
                stage.show();

                stage.setOnHidden(event -> loadfeedbacks());
            } catch (IOException e) {
                showAlert("Error", "Failed to load add form: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void showAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        }

    public void reclamtion(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Admin/aff_rec_ad.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("list reclamation");
            stage.show();

            stage.setOnHidden(event -> loadfeedbacks());
        } catch (IOException e) {
            showAlert("Error", "Failed to load add form: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
