package com.example;

import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseCache {
    private static final String DB_URL = "jdbc:sqlite:document_cache.db";
    private static DatabaseCache instance;

    @SneakyThrows
    private DatabaseCache() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS cache (" +
                    "key TEXT PRIMARY KEY," +
                    "value TEXT NOT NULL," +
                    "timestamp INTEGER NOT NULL" +
                    ")";
            stmt.execute(sql);
        }
    }

    public static synchronized DatabaseCache getInstance() {
        if (instance == null) {
            instance = new DatabaseCache();
        }
        return instance;
    }

    @SneakyThrows
    private Connection getConnection() {
        return DriverManager.getConnection(DB_URL);
    }

    @SneakyThrows
    public String get(String key) {
        String sql = "SELECT value FROM cache WHERE key = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("value");
                }
            }
        }
        return null;
    }

    @SneakyThrows
    public void put(String key, String value) {
        String sql = "INSERT OR REPLACE INTO cache (key, value, timestamp) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.setLong(3, System.currentTimeMillis());
            pstmt.executeUpdate();
        }
    }

    @SneakyThrows
    public boolean contains(String key) {
        return get(key) != null;
    }

    @SneakyThrows
    public void clear() {
        String sql = "DELETE FROM cache";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}
