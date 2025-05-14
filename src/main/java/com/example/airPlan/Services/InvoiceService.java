package com.example.airPlan.Services;

import com.example.airPlan.models.FlightModel;
import com.example.airPlan.controllers.Client.ReservedFlight;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InvoiceService {

    private static final double TAX_RATE = 0.075; // 7.5%

    public String generateFlightInvoice(ReservedFlight reservedFlight, String clientName) {
        try {
            // Load the HTML template
            InputStream inputStream = getClass().getResourceAsStream("/invoice_template.html");
            String htmlTemplate = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            FlightModel flight = reservedFlight.getFlight();

            // Calculate prices
            double subtotal = reservedFlight.getTotalPrice();
            double taxAmount = subtotal * TAX_RATE;
            double total = subtotal + taxAmount;

            // Format dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            // Create template variables
            Map<String, String> variables = new HashMap<>();
            variables.put("invoiceNumber", "INV-" + System.currentTimeMillis());
            variables.put("issueDate", dateFormat.format(new Date()));
            variables.put("dueDate", dateFormat.format(new Date(System.currentTimeMillis() + 15L * 24 * 60 * 60 * 1000)));
            variables.put("clientName", clientName);
            variables.put("reservationDate", reservedFlight.getFormattedReservationDate());
            variables.put("bookingRef", "RES-" + System.currentTimeMillis());
            variables.put("airline", flight.getAirline());
            variables.put("flightStatus", flight.getStatus());
            variables.put("originCode", flight.getOrigin());
            variables.put("originName", flight.getOrigin() + " International Airport");
            variables.put("destinationCode", flight.getDestination());
            variables.put("destinationName", flight.getDestination() + " International Airport");
            variables.put("departureTime", dateTimeFormat.format(flight.getDepartureDate()));
            variables.put("departureDate", dateFormat.format(flight.getDepartureDate()));
            variables.put("arrivalTime", dateTimeFormat.format(flight.getReturnDate()));
            variables.put("arrivalDate", dateFormat.format(flight.getReturnDate()));
            variables.put("duration", calculateDuration(flight.getDepartureDate(), flight.getReturnDate()));
            variables.put("aircraft", "N/A"); // Add to FlightModel if needed
            variables.put("subtotal", String.format("€%.2f", subtotal));
            variables.put("taxRate", String.format("%.1f%%", TAX_RATE * 100));
            variables.put("taxAmount", String.format("€%.2f", taxAmount));
            variables.put("total", String.format("€%.2f", total));
            variables.put("paymentStatus", reservedFlight.getPaymentStatus());

            // Generate flight items table rows
            String flightItem = "<tr>" +
                    "<td>" + flight.getFlightNumber() + " (" + flight.getOrigin() + " → " + flight.getDestination() + ")</td>" +
                    "<td>" + reservedFlight.getClassType() + "</td>" +
                    "<td>" + reservedFlight.getPassengers() + "</td>" +
                    "<td>" + String.format("€%.2f", flight.getPrice()) + "</td>" +
                    "<td>" + String.format("€%.2f", reservedFlight.getTotalPrice()) + "</td>" +
                    "</tr>";

            variables.put("flightItems", flightItem);

            // Replace all variables in the template
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                htmlTemplate = htmlTemplate.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return htmlTemplate;

        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><h1>Error generating invoice</h1><p>" + e.getMessage() + "</p></body></html>";
        }
    }

    private String calculateDuration(Date departure, Date arrival) {
        if (departure == null || arrival == null) {
            return "N/A";
        }

        long diffMillis = arrival.getTime() - departure.getTime();
        if (diffMillis <= 0) {
            return "N/A";
        }

        long diffSeconds = diffMillis / 1000;
        long hours = diffSeconds / 3600;
        long minutes = (diffSeconds % 3600) / 60;

        return String.format("%dh %02dm", hours, minutes);
    }
    // Add this method to InvoiceService.java
    public String generateMultiFlightInvoice(ArrayList<ReservedFlight> flights, String clientName) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/invoice_template.html");
            String htmlTemplate = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            // Calculate totals
            double subtotal = flights.stream().mapToDouble(ReservedFlight::getTotalPrice).sum();
            double taxAmount = subtotal * TAX_RATE;
            double total = subtotal + taxAmount;

            // Format dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // Create template variables
            Map<String, String> variables = new HashMap<>();
            variables.put("invoiceNumber", "INV-" + System.currentTimeMillis());
            variables.put("issueDate", dateFormat.format(new Date()));
            variables.put("dueDate", dateFormat.format(new Date(System.currentTimeMillis() + 15L * 24 * 60 * 60 * 1000)));
            variables.put("clientName", clientName);
            variables.put("subtotal", String.format("€%.2f", subtotal));
            variables.put("taxAmount", String.format("€%.2f", taxAmount));
            variables.put("total", String.format("€%.2f", total));
            variables.put("paymentStatus", "PAID");


            // Generate flight items table rows
            StringBuilder flightItems = new StringBuilder();
            for (ReservedFlight reservedFlight : flights) {
                FlightModel flight = reservedFlight.getFlight();
                flightItems.append("<tr>")
                        .append("<td>").append(flight.getFlightNumber()).append(" (")
                        .append(flight.getOrigin()).append(" → ")
                        .append(flight.getDestination()).append(")</td>")
                        .append("<td>").append(reservedFlight.getClassType()).append("</td>")
                        .append("<td>").append(reservedFlight.getPassengers()).append("</td>")
                        .append("<td>").append(String.format("€%.2f", flight.getPrice())).append("</td>")
                        .append("<td>").append(String.format("€%.2f", reservedFlight.getTotalPrice())).append("</td>")
                        .append("</tr>");
            }

            variables.put("flightItems", flightItems.toString());

            // Replace all variables in the template
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                htmlTemplate = htmlTemplate.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }

            return htmlTemplate;

        } catch (Exception e) {
            return errorTemplate(e.getMessage());
        }
    }

    private String errorTemplate(String message) {
        return "<html><body><h1>Error generating invoice</h1><p>" + message + "</p></body></html>";
    }
}