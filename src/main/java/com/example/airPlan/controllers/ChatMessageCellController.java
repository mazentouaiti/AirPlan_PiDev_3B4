package com.example.airPlan.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatMessageCellController {
    @FXML private HBox container;
    @FXML private ImageView avatar;
    @FXML private VBox messageBubble;
    @FXML private Label messageLabel;

    public void setMessage(ChatbotController.ChatMessage message) {
        messageLabel.setText(message.getMessage());

        if (message.isUser()) {
            container.getStyleClass().setAll("user-message-container");
            messageBubble.getStyleClass().setAll("user-message-bubble");
            messageLabel.getStyleClass().setAll("user-message-text");
            container.getChildren().remove(avatar);
        } else {
            container.getStyleClass().setAll("bot-message-container");
            messageBubble.getStyleClass().setAll("bot-message-bubble");
            messageLabel.getStyleClass().setAll("bot-message-text");
            if (!container.getChildren().contains(avatar)) {
                container.getChildren().add(0, avatar);
            }
        }
    }
}