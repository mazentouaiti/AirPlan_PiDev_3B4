package com.example.airPlan.controllers.Client;

import com.example.airPlan.models.FlightModel;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class ReservedFlight {
    private FlightModel flight;
    private int passengers;
    private String classType;
    private double totalPrice;
    private Date reservationDate;

    public ReservedFlight(FlightModel flight, int passengers, String classType, double totalPrice) {
        this.flight = flight;
        this.passengers = passengers;
        this.classType = classType;  // Initialize the new field
        this.totalPrice = totalPrice;
        this.reservationDate = new Date(System.currentTimeMillis());
    }

    // Getters
    public FlightModel getFlight() { return flight; }
    public int getPassengers() { return passengers; }
    public String getClassType() { return classType; }
    public double getTotalPrice() { return totalPrice; }
    public Date getReservationDate() { return reservationDate; }


    // Helper method for invoice generation
    public String getFormattedReservationDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(reservationDate);
    }
}