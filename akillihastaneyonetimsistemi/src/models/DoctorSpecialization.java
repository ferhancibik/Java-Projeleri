package models;
import java.util.Scanner;

public class DoctorSpecialization {
    // Uzmanlık alanları listesi
    private static final String[] SPECIALIZATIONS = {
            "Kardiyoloji", "Ortopedi", "Dermatoloji", "Nöroloji", "Genel Cerrahi"
    };

    // Uzmanlık alanlarını listeleyen metod
    public static void displaySpecializations() {
        System.out.println("Mevcut Uzmanlık Alanları:");
        for (int i = 0; i < SPECIALIZATIONS.length; i++) {
            System.out.println((i + 1) + " - " + SPECIALIZATIONS[i]);
        }
    }

    // Kullanıcının bir uzmanlık alanı seçmesini sağlayan metod
    public static String selectSpecialization(Scanner scanner) {
        displaySpecializations();
        int choice;
        while (true) {
            System.out.print("Bir uzmanlık alanı seçin (1-" + SPECIALIZATIONS.length + "): ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Yeni satırı temizle
            if (choice >= 1 && choice <= SPECIALIZATIONS.length) {
                return SPECIALIZATIONS[choice - 1]; // Seçilen uzmanlık alanını döndür
            } else {
                System.out.println("Geçersiz seçim, lütfen tekrar deneyin.");
            }
        }
    }
}
