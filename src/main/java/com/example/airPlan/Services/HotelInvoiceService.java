package com.example.airPlan.Services;

import com.example.airPlan.models.Hebergement;
import com.example.airPlan.models.Reservation;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class HotelInvoiceService {
    private static final double TAX_RATE = 0.10; // 10% tax
    private String templateContent;

    public HotelInvoiceService() {
        loadTemplate();
    }

    private void loadTemplate() {
        try (InputStream inputStream = getClass().getResourceAsStream("/hotel_invoice_template.html");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            templateContent = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load hotel invoice template", e);
        }
    }

    public String generateHotelInvoice(Reservation reservation, Hebergement hotel, String clientName, String clientEmail, String clientPhone) {
        Objects.requireNonNull(reservation, "Reservation cannot be null");
        Objects.requireNonNull(hotel, "Hotel cannot be null");

        // Calculate number of nights
        long nights = ChronoUnit.DAYS.between(
                reservation.getArrivalDate().toLocalDate(),
                reservation.getDepartureDate().toLocalDate()
        );

        // Calculate prices
        double roomSubtotal = hotel.getPricePerNight() * nights * reservation.getNumberOfRooms();
        double taxes = roomSubtotal * TAX_RATE;
        double totalAmount = roomSubtotal + taxes;

        Map<String, String> variables = new HashMap<>();

        // Booking information
        variables.put("bookingReference", "HOTEL-" + System.currentTimeMillis());
        variables.put("issueDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        // Hotel information
        variables.put("hotelName", escapeHtml(hotel.getName()));
        variables.put("hotelAddress", escapeHtml(hotel.getAddress()));
        variables.put("hotelCity", escapeHtml(hotel.getCity()));
        variables.put("hotelCountry", escapeHtml(hotel.getCountry()));
        variables.put("hotelImage", hotel.getPhoto());
        variables.put("hotelRating", getStarRating(hotel.getRating()));
        variables.put("hotelPhone", "+216 XX XXX XXX"); // Replace with actual hotel phone
        variables.put("hotelEmail", "contact@" + hotel.getName().toLowerCase().replace(" ", "") + ".com");

        // Room information
        variables.put("roomType", escapeHtml(hotel.getType()));
        variables.put("roomRate", String.format("%.2f", hotel.getPricePerNight()));
        variables.put("roomSubtotal", String.format("%.2f", roomSubtotal));

        // Guest information
        variables.put("guestName", escapeHtml(clientName));
        variables.put("guestEmail", escapeHtml(clientEmail));
        variables.put("guestPhone", escapeHtml(clientPhone));

        // Dates
        variables.put("checkInDate", reservation.getArrivalDate().toString());
        variables.put("checkOutDate", reservation.getDepartureDate().toString());
        variables.put("numberOfNights", String.valueOf(nights));

        // Occupancy
        variables.put("numberOfRooms", String.valueOf(reservation.getNumberOfRooms()));
        variables.put("numberOfAdults", String.valueOf(reservation.getNumberOfAdults()));
        variables.put("numberOfChildren", String.valueOf(reservation.getNumberOfChildren()));

        // Pricing
        variables.put("subtotal", String.format("%.2f", roomSubtotal));
        variables.put("taxes", String.format("%.2f", taxes));
        variables.put("totalAmount", String.format("%.2f", totalAmount));

        // Special requests
        variables.put("specialRequests",
                reservation.getRequests() != null && !reservation.getRequests().isEmpty() ?
                        escapeHtml(reservation.getRequests()) : "No special requests");

        return replaceTemplateVariables(variables);
    }

    private String getStarRating(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stars.append(i < rating ? "★" : "☆");
        }
        return stars.toString();
    }

    private String replaceTemplateVariables(Map<String, String> variables) {
        String result = templateContent;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }

    private String escapeHtml(String input) {
        return input == null ? "" : input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}