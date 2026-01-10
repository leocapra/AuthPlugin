package br.com.otk.login.session;

import br.com.otk.login.model.PlayerStatus;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<UUID, PlayerStatus> STATUS = new HashMap<>();
    private static final Map<UUID, Boolean> hasPasswordCache = new ConcurrentHashMap<>();
    private static final Map<UUID, Integer> titleTasks = new ConcurrentHashMap<>();

    public static void setHasPassword(UUID uuid, boolean hasPassword) {
        hasPasswordCache.put(uuid, hasPassword);
    }

    public static boolean hasPassword(UUID uuid) {
        return hasPasswordCache.getOrDefault(uuid, false);
    }

    public static void clearHasPassword(UUID uuid) {
        hasPasswordCache.remove(uuid);
    }

    public static void registerTitleTask(UUID uuid, int taskId) {
        titleTasks.put(uuid, taskId);
    }

    public static void cancelTitleTask(UUID uuid) {
        Integer taskId = titleTasks.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }


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

    public static void clear(UUID uuid) {
        STATUS.put(uuid, PlayerStatus.DESLOGADO);
    }

    public static void clearAll() {
        STATUS.clear();
    }
}
