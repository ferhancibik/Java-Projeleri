package models;

import models.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class Doctor extends Person {
    private String specialization;
    private String workingHours;

    public Doctor(int id, String name, int age, String address, String tc, String specialization, String workingHours) {
        super(id, name, age, address, tc);
        this.specialization = specialization;
        this.workingHours = workingHours;
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getWorkingHours() { return workingHours; }
    public void setWorkingHours(String workingHours) { this.workingHours = workingHours; }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Specialization: " + specialization + ", Working Hours: " + workingHours);
    }

    // Doktoru veritabanına eklemek için metod
    public void addDoctorToDatabase() {
        String sql = "INSERT INTO doctors (name, age, address, tc, specialization, working_hours) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, this.name);
            stmt.setInt(2, this.age);
            stmt.setString(3, this.address);
            stmt.setString(4, this.tc);
            stmt.setString(5, this.specialization);
            stmt.setString(6, this.workingHours);

            stmt.executeUpdate();
            System.out.println("Doctor added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // T.C. kimlik numarasına göre doktoru veritabanından almak
    public static Doctor getDoctorByTc(String tc) {
        String sql = "SELECT * FROM doctors WHERE tc = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tc);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                String specialization = rs.getString("specialization");
                String workingHours = rs.getString("working_hours");

                return new Doctor(id, name, age, address, tc, specialization, workingHours);
            } else {
                return null;  // Doktor bulunamadı
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Uzmanlık alanına göre doktorları veritabanından almak
    public static List<Doctor> getDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE specialization = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, specialization);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                String tc = rs.getString("tc");
                String workingHours = rs.getString("working_hours");

                doctors.add(new Doctor(id, name, age, address, tc, specialization, workingHours));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doctors;
    }

    // Veritabanından tüm doktorları almak için metod
    public static List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String address = rs.getString("address");
                String tc = rs.getString("tc");
                String specialization = rs.getString("specialization");
                String workingHours = rs.getString("working_hours");

                doctors.add(new Doctor(id, name, age, address, tc, specialization, workingHours));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doctors;
    }

    public void addAppointment(Scanner scanner) {
        System.out.println("Hasta T.C. Kimlik numarası:");
        String patientTc = scanner.nextLine();

        Patient patient = Patient.getPatientByTc(patientTc);
        if (patient == null) {
            System.out.println("Hasta bulunamadı.");
            return;
        }

        // Randevu Tarihi ve Saati alıyoruz
        System.out.println("Randevu Tarihi (yyyy-MM-dd):");
        String date = scanner.nextLine();
        System.out.println("Randevu Saati (HH:mm):");
        String time = scanner.nextLine();

        // Randevu nesnesi oluşturuluyor ve veritabanına ekleniyor
        Appointment appointment = new Appointment(0, date, time, this.name, this.specialization,
                patient.getTc(), "Henüz belirlenmemiş", null, this.workingHours, patient.getName());

        // Doktor T.C.'sini appointments tablosuna ekliyoruz
        appointment.addAppointmentToDatabase(this.tc);  // this.tc burada doktorun T.C.'sini ifade eder

        System.out.println("Randevunuz başarıyla oluşturuldu.");
    }


    public void requestReferral(String patientTc, String hospitalName, String referralReason) {
        if (hospitalName == null || hospitalName.isEmpty()) {
            System.out.println("Hedef hastane bilgisi boş olamaz.");
            return;
        }
        if (referralReason == null || referralReason.isEmpty()) {
            System.out.println("Sevk sebebi boş olamaz.");
            return;
        }

        // 'status' sütununu sorgudan çıkartıyoruz çünkü varsayılan değeri 'Bekliyor' olarak ayarlanmış
        String sql = "INSERT INTO referrals (patient_tc, doctor_tc, hospital_name, referral_reason, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, patientTc);
            stmt.setString(2, this.tc);  // Doktorun T.C.'sini kullanıyoruz
            stmt.setString(3, hospitalName);
            stmt.setString(4, referralReason);

            stmt.executeUpdate();
            System.out.println("Sevk talebi başarıyla oluşturuldu.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Doctor getDoctorByTcAndPassword(String tc, String password) {
        String sql = "SELECT * FROM doctors WHERE tc = ? AND password = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, tc);
            stmt.setString(2, password);  // Şifreyi de sorguya ekliyoruz
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Doktor bilgilerini döndürüyoruz
                return new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("address"),
                        rs.getString("tc"),
                        rs.getString("specialization"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;  // Doktor bulunamadı veya şifre yanlış
    }
    public void viewLabResults(Scanner scanner) {
        System.out.println("Hasta T.C. Kimlik numarasını girin:");
        String patientTc = scanner.nextLine();

        // Laboratuvar sonuçlarını görüntüleme
        Laboratory.displayLabResults(patientTc);
    }

    @Override
    public String getRole() {
        return "Doctor";
    }
}
