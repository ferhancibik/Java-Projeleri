package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class Database {
    private static final String URL = "jdbc:mariadb://localhost:3325/akillihastane"; // Veritabanı URL'niz
    private static final String USER = "root"; // Veritabanı kullanıcı adı
    private static final String PASSWORD = "ferfeyfat"; // Veritabanı şifresi
    private static Database instance;
    private Connection connection;

    // Veritabanı bağlantısını oluşturuyoruz
    private Database() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection successful.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Singleton pattern ile tek bir instance döndürüyoruz
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // Bağlantıyı geri döndürüyoruz
    public Connection getConnection() {
        if (connection == null || !isConnectionValid()) {
            connection = createNewConnection(); // Bağlantı kapanmışsa yeniden oluşturuyoruz
        }
        return connection;
    }

    // Bağlantının geçerli olup olmadığını kontrol ediyoruz
    private boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Yeni bağlantı oluşturuyoruz
    private Connection createNewConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("New database connection created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Bağlantıyı kapatıyoruz
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Tarih doğrulama metodu
    public boolean isValidDate(String date) {
        String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false); // Katı doğrulama yap
        try {
            sdf.parse(date); // Tarihi parse etmeye çalış
            return true; // Geçerli tarihse true döndür
        } catch (ParseException e) {
            return false; // Geçersiz tarihse false döndür
        }
    }

    public boolean checkLogin(String tc, String password, String userType) {
        String sql = "";
        if (userType.equals("Doktor")) {
            sql = "SELECT * FROM doctors WHERE tc = ? AND password = ?";
        } else if (userType.equals("Hasta")) {
            sql = "SELECT * FROM patients WHERE tc = ? AND password = ?";
        } else if (userType.equals("Personel")) {
            sql = "SELECT * FROM staff WHERE tc = ? AND password = ?";
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tc);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Eğer kayıt varsa giriş başarılı
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Kullanıcı ekleme metodunu ekliyoruz
    public boolean addUser(String userType, String name, int age, String address, String tc, String password) {
        String sql = "";

        // Kullanıcı tipine göre SQL sorgusu oluşturuluyor
        if (userType.equalsIgnoreCase("Doktor")) {
            sql = "INSERT INTO doctors (name, age, address, tc, password) VALUES (?, ?, ?, ?, ?)";
        } else if (userType.equalsIgnoreCase("Hasta")) {
            sql = "INSERT INTO patients (name, age, address, tc, password) VALUES (?, ?, ?, ?, ?)";
        } else if (userType.equalsIgnoreCase("Personel")) {
            sql = "INSERT INTO staff (name, age, address, tc, password) VALUES (?, ?, ?, ?, ?)";
        }

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, address);
            stmt.setString(4, tc);
            stmt.setString(5, password);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // Eğer kayıt başarıyla eklenirse true döner
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;  // Kayıt eklenemezse false döner
    }
}


