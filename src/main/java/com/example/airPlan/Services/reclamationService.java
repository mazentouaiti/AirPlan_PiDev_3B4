package com.example.airPlan.Services;

import com.example.airPlan.models.reclamation;
import com.example.airPlan.Utiles.MyDatabse;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class reclamationService implements IService<reclamation> {

    Connection con = MyDatabse.getInstance().getCon();

    public reclamationService() {
    }

    public void add(reclamation rec) {
        String sql = "INSERT INTO `reclamation`(`type`, `datee`, `description`, `note`, `statut`) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, rec.getType());
            preparedStatement.setDate(2, new java.sql.Date(rec.getDatee().getTime()));
            preparedStatement.setString(3, rec.getDescription());
            preparedStatement.setInt(4, rec.getNote());
            preparedStatement.setString(5, rec.getStatut());
            preparedStatement.executeUpdate();

            // Retrieve the generated ID
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    rec.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void update(reclamation rec) {
        String sql = "UPDATE reclamation SET type=?, datee=?, description=?, note=?, statut=? WHERE id=?";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, rec.getType());
            preparedStatement.setDate(2, new java.sql.Date(rec.getDatee().getTime()));
            preparedStatement.setString(3, rec.getDescription());
            preparedStatement.setInt(4, rec.getNote());
            preparedStatement.setString(5, rec.getStatut());
            preparedStatement.setInt(6, rec.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Reclamation updated successfully: " + rec.getId());
            } else {
                System.out.println("No reclamation found with ID: " + rec.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM reclamation WHERE id = ?";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Reclamation deleted successfully: " + id);
            } else {
                System.out.println("No reclamation found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<reclamation> display() {
        String query = "SELECT * FROM `reclamation`";
        List<reclamation> reclamations = new ArrayList<>();

        try (Statement statement = this.con.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                reclamation reclamation = new reclamation();
                reclamation.setId(rs.getInt("id"));
                reclamation.setType(rs.getString("type"));
                reclamation.setDatee(rs.getDate("datee"));  // Changed from "date" to "datee" to match your table
                reclamation.setNote(rs.getInt("note"));
                reclamation.setDescription(rs.getString("description"));
                reclamation.setStatut(rs.getString("statut"));  // Changed case to match standard SQL

                reclamations.add(reclamation);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reclamations from the database: " + e.getMessage());
            e.printStackTrace();
        }

        return reclamations;
    }

    public Map<String, Integer> fetchReclamationCountsByStatus() {
        Map<String, Integer> reclamationCounts = new HashMap<>();

        String query = "SELECT statut, COUNT(*) AS count FROM reclamation GROUP BY statut";
        try (PreparedStatement statement = con.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String statusStr = resultSet.getString("statut");
                int count = resultSet.getInt("count");
                reclamationCounts.put(statusStr, count);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reclamation counts by status: " + e.getMessage());
            e.printStackTrace();
        }

        return reclamationCounts;
    }

    public Map<String, Integer> fetchReclamationCountsByType() {
        Map<String, Integer> reclamationCounts = new HashMap<>();

        String query = "SELECT type, COUNT(*) AS count FROM reclamation GROUP BY type";
        try (PreparedStatement statement = con.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String typeStr = resultSet.getString("type");
                int count = resultSet.getInt("count");
                reclamationCounts.put(typeStr, count);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reclamation counts by type: " + e.getMessage());
            e.printStackTrace();
        }

        return reclamationCounts;
    }
}