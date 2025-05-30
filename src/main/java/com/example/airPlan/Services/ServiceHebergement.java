package com.example.airPlan.Services;


import com.example.airPlan.models.Hebergement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import com.example.airPlan.Utiles.DBConnection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ServiceHebergement {

    private final Connection cnx;

    public ServiceHebergement() {
        cnx = DBConnection.getConnection();
    }

    public void ajouter(Hebergement h) {
        String req = "INSERT INTO hebergement (name, type, city, address, country, pricePerNight, disponibility, photo, album, description, options, rating, capacity, status, favoris) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, h.getName());
            ps.setString(2, h.getType());
            ps.setString(3, h.getCity());
            ps.setString(4, h.getAddress());
            ps.setString(5, h.getCountry());
            ps.setDouble(6, h.getPricePerNight());
            ps.setBoolean(7, h.isDisponibility());
            ps.setString(8, h.getPhoto());
            ps.setString(9, h.getAlbum());
            ps.setString(10, h.getDescription());
            ps.setString(11, h.getOptions());
            ps.setInt(12, h.getRating());
            ps.setInt(13, h.getCapacity());
            ps.setString(14, h.getStatus());
            ps.setString(15, h.getFavoris() != null ? h.getFavoris() : "unliked");
            ps.executeUpdate();
            System.out.println("Accommodation Added Sucessfully");
        } catch (SQLException e) {
            System.err.println("Error while adding : " + e.getMessage());
        }
    }

    public void modifier(Hebergement h) {
        String req = "UPDATE hebergement SET name=?, type=?, city=?, address=?, country=?, pricePerNight=?, disponibility=?, photo=?, album=?, description=?, options=?, rating=?, capacity=?, status=?, favoris=? WHERE acc_id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, h.getName());
            ps.setString(2, h.getType());
            ps.setString(3, h.getCity());
            ps.setString(4, h.getAddress());
            ps.setString(5, h.getCountry());
            ps.setDouble(6, h.getPricePerNight());
            ps.setBoolean(7, h.isDisponibility());
            ps.setString(8, h.getPhoto());
            ps.setString(9, h.getAlbum());
            ps.setString(10, h.getDescription());
            ps.setString(11, h.getOptions());
            ps.setInt(12, h.getRating());
            ps.setInt(13, h.getCapacity());
            ps.setString(14, h.getStatus());
            ps.setString(15, h.getFavoris());
            ps.setInt(16, h.getId());
            ps.executeUpdate();
            System.out.println("Accommodation Updated Sucessfully");
        } catch (SQLException e) {
            System.err.println("Error While Updating : " + e.getMessage());
        }
    }

    public void supprimer(int acc_id) {
        String req = "DELETE FROM hebergement WHERE acc_id=?";
        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, acc_id);
            ps.executeUpdate();
            System.out.println("Accommodation deleted Sucessfully !");
        } catch (SQLException e) {
            System.err.println("Error while deleting : " + e.getMessage());
        }
    }

    public List<Hebergement> afficher() {
        List<Hebergement> list = new ArrayList<>();
        String req = "SELECT * FROM hebergement";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Hebergement h = new Hebergement(
                        rs.getInt("acc_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("city"),
                        rs.getString("address"),
                        rs.getString("country"),
                        rs.getDouble("pricePerNight"),
                        rs.getBoolean("disponibility"),
                        rs.getString("photo"),
                        rs.getString("album"),
                        rs.getString("description"),
                        rs.getString("options"),
                        rs.getInt("rating"),
                        rs.getInt("capacity"),
                        rs.getString("status"),
                        rs.getString("favoris")
                );
                list.add(h);
            }
            System.out.println("Accommodation displayed Sucessfully !");
        } catch (SQLException e) {
            System.err.println("Error while displaying : " + e.getMessage());
        }
        return list;
    }

    public List<Hebergement> getHebergementsMisenAvant() {
        return afficher().stream()
                .filter(h -> "accepted".equals(h.getStatus()))
                .collect(Collectors.toList());
    }

    public void updateStatus(int id, String newStatus) throws SQLException {
        String sql = "UPDATE hebergement SET status = ? WHERE acc_id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateFavoris(int id, String favorisStatus) throws SQLException {
        String sql = "UPDATE hebergement SET favoris = ? WHERE acc_id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, favorisStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public Map<String, Long> getStatusStatistics() throws SQLException {
        Map<String, Long> stats = new HashMap<>();
        String sql = "SELECT LOWER(status) as status, COUNT(*) as count FROM hebergement GROUP BY LOWER(status)";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getLong("count"));
            }
        }
        return stats;
    }
}