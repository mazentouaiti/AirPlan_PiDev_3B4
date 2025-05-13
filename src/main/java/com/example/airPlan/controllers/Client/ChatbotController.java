package com.example.airPlan.controllers.Client;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import okhttp3.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatbotController {
    @FXML private TextField userInputField;
    @FXML private Button sendButton;
    @FXML private VBox chatBox;
    @FXML private ScrollPane chatScrollPane;

    private final OkHttpClient client = new OkHttpClient();
    private HBox currentTypingIndicator; // To keep track of the active typing indicator

    @FXML
    public void initialize() {
        sendButton.setOnAction(event -> handleSend());
        userInputField.setOnAction(event -> handleSend());
    }

    private void handleSend() {
        String userMessage = userInputField.getText().trim();
        if (!userMessage.isEmpty()) {
            addMessage("You", userMessage, true);
            showTypingIndicator();
            sendMessageToChatbot(userMessage);
            userInputField.clear();
        }
    }

    private void addMessage(String sender, String message, boolean isUser) {
        HBox messageContainer = new HBox();
        VBox messageContent = new VBox();
        Label messageLabel = new Label(message);
        Label timeLabel = new Label(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));

        if (isUser) {
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
            messageLabel.getStyleClass().addAll("message-bubble", "user-message");
            timeLabel.getStyleClass().add("message-time");
            timeLabel.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageContainer.setAlignment(Pos.CENTER_LEFT);
            messageLabel.getStyleClass().addAll("message-bubble", "bot-message");
            timeLabel.getStyleClass().add("message-time");
            timeLabel.setAlignment(Pos.CENTER_LEFT);
        }

        messageContent.getChildren().addAll(messageLabel, timeLabel);
        messageContent.setSpacing(2);
        messageContainer.getChildren().add(messageContent);
        chatBox.getChildren().add(messageContainer);

        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    private void showTypingIndicator() {
        Platform.runLater(() -> {
            // Create container for typing indicator
            HBox typingContainer = new HBox();
            typingContainer.getStyleClass().add("typing-container");
            typingContainer.setAlignment(Pos.CENTER_LEFT);

            // Create dots for animation
            Circle dot1 = new Circle(4);
            Circle dot2 = new Circle(4);
            Circle dot3 = new Circle(4);

            dot1.getStyleClass().add("typing-dot");
            dot2.getStyleClass().add("typing-dot");
            dot3.getStyleClass().add("typing-dot");

            // Create animation
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(dot1.opacityProperty(), 0.3),
                            new KeyValue(dot2.opacityProperty(), 0.3),
                            new KeyValue(dot3.opacityProperty(), 0.3)
                    ),
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(dot1.opacityProperty(), 1)
                    ),
                    new KeyFrame(Duration.millis(600),
                            new KeyValue(dot2.opacityProperty(), 1)
                    ),
                    new KeyFrame(Duration.millis(900),
                            new KeyValue(dot3.opacityProperty(), 1)
                    )
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.setAutoReverse(true);

            // Create bubble with dots
            HBox dotsContainer = new HBox(5, dot1, dot2, dot3);
            dotsContainer.setAlignment(Pos.CENTER);

            VBox typingBubble = new VBox(dotsContainer);
            typingBubble.getStyleClass().add("typing-indicator");
            typingBubble.setPadding(new Insets(8, 12, 8, 12));

            typingContainer.getChildren().add(typingBubble);
            chatBox.getChildren().add(typingContainer);

            // Store reference to remove later
            currentTypingIndicator = typingContainer;
            typingContainer.setUserData(timeline);

            timeline.play();
            Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
        });
    }

    private void removeTypingIndicator() {
        Platform.runLater(() -> {
            if (currentTypingIndicator != null && chatBox.getChildren().contains(currentTypingIndicator)) {
                Timeline timeline = (Timeline) currentTypingIndicator.getUserData();
                if (timeline != null) {
                    timeline.stop();
                }
                chatBox.getChildren().remove(currentTypingIndicator);
                currentTypingIndicator = null;
            }
        });
    }

    private void sendMessageToChatbot(String message) {
        String json = "{\"message\":\"" + message + "\"}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://127.0.0.1:5000/api/chat")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Platform.runLater(() -> {
                    removeTypingIndicator();
                    addMessage("Bot", "Sorry, I'm having trouble connecting. Please try again later.", false);
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                Platform.runLater(() -> removeTypingIndicator());

                if (response.isSuccessful()) {
                    String resJson = response.body().string();
                    String reply = new org.json.JSONObject(resJson).getString("response");
                    Platform.runLater(() -> addMessage("Bot", reply, false));
                } else {
                    Platform.runLater(() ->
                            addMessage("Bot", "Sorry, I encountered an error processing your request.", false));
                }
            }
        });
    }
}