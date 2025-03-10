package models;
import java.util.Scanner;

public class ComplaintMapping {
    // Şikayet listesi
    private static final String[] COMPLAINTS = {
            "Kalp Problemleri", "Kemik Kırığı", "Cilt Sorunları", "Baş Ağrısı", "Ameliyat Gereksinimi"
    };

    // Şikayet listesi gösterme
    public static void displayComplaints() {
        System.out.println("Mevcut Şikayetler:");
        for (int i = 0; i < COMPLAINTS.length; i++) {
            System.out.println((i + 1) + " - " + COMPLAINTS[i]);
        }
    }

    // Kullanıcının şikayet seçmesine izin verme
    public static String selectComplaint(Scanner scanner) {
        displayComplaints();
        int choice;
        while (true) {
            System.out.print("Bir şikayet seçin (1-" + COMPLAINTS.length + "): ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Yeni satırı temizle
            if (choice >= 1 && choice <= COMPLAINTS.length) {
                return COMPLAINTS[choice - 1]; // Seçilen şikayeti döndür
            } else {
                System.out.println("Geçersiz seçim, lütfen tekrar deneyin.");
            }
        }
    }

    // Şikayeti uzmanlık alanına eşleştirme
    public static String mapToSpecialization(String complaint) {
        switch (complaint.toLowerCase()) {
            case "kalp problemleri":
                return "Kardiyoloji";
            case "kemik kırığı":
                return "Ortopedi";
            case "cilt sorunları":
                return "Dermatoloji";
            case "baş ağrısı":
                return "Nöroloji";
            case "ameliyat gereksinimi":
                return "Genel Cerrahi";
            default:
                return "Genel Cerrahi";
        }
    }
}

