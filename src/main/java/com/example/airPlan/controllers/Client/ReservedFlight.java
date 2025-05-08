package com.example.airPlan.controllers.Client;

import com.example.airPlan.models.FlightModel;

class ReservedFlight {
    private FlightModel flight;
    private int passengers;
    private double totalPrice;

    public ReservedFlight(FlightModel flight, int passengers, double totalPrice) {
        this.flight = flight;
        this.passengers = passengers;
        this.totalPrice = totalPrice;
    }

    public FlightModel getFlight() {
        return flight;
    }

    public int getPassengers() {
        return passengers;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
