package br.com.otk.login.infrastructure.persistence;

import br.com.otk.login.domain.model.PlayerAccount;
import br.com.otk.login.domain.repository.PlayerRepository;
import br.com.otk.login.domain.valueobject.PlayerStatus;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record SQLitePlayerRepository(Connection connection) implements PlayerRepository {


    @Override
    public PlayerAccount findByUuid(UUID uuid) {
        String query = "SELECT uuid, username, password, status FROM players WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            return new PlayerAccount(
                    UUID.fromString(rs.getString("uuid")),
                    rs.getString("username"),
                    rs.getString("password"),
                    PlayerStatus.valueOf(rs.getString("status"))
            );
        } catch (SQLException e) {
            throw new RuntimeException("Não foi possivel encontrar jogador");
        }
    }

    @Override
    public boolean exists(UUID uuid) {
        String query = "SELECT 1 FROM players WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            return ps.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência do jogador " + uuid, e);
        }
    }

    @Override
    public void save(PlayerAccount account) {
        System.out.println("SALVAR PLAYER_ACCOUNT " + account.getUuid().toString());
        String query = """
                INSERT INTO players (uuid, username, password, status)
                VALUES (?, ?, ?, ?)
                """;
        String hash = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt(12));

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, account.getUuid().toString());
            ps.setString(2, account.getUsername());
            ps.setString(3, hash);
            ps.setString(4, account.getStatus().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o jogador " + account.getUuid(), e);
        }
    }

    @Override
    public void updateStatus(UUID uuid, PlayerStatus playerStatus) {
        String query = "UPDATE players SET status = ? WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, playerStatus.name());
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar o jogador " + uuid, e);
        }
    }

    @Override
    public boolean matchPassword(UUID uuid, String inputPassword) {
        String query = "SELECT password FROM players WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                String hash = rs.getString("password");
                return BCrypt.checkpw(inputPassword, hash);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao validar senha do jogador " + uuid, e);
        }
    }
}
