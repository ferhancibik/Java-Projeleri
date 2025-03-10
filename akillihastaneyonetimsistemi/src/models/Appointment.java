package models;
import models.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Appointment {
    private int id;
    private String appointmentDate;  // Randevu tarihi
    private String appointmentTime;  // Randevu saati
    private String doctorName;       // Doktor adı
    private String patientName;      // Hasta adı
    private String patientTc;        // Hasta TC (foreign key)
    private String complaint;        // Şikayet
    private String specialization;   // Doktor uzmanlık alanı
    private String workingHours;     // Doktor çalışma saatleri
    private String diagnosis;        // Teşhis

    // Constructor: Randevu oluşturma için
    public Appointment(int id, String appointmentDate, String appointmentTime, String doctorName,
                       String specialization, String patientTc, String complaint, String diagnosis,
                       String workingHours, String patientName) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.patientTc = patientTc;
        this.complaint = complaint;
        this.diagnosis = diagnosis;
        this.workingHours = workingHours;
        this.patientName = patientName;
    }
    public void addAppointmentToDatabase(String doctorTc) {
        String sql = "INSERT INTO appointments (appointment_date, appointment_time, doctor_name, specialization, working_hours, patient_name, patient_tc, complaint, doctor_tc) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, this.appointmentDate);  // appointment_date -> String
            stmt.setString(2, this.appointmentTime);  // appointment_time -> String
            stmt.setString(3, this.doctorName);       // doctor_name -> String
            stmt.setString(4, this.specialization);   // specialization -> String
            stmt.setString(5, this.workingHours);     // working_hours -> String
            stmt.setString(6, this.patientName);      // patient_name -> String
            stmt.setString(7, this.patientTc);        // patient_tc -> String
            stmt.setString(8, this.complaint);        // complaint -> String
            stmt.setString(9, doctorTc);              // doctor_tc -> String (doktor T.C.)

            stmt.executeUpdate();
            System.out.println("Randevu başarıyla eklendi.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Hasta T.C. ve Doktor T.C. ile randevuları getir
    public static List<Appointment> getAppointmentsByPatientTc(String patientTc, String doctorTc) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patient_tc = ? AND doctor_tc = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, patientTc);
            stmt.setString(2, doctorTc);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String appointmentDate = rs.getString("appointment_date");
                String appointmentTime = rs.getString("appointment_time");
                String doctorName = rs.getString("doctor_name");
                String specialization = rs.getString("specialization");
                String workingHours = rs.getString("working_hours");
                String patientName = rs.getString("patient_name");
                String complaint = rs.getString("complaint");

                appointments.add(new Appointment(id, appointmentDate, appointmentTime, doctorName, specialization,
                        rs.getString("patient_tc"), complaint, rs.getString("diagnosis"), workingHours, patientName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    public void addDiagnosis(String diagnosis) {
        // Randevuya teşhis ekleme
        String sqlUpdateAppointment = "UPDATE appointments SET diagnosis = ? WHERE id = ?";

        // Hastanın sağlık geçmişine teşhis ekleme
        String sqlUpdatePatient = "UPDATE patients SET medical_history = CONCAT(IFNULL(medical_history, ''), ?, ' ') WHERE tc = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmtAppointment = connection.prepareStatement(sqlUpdateAppointment);
             PreparedStatement stmtPatient = connection.prepareStatement(sqlUpdatePatient)) {

            // 1. Randevuya teşhis ekleniyor
            stmtAppointment.setString(1, diagnosis);  // Teşhis bilgisini randevuya ekliyoruz
            stmtAppointment.setInt(2, this.id);  // Randevu ID'sini kullanıyoruz
            stmtAppointment.executeUpdate();

            // 2. Hastanın sağlık geçmişine teşhis ekleniyor
            stmtPatient.setString(1, diagnosis);  // Hastalığı sağlık geçmişine ekliyoruz
            stmtPatient.setString(2, this.patientTc);  // Hasta T.C. kimliğine göre sağlık geçmişini güncelliyoruz
            stmtPatient.executeUpdate();

            System.out.println("Teşhis randevuya ve hastanın sağlık geçmişine eklendi.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Randevu detaylarını döndüren metod
    public String getDetails() {
        return "ID: " + id +
                ", Tarih: " + appointmentDate +
                ", Saat: " + appointmentTime +
                ", Doktor: " + doctorName +
                ", Hasta: " + patientName +
                ", Şikayet: " + complaint +
                ", Teşhis: " + (diagnosis != null ? diagnosis : "Henüz girilmedi");
    }

    // Getter ve Setter metodları
    public int getId() { return id; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getDoctorName() { return doctorName; }
    public String getPatientName() { return patientName; }
    public String getPatientTc() { return patientTc; }
    public String getComplaint() { return complaint; }
    public String getSpecialization() { return specialization; }
    public String getWorkingHours() { return workingHours; }
    public String getDiagnosis() { return diagnosis; }
}

