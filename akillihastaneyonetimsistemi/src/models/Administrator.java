package models;
import models.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class Administrator extends Person {
    private String permissionLevel;

    // Constructor
    public Administrator(int id, String name, int age, String address, String tc, String permissionLevel) {
        super(id, name, age, address, tc);
        this.permissionLevel = permissionLevel;
    }

    // Getter ve Setter
    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    // Administrator'un kullanıcı yönetme fonksiyonu
    public void manageUsers() {
        System.out.println("Administrator " + getName() + " is managing users.");
        // Kullanıcı yönetimi fonksiyonu ekleyin
    }

    // Sistem ayarlarını güncelleme fonksiyonu
    public void updateSystemSettings() {
        System.out.println("Administrator " + getName() + " is updating system settings.");
        // Sistem ayarlarını güncelleme fonksiyonu ekleyin
    }

    // Administrator bilgilerini ekrana yazdırma
    @Override
    public void displayInfo() {
        super.displayInfo(); // Person sınıfındaki bilgileri yazdır
        System.out.println("Permission Level: " + permissionLevel);
    }

    // Administrator'u veritabanına ekleme
    public void addAdministratorToDatabase() {
        String sql = "INSERT INTO administrators (name, age, address, tc, permission_level) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, this.name);
            stmt.setInt(2, this.age);
            stmt.setString(3, this.address);
            stmt.setString(4, this.tc);
            stmt.setString(5, this.permissionLevel);

            stmt.executeUpdate();  // Veritabanına ekleme işlemi
            System.out.println("Administrator added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();  // Hata çıktısını yazdır
        }
    }

    public void manageReferrals() {
        String sql = "SELECT * FROM referrals WHERE status = 'Pending'";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String patientTc = rs.getString("patient_tc");
                String hospitalName = rs.getString("hospital_name");
                String referralReason = rs.getString("referral_reason");

                // Sevk bilgilerini göster
                System.out.println("ID: " + id + ", Hasta TC: " + patientTc +
                        ", Hedef Hastane: " + hospitalName +
                        ", Sebep: " + referralReason);

                System.out.println("Onayla (1) veya Reddet (2):");
                int choice = new Scanner(System.in).nextInt();

                // Kullanıcı seçimine göre sevk durumu
                String status = (choice == 1) ? "Approved" : "Rejected";

                // Sevk durumunu güncelle
                String updateSql = "UPDATE referrals SET status = ?, updated_at = NOW() WHERE id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setString(1, status);
                    updateStmt.setInt(2, id);
                    updateStmt.executeUpdate();
                }

                // Güncelleme işlemini logla
                String logSql = "INSERT INTO logs (action, details, created_at) VALUES ('Sevk Güncelleme', ?, NOW())";
                try (PreparedStatement logStmt = connection.prepareStatement(logSql)) {
                    String logDetails = "Sevk ID: " + id + ", Hasta TC: " + patientTc + ", Yeni Durum: " + status;
                    logStmt.setString(1, logDetails);
                    logStmt.executeUpdate();
                }

                // İşlem tamamlandı bilgisi
                System.out.println("Sevk işlemi tamamlandı. Durum: " + status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Sigorta bilgisi ekleme
    public void addInsuranceInfo(String insuranceType, String policyNumber, int coveragePercentage, String coverageDetails,String created_at) {
        String checkPolicySql = "SELECT COUNT(*) FROM insurance_info WHERE policy_number = ?";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkPolicySql)) {

            // Poliçe numarasının benzersiz olup olmadığını kontrol et
            checkStmt.setString(1, policyNumber);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Bu poliçe numarası zaten kayıtlı.");
                return;
            }

            // Sigorta bilgisi ekleme
            String sql = "INSERT INTO insurance_info (patient_tc, insurance_type, policy_number, coverage_percentage, coverage_details, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, this.tc); // Hasta T.C.
                stmt.setString(2, insuranceType);
                stmt.setString(3, policyNumber);
                stmt.setInt(4, coveragePercentage);
                stmt.setString(5, coverageDetails);


                stmt.executeUpdate();
                logAction("Sigorta bilgisi eklendi: " + insuranceType + ", " + policyNumber);
                System.out.println("Sigorta bilgisi başarıyla eklendi.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Sabit Sigorta Türleri
    private static final String[] INSURANCE_TYPES = {
            "Özel Sağlık Sigortası", "Devlet Sağlık Sigortası", "Tamamlayıcı Sigorta"
    };

    // Sigorta türü seçimi
    public static String selectInsuranceType(Scanner scanner) {
        System.out.println("Mevcut Sigorta Türleri:");
        for (int i = 0; i < INSURANCE_TYPES.length; i++) {
            System.out.println((i + 1) + " - " + INSURANCE_TYPES[i]);
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // Boş satırı temizle
        if (choice >= 1 && choice <= INSURANCE_TYPES.length) {
            return INSURANCE_TYPES[choice - 1];
        } else {
            System.out.println("Geçersiz seçim.");
            return null;
        }
    }
    // İşlem loglama
    private void logAction(String details) {
        String logSql = "INSERT INTO logs (action, details, created_at) VALUES ('Sigorta İşlemi', ?, NOW())";

        try (Connection connection = Database.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(logSql)) {

            stmt.setString(1, details);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Administrator'ün rolü
    @Override
    public String getRole() {
        return "Administrator";
    }
}
