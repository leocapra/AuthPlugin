package br.com.otk.login.session;

import br.com.otk.login.model.PlayerStatus;

import java.util.*;

public class SessionManager {

    private static final Map<UUID, PlayerStatus> STATUS = new HashMap<>();

    public static PlayerStatus get(UUID uuid) {
        return STATUS.getOrDefault(uuid, PlayerStatus.DESLOGADO);
    }

    public static boolean isLogged(UUID uuid) {
        return get(uuid) == PlayerStatus.LOGADO;
    }

    public static void login(UUID uuid) {
        STATUS.put(uuid, PlayerStatus.LOGADO);
    }

    public static boolean isBanned(UUID uuid) {
        return get(uuid) == PlayerStatus.BANIDO;
    }

    public static PlayerStatus clear(UUID uuid) {
        return STATUS.put(uuid, PlayerStatus.DESLOGADO);
    }

    public static void clearAll() {
        STATUS.clear();
    }
}
