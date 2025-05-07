package com.example.airPlan.models;

import java.sql.Date;
import java.time.LocalDate;


public class FlightModel {
    private int flight_id , capacity;
    private String airline, origin, destination, classType, status,flight_number,admin_status;
    private Date departureDate, returnDate;
    private double price;


    public FlightModel(int flight_id, String airline, String origin, String destination, String classType, String status, String flight_number,
                       Date departureDate, Date returnDate, double price, int capacity, String admin_status) {
        this.flight_id = flight_id;
        this.flight_number = flight_number;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.classType = classType;
        this.status = status;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.price = price;
        this.capacity = capacity;
        this.admin_status = admin_status;
    }
    public FlightModel(String airline, String origin, String destination, String classType, String status,String flight_number,
                       Date departureDate, Date returnDate, double price, int capacity, String admin_status) {
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.classType = "Economy";
        this.status = status;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.price = price;
        this.flight_number = flight_number;
        this.capacity = capacity;
        this.admin_status = admin_status;
    }

    public FlightModel() {}
    public int getFlight_id() {
        return flight_id;
    }
    public void setFlight_id(int flight_id) {
        this.flight_id = flight_id;
    }
    public String getAirline() {
        return airline;
    }
    public void setAirline(String airline) {
        this.airline = airline;
    }
    public String getOrigin() {
        return origin;
    }
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    public String getDestination() {
        return destination;
    }
    public void setDestination(String destination) {
        this.destination = destination;
    }
    public String getClassType() {
        return classType;
    }
    public void setClassType(String classType) {
        this.classType = classType;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getFlightNumber() {
        return flight_number;
    }
    public void setFlightNumber(String flight_number) {
        this.flight_number = flight_number;
    }
    public Date getDepartureDate() {
        return departureDate;
    }
    public void setDepartureDate(Date departureDate) {
        this.departureDate = (Date) departureDate;
    }
    public int getCapacity() {return capacity;}
    public void setCapacity(int capacity) {this.capacity = capacity;}
    public Date getReturnDate() {
        return returnDate;
    }
    public void setReturnDate(Date returnDate) {
        this.returnDate = (Date) returnDate;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {this.price = price;}
    public String getAdminStatus() {return admin_status;}
    public void setAdminStatus(String admin_status) {this.admin_status = admin_status;}


    public String getCalculatedStatus() {
        // Keep manually set statuses that shouldn't auto-update
        if (this.status.equalsIgnoreCase("Cancelled") ||
                this.status.equalsIgnoreCase("Boarding") ||
                this.status.equalsIgnoreCase("Landed")) {
            return this.status;
        }

        LocalDate today = LocalDate.now();
        LocalDate returnDate = this.returnDate.toLocalDate();

        if (returnDate.isBefore(today)) {
            return "Landed";
        } else if (returnDate.isEqual(today)) {
            return "Boarding"; // or "Delayed" if you prefer
        } else {
            return "Scheduled";
        }
    }

    @Override
    public String toString() {
        return "Flight {" +
                "ID=" + flight_id +
                ", Flight Number='" + flight_number + '\'' +
                ", Airline='" + airline + '\'' +
                ", Origin='" + origin + '\'' +
                ", Destination='" + destination + '\'' +
                ", Class Type='" + classType + '\'' +
                ", Status='" + status + '\'' +
                ", Departure Date=" + departureDate +
                ", Return Date=" + returnDate +
                ", Price=" + price +
                ", Capacity=" + capacity +
                ", Admin Status='" + admin_status + '\'' +
                '}';
    }

}
