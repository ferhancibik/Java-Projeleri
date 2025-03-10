package gui;

import models.Database;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.TableCellRenderer;


public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();  // Ana pencereyi başlat
        });
    }

    // Ana pencere (JFrame)
    static class MainFrame extends JFrame {
        private JButton patientButton;
        private JButton adminButton;
        private JButton doctorButton;
        private JButton staffButton;


        public MainFrame() {
            setTitle("Hastane Yönetim Sistemi");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            // Ana panel
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(5, 1));
            panel.setLayout(new GridLayout(6, 1));

            patientButton = new JButton("Hasta Girişi");
            adminButton = new JButton("Yönetici Girişi");
            doctorButton = new JButton("Doktor Girişi");
            staffButton = new JButton("Görevli Girişi");



            // Butonlara tıklama işlemleri
            patientButton.addActionListener(e -> loginAction("Hasta"));
            adminButton.addActionListener(e -> loginAction("Yönetici"));
            doctorButton.addActionListener(e -> loginAction("Doktor"));
            staffButton.addActionListener(e -> loginAction("Görevli"));

            // Panel düzeni
            panel.add(patientButton);
            panel.add(adminButton);
            panel.add(doctorButton);
            panel.add(staffButton);

            add(panel);
            setVisible(true);
        }

        // Butonlara tıklama işlemi
        private void loginAction(String role) {
            // Role göre giriş işlemi yapılacak
            new LoginFrame(role);
            this.dispose(); // Ana pencereyi kapat
        }
    }
    // Giriş ekranı (role göre farklı işlemler yapılacak)
    static class LoginFrame extends JFrame {
        private JTextField tcField;
        private JPasswordField passwordField;
        private JButton loginButton;
        private JButton registerButton;
        private String role;

        public LoginFrame(String role) {
            this.role = role;

            setTitle(role + " Girişi");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            // Panel
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(4, 2));

            JLabel tcLabel = new JLabel("T.C. Kimlik:");
            tcField = new JTextField();
            JLabel passwordLabel = new JLabel("Şifre:");
            passwordField = new JPasswordField();
            loginButton = new JButton("Giriş Yap");
            registerButton = new JButton("Kayıt Ol");

            // Panel düzeni
            panel.add(tcLabel);
            panel.add(tcField);
            panel.add(passwordLabel);
            panel.add(passwordField);
            panel.add(loginButton);

            // Eğer role "Hasta" veya "Görevli" ise "Kayıt Ol" butonunu ekle
            if (role.equals("Hasta")) {
                panel.add(registerButton);
                registerButton.addActionListener(e -> registerAction());
            }
            loginButton.addActionListener(e -> loginAction());

            add(panel);
            setVisible(true);
        }

        // Giriş işlemi
        private void loginAction() {
            String tc = tcField.getText();
            String password = new String(passwordField.getPassword());

            if (isValidLogin(tc, password)) {
                JOptionPane.showMessageDialog(this, "Giriş başarılı!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

                if (role.equals("Yönetici")) {
                    new AdminMenuFrame(tc);
                } else if (role.equals("Doktor")) {
                    new DoctorMenuFrame(tc);
                }else if (role.equals("Görevli")) {
                    new StaffMenuFrame(tc); // Görevli menüsüne yönlendir
                }else if (role.equals("Görevli")) {
                new StaffMenuFrame(tc);
                } else {
                    new UserMenuFrame(tc);
                }

                this.dispose(); // Giriş penceresini kapat
            } else {
                JOptionPane.showMessageDialog(this, "Geçersiz T.C. veya şifre.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }


        // Kayıt olma işlemi
        private void registerAction() {
            new RegisterFrame();
            this.dispose(); // Giriş penceresini kapat
        }

        // Veritabanı bağlantısı ve giriş kontrolü
        private boolean isValidLogin(String tc, String password) {
            // Role göre tablo adını belirleyelim
            String tableName = "";

            // Role göre doğru tabloyu seçelim
            if (role.equals("Hasta")) {
                tableName = "patients";
            } else if (role.equals("Doktor")) {
                tableName = "doctors";
            } else if (role.equals("Yönetici")) {
                tableName = "administrators";
            } else if (role.equals("Görevli")) {
                tableName = "staff";
            }

            // Dinamik olarak oluşturulan SQL sorgusu
            String sql = "SELECT * FROM " + tableName + " WHERE tc = ? AND password = ?";

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, tc);  // T.C. Kimlik
                stmt.setString(2, password);  // Şifre
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return true; // Eğer kullanıcı bulunursa true döner
                } else {
                    return false; // Kullanıcı bulunmazsa false döner
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false; // Hata durumunda false döner
            }
        }

    }

    // Kayıt olma ekranı
    static class RegisterFrame extends JFrame {
        private JTextField nameField;
        private JTextField tcField;
        private JTextField addressField;
        private JTextField medicalHistoryField;
        private JTextField ageField;
        private JPasswordField passwordField;
        private JButton registerButton;

        public RegisterFrame() {
            setTitle("Hasta Kayıt");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            // Panel
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(7, 2));

            JLabel nameLabel = new JLabel("Ad:");
            nameField = new JTextField();
            JLabel tcLabel = new JLabel("T.C. Kimlik:");
            tcField = new JTextField();
            JLabel addressLabel = new JLabel("Adres:");
            addressField = new JTextField();
            JLabel medicalHistoryLabel = new JLabel("Tıbbi Geçmiş:");
            medicalHistoryField = new JTextField();
            JLabel ageLabel = new JLabel("Yaş:");
            ageField = new JTextField();
            JLabel passwordLabel = new JLabel("Şifre:");
            passwordField = new JPasswordField();
            registerButton = new JButton("Kayıt Ol");

            // Panel düzeni
            panel.add(nameLabel);
            panel.add(nameField);
            panel.add(tcLabel);
            panel.add(tcField);
            panel.add(addressLabel);
            panel.add(addressField);
            panel.add(medicalHistoryLabel);
            panel.add(medicalHistoryField);
            panel.add(ageLabel);
            panel.add(ageField);
            panel.add(passwordLabel);
            panel.add(passwordField);
            panel.add(registerButton);

            registerButton.addActionListener(e -> registerAction());

            add(panel);
            setVisible(true);
        }

        // Kayıt işlemi
        private void registerAction() {
            String name = nameField.getText();
            String tc = tcField.getText();
            String address = addressField.getText();
            String medicalHistory = medicalHistoryField.getText();
            String age = ageField.getText();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || tc.isEmpty() || address.isEmpty() || medicalHistory.isEmpty() || age.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Veritabanında tc numarasının var olup olmadığını kontrol et
            String checkSql = "SELECT COUNT(*) FROM patients WHERE tc = ?";
            try (Connection connection = Database.getConnection();
                 PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {

                checkStmt.setString(1, tc);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    // Eğer tc zaten varsa, kullanıcıya uyarı ver
                    JOptionPane.showMessageDialog(this, "Bu T.C. Kimlik Numarası zaten kayıtlı.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Eğer tc yoksa, kaydı ekleyelim
                String sql = "INSERT INTO patients (name, tc, address, medical_history, age, password, created_at) VALUES (?, ?, ?, ?, ?, ?, NOW())";

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, tc);
                    stmt.setString(3, address);
                    stmt.setString(4, medicalHistory);
                    stmt.setString(5, age);
                    stmt.setString(6, password);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Kayıt başarılı! Giriş yapabilirsiniz.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                        new LoginFrame("Hasta");
                        this.dispose(); // Kayıt penceresini kapat
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Kayıt sırasında hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Görevli Menüsü
    static class StaffMenuFrame extends JFrame {
        private String staffTc;

        public StaffMenuFrame(String staffTc) {
            this.staffTc = staffTc;

            setTitle("Görevli Menüsü");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            // Panel
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 1, 10, 10));


            JLabel welcomeLabel = new JLabel("Hoşgeldiniz, Görevli", JLabel.CENTER);
            JButton addAppointmentButton = new JButton("Randevu Ekle");

            // "Randevu Ekle" butonuna tıklama işlemi
            addAppointmentButton.addActionListener(e -> addAppointmentAction());

            panel.add(welcomeLabel);
            panel.add(addAppointmentButton);

            if (isLaborant(staffTc)) {
                System.out.println("Laborant giriş yaptı.");
                JButton labOperationsButton = new JButton("Laboratuvar İşlemleri");
                labOperationsButton.addActionListener(e -> openLabOperations());
                panel.add(labOperationsButton);
                panel.revalidate();
                panel.repaint();
            } else {
                System.out.println("Laborant değil.");
            }


            add(panel);
            setVisible(true);
        }

        // Laborant olup olmadığını kontrol et
        private boolean isLaborant(String staffTc) {
            String sql = "SELECT job_description FROM staff WHERE tc = ?";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, staffTc);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String jobDescription = rs.getString("job_description");
                    System.out.println("Kullanıcı Görevi: " + jobDescription); // Kontrol amaçlı
                    return jobDescription != null && jobDescription.equalsIgnoreCase("Laborant");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }


        // Randevu ekleme işlemi
        private void addAppointmentAction() {
            // Randevu bilgilerini kullanıcıdan alın
            String patientTc = JOptionPane.showInputDialog(this, "Hasta TC:");
            String doctorName = JOptionPane.showInputDialog(this, "Doktor Adı:");
            String appointmentDate = JOptionPane.showInputDialog(this, "Randevu Tarihi (YYYY-MM-DD):");
            String appointmentTime = JOptionPane.showInputDialog(this, "Randevu Saati (HH:MM):");

            if (patientTc == null || doctorName == null || appointmentDate == null || appointmentTime == null ||
                    patientTc.isEmpty() || doctorName.isEmpty() || appointmentDate.isEmpty() || appointmentTime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tüm alanları doldurmanız gerekmektedir.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Randevuyu veritabanına kaydet
            String sql = "INSERT INTO appointments (patient_tc, doctor_name, appointment_date, appointment_time, created_at) VALUES (?, ?, ?, ?, NOW())";

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, patientTc);
                stmt.setString(2, doctorName);
                stmt.setString(3, appointmentDate);
                stmt.setString(4, appointmentTime);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Randevu başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Randevu eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Laboratuvar işlemleri sayfasını aç
        private void openLabOperations() {
            new LabOperationsFrame(staffTc);  // Laboratuvar işlemleri penceresini aç
        }
    }

    // Laboratuvar işlemleri sayfası (Bağımsız sınıf olarak)
    public static class LabOperationsFrame extends JFrame {
        private String staffTc;

        public LabOperationsFrame(String staffTc) {
            this.staffTc = staffTc;

            setTitle("Laboratuvar İşlemleri");
            setSize(500, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 1));

            JLabel labLabel = new JLabel("Laboratuvar Sonuçları", JLabel.CENTER);
            JButton addResultButton = new JButton("Sonuç Ekle");
            JButton viewResultsButton = new JButton("Sonuçları Görüntüle");

            // Sonuç ekleme butonuna tıklama işlemi
            addResultButton.addActionListener(e -> addLabResultAction());

            // Sonuç görüntüleme butonuna tıklama işlemi
            viewResultsButton.addActionListener(e -> viewLabResultsAction());

            panel.add(labLabel);
            panel.add(addResultButton);
            panel.add(viewResultsButton);

            add(panel);
            setVisible(true);
        }

        // Laboratuvar sonucu ekleme
        private void addLabResultAction() {
            String patientTc = JOptionPane.showInputDialog(this, "Hasta TC:");
            String testType = JOptionPane.showInputDialog(this, "Test Türü (Kan Testi, Röntgen, vb.):");
            String testResult = JOptionPane.showInputDialog(this, "Test Sonucu:");

            if (patientTc == null || testType == null || testResult == null ||
                    patientTc.isEmpty() || testType.isEmpty() || testResult.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tüm alanları doldurmanız gerekmektedir.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO lab_results (patient_tc, test_type, test_result, created_at) VALUES (?, ?, ?, NOW())";

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, patientTc);
                stmt.setString(2, testType);
                stmt.setString(3, testResult);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Test sonucu başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Test sonucu eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Laboratuvar sonuçlarını görüntüleme
        private void viewLabResultsAction() {
            String patientTc = JOptionPane.showInputDialog(this, "Hasta TC:");

            if (patientTc == null || patientTc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Hasta TC girmeniz gerekmektedir.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "SELECT test_type, test_result, created_at FROM lab_results WHERE patient_tc = ?";

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, patientTc);
                ResultSet rs = stmt.executeQuery();

                StringBuilder results = new StringBuilder("Laboratuvar Sonuçları:\n\n");
                while (rs.next()) {
                    String testType = rs.getString("test_type");
                    String testResult = rs.getString("test_result");
                    String createdAt = rs.getString("created_at");
                    results.append("Test Türü: ").append(testType)
                            .append("\nSonuç: ").append(testResult)
                            .append("\nTarih: ").append(createdAt)
                            .append("\n\n");
                }

                JOptionPane.showMessageDialog(this, results.toString(), "Sonuçlar", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Kullanıcı menüsü
    static class UserMenuFrame extends JFrame {
        private String patientTc;

        public UserMenuFrame(String patientTc) {
            this.patientTc = patientTc;

            setTitle("Hasta Menüsü");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 1));

            // Hasta bilgilerini alalım
            String patientName = getPatientName(patientTc);
            JLabel welcomeLabel = new JLabel("Hoşgeldiniz, " + patientName, JLabel.CENTER);
            JButton appointmentButton = new JButton("Randevu Alma");
            JButton insuranceButton = new JButton("Sigorta Bilgileri");

            appointmentButton.addActionListener(e -> appointmentAction(patientName));
            insuranceButton.addActionListener(e -> showInsuranceInfo(patientTc));

            panel.add(welcomeLabel);
            panel.add(appointmentButton);
            panel.add(insuranceButton);

            add(panel);
            setVisible(true);
        }

        // Hasta ismini veritabanından alalım
        private String getPatientName(String tc) {
            String sql = "SELECT name FROM patients WHERE tc = ?";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, tc);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getString("name");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Hasta"; // Eğer hata olursa
        }

        // Randevu alma işlemi
        private void appointmentAction(String patientName) {
            // Şikayet seçimi için ComboBox
            String[] complaints = {"Sivilce ve Cilt Problemleri", "Diş Çürüğü", "Tansiyon", "Depresyon", "Kalp", "Baş Ağrısı", "Kemik", "Diğer"};
            JComboBox<String> complaintComboBox = new JComboBox<>(complaints);
            int option = JOptionPane.showConfirmDialog(this, complaintComboBox, "Şikayetinizi Seçin", JOptionPane.OK_CANCEL_OPTION);

            if (option != JOptionPane.OK_OPTION) {
                return; // Kullanıcı iptal etti
            }

            String complaint = (String) complaintComboBox.getSelectedItem();
            if (complaint == null || complaint.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Şikayet seçimi boş bırakılamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Şikayete göre branş belirle
            String specialization = null;
            switch (complaint.toLowerCase()) {
                case "sivilce ve cilt problemleri":
                    specialization = "Dermatoloji";
                    break;
                case "tansiyon":
                    specialization = "Kardiyoloji";
                    break;
                case "depresyon":
                    specialization = "Psikiyatrist";
                    break;
                case "diş çürüğü":
                    specialization = "Diş Hekimi";
                    break;
                case "kalp":
                    specialization = "Kardiyoloji";
                    break;
                case "baş ağrısı":
                    specialization = "Nöroloji";
                    break;
                case "kemik":
                    specialization = "Ortopedi";
                    break;
                default:
                    specialization = "Genel"; // Diğer durumlar için genel bir branş
                    break;
            }

            // Doktorları getir
            List<String> doctorList = new ArrayList<>();
            String sql = "SELECT name FROM doctors WHERE specialization = ?";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, specialization);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    doctorList.add(rs.getString("name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Doktor listesi alınırken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (doctorList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Bu branşta uygun doktor bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Doktor seçimi
            String selectedDoctor = (String) JOptionPane.showInputDialog(this,
                    "Bir doktor seçin:",
                    "Doktor Seçimi",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    doctorList.toArray(),
                    doctorList.get(0));

            if (selectedDoctor == null || selectedDoctor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Doktor seçimi yapılmadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Randevu tarihi ve saati al
            String appointmentDate = JOptionPane.showInputDialog(this, "Randevu Tarihi (YYYY-MM-DD):");
            String appointmentTime = JOptionPane.showInputDialog(this, "Randevu Saati (HH:MM):");

            if (appointmentDate == null || appointmentTime == null || appointmentDate.isEmpty() || appointmentTime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tarih ve saat bilgisi boş bırakılamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Doktor bilgilerini al
            String doctorWorkingHours = null;
            String doctorSql = "SELECT working_hours FROM doctors WHERE name = ?";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(doctorSql)) {

                stmt.setString(1, selectedDoctor); // Seçilen doktorun ismini kullanıyoruz
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    doctorWorkingHours = rs.getString("working_hours");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Doktor bilgileri alınırken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (doctorWorkingHours == null ) {
                JOptionPane.showMessageDialog(this, "Seçilen doktorun bilgileri eksik.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Seçilen doktorun TC'sini al
            String doctorTc = null;
            String doctorSql2 = "SELECT tc FROM doctors WHERE name = ?";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(doctorSql2)) {

                stmt.setString(1, selectedDoctor);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    doctorTc = rs.getString("tc");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Doktor bilgileri alınırken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (doctorTc == null) {
                JOptionPane.showMessageDialog(this, "Seçilen doktorun TC bilgisi alınamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Randevuyu kaydet
            String insertSql = "INSERT INTO appointments (appointment_date, appointment_time, doctor_name, patient_tc, patient_name, complaint, specialization, working_hours, diagnosis, doctor_tc, created_at, prescribed_medicine) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, NOW(), ?)";
            String prescribedMedicine = null;  // Başlangıçta null

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(insertSql)) {

                // Parametreleri sırasıyla ayarlayın
                stmt.setString(1, appointmentDate);        // appointment_date
                stmt.setString(2, appointmentTime);        // appointment_time
                stmt.setString(3, selectedDoctor);         // doctor_name
                stmt.setString(4, patientTc);              // patient_tc
                stmt.setString(5, patientName);            // patient_name
                stmt.setString(6, complaint);              // complaint
                stmt.setString(7, specialization);         // specialization
                stmt.setString(8, doctorWorkingHours);     // working_hours
                stmt.setNull(9, Types.NULL);               // diagnosis (NULL olarak gönderiliyor)
                stmt.setString(10, doctorTc);              // doctor_tc
                stmt.setNull(11, Types.TIMESTAMP);         // created_at (veritabanı otomatik olarak alacak)

                // prescribed_medicine için NULL kontrolü
                if (prescribedMedicine == null) {
                    stmt.setNull(12, Types.NULL);          // prescribed_medicine (NULL)
                } else {
                    stmt.setString(12, prescribedMedicine); // prescribed_medicine (değer varsa)
                }

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Randevu başarıyla oluşturuldu.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Randevu oluşturulurken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
        private String getInsuranceInfo(String patientTc) {
            String sql = "SELECT insurance_type, policy_number, coverage_percentage, coverage_details FROM insurance_info WHERE patient_tc = ?";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, patientTc);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String insuranceType = rs.getString("insurance_type");
                    String policyNumber = rs.getString("policy_number");
                    String coveragePercentage = rs.getString("coverage_percentage");
                    String coverageDetails = rs.getString("coverage_details");

                    return "Sigorta Türü: " + insuranceType + "\nPoliçe Numarası: " + policyNumber +
                            "\nKapsam Yüzdesi: " + coveragePercentage + "%" + "\nKapsam Detayları: " + coverageDetails;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Sigorta bilgileri bulunamadı."; // Eğer hata olursa
        }
        private void showInsuranceInfo(String patientTc) {
            String insuranceInfo = getInsuranceInfo(patientTc);
            JOptionPane.showMessageDialog(this, insuranceInfo, "Sigorta Bilgileri", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    // Veritabanı bağlantısı
    public static class Database {
        private static final String URL = "jdbc:mariadb://localhost:3325/akillihastane";
        private static final String USER = "root";
        private static final String PASSWORD = "ferfeyfat";

        private static Connection connection;

        // Veritabanı bağlantısını almak için metod
        public static Connection getConnection() throws SQLException {
            if (connection == null || connection.isClosed()) {
                // Bağlantı yoksa veya kapalıysa, yeni bağlantı oluştur
                try {
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Bağlantı başarıyla kuruldu.");
                } catch (SQLException e) {
                    System.out.println("Veritabanı bağlantısı kurulamadı: " + e.getMessage());
                    throw e;
                }
            }
            return connection;
        }
    }

    // Yönetici Menüsü
    static class AdminMenuFrame extends JFrame {
        public AdminMenuFrame(String adminTc) {
            setTitle("Yönetici Menüsü");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(4, 1));

            JButton addDoctorButton = new JButton("Doktor Ekle");
            JButton addAdminButton = new JButton("Yönetici Ekle");
            JButton addStaffButton = new JButton("Görevli Ekle");
            JButton reportButton = new JButton("Raporlama");
            JButton manageStockButton = new JButton("Yeni İlaç Stoğu Yönetimi");
            JButton manageInsuranceAdminButton = new JButton("Sigorta Yönetimi");
            JButton manageReferralsButton = new JButton("Sevk Taleplerini Yönet");
            JButton referralReportButton = new JButton("Sevk Raporu");




            addDoctorButton.addActionListener(e -> addDoctorAction());
            addAdminButton.addActionListener(e -> addAdminAction());
            reportButton.addActionListener(e -> generateReportAction());
            manageStockButton.addActionListener(e -> new StockManagementFrame());
            addStaffButton.addActionListener(e -> addStaffAction());
            manageInsuranceAdminButton.addActionListener(e -> new InsuranceAdminManagementFrame());
            manageReferralsButton.addActionListener(e -> manageReferralsAction());
            referralReportButton.addActionListener(e -> generateReferralReport());

            panel.add(addDoctorButton);
            panel.add(addAdminButton);
            panel.add(reportButton);
            panel.add(manageStockButton);
            panel.add(addStaffButton);
            panel.add(manageInsuranceAdminButton);
            panel.add(manageReferralsButton);
            panel.add(referralReportButton);


            add(panel);
            setVisible(true);
        }

        private void addStaffAction() {
            // Giriş alanları
            JTextField nameField = new JTextField();
            JTextField ageField = new JTextField();
            JTextField addressField = new JTextField();
            JTextField tcField = new JTextField();
            String[] jobDescriptions = {
                    "Hasta Bakıcılar",
                    "Tıbbi Sekreterler",
                    "Bilgi Teknolojileri (BT) Personeli",
                    "Halkla İlişkiler ve Pazarlama Uzmanları",
                    "Temizlik Personeli",
                    "Güvenlik Personeli",
                    "Bakım ve Onarım Personeli",
                    "Aşçılar ve Mutfak Personeli",
                    "Danışmanlar",
                    "Laborant",
                    "Sigortacı"
            };
            JComboBox<String> jobDescriptionComboBox = new JComboBox<>(jobDescriptions);
            JPasswordField passwordField = new JPasswordField();

            Object[] message = {
                    "Ad:", nameField,
                    "Yaş:", ageField,
                    "Adres:", addressField,
                    "T.C. Kimlik:", tcField,
                    "Görev Tanımı:", jobDescriptionComboBox,
                    "Şifre:", passwordField,
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Yeni Görevli Ekle", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                String age = ageField.getText();
                String address = addressField.getText();
                String tc = tcField.getText();
                String jobDescription = (String) jobDescriptionComboBox.getSelectedItem();
                String password = new String(passwordField.getPassword());

                if (name == null || name.isEmpty() ||
                        age == null || age.isEmpty() ||
                        address == null || address.isEmpty() ||
                        tc == null || tc.isEmpty() ||
                        jobDescription == null || jobDescription.isEmpty() ||
                        password == null || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Veritabanına görevli ekleme
                String sql = "INSERT INTO staff (name, age, address, tc, job_description, password) VALUES (?, ?, ?, ?, ?, ?)";

                try (Connection connection = Database.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(sql)) {

                    stmt.setString(1, name);
                    stmt.setString(2, age);
                    stmt.setString(3, address);
                    stmt.setString(4, tc);
                    stmt.setString(5, jobDescription); // Seçilen görev tanımını ekle
                    stmt.setString(6, password);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Görevli başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Görevli eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }


        private void addDoctorAction() {
            // Giriş alanları
            JTextField nameField = new JTextField();
            JTextField ageField = new JTextField();
            JTextField addressField = new JTextField();
            JTextField tcField = new JTextField();
            String[] specializations = {"Kardiyoloji", "Nöroloji", "Ortopedi", "Pediatri", "Dermatoloji", "Diş Hekimi", "Psikiyatrist"}; // Branşlar
            JComboBox<String> specializationComboBox = new JComboBox<>(specializations);
            JTextField workingHoursField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            Object[] message = {
                    "Ad:", nameField,
                    "Yaş:", ageField,
                    "Adres:", addressField,
                    "T.C. Kimlik:", tcField,
                    "Uzmanlık Alanı:", specializationComboBox,
                    "Çalışma Saatleri:", workingHoursField,
                    "Şifre:", passwordField,
            };


            int option = JOptionPane.showConfirmDialog(this, message, "Yeni Doktor Ekle", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                String age = ageField.getText();
                String address = addressField.getText();
                String tc = tcField.getText();
                String specialization = (String) specializationComboBox.getSelectedItem();
                String workingHours = workingHoursField.getText();
                String password = new String(passwordField.getPassword());

                if (name == null || name.isEmpty() ||
                        age == null || age.isEmpty() ||
                        address == null || address.isEmpty() ||
                        tc == null || tc.isEmpty() ||
                        specialization == null || specialization.isEmpty() ||
                        workingHours == null || workingHours.isEmpty() ||
                        password == null || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                // Veritabanına doktor ekleme
                String sql = "INSERT INTO doctors (name, age, address, tc, specialization, working_hours, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (Connection connection = Database.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(sql)) {

                    stmt.setString(1, name);
                    stmt.setString(2, age);
                    stmt.setString(3, address);
                    stmt.setString(4, tc);
                    stmt.setString(5, specialization); // Seçilen branşı ekle
                    stmt.setString(6, workingHours);
                    stmt.setString(7, password);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Doktor başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Doktor eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }


        private void addAdminAction() {
            // Yeni bir yönetici ekleme penceresi açalım
            String name = JOptionPane.showInputDialog(this, "Yönetici Adı:");
            String age = JOptionPane.showInputDialog(this, "Yaş:");
            String address = JOptionPane.showInputDialog(this, "Adres:");
            String tc = JOptionPane.showInputDialog(this, "T.C. Kimlik:");
            String permission = JOptionPane.showInputDialog(this, "İzin Durumu (superadmin/admin):");
            String password = JOptionPane.showInputDialog(this, "Şifre:");

            if (name == null || age == null || address == null || tc == null || permission == null || password == null) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Veritabanına yönetici ekleyelim
            String sql = "INSERT INTO administrators (name, age, address, tc, permission, password) VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, name);
                stmt.setString(2, age);
                stmt.setString(3, address);
                stmt.setString(4, tc);
                stmt.setString(5, permission);
                stmt.setString(6, password);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Yönetici başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Yönetici eklenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }


        private void generateReportAction() {
            // Raporlama menüsünü oluşturacak bir panel
            JPanel reportPanel = new JPanel();
            reportPanel.setLayout(new GridLayout(5, 1));  // 5 satırlık bir grid

            // Raporlama seçenekleri için butonlar
            JButton patientReportButton = new JButton("Hasta Raporu");
            JButton appointmentReportButton = new JButton("Randevu Raporu");
            JButton staffReportButton = new JButton("Personel Raporu");
            JButton doctorReportButton = new JButton("Doktor Raporu");
            JButton exitButton = new JButton("Çıkış");

            // Butonlara tıklama olayları ekleyelim
            patientReportButton.addActionListener(e -> generatePatientReport());
            appointmentReportButton.addActionListener(e -> generateAppointmentReport());
            staffReportButton.addActionListener(e -> generateStaffReport());
            doctorReportButton.addActionListener(e -> generateDoctorReport());
            exitButton.addActionListener(e -> this.dispose());  // Çıkış butonuna basıldığında pencereyi kapat

            // Butonları panele ekleyelim
            reportPanel.add(patientReportButton);
            reportPanel.add(appointmentReportButton);
            reportPanel.add(staffReportButton);
            reportPanel.add(doctorReportButton);
            reportPanel.add(exitButton);

            // Paneli JFrame'e ekleyelim
            setContentPane(reportPanel);
            revalidate();  // Paneli güncelle
        }

        private void generatePatientReport() {
            // Veritabanından hasta raporunu alalım
            String sql = "SELECT * FROM patients";  // Örnek sorgu, ihtiyaçlarınıza göre özelleştirebilirsiniz

            try (Connection connection = Database.getConnection();
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                StringBuilder report = new StringBuilder();
                while (rs.next()) {
                    String name = rs.getString("name");
                    String age = rs.getString("age");
                    String tc = rs.getString("tc");
                    report.append("Adı: ").append(name).append(", Yaş: ").append(age).append(", T.C.: ").append(tc).append("\n");
                }

                JOptionPane.showMessageDialog(this, report.toString(), "Hasta Raporu", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void generateAppointmentReport() {
            // Veritabanından randevu raporunu alalım
            String sql = "SELECT * FROM appointments";  // Örnek sorgu

            try (Connection connection = Database.getConnection();
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                StringBuilder report = new StringBuilder();
                while (rs.next()) {
                    String patientName = rs.getString("patient_name");
                    String doctorName = rs.getString("doctor_name");
                    String appointmentDate = rs.getString("appointment_date");
                    report.append("Hasta: ").append(patientName).append(", Doktor: ").append(doctorName)
                            .append(", Tarih: ").append(appointmentDate).append("\n");
                }

                JOptionPane.showMessageDialog(this, report.toString(), "Randevu Raporu", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        // Personel raporunu oluştur
        private void generateStaffReport() {
            // Personel raporunu oluşturma işlemi
            String sql = "SELECT id, name, age, address, tc, job_description, created_at FROM staff";

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                StringBuilder report = new StringBuilder();
                report.append("Personel Raporu:\n\n");

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int age = rs.getInt("age");
                    String address = rs.getString("address");
                    String tc = rs.getString("tc");
                    String jobDescription = rs.getString("job_description");
                    String createdAt = rs.getString("created_at");

                    report.append("ID: ").append(id).append("\n");
                    report.append("Ad: ").append(name).append("\n");
                    report.append("Yaş: ").append(age).append("\n");
                    report.append("Adres: ").append(address).append("\n");
                    report.append("T.C.: ").append(tc).append("\n");
                    report.append("Görev: ").append(jobDescription).append("\n");
                    report.append("Oluşturulma Tarihi: ").append(createdAt).append("\n\n");
                }

                JOptionPane.showMessageDialog(this, report.toString(), "Personel Raporu", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Personel raporu oluşturulurken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }


        // Doktor raporunu oluştur
        private void generateDoctorReport() {
            // Doktor raporunu oluşturma işlemi
            // Veritabanından doktor bilgilerini alacağız
            String sql = "SELECT id, name, specialization, working_hours FROM doctors";

            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                StringBuilder report = new StringBuilder();
                report.append("Doktor Raporu:\n\n");

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String specialization = rs.getString("specialization");
                    String workingHours = rs.getString("working_hours");

                    report.append("ID: ").append(id).append("\n");
                    report.append("Ad: ").append(name).append("\n");
                    report.append("Uzmanlık Alanı: ").append(specialization).append("\n");
                    report.append("Çalışma Saatleri: ").append(workingHours).append("\n\n");
                }

                JOptionPane.showMessageDialog(this, report.toString(), "Doktor Raporu", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Doktor raporu oluşturulurken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        public class StockManagementFrame extends JFrame {

            private JTextField quantityField;
            private JTextField priceField;
            private JTextField supplierField;
            private JComboBox<String> productComboBox;
            private JButton addButton;
            private JButton updateButton;
            private JButton deleteButton;
            private JButton reportButton;
            private JTextField diseaseField;  // Hastalık için JTextField


            // İlaç ve hastalık listesi
            private static String[] drugs = {
                    "Aspirin - Baş ağrısı",
                    "Parol - Soğuk algınlığı",
                    "Amoksisilin - Enfeksiyonlar",
                    "Ibuprofen - Kas ağrıları",
                    "Metformin - Diyabet",
                    "Lisinopril - Yüksek tansiyon",
                    "Omeprazol - Mide asidi",
                    "Losartan - Hipertansiyon",
                    "Simvastatin - Kolesterol",
                    "Prednizon - Enflamasyon"
            };

            public StockManagementFrame() {
                setTitle("İlaç Stoğu Yönetimi");
                setSize(400, 300);
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Bu pencereyi kapatınca programı kapatmaz
                setLocationRelativeTo(null);

                // Panel ve layout
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(7, 2));

                // Etiketler ve giriş alanları
                JLabel productLabel = new JLabel("İlaç Seçimi:");
                productComboBox = new JComboBox<>(drugs);
                JLabel quantityLabel = new JLabel("Miktar:");
                quantityField = new JTextField();
                JLabel priceLabel = new JLabel("Fiyat:");
                priceField = new JTextField();
                JLabel supplierLabel = new JLabel("Tedarikçi:");
                supplierField = new JTextField();
                JLabel diseaseLabel = new JLabel("Hastalık:");  // Hastalık için etiket
                diseaseField = new JTextField();  // Hastalık için giriş alanı
                addButton = new JButton("Ekle");
                updateButton = new JButton("Güncelle");
                deleteButton = new JButton("Sil");
                reportButton = new JButton("Stok Raporu");

                // Panel düzeni
                panel.add(productLabel);
                panel.add(productComboBox);
                panel.add(quantityLabel);
                panel.add(quantityField);
                panel.add(priceLabel);
                panel.add(priceField);
                panel.add(supplierLabel);
                panel.add(supplierField);
                panel.add(diseaseLabel);  // Hastalık etiketi
                panel.add(diseaseField);  // Hastalık giriş alanı
                panel.add(addButton);
                panel.add(updateButton);
                panel.add(deleteButton);
                panel.add(reportButton);

                // Butonlara aksiyon ekleyin
                addButton.addActionListener(e -> addNewProduct());
                updateButton.addActionListener(e -> updateProduct());
                deleteButton.addActionListener(e -> deleteProduct());
                reportButton.addActionListener(e -> generateStockReport());

                add(panel);
                setVisible(true);
            }

            private void addNewProduct() {
                try {
                    String selectedDrug = (String) productComboBox.getSelectedItem();
                    if (selectedDrug == null || selectedDrug.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Lütfen bir ilaç seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String productName = selectedDrug.split(" - ")[0]; // İlacın adını al
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    String supplier = supplierField.getText().trim();
                    String disease = diseaseField.getText().trim();

                    if (supplier.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Tedarikçi alanı boş olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // SQL sorgusu
                    String sql = "INSERT INTO stock (product_name, quantity, price, supplier, disease,created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";

                    try (Connection connection = Database.getConnection();
                         PreparedStatement stmt = connection.prepareStatement(sql)) {

                        stmt.setString(1, productName);
                        stmt.setInt(2, quantity);
                        stmt.setDouble(3, price);
                        stmt.setString(4, supplier);
                        stmt.setString(5, disease);

                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Ürün başarıyla eklendi.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Ürün eklenemedi.");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Veritabanı hatası: Ürün eklenemedi.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Lütfen geçerli bir miktar ve fiyat girin.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }


            // Ürün güncelleme işlemi
            private void updateProduct() {
                // Kullanıcıdan ürün adı, miktar, fiyat ve tedarikçi bilgilerini alıyoruz
                String productName = (String) productComboBox.getSelectedItem();
                String quantityText = quantityField.getText();
                String priceText = priceField.getText();
                String supplier = supplierField.getText();


                // Girişlerin geçerliliğini kontrol et
                if (productName == null || productName.isEmpty() || quantityText.isEmpty() || priceText.isEmpty() || supplier.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Miktar ve fiyatın sayısal değer olup olmadığını kontrol et
                int quantity;
                double price;
                try {
                    quantity = Integer.parseInt(quantityText);
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Geçersiz miktar veya fiyat.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Güncellenmek istenen ürünün ID'sini almak için veritabanına sorgu gönder
                String sql = "SELECT id FROM stock WHERE product_name = ? AND supplier = ?";

                try (Connection connection = Database.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(sql)) {

                    stmt.setString(1, productName);
                    stmt.setString(2, supplier);

                    ResultSet rs = stmt.executeQuery();

                    // Eğer ürün bulunursa, ID'yi al ve güncelleme işlemi yap
                    if (rs.next()) {
                        int productId = rs.getInt("id");

                        // Ürünü güncellemek için SQL sorgusu
                        String updateSql = "UPDATE stock SET quantity = ?, price = ?, updated_at = NOW() WHERE id = ?";

                        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, quantity);
                            updateStmt.setDouble(2, price);
                            updateStmt.setInt(3, productId);

                            int rowsUpdated = updateStmt.executeUpdate();

                            if (rowsUpdated > 0) {
                                JOptionPane.showMessageDialog(this, "Ürün başarıyla güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(this, "Ürün güncellenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                            }
                        }

                    } else {
                        JOptionPane.showMessageDialog(this, "Ürün bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Ürün silme işlemi
            private void deleteProduct() {
                // Kullanıcıdan ürün adı ve tedarikçi bilgilerini alıyoruz
                String productName = (String) productComboBox.getSelectedItem();
                String supplier = supplierField.getText();

                // Girişlerin geçerliliğini kontrol et
                if (productName == null || productName.isEmpty() || supplier.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen ürün adı ve tedarikçi bilgisini girin.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Ürünü veritabanından silmek için SQL sorgusu
                String sql = "DELETE FROM stock WHERE product_name = ? AND supplier = ?";

                try (Connection connection = Database.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(sql)) {

                    stmt.setString(1, productName);
                    stmt.setString(2, supplier);

                    int rowsDeleted = stmt.executeUpdate();

                    if (rowsDeleted > 0) {
                        JOptionPane.showMessageDialog(this, "Ürün başarıyla silindi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Ürün bulunamadı veya silme işlemi başarısız oldu.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }


            private void generateStockReport() {
                String sql = "SELECT id, product_name, quantity, price, supplier, disease, created_at, updated_at FROM stock";

                try (Connection connection = Database.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    StringBuilder report = new StringBuilder();
                    report.append("Stok Raporu:\n\n");

                    // Eğer sonuçlar boşsa, kullanıcıya bilgi verelim
                    if (!rs.isBeforeFirst()) {
                        JOptionPane.showMessageDialog(this, "Veritabanında stok verisi bulunamadı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String productName = rs.getString("product_name");
                        int quantity = rs.getInt("quantity");
                        double price = rs.getDouble("price");
                        String supplier = rs.getString("supplier");
                        String disease = rs.getString("disease");
                        String createdAt = rs.getString("created_at");
                        String updatedAt = rs.getString("updated_at");

                        report.append("ID: ").append(id).append("\n");
                        report.append("Ürün Adı: ").append(productName).append("\n");
                        report.append("Miktar: ").append(quantity).append("\n");
                        report.append("Fiyat: ").append(price).append("\n");
                        report.append("Tedarikçi: ").append(supplier).append("\n");
                        report.append("Hastalık: ").append(disease).append("\n");
                        report.append("Oluşturulma Tarihi: ").append(createdAt).append("\n");
                        report.append("Güncellenme Tarihi: ").append(updatedAt).append("\n\n");
                    }

                    // Raporu kullanıcıya gösterelim
                    JOptionPane.showMessageDialog(this, report.toString(), "Stok Raporu", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException e) {
                    // Detaylı hata mesajı ekleyelim
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Stok raporu oluşturulurken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // Doktor reçete yazarken ilaç seçimi ve stok güncelleme işlemi
        private void writePrescription() {
            // İlaçları seçmek için JComboBox kullanacağız
            JComboBox<String> drugComboBox = new JComboBox<>(StockManagementFrame.drugs);
            JTextField quantityField = new JTextField(5);

            JPanel panel = new JPanel();
            panel.add(new JLabel("İlaç Seçin:"));
            panel.add(drugComboBox);
            panel.add(new JLabel("Miktar:"));
            panel.add(quantityField);

            int option = JOptionPane.showConfirmDialog(null, panel, "Reçete Yaz", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String selectedDrug = (String) drugComboBox.getSelectedItem();
                String productName = selectedDrug.split(" - ")[0]; // İlacın adını al
                int quantity = Integer.parseInt(quantityField.getText());

                // Stoktan ilaç miktarını düşür
                String sql = "UPDATE stock SET quantity = quantity - ? WHERE product_name = ?";

                try (Connection connection = Database.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(sql)) {

                    stmt.setInt(1, quantity);
                    stmt.setString(2, productName);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Reçete başarıyla yazıldı.");
                    } else {
                        JOptionPane.showMessageDialog(null, "İlaç miktarı yetersiz.");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Veritabanı hatası: Reçete yazılamadı.");
                }
            }
        }
        private void manageReferralsAction() {
            List<String[]> referrals = new ArrayList<>();
            String sql = "SELECT id, patient_tc, doctor_tc, hospital_name, referral_reason, status FROM referrals WHERE status = 'Pending'";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String[] referral = {
                            rs.getString("id"),
                            rs.getString("patient_tc"),
                            rs.getString("doctor_tc"),
                            rs.getString("hospital_name"),
                            rs.getString("referral_reason"),
                            rs.getString("status")
                    };
                    referrals.add(referral);
                }

                if (referrals.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Bekleyen sevk talebi bulunmamaktadır.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Talepleri listele
                JPanel panel = new JPanel(new GridLayout(referrals.size(), 1));
                for (String[] referral : referrals) {
                    JButton button = new JButton("Hasta TC: " + referral[1] + " - Hastane: " + referral[3]);
                    button.addActionListener(e -> handleReferralAction(referral));
                    panel.add(button);
                }

                JFrame referralsFrame = new JFrame("Sevk Talepleri");
                referralsFrame.setSize(500, 400);
                referralsFrame.setLocationRelativeTo(this);
                referralsFrame.add(new JScrollPane(panel));
                referralsFrame.setVisible(true);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void handleReferralAction(String[] referral) {
            String message = "Hasta TC: " + referral[1] + "\n"
                    + "Doktor TC: " + referral[2] + "\n"
                    + "Hastane: " + referral[3] + "\n"
                    + "Sebep: " + referral[4] + "\n\n"
                    + "Bu sevk talebini onaylamak veya reddetmek istiyor musunuz?";
            int option = JOptionPane.showConfirmDialog(this, message, "Sevk Talebi", JOptionPane.YES_NO_CANCEL_OPTION);

            String newStatus = null;
            if (option == JOptionPane.YES_OPTION) {
                newStatus = "Approved";
            } else if (option == JOptionPane.NO_OPTION) {
                newStatus = "Rejected";
            }

            if (newStatus != null) {
                String sql = "UPDATE referrals SET status = ? WHERE id = ?";
                try (Connection connection = Database.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(sql)) {

                    stmt.setString(1, newStatus);
                    stmt.setInt(2, Integer.parseInt(referral[0]));
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Sevk talebi " + (newStatus.equals("Approved") ? "onaylandı." : "reddedildi."), "Bilgi", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        private void generateReferralReport() {
            JFrame referralReportFrame = new JFrame("Sevk Raporu");
            referralReportFrame.setSize(800, 600);
            referralReportFrame.setLocationRelativeTo(this);

            String[] columnNames = {"ID", "Hasta TC", "Doktor TC", "Hastane", "Sebep", "Durum"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            String sql = "SELECT id, patient_tc, doctor_tc, hospital_name, referral_reason, status FROM referrals";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Object[] row = {
                            rs.getInt("id"),
                            rs.getString("patient_tc"),
                            rs.getString("doctor_tc"),
                            rs.getString("hospital_name"),
                            rs.getString("referral_reason"),
                            rs.getString("status")
                    };
                    model.addRow(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTable table = new JTable(model) {
                @Override
                public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                    Component c = super.prepareRenderer(renderer, row, column);
                    String status = (String) getValueAt(row, 5);
                    if ("Pending".equals(status)) {
                        c.setBackground(Color.YELLOW);
                    } else if ("Approved".equals(status)) {
                        c.setBackground(Color.GREEN);
                    } else if ("Rejected".equals(status)) {
                        c.setBackground(Color.RED);
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                    return c;
                }
            };

            table.setFillsViewportHeight(true);
            JScrollPane scrollPane = new JScrollPane(table);
            referralReportFrame.add(scrollPane);
            referralReportFrame.setVisible(true);
        }
    }
    static class InsuranceAdminManagementFrame extends JFrame {
        private JTable insuranceTable;
        private JScrollPane scrollPane;

        public InsuranceAdminManagementFrame() {
            insuranceTable = new JTable(); // Burada oluşturun
            JScrollPane scrollPane = new JScrollPane(insuranceTable);
            setTitle("Yönetici Sigorta Yönetimi");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            scrollPane = new JScrollPane(insuranceTable);

            JTable insuranceTable = new JTable(); // Veritabanı bağlantısı ile doldurulacak
            JButton addPolicyButton = new JButton("Poliçe Ekle");
            JButton editPolicyButton = new JButton("Poliçe Düzenle");
            JButton deletePolicyButton = new JButton("Poliçe Sil");
            JButton viewStaffActionsButton = new JButton("Personel İşlemleri Gör");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(addPolicyButton);
            buttonPanel.add(editPolicyButton);
            buttonPanel.add(deletePolicyButton);
            buttonPanel.add(viewStaffActionsButton);

            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            add(panel);
            setVisible(true);

            // Veritabanı işlemleri
            loadInsuranceData(insuranceTable);

            addPolicyButton.addActionListener(e -> addPolicy());
            editPolicyButton.addActionListener(e -> editPolicy(insuranceTable));
            deletePolicyButton.addActionListener(e -> deletePolicy(insuranceTable));
            viewStaffActionsButton.addActionListener(e -> viewStaffActions());
        }

        private void loadInsuranceData(JTable table) {
            String sql = "SELECT patient_tc, insurance_type, policy_number, coverage_percentage, coverage_details, created_at FROM insurance_info";
            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                // Tablo modelini oluştur
                DefaultTableModel model = new DefaultTableModel(
                        new String[]{"Hasta TC", "Sigorta Türü", "Poliçe Numarası", "Kapsam Yüzdesi", "Kapsam Detayları", "Oluşturulma Tarihi"}, 0);

                // ResultSet'teki verileri tabloya ekle
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("patient_tc"),
                            rs.getString("insurance_type"),
                            rs.getString("policy_number"),
                            rs.getString("coverage_percentage"),
                            rs.getString("coverage_details"),
                            rs.getString("created_at")
                    });
                }

                table.setModel(model);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Sigorta bilgileri yüklenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }



        private void addPolicy() {
            JTextField patientTcField = new JTextField();
            JTextField insuranceTypeField = new JTextField();
            JTextField policyNumberField = new JTextField();
            JTextField coveragePercentageField = new JTextField();
            JTextArea coverageDetailsArea = new JTextArea(3, 20);

            Object[] message = {
                    "Hasta TC:", patientTcField,
                    "Sigorta Türü:", insuranceTypeField,
                    "Poliçe Numarası:", policyNumberField,
                    "Kapsam Yüzdesi:", coveragePercentageField,
                    "Kapsam Detayları:", new JScrollPane(coverageDetailsArea)
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Yeni Poliçe Ekle", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String patientTc = patientTcField.getText();
                String insuranceType = insuranceTypeField.getText();
                String policyNumber = policyNumberField.getText();
                String coveragePercentage = coveragePercentageField.getText();
                String coverageDetails = coverageDetailsArea.getText();

                String sql = "INSERT INTO insurance_info (patient_tc, insurance_type, policy_number, coverage_percentage, coverage_details, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, NOW())";
                try (Connection conn = Database.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, patientTc);
                    stmt.setString(2, insuranceType);
                    stmt.setString(3, policyNumber);
                    stmt.setString(4, coveragePercentage);
                    stmt.setString(5, coverageDetails);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Yeni poliçe başarıyla eklendi.");
                        logAction("Poliçe Ekle", "Hasta TC: " + patientTc + ", Poliçe Numarası: " + policyNumber);
                        loadInsuranceData(insuranceTable); // Tabloyu güncelle
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Poliçe eklenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
                if (patientTc.isEmpty() || insuranceType.isEmpty() || policyNumber.isEmpty() || coveragePercentage.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            }
        }

        private void editPolicy(JTable table) {
            // Veritabanından poliçeleri al ve bir JComboBox oluştur
            JComboBox<String> policyComboBox = new JComboBox<>();
            String sqlFetchPolicies = "SELECT patient_tc, policy_number FROM insurance_info";
            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlFetchPolicies);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String patientTc = rs.getString("patient_tc");
                    String policyNumber = rs.getString("policy_number");
                    policyComboBox.addItem(patientTc + " - " + policyNumber);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Poliçeler alınırken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kullanıcıdan düzenlemek istediği poliçeyi seçmesini iste
            int option = JOptionPane.showConfirmDialog(this, policyComboBox, "Düzenlenecek Poliçeyi Seçin", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            // Seçilen poliçeyi ayıkla
            String selectedPolicy = (String) policyComboBox.getSelectedItem();
            if (selectedPolicy == null) {
                JOptionPane.showMessageDialog(this, "Geçerli bir poliçe seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] parts = selectedPolicy.split(" - ");
            String patientTc = parts[0];
            String policyNumber = parts[1];

            // Seçilen poliçenin mevcut bilgilerini getir
            String sqlFetchPolicyDetails = "SELECT insurance_type, policy_number, coverage_percentage, coverage_details " +
                    "FROM insurance_info WHERE patient_tc = ? AND policy_number = ?";
            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlFetchPolicyDetails)) {

                stmt.setString(1, patientTc);
                stmt.setString(2, policyNumber);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Mevcut bilgileri al
                        String insuranceType = rs.getString("insurance_type");
                        String currentPolicyNumber = rs.getString("policy_number");
                        String coveragePercentage = rs.getString("coverage_percentage");
                        String coverageDetails = rs.getString("coverage_details");

                        // Güncelleme formunu oluştur
                        JTextField insuranceTypeField = new JTextField(insuranceType);
                        JTextField policyNumberField = new JTextField(currentPolicyNumber);
                        JTextField coveragePercentageField = new JTextField(coveragePercentage);
                        JTextArea coverageDetailsArea = new JTextArea(coverageDetails, 3, 20);

                        Object[] message = {
                                "Sigorta Türü:", insuranceTypeField,
                                "Poliçe Numarası:", policyNumberField,
                                "Kapsam Yüzdesi:", coveragePercentageField,
                                "Kapsam Detayları:", new JScrollPane(coverageDetailsArea)
                        };

                        // Kullanıcıdan yeni bilgileri al
                        int updateOption = JOptionPane.showConfirmDialog(this, message, "Poliçe Güncelle", JOptionPane.OK_CANCEL_OPTION);
                        if (updateOption == JOptionPane.OK_OPTION) {
                            String newInsuranceType = insuranceTypeField.getText();
                            String newPolicyNumber = policyNumberField.getText();
                            String newCoveragePercentage = coveragePercentageField.getText();
                            String newCoverageDetails = coverageDetailsArea.getText();

                            // Girişlerin doğruluğunu kontrol et
                            if (newInsuranceType.isEmpty() || newPolicyNumber.isEmpty() || newCoveragePercentage.isEmpty()) {
                                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun.", "Hata", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            // Poliçeyi güncelle
                            String sqlUpdatePolicy = "UPDATE insurance_info SET insurance_type = ?, policy_number = ?, " +
                                    "coverage_percentage = ?, coverage_details = ? " +
                                    "WHERE patient_tc = ? AND policy_number = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdatePolicy)) {
                                updateStmt.setString(1, newInsuranceType);
                                updateStmt.setString(2, newPolicyNumber);
                                updateStmt.setString(3, newCoveragePercentage);
                                updateStmt.setString(4, newCoverageDetails);
                                updateStmt.setString(5, patientTc);
                                updateStmt.setString(6, policyNumber);

                                int rowsAffected = updateStmt.executeUpdate();
                                if (rowsAffected > 0) {
                                    JOptionPane.showMessageDialog(this, "Poliçe başarıyla güncellendi.");
                                    logAction("Poliçe Düzenle", "Hasta TC: " + patientTc + ", Poliçe Numarası: " + newPolicyNumber);
                                    loadInsuranceData(table); // Tabloyu güncelle
                                } else {
                                    JOptionPane.showMessageDialog(this, "Poliçe güncellenirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Seçilen poliçe bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Poliçe detayları alınırken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deletePolicy(JTable table) {
            // Veritabanından poliçeleri al ve bir JComboBox oluştur
            JComboBox<String> policyComboBox = new JComboBox<>();
            String sqlFetchPolicies = "SELECT patient_tc, policy_number FROM insurance_info";
            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlFetchPolicies);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String patientTc = rs.getString("patient_tc");
                    String policyNumber = rs.getString("policy_number");
                    policyComboBox.addItem(patientTc + " - " + policyNumber);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Poliçeler alınırken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Kullanıcıdan silmek istediği poliçeyi seçmesini iste
            int option = JOptionPane.showConfirmDialog(this, policyComboBox, "Silinecek Poliçeyi Seçin", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) {
                return;
            }

            // Seçilen poliçeyi ayıkla
            String selectedPolicy = (String) policyComboBox.getSelectedItem();
            if (selectedPolicy == null) {
                JOptionPane.showMessageDialog(this, "Geçerli bir poliçe seçin.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String[] parts = selectedPolicy.split(" - ");
            String patientTc = parts[0];
            String policyNumber = parts[1];

            // Kullanıcıdan silme işlemini onaylamasını iste
            int confirm = JOptionPane.showConfirmDialog(this, "Bu poliçeyi silmek istediğinize emin misiniz?", "Poliçe Sil", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String sqlDeletePolicy = "DELETE FROM insurance_info WHERE patient_tc = ? AND policy_number = ?";
                try (Connection conn = Database.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sqlDeletePolicy)) {

                    stmt.setString(1, patientTc);
                    stmt.setString(2, policyNumber);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Poliçe başarıyla silindi.");
                        logAction("Poliçe Sil", "Hasta TC: " + patientTc + ", Poliçe Numarası: " + policyNumber);
                        loadInsuranceData(insuranceTable); // Tabloyu güncelle
                    } else {
                        JOptionPane.showMessageDialog(this, "Poliçe silinirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Poliçe silinirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void viewStaffActions() {
            String sql = "SELECT action, details, created_at FROM logs ORDER BY created_at DESC";
            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                // Tablo başlıklarını tanımlayın
                String[] columnNames = {"Eylem", "Detaylar", "Oluşturulma Tarihi"};
                DefaultTableModel model = new DefaultTableModel(columnNames, 0);

                // ResultSet'teki verileri tablo modeline ekleyin
                while (rs.next()) {
                    String action = rs.getString("action");
                    String details = rs.getString("details");
                    String createdAt = rs.getString("created_at");
                    model.addRow(new Object[]{action, details, createdAt});
                }

                // JTable'ı oluşturun ve modele bağlayın
                JTable logTable = new JTable(model);

                // JTable'ı bir JScrollPane içine ekleyin ve gösterin
                JOptionPane.showMessageDialog(this, new JScrollPane(logTable), "Personel İşlemleri", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Personel işlemleri yüklenirken bir hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void logAction(String action, String details) {
            String sql = "INSERT INTO logs (action, details, created_at) VALUES (?, ?, NOW())";
            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, action);
                stmt.setString(2, details);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //doktor menüsü
    public static class DoctorMenuFrame extends JFrame {

        private String doctorTc;

        // Doktor TC'sini alıp, frame'i başlatan constructor
        public DoctorMenuFrame(String doctorTc) {
            this.doctorTc = doctorTc;  // Doktorun TC'sini sakla

            setTitle("Doktor Menüsü");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 1));

            JButton addDiagnosisButton = new JButton("Randevuya Teşhis Ekle");
            JButton prescribeMedicineButton = new JButton("Hastaya İlaç Yaz");
            JButton viewLabResultsButton = new JButton("Laboratuvar Sonuçlarını Görüntüle");
            JButton referPatientButton = new JButton("Hasta Sevk Et");


            // Butonlara aksiyon ekle
            addDiagnosisButton.addActionListener(e -> addDiagnosisAction());
            prescribeMedicineButton.addActionListener(e -> prescribeMedicineAction());
            viewLabResultsButton.addActionListener(e -> viewLabResultsAction());
            referPatientButton.addActionListener(e -> referPatientAction());

            panel.add(addDiagnosisButton);
            panel.add(prescribeMedicineButton);
            panel.add(viewLabResultsButton);
            panel.add(referPatientButton);


            add(panel);
            setVisible(true);
        }

        // Teşhis ekleme işlemi
        private void addDiagnosisAction() {
            // Hastanın TC'sini al
            String patientTc = JOptionPane.showInputDialog(this, "Hastanın TC Kimlik Numarasını Girin:");
            if (patientTc == null || patientTc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "TC Kimlik Numarası boş bırakılamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Veritabanında hastanın randevularını kontrol et
            List<String> appointments = new ArrayList<>();
            String sql = "SELECT * FROM appointments WHERE doctor_tc = ? AND patient_tc = ? AND diagnosis IS NULL";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, doctorTc); // doctorTc burada sınıfın bir üyesi olarak kullanılıyor
                stmt.setString(2, patientTc);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    appointments.add(rs.getString("appointment_date") + " " + rs.getString("appointment_time"));
                }

                if (appointments.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Bu hastanın randevusu veya teşhisi bulunmamaktadır.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Randevuları göster
                String appointmentList = String.join("\n", appointments);
                String appointmentSelection = JOptionPane.showInputDialog(this, "Randevular:\n" + appointmentList + "\n\nTeşhis eklemek için randevuyu seçin:");

                if (appointmentSelection == null || appointmentSelection.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Randevu seçimi yapılmadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Teşhis girme
                String diagnosis = JOptionPane.showInputDialog(this, "Teşhis Girin:");
                if (diagnosis == null || diagnosis.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Teşhis boş bırakılamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Teşhisi appointments tablosuna kaydet
                String updateSql = "UPDATE appointments SET diagnosis = ? WHERE doctor_tc = ? AND patient_tc = ? AND appointment_date = ? AND appointment_time = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setString(1, diagnosis);
                    updateStmt.setString(2, doctorTc); // doctorTc burada sınıfın bir üyesi olarak kullanılıyor
                    updateStmt.setString(3, patientTc);
                    updateStmt.setString(4, appointments.get(0).split(" ")[0]); // randevu tarihi
                    updateStmt.setString(5, appointments.get(0).split(" ")[1]); // randevu saati
                    updateStmt.executeUpdate();
                }

                // Teşhisi patients tablosuna kaydet (medical_history)
                String updatePatientSql = "UPDATE patients SET medical_history = CONCAT(IFNULL(medical_history, ''), ?) WHERE tc = ?";
                try (PreparedStatement patientStmt = connection.prepareStatement(updatePatientSql)) {
                    patientStmt.setString(1, "\n" + appointments.get(0) + " - " + diagnosis);
                    patientStmt.setString(2, patientTc);
                    patientStmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Teşhis başarıyla kaydedildi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }

        // İlaç yazma işlemi
        private void prescribeMedicineAction() {
            // Hastanın TC'sini al
            String patientTc = JOptionPane.showInputDialog(this, "Hastanın TC Kimlik Numarasını Girin:");
            if (patientTc == null || patientTc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "TC Kimlik Numarası boş bırakılamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Veritabanında hastanın randevularını kontrol et (teşhis eklenmiş olanlar)
            List<String> appointments = new ArrayList<>();
            String sql = "SELECT * FROM appointments WHERE doctor_tc = ? AND patient_tc = ? AND diagnosis IS NOT NULL";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, doctorTc); // doctorTc burada sınıfın bir üyesi olarak kullanılıyor
                stmt.setString(2, patientTc);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    appointments.add(rs.getString("appointment_date") + " " + rs.getString("appointment_time"));
                }

                if (appointments.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Bu hastanın randevusu bulunmamaktadır veya teşhis eklenmemiştir.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Randevuları göster
                String appointmentList = String.join("\n", appointments);
                String appointmentSelection = JOptionPane.showInputDialog(this, "Randevular:\n" + appointmentList + "\n\nBir randevu seçin:");

                if (appointmentSelection == null || appointmentSelection.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Randevu seçimi yapılmadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // İlaç seçme
                JComboBox<String> drugComboBox = new JComboBox<>(AdminMenuFrame.StockManagementFrame.drugs);
                JPanel panel = new JPanel();
                panel.add(new JLabel("İlaç Seçin:"));
                panel.add(drugComboBox);

                int option = JOptionPane.showConfirmDialog(this, panel, "İlaç Seç", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String selectedDrug = (String) drugComboBox.getSelectedItem();
                    String productName = selectedDrug.split(" - ")[0]; // İlacın adını al

                    // Seçilen randevuya ilaç ekleme
                    String updateAppointmentSql = "UPDATE appointments SET prescribed_medicine = ? WHERE doctor_tc = ? AND patient_tc = ? AND appointment_date = ? AND appointment_time = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateAppointmentSql)) {
                        updateStmt.setString(1, productName);
                        updateStmt.setString(2, doctorTc); // doctorTc burada sınıfın bir üyesi olarak kullanılıyor
                        updateStmt.setString(3, patientTc);
                        updateStmt.setString(4, appointmentSelection.split(" ")[0]); // randevu tarihi
                        updateStmt.setString(5, appointmentSelection.split(" ")[1]); // randevu saati
                        updateStmt.executeUpdate();
                    }

                    // Stoktan ilaç miktarını düşür
                    String updateStockSql = "UPDATE stock SET quantity = quantity - 1 WHERE product_name = ?";
                    try (PreparedStatement updateStockStmt = connection.prepareStatement(updateStockSql)) {
                        updateStockStmt.setString(1, productName);
                        updateStockStmt.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this, "İlaç başarıyla yazıldı ve stok güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void viewLabResultsAction() {
            // Hastanın TC'sini al
            String patientTc = JOptionPane.showInputDialog(this, "Hastanın TC Kimlik Numarasını Girin:");
            if (patientTc == null || patientTc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "TC Kimlik Numarası boş bırakılamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Veritabanında laboratuvar sonuçlarını sorgula
            List<String[]> labResults = new ArrayList<>();
            String sql = "SELECT id, test_type, test_result, created_at FROM lab_results WHERE patient_tc = ?";
            try (Connection connection = Database.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, patientTc);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String[] result = {
                            rs.getString("id"),
                            rs.getString("test_type"),
                            rs.getString("test_result"),
                            rs.getString("created_at")
                    };
                    labResults.add(result);
                }

                if (labResults.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Bu hastaya ait laboratuvar sonucu bulunmamaktadır.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Sonuçları butonlarla göster
                JPanel panel = new JPanel(new GridLayout(labResults.size(), 1));
                for (String[] result : labResults) {
                    JButton button = new JButton(result[1] + " - " + result[3]); // Test türü ve tarih
                    button.addActionListener(e -> showLabResultDetails(result));
                    panel.add(button);
                }

                // Pencereyi göster
                JFrame resultsFrame = new JFrame("Laboratuvar Sonuçları");
                resultsFrame.setSize(400, 300);
                resultsFrame.setLocationRelativeTo(this);
                resultsFrame.add(new JScrollPane(panel));
                resultsFrame.setVisible(true);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
        private void showLabResultDetails(String[] result) {
            String message = "Test Türü: " + result[1] + "\n"
                    + "Sonuç: " + result[2] + "\n"
                    + "Oluşturulma Tarihi: " + result[3];
            JOptionPane.showMessageDialog(this, message, "Laboratuvar Sonucu Detayları", JOptionPane.INFORMATION_MESSAGE);
        }
        private void referPatientAction() {
            // Hasta TC'sini al
            String patientTc = JOptionPane.showInputDialog(this, "Hastanın TC Kimlik Numarasını Girin:");
            if (patientTc == null || patientTc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "TC Kimlik Numarası boş bırakılamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hastane adı ve sevk sebebi için bir form oluştur
            JTextField hospitalField = new JTextField();
            JTextArea reasonArea = new JTextArea(5, 20);
            JPanel panel = new JPanel(new GridLayout(4, 1));
            panel.add(new JLabel("Sevk Edilecek Hastane:"));
            panel.add(hospitalField);
            panel.add(new JLabel("Sevk Sebebi:"));
            panel.add(new JScrollPane(reasonArea));

            int option = JOptionPane.showConfirmDialog(this, panel, "Sevk İşlemi", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String hospitalName = hospitalField.getText();
                String referralReason = reasonArea.getText();

                if (hospitalName.isEmpty() || referralReason.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tüm alanlar doldurulmalıdır.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Sevk isteğini veritabanına kaydet
                String sql = "INSERT INTO referrals (patient_tc, doctor_tc, hospital_name, referral_reason) VALUES (?, ?, ?, ?)";
                try (Connection connection = Database.getConnection();
                     PreparedStatement stmt = connection.prepareStatement(sql)) {

                    stmt.setString(1, patientTc);
                    stmt.setString(2, doctorTc); // Sınıfın üyesi olarak doktor TC
                    stmt.setString(3, hospitalName);
                    stmt.setString(4, referralReason);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Sevk isteği başarıyla oluşturuldu.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Veritabanı hatası.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

    }
}


