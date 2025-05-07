package com.example.airPlan.models;

import java.sql.Timestamp;
import java.util.List;

public class Reservation {
    private int reservationId;
    private int flightId;
    private int userId;
    private int passengerCount;
    private String classType;
    private double totalPrice;
    private Timestamp reservationDate;
    private String status;
    private FlightModel flightDetails;
    private List<Passenger> passengers;

    // Getters and Setters
    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getPassengerCount() { return passengerCount; }
    public void setPassengerCount(int passengerCount) { this.passengerCount = passengerCount; }
    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public Timestamp getReservationDate() { return reservationDate; }
    public void setReservationDate(Timestamp reservationDate) { this.reservationDate = reservationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public FlightModel getFlightDetails() { return flightDetails; }
    public void setFlightDetails(FlightModel flightDetails) { this.flightDetails = flightDetails; }
    public List<Passenger> getPassengers() { return passengers; }
    public void setPassengers(List<Passenger> passengers) { this.passengers = passengers; }
}