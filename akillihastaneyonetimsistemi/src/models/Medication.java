package models;
import models.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class Medication {
    private String medicationName;  // İlacın adı (örn: "Parol")
    private String dosage;          // İlacın dozajı (örn: "500mg")
    private int stock;              // İlacın mevcut stok miktarı

         public Medication(String medicationName, String dosage, int stock) {
            this.medicationName = medicationName;
            this.dosage = dosage;
            this.stock = stock;
        }

        public String getMedicationName() {
            return medicationName;
        }

        public void setMedicationName(String medicationName) {
            this.medicationName = medicationName;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public void updateStock(int quantity) {
            this.stock += quantity;
        }

        public boolean isAvailable() {
            return stock > 0;
        }
    // İlaç bilgilerini veritabanına ekler
    public void addMedicationToDatabase() {
        String sql = "INSERT INTO medications (name, dosage, stock) VALUES (?, ?, ?)";
        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, this.medicationName);
            stmt.setString(2, this.dosage);
            stmt.setInt(3, this.stock);
            stmt.executeUpdate();
            System.out.println("İlaç başarıyla veritabanına eklendi.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void displayMedicationInfo() {
            System.out.println("İlaç Adı: " + medicationName);
            System.out.println("Dozaj: " + dosage);
            System.out.println("Stok: " + stock);
        }


}


