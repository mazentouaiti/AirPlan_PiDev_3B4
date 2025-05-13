package com.example.airPlan.controllers.Client;

import okhttp3.*;

import java.util.concurrent.TimeUnit;

public class BrevoEmailSender {

    private static final String API_KEY = "";
    private static final String SENDER_EMAIL = "ayaajmaa@gmail.com";
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";

    public static void sendEmail(String recipientEmail, String recipientName) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        // Professional HTML email content with logo and better design
        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>AirPlan Reservation Confirmation</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Arial', sans-serif;\n" +
                "            line-height: 1.6;\n" +
                "            color: #333;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f7f7f7;\n" +
                "        }\n" +
                "        .container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            overflow: hidden;\n" +
                "            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        .header {\n" +
                "            background-color: #2c3e50;\n" +
                "            padding: 30px 20px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .logo {\n" +
                "            max-width: 200px;\n" +
                "            height: auto;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 30px;\n" +
                "        }\n" +
                "        h1 {\n" +
                "            color: #2c3e50;\n" +
                "            margin-top: 0;\n" +
                "        }\n" +
                "        p {\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 12px 24px;\n" +
                "            background-color: #3498db;\n" +
                "            color: #ffffff !important;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 4px;\n" +
                "            font-weight: bold;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            background-color: #f2f2f2;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "            font-size: 12px;\n" +
                "            color: #666;\n" +
                "        }\n" +
                "        .highlight {\n" +
                "            background-color: #f8f9fa;\n" +
                "            padding: 15px;\n" +
                "            border-left: 4px solid #3498db;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <img src=\"https://i.imgur.com/gAPDfoD.png\" alt=\"AirPlan Logo\" class=\"logo\">\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <h1>Reservation Confirmed!</h1>\n" +
                "            <p>Dear " + recipientName + ",</p>\n" +
                "            <p>Thank you for choosing <strong>AirPlan</strong> for your accommodation needs. We're delighted to confirm your reservation and look forward to serving you.</p>\n" +
                "            \n" +
                "            <div class=\"highlight\">\n" +
                "                <p>Your booking has been successfully processed. We've sent all the details to your email address.</p>\n" +
                "            </div>\n" +
                "            \n" +
                "            <p>Should you need any assistance or have special requests, please don't hesitate to contact our customer support team.</p>\n" +
                "            \n" +
                "            <p>We wish you a pleasant journey and a comfortable stay!</p>\n" +
                "            \n" +
                "            <p>Warm regards,</p>\n" +
                "            <p><strong>The AirPlan Team</strong></p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>&copy; 2025 AirPlan. All rights reserved.</p>\n" +
                "            <p>If you didn't make this reservation, please contact us immediately.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        // JSON body for the API request
        String jsonBody = "{"
                + "\"sender\":{\"name\":\"AirPlan\",\"email\":\"" + SENDER_EMAIL + "\"},"
                + "\"to\":[{\"email\":\"" + recipientEmail + "\",\"name\":\"" + recipientName + "\"}],"
                + "\"subject\":\"Your AirPlan Reservation Confirmation\","
                + "\"htmlContent\":\"" + htmlContent.replace("\"", "\\\"").replace("\n", "\\n") + "\""
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
}