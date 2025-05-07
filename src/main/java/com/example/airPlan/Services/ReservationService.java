package com.example.airPlan.Services;

import com.example.airPlan.Utiles.DBConnection;
import com.example.airPlan.models.FlightModel;
import com.example.airPlan.models.Passenger;
import com.example.airPlan.models.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private Connection connection;

    public ReservationService() {
        connection = DBConnection.getInstance().getConnection();
    }

    private int lastReservationId = -1;

    public boolean createReservation(int flightId, int userId, int passengerCount,
                                     String classType, double totalPrice) {
        String sql = "INSERT INTO reservations (flight_id, user_id, passenger_count, " +
                "class_type, total_price) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, flightId);
            ps.setInt(2, userId);
            ps.setInt(3, passengerCount);
            ps.setString(4, classType);
            ps.setDouble(5, totalPrice);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.lastReservationId = rs.getInt(1);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating reservation: " + e.getMessage());
        }
        return false;
    }

    public int getLastReservationId() {
        return this.lastReservationId;
    }

    public Reservation getReservationById(int reservationId) {
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("reservation_id"));
                reservation.setFlightId(rs.getInt("flight_id"));
                reservation.setUserId(rs.getInt("user_id"));
                reservation.setPassengerCount(rs.getInt("passenger_count"));
                reservation.setClassType(rs.getString("class_type"));
                reservation.setTotalPrice(rs.getDouble("total_price"));
                reservation.setReservationDate(rs.getTimestamp("reservation_date"));
                reservation.setStatus(rs.getString("status"));
                return reservation;
            }
        } catch (SQLException e) {
            System.out.println("Error getting reservation: " + e.getMessage());
        }
        return null;
    }

    public List<Reservation> getUserReservations(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setReservationId(rs.getInt("reservation_id"));
                reservation.setFlightId(rs.getInt("flight_id"));
                reservation.setUserId(rs.getInt("user_id"));
                reservation.setPassengerCount(rs.getInt("passenger_count"));
                reservation.setClassType(rs.getString("class_type"));
                reservation.setTotalPrice(rs.getDouble("total_price"));
                reservation.setReservationDate(rs.getTimestamp("reservation_date"));
                reservation.setStatus(rs.getString("status"));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.out.println("Error getting user reservations: " + e.getMessage());
        }
        return reservations;
    }

    public boolean cancelReservation(int reservationId) {
        String sql = "UPDATE reservations SET status = 'cancelled' WHERE reservation_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error cancelling reservation: " + e.getMessage());
        }
        return false;
    }

    public boolean addPassenger(int reservationId, String firstName, String lastName, String passportNumber) {
        String sql = "INSERT INTO passengers (reservation_id, first_name, last_name, passport_number) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, passportNumber);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error adding passenger: " + e.getMessage());
        }
        return false;
    }

    public List<Passenger> getPassengersForReservation(int reservationId) {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT * FROM passengers WHERE reservation_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setPassengerId(rs.getInt("passenger_id"));
                passenger.setReservationId(rs.getInt("reservation_id"));
                passenger.setFirstName(rs.getString("first_name"));
                passenger.setLastName(rs.getString("last_name"));
                passenger.setPassportNumber(rs.getString("passport_number"));
                passengers.add(passenger);
            }
        } catch (SQLException e) {
            System.out.println("Error getting passengers: " + e.getMessage());
        }
        return passengers;
    }
}