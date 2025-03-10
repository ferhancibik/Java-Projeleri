package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Laboratory {
    public static void addLabResult(String patientTc, String testType, String testResult) {
        String sql = "INSERT INTO lab_results (patient_tc, test_type, test_result, created_at) VALUES (?, ?, ?, NOW())";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, patientTc);
            stmt.setString(2, testType);
            stmt.setString(3, testResult);
            stmt.executeUpdate();
            System.out.println("Laboratuvar sonucu başarıyla eklendi.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void displayLabResults(String patientTc) {
        String sql = "SELECT * FROM lab_results WHERE patient_tc = ? ORDER BY created_at DESC";


        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, patientTc);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("Test Türü: " + rs.getString("test_type"));
                System.out.println("Sonuç: " + rs.getString("test_result"));
                System.out.println("Oluşturulma Tarihi: " + rs.getString("created_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static final String[] TEST_TYPES = {
            "Kan Testi", "MR", "Röntgen", "Ultrason", "CT Tarama"
    };

    public static String[] getTestTypes() {
        return TEST_TYPES;
    }

    public static void displayTestTypes() {
        System.out.println("Mevcut Test Türleri:");
        for (int i = 0; i < TEST_TYPES.length; i++) {
            System.out.println((i + 1) + " - " + TEST_TYPES[i]);
        }
    }

}

