package org.pharmago.bdd;

import java.sql.*;

public class test {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                String query = "SELECT * FROM client";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    System.out.println(rs.getInt("codeClt") + " - " +
                            rs.getString("prenom") + " - " );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
