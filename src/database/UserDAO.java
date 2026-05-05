package database;

import model.User;
import utils.PasswordUtils;

import java.sql.*;

public class UserDAO {

    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (full_name, email, phone, password) VALUES (?, ?, ?, ?)";
        String hashedPassword = PasswordUtils.hashPassword(user.getPassword());
        Connection con = DBConnection.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, hashedPassword);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error registering user", e);
        }
    }

    public User loginUser(String input, String password) {
        String sql = "SELECT * FROM users WHERE email = ? OR phone = ? LIMIT 1";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, input);
            pstmt.setString(2, input);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtils.verifyPassword(password, storedHash)) {
                        User user = new User();
                        user.setId(rs.getInt("id"));
                        user.setFullName(rs.getString("full_name"));
                        user.setEmail(rs.getString("email"));
                        user.setPhone(rs.getString("phone"));
                        user.setCreatedAt(rs.getTimestamp("created_at"));
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login failed", e);
        }
        return null;
    }

    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        String hashedPassword = PasswordUtils.hashPassword(newPassword);
        Connection con = DBConnection.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, hashedPassword);
            pstmt.setString(2, email);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating password", e);
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking email", e);
        }
    }

    public boolean phoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking phone", e);
        }
    }

    public boolean verifyPassword(int userId, String plainPassword) {
        String sql = "SELECT password FROM users WHERE id = ?";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    return PasswordUtils.verifyPassword(plainPassword, storedHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(int userId, String newPlainPassword) {
        String hashed = PasswordUtils.hashPassword(newPlainPassword);
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hashed);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}