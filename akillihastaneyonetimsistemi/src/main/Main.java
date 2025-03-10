package main;

import models.Database;

import java.util.Scanner;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.Doctor;
import models.Medication;
import models.DoctorSpecialization;
import models.Person;
import models.Patient;
import models.ReportMenu;
import models.Staff;
import models.Treatment;
import models.Administrator;
import models.Appointment;
import models.ComplaintMapping;
import models.Laboratory;

import javax.swing.*;


public class Main {

     public static void main(String[] args) {
          Database db = Database.getInstance();
          Scanner scanner = new Scanner(System.in);

          while (true) {
               System.out.println("Lütfen eklemek istediğiniz türü seçin:");
               System.out.println("1 - Yönetici İşlemleri");
               System.out.println("2 - Doktor İşlemleri");
               System.out.println("3 - Hasta Girişi");
               System.out.println("4 - Personel Ekle");
               System.out.println("5 - Personel Girişi");
               System.out.println("6 - Çıkış");
               System.out.print("Seçiminiz: ");

               int choice = scanner.nextInt();
               scanner.nextLine();

               switch (choice) {
                    case 1:
                         adminLogin(scanner);
                         break;
                    case 2:
                         doctorLogin(scanner);
                         break;
                    case 3:
                         patientLogin(scanner);
                         break;
                    case 4:
                         addStaff(scanner);
                         break;
                    case 5:
                         staffLogin(scanner);
                         break;
                    case 6:
                         System.out.println("Çıkılıyor...");
                         db.closeConnection();
                         return;
                    default:
                         System.out.println("Geçersiz seçim, lütfen tekrar deneyin.");
               }
          }
     }

     private static void doctorLogin(Scanner scanner) {
          System.out.println("Doktor T.C. Kimlik Girin:");
          String doctorTc = scanner.nextLine();

          System.out.println("Şifrenizi girin:");
          String password = scanner.nextLine();

          Doctor doctor = Doctor.getDoctorByTcAndPassword(doctorTc, password);

          if (doctor == null) {
               System.out.println("Doktor bulunamadı veya şifre yanlış!");
               return;
          }

          doctorMenu(scanner, doctor);
     }


     private static void doctorMenu(Scanner scanner, Doctor doctor) {
          while (true) {
               System.out.println("Doktor Menüsü:");
               System.out.println("1 - Randevu Ekle");
               System.out.println("2 - Teşhis Ekle");
               System.out.println("3 - Sevk Talebi Gönder");
               System.out.println("4 - Laboratuvar Sonuçlarını Görüntüle");
               System.out.println("5 - Çıkış");
               System.out.print("Seçiminiz: ");

               int choice = scanner.nextInt();
               scanner.nextLine();

               switch (choice) {
                    case 1:
                         doctor.addAppointment(scanner); // Doktor sınıfındaki addAppointment metodunu çağırıyoruz
                         break;
                    case 2:
                         addDiagnosis(scanner, doctor); // Teşhis ekleme
                         break;
                    case 3:
                         requestReferral(scanner, doctor); // Sevk talebi oluşturma
                         break;
                    case 4:
                         doctor.viewLabResults(scanner); // Laboratuvar sonuçlarını görüntüleme
                         break;
                    case 5:
                         System.out.println("Çıkılıyor...");
                         return;
                    default:
                         System.out.println("Geçersiz seçim!");
               }
          }
     }
     public static void requestReferral(Scanner scanner, Doctor doctor) {
          System.out.println("Hastanın T.C. Kimlik numarasını girin:");
          String patientTc = scanner.nextLine();

          // Hasta bilgilerini alıyoruz
          Patient patient = Patient.getPatientByTc(patientTc);
          if (patient == null) {
               System.out.println("Hasta bulunamadı.");
               return;
          }

          // Hedef hastane adı ve sevk sebebini alıyoruz
          System.out.println("Hedef hastane adını girin:");
          String hospitalName = scanner.nextLine();
          System.out.println("Sevk sebebini girin:");
          String referralReason = scanner.nextLine();

          // Sevk talebini veritabanına kaydediyoruz
          doctor.requestReferral(patientTc, hospitalName, referralReason);

          System.out.println("Sevk talebi başarıyla oluşturuldu.");
     }

     private static void patientLogin(Scanner scanner) {
          System.out.println("Hasta T.C. Kimlik Girin:");
          String patientTc = scanner.nextLine();

          System.out.println("Şifrenizi girin:");
          String password = scanner.nextLine();

          Patient patient = Patient.getPatientByTcAndPassword(patientTc, password);

          if (patient == null) {
               System.out.println("Hasta bulunamadı veya şifre yanlış! Kayıt olun.");
               addPatient(scanner);  // Hasta kaydını ekleyelim
               return;
          }

          patientMenu(scanner, patient);
     }

     // Hasta Menüsü - Randevu alma
     private static void patientMenu(Scanner scanner, Patient patient) {
          while (true) {
               System.out.println("Hasta Menüsü:");
               System.out.println("1 - Randevu Al");
               System.out.println("2 - Çıkış");
               System.out.println("3 - Sigorta Bilgisi Görüntüle");
               System.out.print("Seçiminiz: ");

               int choice = scanner.nextInt();
               scanner.nextLine();

               switch (choice) {
                    case 1:
                         addAppointmentForPatient(scanner, patient);
                         break;
                    case 2:
                         System.out.println("Çıkılıyor...");
                         return;
                    case 3:
                         Patient.displayInsuranceInfo(patient.getTc());
                         break;
                    default:
                         System.out.println("Geçersiz seçim!");
               }
          }
     }
     // Hasta için Randevu Ekleme
     private static void addAppointmentForPatient(Scanner scanner, Patient patient) {
          // Şikayet seçimi
          System.out.println("Şikayetini seçin:");
          String complaint = ComplaintMapping.selectComplaint(scanner);

          // Şikayete göre uygun uzmanlık alanını bul
          String specialization = ComplaintMapping.mapToSpecialization(complaint);

          // Seçilen uzmanlık alanındaki doktorları al
          List<Doctor> doctors = Doctor.getDoctorsBySpecialization(specialization);

          if (doctors.isEmpty()) {
               System.out.println("Bu alanda hiç doktor bulunmamaktadır.");
               return;
          }

          // Uygun doktorları listele
          System.out.println("Uygun doktorlar:");
          for (int i = 0; i < doctors.size(); i++) {
               Doctor doctor = doctors.get(i);
               System.out.println((i + 1) + " - " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
          }

          // Kullanıcıdan doktor seçmesini iste
          System.out.print("Bir doktor seçin (1-" + doctors.size() + "): ");
          int doctorChoice = scanner.nextInt();
          scanner.nextLine();

          if (doctorChoice < 1 || doctorChoice > doctors.size()) {
               System.out.println("Geçersiz seçim!");
               return;
          }

          Doctor selectedDoctor = doctors.get(doctorChoice - 1);

          // Randevu tarihi ve saati seçimi
          System.out.println("Randevu tarihi (yyyy-MM-dd) girin:");
          String date = scanner.nextLine();
          System.out.println("Randevu saati (HH:mm) girin:");
          String time = scanner.nextLine();

          // Seçilen doktorun T.C. kimliği
          String doctorTc = selectedDoctor.getTc();

          // Randevu oluştur
          Appointment appointment = new Appointment(0, date, time, selectedDoctor.getName(), selectedDoctor.getSpecialization(),
                  patient.getTc(), complaint, "Henüz belirlenmemiş", selectedDoctor.getWorkingHours(), patient.getName());

          // Doktor T.C.'sini appointments tablosuna ekliyoruz
          appointment.addAppointmentToDatabase(doctorTc);  // doctorTc parametresi burada iletiliyor

          System.out.println("Randevunuz başarıyla oluşturuldu.");
     }
     private static void addAppointmentForDoctor(Scanner scanner, Doctor doctor) {
          System.out.println("Hasta T.C. Kimlik numarası:");
          String patientTc = scanner.nextLine();

          Patient patient = Patient.getPatientByTc(patientTc);
          if (patient == null) {
               System.out.println("Hasta bulunamadı.");
               return;
          }

          System.out.println("Randevu Tarihi (yyyy-MM-dd):");
          String date = scanner.nextLine();
          System.out.println("Randevu Saati (HH:mm):");
          String time = scanner.nextLine();

          Appointment appointment = new Appointment(0, date, time, doctor.getName(),
                  doctor.getSpecialization(), patient.getTc(), "Henüz belirlenmemiş", null,
                  doctor.getWorkingHours(), patient.getName());
          appointment.addAppointmentToDatabase(doctor.getTc());  // doctorTc parametresi burada iletiliyor
     }

     private static void addDiagnosis(Scanner scanner, Doctor doctor) {
          System.out.println("Hasta T.C. Kimlik numarası:");
          String patientTc = scanner.nextLine();

          List<Appointment> appointments = Appointment.getAppointmentsByPatientTc(patientTc, doctor.getTc());
          if (appointments.isEmpty()) {
               System.out.println("Randevu bulunamadı.");
               return;
          }

          for (int i = 0; i < appointments.size(); i++) {
               System.out.println((i + 1) + ". " + appointments.get(i).getDetails());
          }

          System.out.println("Teşhis eklemek istediğiniz randevu numarasını seçin:");
          int choice = scanner.nextInt();
          scanner.nextLine();

          Appointment selectedAppointment = appointments.get(choice - 1);
          System.out.println("Teşhis Girin:");
          String diagnosis = scanner.nextLine();

          selectedAppointment.addDiagnosis(diagnosis);  // Teşhisi ekliyoruz
     }


     private static void addPatient(Scanner scanner) {
          System.out.println("Hasta Adı:");
          String name = scanner.nextLine();
          System.out.println("Yaş:");
          int age = scanner.nextInt();
          scanner.nextLine();
          System.out.println("Adres:");
          String address = scanner.nextLine();
          System.out.println("T.C. Kimlik:");
          String tc = scanner.nextLine();

          Patient patient = new Patient(0, name, age, address, tc, "");
          patient.addPatientToDatabase();
     }

     private static void addStaff(Scanner scanner) {
          System.out.println("Personel Adı:");
          String name = scanner.nextLine();
          System.out.println("Şifre:");
          String password = scanner.nextLine();

          int age;
          while (true) {
               try {
                    System.out.println("Yaş:");
                    age = Integer.parseInt(scanner.nextLine());
                    break;  // Geçerli yaş girilirse döngüden çık
               } catch (NumberFormatException e) {
                    System.out.println("Geçersiz yaş girdiniz. Lütfen tekrar deneyin.");
               }
          }

          System.out.println("Adres:");
          String address = scanner.nextLine();

          System.out.println("T.C. Kimlik:");
          String tc = scanner.nextLine();

          // İş tanımı seçeneklerini göster
          String[] jobDescriptions = {"Hemşire", "Temizlik Personeli", "Laborant", "Resepsiyonist", "Teknik Destek"};
          System.out.println("Görev Tanımları:");
          for (int i = 0; i < jobDescriptions.length; i++) {
               System.out.println((i + 1) + " - " + jobDescriptions[i]);
          }

          // Kullanıcıdan seçim yapmasını iste
          String jobDescription;
          while (true) {
               System.out.print("Bir görev seçin (1-" + jobDescriptions.length + "): ");
               int choice = scanner.nextInt();
               scanner.nextLine(); // Boş satırı temizle
               if (choice >= 1 && choice <= jobDescriptions.length) {
                    jobDescription = jobDescriptions[choice - 1];  // Seçilen görevi al
                    break;
               } else {
                    System.out.println("Geçersiz seçim. Lütfen tekrar deneyin.");
               }
          }

          // Personel nesnesini oluştur ve veritabanına ekle
          Staff staff = new Staff(0, name, age, address, tc, jobDescription, password);
          staff.addStaffToDatabase();

          System.out.println("Personel başarıyla eklendi: " + name + " (" + jobDescription + ")");
     }
     private static boolean isLaborant(String tc) {
          String sql = "SELECT job_description FROM staff WHERE tc = ? AND job_description = 'Laborant'";

          try (Connection connection = Database.getInstance().getConnection();
               PreparedStatement stmt = connection.prepareStatement(sql)) {

               stmt.setString(1, tc);
               ResultSet rs = stmt.executeQuery();
               return rs.next();
          } catch (SQLException e) {
               e.printStackTrace();
               return false;
          }
     }

     private static void addLabResult(Scanner scanner) {
          System.out.println("Hasta T.C. Kimlik Girin:");
          String patientTc = scanner.nextLine();

          System.out.println("Test Türü (örn: Kan Testi, MR):");
          String testType = scanner.nextLine();

          System.out.println("Sonuç Detayları:");
          String resultDetails = scanner.nextLine();

          String sql = "INSERT INTO laboratory_results (patient_tc, test_type, result_details) VALUES (?, ?, ?)";

          try (Connection connection = Database.getInstance().getConnection();
               PreparedStatement stmt = connection.prepareStatement(sql)) {

               stmt.setString(1, patientTc);
               stmt.setString(2, testType);
               stmt.setString(3, resultDetails);

               stmt.executeUpdate();
               System.out.println("Sonuç başarıyla eklendi.");
          } catch (SQLException e) {
               e.printStackTrace();
          }
     }

     private static boolean isAdmin(String tc, String password) {
          String sql = "SELECT * FROM administrators WHERE tc = ? AND password = ?";

          try (Connection connection = Database.getInstance().getConnection();
               PreparedStatement stmt = connection.prepareStatement(sql)) {

               stmt.setString(1, tc);
               stmt.setString(2, password);  // Şifreyi de sorguya ekliyoruz
               ResultSet rs = stmt.executeQuery();

               if (rs.next()) {
                    return true;  // Admin var ve şifre doğru
               } else {
                    return false;  // Admin yok veya şifre yanlış
               }
          } catch (SQLException e) {
               e.printStackTrace();
               return false;
          }
     }


     private static void adminLogin(Scanner scanner) {
          System.out.println("Lütfen T.C. Kimlik numaranızı girin:");
          String tc = scanner.nextLine();

          System.out.println("Şifrenizi girin:");
          String password = scanner.nextLine();

          if (isAdmin(tc, password)) {
               System.out.println("Giriş başarılı! Yönetici Menüsüne yönlendiriliyorsunuz.");
               adminMenu(scanner, tc); // Yönetici menüsüne yönlendirme
          } else {
               System.out.println("Giriş başarısız. Yönetici kaydı bulunamadı veya şifre yanlış.");
          }
     }
     private static void adminMenu(Scanner scanner, String loggedInAdminTc) {

          // Örnek bir Administrator nesnesi oluşturuluyor
          Administrator loggedInAdmin = getAdministratorByTc(loggedInAdminTc);

          while (true) {
               System.out.println("Yönetici Menüsü:");
               System.out.println("1 - Yeni Yönetici Ekle");
               System.out.println("2 - Raporlama");
               System.out.println("3 - Sevk Yönetimi");
               System.out.println("4 - Sigorta Ekle");
               System.out.println("5 - Çıkış");
               System.out.print("Seçiminiz: ");

               int choice = scanner.nextInt();
               scanner.nextLine(); // Satırı temizle

               switch (choice) {
                    case 1:
                         addAdministrator(scanner);
                         break;
                    case 2:
                         reportMenu(scanner);
                         break;
                    case 3:
                         // Burada loggedInAdmin nesnesi üzerinden manageReferrals çağrılıyor
                         loggedInAdmin.manageReferrals();
                         break;
                    case 4:
                         addInsurance(scanner, loggedInAdmin);
                         break;
                    case 5:
                         System.out.println("Çıkış yapılıyor...");
                         return;
                    default:
                         System.out.println("Geçersiz seçim, lütfen tekrar deneyin.");
               }
          }
     }
     private static void addInsurance(Scanner scanner, Administrator admin) {
          System.out.print("Sigorta Türü: ");
          String insuranceType = scanner.nextLine();

          System.out.print("Sigorta Açıklaması: ");
          String description = scanner.nextLine();

          System.out.print("Poliçe Numarası: ");
          String policyNumber = scanner.nextLine();

          System.out.print("Sigorta Oranı: ");
          int coveragePercentage = scanner.nextInt(); // Sigorta oranını alıyoruz
          scanner.nextLine(); // Satırı temizle

          System.out.print("Hasta T.C. Kimlik Numarası: ");
          String patientTc = scanner.nextLine();

          // T.C. kimlik numarasına göre hastayı veritabanından buluyoruz
          Patient patient = Patient.getPatientByTc(patientTc);

          if (patient == null) {
               System.out.println("Hasta bulunamadı.");
               return;
          }

          // Sigorta bilgilerini veritabanına eklemek için gerekli SQL sorgusunu yazıyoruz
          String sql = "INSERT INTO insurance_info (patient_tc,insurance_type, coverage_details, policy_number, coverage_percentage, created_at) VALUES (?, ?, ?, ?, ?, NOW())";

          try (Connection connection = Database.getInstance().getConnection();
               PreparedStatement stmt = connection.prepareStatement(sql)) {
               stmt.setString(1, patientTc);
               stmt.setString(2, insuranceType); // Sigorta türü
               stmt.setString(3, description); // Sigorta açıklaması
               stmt.setString(4, policyNumber); // Poliçe numarası
               stmt.setInt(5, coveragePercentage); // Sigorta oranı

               stmt.executeUpdate();
               System.out.println("Sigorta başarıyla eklendi.");
          } catch (SQLException e) {
               e.printStackTrace();
          }
     }

     private static void addAppointment(Scanner scanner, Administrator admin) {
          System.out.print("Randevu Tarihi (YYYY-MM-DD): ");
          String date = scanner.nextLine();

          System.out.print("Randevu Saati (HH:MM): ");
          String time = scanner.nextLine();

          System.out.print("Doktor Adı: ");
          String doctorName = scanner.nextLine();

          System.out.print("Uzmanlık Alanı: ");
          String specialization = scanner.nextLine();

          System.out.print("Doktor Çalışma Saatleri: ");
          String workingHours = scanner.nextLine();

          System.out.print("Hasta Adı: ");
          String patientName = scanner.nextLine();

          System.out.print("Hasta T.C. Kimlik Numarası: ");
          String patientTc = scanner.nextLine();

          System.out.print("Şikayet: ");
          String complaint = scanner.nextLine();

          System.out.print("Doktor T.C. Kimlik Numarası: ");
          String doctorTc = scanner.nextLine();

          Appointment appointment = new Appointment(0, date, time, doctorName, specialization, patientTc, complaint, null, workingHours, patientName);
          appointment.addAppointmentToDatabase(doctorTc);
     }
     private static void listAppointments(Scanner scanner, Administrator admin) {
          System.out.print("Hasta T.C. Kimlik Numarası: ");
          String patientTc = scanner.nextLine();

          System.out.print("Doktor T.C. Kimlik Numarası: ");
          String doctorTc = scanner.nextLine();

          List<Appointment> appointments = Appointment.getAppointmentsByPatientTc(patientTc, doctorTc);

          if (appointments.isEmpty()) {
               System.out.println("Belirtilen kriterlere uygun randevu bulunamadı.");
          } else {
               System.out.println("Randevular:");
               for (Appointment appointment : appointments) {
                    System.out.println(appointment.getDetails());
               }
          }
     }

     // T.C. Kimlik numarasına göre Administrator nesnesi getiren yardımcı metod
     private static Administrator getAdministratorByTc(String tc) {

          String sql = "SELECT * FROM administrators WHERE tc = ?";
          try (Connection connection = Database.getInstance().getConnection();
               PreparedStatement stmt = connection.prepareStatement(sql)) {

               stmt.setString(1, tc);
               ResultSet rs = stmt.executeQuery();

               if (rs.next()) {
                    // Administrator bilgilerini alıp bir nesne oluşturuyoruz
                    return new Administrator(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("address"),
                            rs.getString("tc"),
                            rs.getString("permission")
                    );
               }
          } catch (SQLException e) {
               e.printStackTrace();
          }

          return null; // Eğer administrator bulunamazsa null döner
     }

     private static void addAdministrator(Scanner scanner) {
          System.out.println("Yeni Yönetici Adı:");
          String name = scanner.nextLine();
          System.out.println("Yaş:");
          int age = scanner.nextInt();
          scanner.nextLine(); // Satırı temizle
          System.out.println("Adres:");
          String address = scanner.nextLine();
          System.out.println("T.C. Kimlik:");
          String tc = scanner.nextLine();  // Burada tc'yi doğru aldığınızdan emin olun
          System.out.println("Yetki Seviyesi:");
          String permission = scanner.nextLine();

          String sql = "INSERT INTO administrators (name, age, address, tc, permission) VALUES (?, ?, ?, ?, ?)";

          try (Connection connection = Database.getInstance().getConnection();
               PreparedStatement stmt = connection.prepareStatement(sql)) {

               stmt.setString(1, name);
               stmt.setInt(2, age);
               stmt.setString(3, address);
               stmt.setString(4, tc);  // Burada tc'yi doğru şekilde veritabanına eklediğinizden emin olun
               stmt.setString(5, permission);

               stmt.executeUpdate();
               System.out.println("Yeni yönetici başarıyla eklendi.");
          } catch (SQLException e) {
               e.printStackTrace();
               System.out.println("Yönetici eklenirken hata oluştu.");
          }
     }

     // Raporlama menüsünü göster
     private static void reportMenu(Scanner scanner) {
          while (true) {
               System.out.println("Raporlama Menüsü:");
               System.out.println("1 - Hasta Raporu");
               System.out.println("2 - Randevu Raporu");
               System.out.println("3 - Personel Raporu");
               System.out.println("4 - Gelir Raporu");
               System.out.println("5 - Çıkış");
               System.out.print("Seçiminiz: ");

               int choice = scanner.nextInt();
               scanner.nextLine(); // Satırı temizle
               ReportMenu reportMenu = new ReportMenu();

               switch (choice) {
                    case 1:
                         reportMenu.generatePatientReport(scanner);  // Hasta raporunu oluştur
                         break;
                    case 2:
                         reportMenu.generateAppointmentReport(scanner);  // Randevu raporunu oluştur
                         break;
                    case 3:
                         reportMenu.generateStaffReport(scanner);  // Personel raporunu oluştur
                         break;
                    case 4:
                         reportMenu.generateRevenueReport(scanner);  // Gelir raporunu oluştur
                         break;
                    case 5:
                         System.out.println("Çıkılıyor...");
                         return;  // Çıkış yap
                    default:
                         System.out.println("Geçersiz seçim, lütfen tekrar deneyin.");
               }
          }

     }
     private static boolean isStaff(String tc, String password) {
          // Veritabanına bağlanıyoruz ve T.C. kimlik numarasına göre personel kontrolü yapıyoruz
          String sql = "SELECT * FROM staff WHERE tc = ? AND password = ?";

          try (Connection connection = Database.getInstance().getConnection();
               PreparedStatement stmt = connection.prepareStatement(sql)) {

               stmt.setString(1, tc);  // Burada TC'yi sorguya parametre olarak veriyoruz
               stmt.setString(2, password);  // Burada şifreyi sorguya parametre olarak veriyoruz
               ResultSet rs = stmt.executeQuery();

               // Personel olup olmadığını kontrol ediyoruz
               if (rs.next()) {
                    System.out.println("Personel bulundu: " + tc); // Debugging: Personel bulundu
                    return true; // Personel var
               } else {
                    System.out.println("Personel bulunamadı veya şifre yanlış: " + tc); // Debugging: Personel bulunamadı veya şifre yanlış
                    return false; // Personel yok veya şifre yanlış
               }
          } catch (SQLException e) {
               e.printStackTrace();
               return false;  // Hata durumunda false döner
          }
     }
     private static void staffLogin(Scanner scanner) {
          System.out.println("Personel T.C. Kimlik numaranızı girin:");
          String tc = scanner.nextLine();

          System.out.println("Şifrenizi girin:");
          String password = scanner.nextLine();

          if (isStaff(tc, password)) {
               System.out.println("Giriş başarılı! Personel Menüsüne yönlendiriliyorsunuz.");
               staffMenu(scanner, tc); // Personel menüsüne yönlendirme
          } else {
               System.out.println("Giriş başarısız. Personel kaydı bulunamadı veya şifre yanlış.");
          }
     }

     private static void staffMenu(Scanner scanner, String loggedInStaffTc) {
          while (true) {
               System.out.println("Personel Menüsü:");
               System.out.println("1 - Laboratuvar Sonuçları Ekle");
               System.out.println("2 - Çıkış");
               System.out.print("Seçiminiz: ");

               int choice = scanner.nextInt();
               scanner.nextLine(); // Satırı temizle

               switch (choice) {
                    case 1:
                         // Laboratuvar sonucu ekleme işlemi
                         laboratoryMenu(scanner, loggedInStaffTc);
                         break;
                    case 2:
                         System.out.println("Çıkılıyor...");
                         return; // Çıkış yap
                    default:
                         System.out.println("Geçersiz seçim, lütfen tekrar deneyin.");
               }
          }
     }

     private static void laboratoryMenu(Scanner scanner, String loggedInStaffTc) {
          System.out.println("Laboratuvar Sonuçları Ekleme Menüsü:");

          // Test türlerini listele
          Laboratory.displayTestTypes();

          // Test türünü seçme
          System.out.print("Test türünü seçin (1-5): ");
          int testTypeChoice = scanner.nextInt();
          scanner.nextLine(); // Satırı temizle

          String testType = Laboratory.getTestTypes()[testTypeChoice - 1];

          // Sonuç açıklamasını al
          System.out.print("Test sonucunu girin: ");
          String testResult = scanner.nextLine();

          // Hasta T.C. kimlik numarasını al
          System.out.print("Hasta T.C. Kimlik numarasını girin: ");
          String patientTc = scanner.nextLine();

          // Laboratuvar sonucunu veritabanına ekle
          Laboratory.addLabResult(patientTc, testType, testResult);

          System.out.println("Laboratuvar sonucu başarıyla eklendi.");
     }

}


