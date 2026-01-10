package br.com.otk.login.database;

import br.com.otk.login.model.PlayerStatus;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerRepository {

    private final Connection connection;

    public PlayerRepository(Connection connection) {
        this.connection = connection;
    }

    public boolean exists(UUID uuid) throws SQLException {

        String query = "SELECT 1 FROM players WHERE uuid = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, uuid.toString());
            return ps.executeQuery().next();
        }
    }

    public void save(UUID uuid, String username, String password) throws SQLException {
        String query = "INSERT INTO players (uuid, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, username);
            ps.setString(3, password);
            ps.executeUpdate();
        }
    }

    public String findPassword(UUID uuid) throws SQLException {
        String query = "SELECT password FROM players WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
        }
        return null;
    }

    public PlayerStatus getStatus(UUID uuid) throws SQLException {
        String query = "SELECT status FROM players WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return PlayerStatus.valueOf(rs.getString("status"));
            }
        }
        return PlayerStatus.DESLOGADO;
    }

    public void updateStatus(UUID uuid, PlayerStatus status) throws SQLException {
        String query = "UPDATE players SET status = ? WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, status.name());
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        }
    }



}
