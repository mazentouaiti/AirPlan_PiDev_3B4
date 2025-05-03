package com.example.airPlan.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class ChatbotController implements Initializable {

    @FXML private ListView<ChatMessage> chatListView;
    @FXML private TextField messageInput;

    private ObservableList<ChatMessage> chatMessages = FXCollections.observableArrayList();

    public static class ChatMessage {
        private final String message;
        private final boolean isUser;

        public ChatMessage(String message, boolean isUser) {
            this.message = message;
            this.isUser = isUser;
        }

        public String getMessage() {
            return message;
        }

        public boolean isUser() {
            return isUser;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize with welcome message
        chatMessages.add(new ChatMessage("Hello! I'm your travel assistant. How can I help you today?", false));

        chatListView.setItems(chatMessages);
        chatListView.setCellFactory(new Callback<ListView<ChatMessage>, ListCell<ChatMessage>>() {
            @Override
            public ListCell<ChatMessage> call(ListView<ChatMessage> param) {
                return new ListCell<ChatMessage>() {
                    private FXMLLoader loader;
                    private ChatMessageCellController controller;

                    @Override
                    protected void updateItem(ChatMessage item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            try {
                                if (loader == null) {
                                    loader = new FXMLLoader(getClass().getResource("/com/example/airPlan/views/cellchatbot.fxml"));
                                    controller = loader.getController();
                                    setGraphic(loader.load());
                                }
                                controller.setMessage(item);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void handleSendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            // Add user message
            chatMessages.add(new ChatMessage(message, true));
            messageInput.clear();

            // Add bot response (simplified for example)
            String response = generateBotResponse(message);
            chatMessages.add(new ChatMessage(response, false));

            // Scroll to bottom
            chatListView.scrollTo(chatMessages.size() - 1);
        }
    }

    private String generateBotResponse(String userMessage) {
        // Simple response logic - replace with your actual chatbot logic
        userMessage = userMessage.toLowerCase();

        if (userMessage.contains("hello") || userMessage.contains("hi")) {
            return "Hello there! How can I assist with your travel plans?";
        } else if (userMessage.contains("flight") || userMessage.contains("fly")) {
            return "I can help with flight information. Where are you flying to?";
        } else if (userMessage.contains("hotel") || userMessage.contains("stay")) {
            return "For hotel bookings, please specify your destination and dates.";
        } else {
            return "I'm here to help with your travel needs. Could you please provide more details?";
        }
    }
}