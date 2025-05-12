package com.example.airPlan.Services;


import com.example.airPlan.Utiles.DBConnection;
import com.example.airPlan.models.FlightModel;
import com.sun.javafx.collections.MappingChange;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class FlightServices implements Services{
    private Connection connection;
    public FlightServices() {connection= DBConnection.getInstance().getConnection();}
    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public void addFlight(FlightModel flight) {
        if (isNumeric(flight.getOrigin())) {
            System.out.println("Origin cannot be a number");
            return;
        }
        if (isNumeric(flight.getDestination())) {
            System.out.println("Destination cannot be a number");
            return;
        }
        if (isNumeric(flight.getAirline())) {
            System.out.println("Airline cannot be a number");
            return;
        }
        String checkSql = "SELECT COUNT(*) FROM flights WHERE flight_number = ?";
        try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setString(1, flight.getFlightNumber());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Flight " + flight.getFlightNumber() + " already exists, skipping insertion.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Error checking flight existence: " + e.getMessage());
        }
        String sql = "INSERT INTO flights (flight_number, airline, origin, destination, departureDate, return_date, class_type, status, price,capacity ,admin_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        try {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getAirline());
            ps.setString(3, flight.getOrigin());
            ps.setString(4, flight.getDestination());
            ps.setDate(5, flight.getDepartureDate());
            ps.setDate(6, flight.getReturnDate());
            ps.setString(7, flight.getClassType() != null ? flight.getClassType() : "Economy");
            ps.setString(8, flight.getStatus());
            ps.setDouble(9, flight.getPrice());
            ps.setInt(10,flight.getCapacity());
            ps.setString(11, "pending");
            ps.executeUpdate();
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public  List<FlightModel> getAllFlights()  {
        List<FlightModel> flights = new ArrayList<>();
        String sql = "SELECT * FROM flights";

        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                FlightModel f = new FlightModel();
                f.setFlight_id(rs.getInt("flight_id"));
                f.setFlightNumber(rs.getString("flight_number"));
                f.setAirline(rs.getString("airline"));
                f.setOrigin(rs.getString("origin"));
                f.setDestination(rs.getString("destination"));
                f.setDepartureDate(rs.getDate("departureDate"));
                f.setReturnDate(rs.getDate("return_date"));
                f.setClassType(rs.getString("class_type"));
                f.setStatus(rs.getString("status"));
                f.setPrice(rs.getDouble("price"));
                f.setCapacity(rs.getInt("capacity"));
                f.setAdminStatus(rs.getString("admin_status"));
                flights.add(f);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return flights;
    }
    public void updateFlight(FlightModel flight)  {
        if (isNumeric(flight.getOrigin())) {
            System.out.println("Origin cannot be a number");
            return;
        }
        if (isNumeric(flight.getDestination())) {
            System.out.println("Destination cannot be a number");
            return;
        }
        if (isNumeric(flight.getAirline())) {
            System.out.println("Airline cannot be a number");
            return;
        }
        String sql = "UPDATE flights SET flight_number=?, airline=?, origin=?, destination=?, departureDate=?, return_date=?, class_type=?, status=?, price=? , capacity=? WHERE flight_id=?";
        try  {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getAirline());
            ps.setString(3, flight.getOrigin());
            ps.setString(4, flight.getDestination());
            ps.setDate(5, flight.getDepartureDate());
            ps.setDate(6, flight.getReturnDate());
            ps.setString(7, flight.getClassType());
            ps.setString(8, flight.getStatus());
            ps.setDouble(9, flight.getPrice());
            ps.setInt(10, flight.getCapacity());
            ps.setInt(11, flight.getFlight_id());

            ps.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public void deleteFlight(int id)  {
        String sql = "DELETE FROM flights WHERE flight_id=?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public List<FlightModel> searchFlights(String departure, String destination,
                                           Date departureDate,
                                           String priceFilter) {
        List<FlightModel> flights = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM flights WHERE admin_status = 'approved'");

        // Add conditions based on parameters
        if (departure != null) {
            sql.append(" AND origin LIKE ?");
        }
        if (destination != null) {
            sql.append(" AND destination LIKE ?");
        }
        if (departureDate != null) {
            sql.append(" AND departureDate = ?");
        }

        if (priceFilter != null && !priceFilter.equals("All")) {
            switch (priceFilter) {
                case "Under 100€":
                    sql.append(" AND price < 100");
                    break;
                case "100–300€":
                    sql.append(" AND price BETWEEN 100 AND 300");
                    break;
                case "Above 300€":
                    sql.append(" AND price > 300");
                    break;
            }
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (departure != null) {
                ps.setString(paramIndex++, "%" + departure + "%");
            }
            if (destination != null) {
                ps.setString(paramIndex++, "%" + destination + "%");
            }
            if (departureDate != null) {
                ps.setDate(paramIndex++, departureDate);
            }


            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                FlightModel flight = new FlightModel();
                flight.setFlight_id(rs.getInt("flight_id"));
                flight.setFlightNumber(rs.getString("flight_number"));
                flight.setAirline(rs.getString("airline"));
                flight.setOrigin(rs.getString("origin"));
                flight.setDestination(rs.getString("destination"));
                flight.setDepartureDate(rs.getDate("departureDate"));
                flight.setReturnDate(rs.getDate("return_date"));
                flight.setClassType(rs.getString("class_type"));
                flight.setStatus(rs.getString("status"));
                flight.setPrice(rs.getDouble("price"));
                flight.setCapacity(rs.getInt("capacity"));
                flight.setAdminStatus(rs.getString("admin_status"));
                flights.add(flight);
            }
        } catch (SQLException e) {
            System.out.println("Error searching flights: " + e.getMessage());
        }
        return flights;
    }
    public void approveFlight(int flightId) {
        String sql = "UPDATE flights SET admin_status = 'approved' WHERE flight_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error approving flight: " + e.getMessage());
        }
    }
    public void rejectFlight(int flightId) {
        String sql = "UPDATE flights SET admin_status = 'rejected' WHERE flight_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error rejecting flight: " + e.getMessage());
        }
    }
    public void approveAllPendingFlights() {
        String sql = "UPDATE flights SET admin_status = 'approved' WHERE admin_status = 'pending' OR admin_status IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int updatedRows = ps.executeUpdate();
            System.out.println("Approved " + updatedRows + " pending flights");
        } catch (SQLException e) {
            System.out.println("Error approving all pending flights: " + e.getMessage());
        }
    }
    public void rejectAllPendingFlights() {
        String sql = "UPDATE flights SET admin_status = 'rejected' WHERE admin_status = 'pending' OR admin_status IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int updatedRows = ps.executeUpdate();
            System.out.println("Rejected " + updatedRows + " pending flights");
        } catch (SQLException e) {
            System.out.println("Error rejecting all pending flights: " + e.getMessage());
        }
    }
    public void resetAllFlightStatuses() {
        String sql = "UPDATE flights SET admin_status = 'pending' WHERE admin_status IN ('approved', 'rejected')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int updatedRows = ps.executeUpdate();
            System.out.println("Reset " + updatedRows + " flights to pending status");
        } catch (SQLException e) {
            System.out.println("Error resetting all flight statuses: " + e.getMessage());
        }
    }
    public void resetFlightStatus(int flightId) {
        String sql = "UPDATE flights SET admin_status = 'pending' WHERE flight_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, flightId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error resetting flight status: " + e.getMessage());
        }
    }
    public void updateAllFlightStatuses() {
        String selectSql = "SELECT * FROM flights";
        String updateSql = "UPDATE flights SET status = ? WHERE flight_id = ?";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(selectSql);
             PreparedStatement ps = connection.prepareStatement(updateSql)) {

            while (rs.next()) {
                FlightModel flight = extractFlightFromResultSet(rs);
                String currentStatus = flight.getStatus();
                String calculatedStatus = flight.getCalculatedStatus();

                // Only update if status should change
                if (!currentStatus.equals(calculatedStatus) &&
                        !currentStatus.equals("Cancelled")) { // Never auto-update cancelled flights
                    ps.setString(1, calculatedStatus);
                    ps.setInt(2, flight.getFlight_id());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.out.println("Error updating flight statuses: " + e.getMessage());
        }
    }
    private FlightModel extractFlightFromResultSet(ResultSet rs) throws SQLException {
        FlightModel flight = new FlightModel();
        flight.setFlight_id(rs.getInt("flight_id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setAirline(rs.getString("airline"));
        flight.setOrigin(rs.getString("origin"));
        flight.setDestination(rs.getString("destination"));
        flight.setDepartureDate(rs.getDate("departureDate"));
        flight.setReturnDate(rs.getDate("return_date"));
        flight.setClassType(rs.getString("class_type"));
        flight.setStatus(rs.getString("status"));
        flight.setPrice(rs.getDouble("price"));
        flight.setCapacity(rs.getInt("capacity"));
        flight.setAdminStatus(rs.getString("admin_status"));
        return flight;
    }
    public Map<String, Integer> getFlightCountByAirline() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT airline, COUNT(*) as count FROM flights GROUP BY airline";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("airline"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting airline stats: " + e.getMessage());
        }
        return stats;
    }

    public Map<String, Integer> getFlightCountByStatus() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT status, COUNT(*) as count FROM flights GROUP BY status";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting status stats: " + e.getMessage());
        }
        return stats;
    }

    public Map<String, Integer> getPopularRoutes(int limit) {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT CONCAT(origin, ' → ', destination) as route, COUNT(*) as count " +
                "FROM flights GROUP BY route ORDER BY count DESC LIMIT ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("route"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting route stats: " + e.getMessage());
        }
        return stats;
    }

    public Map<String, Integer> getFlightsByMonth() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT DATE_FORMAT(departureDate, '%Y-%m') as month, COUNT(*) as count " +
                "FROM flights GROUP BY month";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("month"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.out.println("Error getting monthly stats: " + e.getMessage());
        }
        return stats;
    }
}
