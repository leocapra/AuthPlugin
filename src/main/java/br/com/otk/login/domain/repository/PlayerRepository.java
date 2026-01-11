package br.com.otk.login.domain.repository;

import br.com.otk.login.domain.model.PlayerAccount;
import br.com.otk.login.domain.valueobject.PlayerStatus;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {
    PlayerAccount findByUuid(UUID uuid) throws SQLException;
    boolean exists(UUID uuid);
    void save(PlayerAccount account);
    void updateStatus(UUID uuid, PlayerStatus playerStatus);
    boolean matchPassword(UUID uuid, String password);
}
