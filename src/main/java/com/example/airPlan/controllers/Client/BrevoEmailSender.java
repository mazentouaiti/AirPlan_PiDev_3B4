package com.example.airPlan.controllers.Client;

import okhttp3.*;

import java.util.concurrent.TimeUnit;

public class BrevoEmailSender {

    private static final String API_KEY = "";
    private static final String SENDER_EMAIL = "ayaajmaa@gmail.com"; // Remplace par ton adresse e-mail
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";

    public static void sendEmail(String recipientEmail, String recipientName) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)   // Temps d'attente pour établir la connexion
                .writeTimeout(30, TimeUnit.SECONDS)     // Temps d'attente pour envoyer les données
                .readTimeout(30, TimeUnit.SECONDS)      // Temps d'attente pour recevoir la réponse
                .build();

        // Contenu HTML du message
        String htmlContent = "<h2>Thank you for using AirPlan!</h2>"
                + "<p>Dear " + recipientName + ",</p>"
                + "<p>We sincerely appreciate you choosing <strong>AirPlan</strong> to book your accommodation.</p>"
                + "<p>Your reservation has been successfully confirmed. We are thrilled to have you with us and we hope you enjoy your stay!</p>"
                + "<p>Have a wonderful trip and a pleasant stay!</p>"
                + "<br/><p>Warm regards,<br/>The AirPlan Team</p>";

        // Chaîne JSON brute
        String jsonBody = "{"
                + "\"sender\":{\"name\":\"AirPlan\",\"email\":\"" + SENDER_EMAIL + "\"},"
                + "\"to\":[{\"email\":\"" + recipientEmail + "\",\"name\":\"" + recipientName + "\"}],"
                + "\"subject\":\"Your AirPlan Reservation Confirmation\","
                + "\"htmlContent\":\"" + htmlContent.replace("\"", "\\\"").replace("\n", "") + "\""
                + "}";

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("api-key", API_KEY)
                .addHeader("content-type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Email sent successfully to " + recipientEmail);
            } else {
                System.out.println("Failed to send email: " + response.code() + " - " + response.message());
                System.out.println(response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public static void main(String[] args) {
        sendEmail("eyaajmaa@gmail.com", "Eya");
    }*/
}

