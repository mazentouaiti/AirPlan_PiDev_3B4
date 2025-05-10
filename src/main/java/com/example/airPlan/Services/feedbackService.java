package com.example.airPlan.Services;

import com.example.airPlan.models.feedback;
import com.example.airPlan.Utiles.MyDatabse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class feedbackService  implements IService <feedback>{


        Connection con = MyDatabse.getInstance().getCon();


        public void add(feedback feed) {

            String sql = "INSERT INTO `feedback`(`titlefeed`, `statutfeed`, `datefeed`, `reponsefeed`) " +
                    "VALUES (?, ?, ?, ? )";

            try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setString(1, feed.getTitlefeed());
                preparedStatement.setString(2, feed.getStatutfeed());
                preparedStatement.setDate(3, feed.getDatefeed());
                preparedStatement.setString(4, feed.getReponsefeed());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'ajout : " + e.getMessage());
            }
        }

    // Update an existing feedback
    public void update(feedback feed) {
        String sql = "UPDATE feedback SET titlefeed=?, statutfeed=?, datefeed=?, reponsefeed=?, id_user=? WHERE idfeed=?";

        try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, feed.getTitlefeed());
            preparedStatement.setString(2, feed.getStatutfeed());
            preparedStatement.setDate(3, feed.getDatefeed());
            preparedStatement.setString(4, feed.getReponsefeed());
            preparedStatement.setInt(5, feed.getId_user()); // Set user ID
            preparedStatement.setInt(6, feed.getIdfeed()); // Set the ID for the WHERE clause

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Feedback updated successfully: " + feed.getIdfeed());
            } else {
                System.out.println("No feedback found with ID: " + feed.getIdfeed());
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour : " + e.getMessage());
            e.printStackTrace();
        }
    }

        // Delete a feedback by ID
        public void delete(int id) {
            String sql = "DELETE FROM feedback WHERE idfeed = ?";

            try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la suppression : " + e.getMessage());
            }
        }

        // Display all feedbacks
        public List<feedback> display() {
            String query = "SELECT * FROM `feedback`";
            List<feedback> feeds = new ArrayList<>();

            try (Statement statement = con.createStatement();
                 ResultSet rs = statement.executeQuery(query)) {

                while (rs.next()) {
                    feedback feed = new feedback();
                    feed.setIdfeed(rs.getInt("idfeed"));
                    feed.setTitlefeed(rs.getString("titlefeed"));
                    feed.setReponsefeed(rs.getString("reponsefeed"));
                    feed.setStatutfeed(rs.getString("statutfeed"));
                    feed.setDatefeed(rs.getDate("datefeed"));


                    feed.setId_user(rs.getInt("id_user"));
                    feeds.add(feed);
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'affichage : " + e.getMessage());
            }

            return feeds;
        }
    
}
