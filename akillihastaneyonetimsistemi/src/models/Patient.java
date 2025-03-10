package models;
import models.Database;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Scanner;

public class Patient extends Person {
    private String medicalHistory;
    private ArrayList<Appointment> appointmentList;

    public Patient(int id, String name, int age, String address, String tc, String medicalHistory) {
        super(id, name, age, address, tc);
        this.medicalHistory = medicalHistory;
        this.appointmentList = new ArrayList<>();
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public void addAppointment(Appointment appointment) {
        appointmentList.add(appointment);
        System.out.println("Added appointment: " + appointment.getDetails());
    }

    public void displayAppointments() {
        System.out.println("Appointments for " + getName() + ":");
        for (Appointment appointment : appointmentList) {
            System.out.println(appointment.getDetails());
        }
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Medical History: " + medicalHistory);
    }

    public void addPatientToDatabase() {
        String sql = "INSERT INTO patients (name, tc, address, medical_history,age) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, this.name);           // name
            stmt.setString(2, this.tc);             // tc
            stmt.setString(3, this.address);        // address
            stmt.setString(4, this.medicalHistory); // medical_history
            stmt.setInt(5, this.age);               // age

            stmt.executeUpdate();
            System.out.println("Patient added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // T.C. Kimlik numarasına göre hasta arama
    public static Patient getPatientByTc(String tc) {
        String sql = "SELECT * FROM patients WHERE tc = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tc);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Hasta bulundu, hasta bilgilerini döndürüyoruz
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                String medicalHistory = rs.getString("medical_history");

                return new Patient(id, name, age, address, tc, medicalHistory);
            } else {
                // Hasta bulunamadı
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Sağlık geçmişini güncelle
    public static void updateMedicalHistory(String patientTc, String newDiagnosis) {
        String sqlFetch = "SELECT medical_history FROM patients WHERE tc = ?";
        String sqlUpdate = "UPDATE patients SET medical_history = ? WHERE tc = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement fetchStmt = connection.prepareStatement(sqlFetch);
             PreparedStatement updateStmt = connection.prepareStatement(sqlUpdate)) {

            // Mevcut sağlık geçmişini al
            fetchStmt.setString(1, patientTc);
            ResultSet rs = fetchStmt.executeQuery();
            String currentHistory = "";
            if (rs.next()) {
                currentHistory = rs.getString("medical_history");
            }

            // Yeni teşhisi mevcut sağlık geçmişine ekle
            String updatedHistory = (currentHistory == null || currentHistory.isEmpty())
                    ? newDiagnosis
                    : currentHistory + ", " + newDiagnosis;

            // Güncellemeyi yap
            updateStmt.setString(1, updatedHistory);
            updateStmt.setString(2, patientTc);
            updateStmt.executeUpdate();

            System.out.println("Sağlık geçmişi başarıyla güncellendi.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sigorta bilgisi görüntüleme
    public static void displayInsuranceInfo(String patientTc) {
        String sql = "SELECT * FROM insurance_info WHERE patient_tc = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, patientTc);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println("Sigorta Türü: " + rs.getString("insurance_type"));
                System.out.println("Poliçe Numarası: " + rs.getString("policy_number"));
                System.out.println("Kapsam Yüzdesi: " + rs.getInt("coverage_percentage"));
                System.out.println("Kapsam Detayları: " + rs.getString("coverage_details"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static Patient getPatientByTcAndPassword(String tc, String password) {
        String sql = "SELECT * FROM patients WHERE tc = ? AND password = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, tc);
            stmt.setString(2, password);  // Şifreyi de sorguya ekliyoruz
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Hasta bilgilerini döndürüyoruz
                return new Patient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("address"),
                        rs.getString("tc"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;  // Hasta bulunamadı veya şifre yanlış
    }



    @Override
    public String getRole() {
        return "Patient";
    }
}


