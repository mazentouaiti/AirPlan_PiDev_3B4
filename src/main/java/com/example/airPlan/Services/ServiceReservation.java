package com.example.airPlan.Services;

import com.example.airPlan.models.Reservation;
import com.example.airPlan.Utiles.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation {

    private final Connection connection;

    public ServiceReservation() {
        connection = DBConnection.getConnection();
    }

    public void addReservation(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservation (id_user, id_acc, type_reservation, number_of_rooms, number_of_adults, number_of_children, requests, departure_date, arrival_date, price_children_acc, price_adults_acc, total_price_acc, name_of_reservated_accommodation, destination) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Start transaction
        connection.setAutoCommit(false);

        try {
            // Step 1: Add the reservation
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
            stmt.setString(14, reservation.getDestination());

            stmt.executeUpdate();
            System.out.println("Réservation ajoutée avec succès");

            // Step 2: Update the capacity in hebergement table
            updateHebergementCapacity(reservation.getIdAcc(), reservation.getNumberOfRooms());

            // Commit transaction
            connection.commit();
        } catch (SQLException e) {
            // Rollback transaction if any error occurs
            connection.rollback();
            System.err.println("Erreur lors de l'ajout de la réservation: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void updateHebergementCapacity(int hebergementId, int roomsReserved) throws SQLException {
        String sql = "UPDATE hebergement SET capacity = capacity - ? WHERE acc_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, roomsReserved);
            stmt.setInt(2, hebergementId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating hebergement capacity failed, no rows affected.");
            }
            System.out.println("Capacity updated successfully for hebergement ID: " + hebergementId);
        }
    }

    public void deleteReservation(int reservationId) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id_reservation = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, reservationId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Réservation supprimée avec succès");
            } else {
                System.out.println("Aucune réservation trouvée avec l'ID: " + reservationId);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réservation: " + e.getMessage());
            throw e;
        }
    }

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setIdReservation(rs.getInt("id_reservation"));
                reservation.setIdUser(rs.getInt("id_user"));
                reservation.setIdAcc(rs.getInt("id_acc"));
                reservation.setTypeReservation(rs.getString("type_reservation"));
                reservation.setNumberOfRooms(rs.getInt("number_of_rooms"));
                reservation.setNumberOfAdults(rs.getInt("number_of_adults"));
                reservation.setNumberOfChildren(rs.getInt("number_of_children"));
                reservation.setRequests(rs.getString("requests"));
                reservation.setDepartureDate(rs.getDate("departure_date"));
                reservation.setArrivalDate(rs.getDate("arrival_date"));
                reservation.setPriceChildrenAcc(rs.getDouble("price_children_acc"));
                reservation.setPriceAdultsAcc(rs.getDouble("price_adults_acc"));
                reservation.setTotalPriceAcc(rs.getDouble("total_price_acc"));
                reservation.setNameOfReservatedAccommodation(rs.getString("name_of_reservated_accommodation"));
                reservation.setDestination(rs.getString("destination"));

                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réservations: " + e.getMessage());
            throw e;
        }

        return reservations;
    }

    public List<Reservation> getReservationsByUserId(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE id_user = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setIdReservation(rs.getInt("id_reservation"));
                    reservation.setIdUser(rs.getInt("id_user"));
                    reservation.setIdAcc(rs.getInt("id_acc"));
                    reservation.setTypeReservation(rs.getString("type_reservation"));
                    reservation.setNumberOfRooms(rs.getInt("number_of_rooms"));
                    reservation.setNumberOfAdults(rs.getInt("number_of_adults"));
                    reservation.setNumberOfChildren(rs.getInt("number_of_children"));
                    reservation.setRequests(rs.getString("requests"));
                    reservation.setDepartureDate(rs.getDate("departure_date"));
                    reservation.setArrivalDate(rs.getDate("arrival_date"));
                    reservation.setPriceChildrenAcc(rs.getDouble("price_children_acc"));
                    reservation.setPriceAdultsAcc(rs.getDouble("price_adults_acc"));
                    reservation.setTotalPriceAcc(rs.getDouble("total_price_acc"));
                    reservation.setNameOfReservatedAccommodation(rs.getString("name_of_reservated_accommodation"));
                    reservation.setDestination(rs.getString("destination"));

                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réservations par utilisateur: " + e.getMessage());
            throw e;
        }

        return reservations;
    }

    public Reservation getReservationById(int reservationId) throws SQLException {
        String sql = "SELECT * FROM reservation WHERE id_reservation = ?";
        Reservation reservation = null;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reservation = new Reservation();
                    reservation.setIdReservation(rs.getInt("id_reservation"));
                    reservation.setIdUser(rs.getInt("id_user"));
                    reservation.setIdAcc(rs.getInt("id_acc"));
                    reservation.setTypeReservation(rs.getString("type_reservation"));
                    reservation.setNumberOfRooms(rs.getInt("number_of_rooms"));
                    reservation.setNumberOfAdults(rs.getInt("number_of_adults"));
                    reservation.setNumberOfChildren(rs.getInt("number_of_children"));
                    reservation.setRequests(rs.getString("requests"));
                    reservation.setDepartureDate(rs.getDate("departure_date"));
                    reservation.setArrivalDate(rs.getDate("arrival_date"));
                    reservation.setPriceChildrenAcc(rs.getDouble("price_children_acc"));
                    reservation.setPriceAdultsAcc(rs.getDouble("price_adults_acc"));
                    reservation.setTotalPriceAcc(rs.getDouble("total_price_acc"));
                    reservation.setNameOfReservatedAccommodation(rs.getString("name_of_reservated_accommodation"));
                    reservation.setDestination(rs.getString("destination"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la réservation: " + e.getMessage());
            throw e;
        }

        return reservation;
    }
}