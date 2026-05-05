package database;

import model.Account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public List<Account> getAccountsByUser(int userId) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE user_id = ? ORDER BY account_name";
        Connection con = DBConnection.getConnection();                 // ✅ outside try‑with
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Account a = new Account();
                    a.setId(rs.getInt("id"));
                    a.setUserId(rs.getInt("user_id"));
                    a.setAccountName(rs.getString("account_name"));
                    a.setAccountType(rs.getString("account_type"));
                    a.setBalance(rs.getDouble("balance"));
                    a.setCurrency(rs.getString("currency"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // ❌ do NOT close con
        return list;
    }

    public boolean addAccount(Account account) {
        String sql = "INSERT INTO accounts (user_id, account_name, account_type, balance, currency) VALUES (?, ?, ?, ?, ?)";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, account.getUserId());
            ps.setString(2, account.getAccountName());
            ps.setString(3, account.getAccountType());
            ps.setDouble(4, account.getBalance());
            ps.setString(5, account.getCurrency() == null ? "INR" : account.getCurrency());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAccount(Account acc) {
        String sql = "UPDATE accounts SET account_name=?, account_type=?, balance=? WHERE id=?";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, acc.getAccountName());
            ps.setString(2, acc.getAccountType());
            ps.setDouble(3, acc.getBalance());
            ps.setInt(4, acc.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAccount(int accountId) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalBalance(int userId) {
        String sql = "SELECT SUM(balance) FROM accounts WHERE user_id = ?";
        Connection con = DBConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}