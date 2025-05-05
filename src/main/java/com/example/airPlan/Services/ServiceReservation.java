package com.example.airPlan.Services;

import com.example.airPlan.models.Reservation;
import com.example.airPlan.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ServiceReservation {

    private final Connection connection;
    public ServiceReservation() {
        connection = DBConnection.getConnection();
    }

    public void addReservation(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservation (id_user, id_acc, type_reservation, number_of_rooms, number_of_adults, number_of_children, requests, departure_date, arrival_date, price_children_acc, price_adults_acc, total_price_acc, name_of_reservated_accommodation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, reservation.getIdUser());
        stmt.setInt(2, reservation.getIdAcc());
        stmt.setString(3, reservation.getTypeReservation());
        stmt.setInt(4, reservation.getNumberOfRooms());
        stmt.setInt(5, reservation.getNumberOfAdults());
        stmt.setInt(6, reservation.getNumberOfChildren());
        stmt.setString(7, reservation.getRequests());
        stmt.setDate(8, reservation.getDepartureDate());
        stmt.setDate(9, reservation.getArrivalDate());
        stmt.setDouble(10, reservation.getPriceChildrenAcc());
        stmt.setDouble(11, reservation.getPriceAdultsAcc());
        stmt.setDouble(12, reservation.getTotalPriceAcc());
        stmt.setString(13, reservation.getNameOfReservatedAccommodation());

        stmt.executeUpdate();
            System.out.println("Reservated");
        } catch (SQLException e) {
            System.err.println("Erreur reservation: " + e.getMessage());
        }
    }
}
