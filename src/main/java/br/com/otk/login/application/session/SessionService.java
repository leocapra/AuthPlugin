package br.com.otk.login.application.session;

import br.com.otk.login.domain.valueobject.PlayerStatus;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionService {

    private final Map<UUID, PlayerStatus> sessions = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> titleTasks = new ConcurrentHashMap<>();


    public void login(UUID uuid) {
        sessions.put(uuid, PlayerStatus.LOGADO);
    }

    public boolean isLogged(UUID uuid) {
        return sessions.getOrDefault(uuid, PlayerStatus.DESLOGADO) == PlayerStatus.LOGADO;
    }

    public void logout(UUID uuid) {
        sessions.remove(uuid);
    }

    public void clear() {
        sessions.clear();
    }

    public void cancelTitleTask(UUID uuid) {
        Integer taskId = titleTasks.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public void registerTitleTask(UUID uuid, int taskId) {
        titleTasks.put(uuid, taskId);
    }
}
