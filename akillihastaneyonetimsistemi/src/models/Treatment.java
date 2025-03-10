package models;
import models.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class Treatment {
    private int id; // Tedavi ID'si
    private String treatmentName; // Tedavi adı
    private String treatmentDescription; // Tedavi açıklaması
    private Date startDate; // Başlangıç tarihi
    private Date endDate; // Bitiş tarihi
    private Doctor doctor; // Tedaviyi uygulayan doktor

    public Treatment(int id, String treatmentName, String treatmentDescription, Date startDate, Date endDate, Doctor doctor) {
        this.id = id;
        this.treatmentName = treatmentName;
        this.treatmentDescription = treatmentDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.doctor = doctor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTreatmentName() { return treatmentName; }
    public void setTreatmentName(String treatmentName) { this.treatmentName = treatmentName; }

    public String getTreatmentDescription() { return treatmentDescription; }
    public void setTreatmentDescription(String treatmentDescription) { this.treatmentDescription = treatmentDescription; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    // Tedaviyi veritabanına ekler
    public void addTreatmentToDatabase() {
        String sql = "INSERT INTO treatments (treatment_name, treatment_description, start_date, end_date, doctor_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, this.treatmentName);
            stmt.setString(2, this.treatmentDescription);
            stmt.setDate(3, this.startDate);
            stmt.setDate(4, this.endDate);
            stmt.setInt(5, this.doctor.getId()); // Doktor ID'si

            stmt.executeUpdate();
            System.out.println("Tedavi başarıyla veritabanına eklendi.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayTreatmentInfo() {
        System.out.println("Tedavi ID: " + id);
        System.out.println("Tedavi Adı: " + treatmentName);
        System.out.println("Açıklama: " + treatmentDescription);
        System.out.println("Başlangıç Tarihi: " + startDate);
        System.out.println("Bitiş Tarihi: " + endDate);
        if (doctor != null) {
            System.out.println("Doktor: " + doctor.getName());
        }
    }
}
