package br.com.otk.login.listener;

import br.com.otk.login.database.PlayerRepository;
import br.com.otk.login.model.PlayerStatus;
import br.com.otk.login.session.SessionManager;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.UUID;

public record PlayerRestrictionListener(
        PlayerRepository repository,
        JavaPlugin plugin
) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws SQLException {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (SessionManager.isLogged(uuid)) return;
        boolean hasPassword = repository.findPassword(uuid) != null;
        SessionManager.setHasPassword(uuid, hasPassword);

        int taskId = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {

                    if (!player.isOnline() || SessionManager.isLogged(uuid)) {
                        SessionManager.cancelTitleTask(uuid);
                        player.clearTitle();
                        return;
                    }

                    if (!SessionManager.hasPassword(uuid)) {
                        player.showTitle(
                                Title.title(
                                        Component.text("§7Registro necessário"),
                                        Component.text("§7Use /register <senha> <senha>")
                                )
                        );
                    } else {
                        player.showTitle(
                                Title.title(
                                        Component.text("§7Login necessário"),
                                        Component.text("§7Use /login <senha>")
                                )
                        );
                    }

                },
                0L,
                20L * 5
        ).getTaskId();

        SessionManager.registerTitleTask(uuid, taskId);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        SessionManager.clear(uuid);
        SessionManager.cancelTitleTask(uuid);
        SessionManager.clearHasPassword(uuid);

        try {
            repository.updateStatus(uuid, PlayerStatus.DESLOGADO);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Não foi possível atualizar status do jogador " + uuid, e
            );
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!SessionManager.isLogged(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (!SessionManager.isLogged(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (SessionManager.isLogged(player.getUniqueId())) return;

        String message = event.getMessage().toLowerCase();
        if (message.startsWith("/login") || message.startsWith("/register")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!SessionManager.isLogged(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!SessionManager.isLogged(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
