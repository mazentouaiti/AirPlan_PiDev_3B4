package com.example.airPlan.models;

import java.sql.Date;
import java.sql.Timestamp;

public class Reservation{
    private int idReservation;
    private int idUser;
    private int idAcc;
    private String typeReservation;
    private int numberOfRooms;
    private int numberOfAdults;
    private int numberOfChildren;
    private String requests;
    private Date departureDate;
    private Date arrivalDate;
    private double priceChildrenAcc;
    private double priceAdultsAcc;
    private double totalPriceAcc;
    private String nameOfReservatedAccommodation;
    private Timestamp reservationTime;
    private String destination;

    // Getters & Setters
    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdAcc() {
        return idAcc;
    }

    public void setIdAcc(int idAcc) {
        this.idAcc = idAcc;
    }

    public String getTypeReservation() {
        return typeReservation;
    }

    public void setTypeReservation(String typeReservation) {
        this.typeReservation = typeReservation;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public String getRequests() {
        return requests;
    }

    public void setRequests(String requests) {
        this.requests = requests;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public double getPriceChildrenAcc() {
        return priceChildrenAcc;
    }

    public void setPriceChildrenAcc(double priceChildrenAcc) {
        this.priceChildrenAcc = priceChildrenAcc;
    }

    public double getPriceAdultsAcc() {
        return priceAdultsAcc;
    }

    public void setPriceAdultsAcc(double priceAdultsAcc) {
        this.priceAdultsAcc = priceAdultsAcc;
    }

    public double getTotalPriceAcc() {
        return totalPriceAcc;
    }

    public void setTotalPriceAcc(double totalPriceAcc) {
        this.totalPriceAcc = totalPriceAcc;
    }

    public String getNameOfReservatedAccommodation() {
        return nameOfReservatedAccommodation;
    }

    public void setNameOfReservatedAccommodation(String nameOfReservatedAccommodation) {
        this.nameOfReservatedAccommodation = nameOfReservatedAccommodation;
    }

    public Timestamp getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(Timestamp reservationTime) {
        this.reservationTime = reservationTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
