package models;
import models.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;


public class Staff extends Person {
    private String jobDescription; // Personelin görev tanımı
    private String password; // Şifre
    public Staff(int id, String name, int age, String address, String tc, String jobDescription,String password) {
        super(id, name, age, address, tc);
        this.jobDescription = jobDescription;
        this.password = password;
    }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getJobDescription() { return jobDescription; }
    public void setJobDescription(String jobDescription) { this.jobDescription = jobDescription; }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Görev Tanımı: " + jobDescription);
    }

    @Override
    public String getRole() {
        return "Staff";
    }

    public void addStaffToDatabase() {
        String sql = "INSERT INTO staff (name, age, address, tc, job_description, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, this.name);
            stmt.setInt(2, this.age);
            stmt.setString(3, this.address);
            stmt.setString(4, this.tc);
            stmt.setString(5, this.jobDescription);
            stmt.setString(6, this.password);

            stmt.executeUpdate();
            System.out.println("Personel veritabanına başarıyla eklendi.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public static Staff login(String tc, String password) {

        String sql = "SELECT * FROM staff WHERE tc = ? AND password = ?";
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, tc);
            stmt.setString(2, password);

            var rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                String jobDescription = rs.getString("job_description");

                return new Staff(id, name, age, address, tc, jobDescription, password);
            } else {
                System.out.println("Hatalı TC veya şifre.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
