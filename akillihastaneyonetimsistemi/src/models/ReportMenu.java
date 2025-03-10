package models;
import models.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;



public class ReportMenu {
    // Hasta raporu oluşturma metodu
    public void generatePatientReport(Scanner scanner) {
        System.out.println("Hasta raporu oluşturuluyor...");
        System.out.println("Başlangıç tarihi (yyyy-MM-dd): ");
        String startDate = scanner.nextLine();
        System.out.println("Bitiş tarihi (yyyy-MM-dd): ");
        String endDate = scanner.nextLine();

        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date BETWEEN ? AND ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int patientCount = rs.getInt(1);
                System.out.println("Belirtilen tarihler arasında " + patientCount + " hasta randevusu var.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Randevu raporu oluşturma metodu
    public void generateAppointmentReport(Scanner scanner) {
        System.out.println("Randevu raporu oluşturuluyor...");
        System.out.println("Başlangıç tarihi (yyyy-MM-dd): ");
        String startDate = scanner.nextLine();
        System.out.println("Bitiş tarihi (yyyy-MM-dd): ");
        String endDate = scanner.nextLine();

        String sql = "SELECT doctor_name, COUNT(*) FROM appointments WHERE appointment_date BETWEEN ? AND ? GROUP BY doctor_name";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String doctorName = rs.getString("doctor_name");
                int appointmentCount = rs.getInt(2);
                System.out.println(doctorName + " - " + appointmentCount + " randevu.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Personel raporu oluşturma metodu
    public void generateStaffReport(Scanner scanner) {
        System.out.println("Personel raporu oluşturuluyor...");
        String sql = "SELECT name, job_description FROM staff";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String jobDescription = rs.getString("job_description");
                System.out.println(name + " - " + jobDescription);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Gelir raporu oluşturma metodu
    public void generateRevenueReport(Scanner scanner) {
        System.out.println("Gelir raporu oluşturuluyor...");
        System.out.println("Başlangıç tarihi (yyyy-MM-dd): ");
        String startDate = scanner.nextLine();
        System.out.println("Bitiş tarihi (yyyy-MM-dd): ");
        String endDate = scanner.nextLine();

        String sql = "SELECT SUM(price) FROM appointments WHERE appointment_date BETWEEN ? AND ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double totalRevenue = rs.getDouble(1);
                System.out.println("Belirtilen tarihler arasında toplam gelir: " + totalRevenue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
