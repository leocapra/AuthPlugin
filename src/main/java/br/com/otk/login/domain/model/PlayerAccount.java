package br.com.otk.login.domain.model;

import br.com.otk.login.domain.valueobject.PlayerStatus;

import java.util.Objects;
import java.util.UUID;

public class PlayerAccount {

    private final UUID uuid;
    private final String username;
    private String password;
    private PlayerStatus status;

    public PlayerAccount(UUID uuid, String username, String password, PlayerStatus status) {
        this.uuid = uuid;
        this.username = username;
        this.password = password;
        this.status = status;
    }

    public void login(String password) {
        if (!Objects.equals(this.password, password)) {
            throw new RuntimeException("Senha inválida");
        }
        this.status = PlayerStatus.LOGADO;
    }

    public void register(String password) {
        this.password = password;
        this.status = PlayerStatus.LOGADO;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public PlayerStatus getStatus() {
        return status;
    }
}
