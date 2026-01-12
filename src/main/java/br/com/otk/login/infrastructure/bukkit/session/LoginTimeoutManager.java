package br.com.otk.login.infrastructure.bukkit.session;

import br.com.otk.login.application.session.SessionService;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public record LoginTimeoutManager(JavaPlugin plugin, SessionService sessionService) {

    private static final Map<UUID, Integer> TASKS = new ConcurrentHashMap<>();
    private static final int TIMEOUT_TICKS = 20 * 30;


    public void start(JavaPlugin plugin, Player player) {
        UUID uuid = player.getUniqueId();

        int taskId = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!sessionService.isLogged(uuid)) {
                player.kick(Component.text("§cVocê demorou para fazer login.\nUse /login ou /register."));
            }
            TASKS.remove(uuid);
        }, TIMEOUT_TICKS).getTaskId();

        TASKS.put(uuid, taskId);
    }

    public static void cancel(UUID uuid) {
        Integer taskId = TASKS.remove(uuid);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}
