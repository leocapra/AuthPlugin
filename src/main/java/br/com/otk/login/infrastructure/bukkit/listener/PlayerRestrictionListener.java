package br.com.otk.login.infrastructure.bukkit.listener;

import br.com.otk.login.LoginPlugin;
import br.com.otk.login.application.session.SessionService;
import br.com.otk.login.domain.model.PlayerAccount;
import br.com.otk.login.domain.repository.PlayerRepository;
import br.com.otk.login.domain.valueobject.PlayerStatus;
import br.com.otk.login.infrastructure.bukkit.session.LoginTimeoutManager;
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
        SessionService sessionService,
        LoginTimeoutManager loginTimeoutManager,
        JavaPlugin plugin
        ) implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws SQLException {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        loginTimeoutManager.start(plugin, player);


        if (sessionService.isLogged(uuid)) return;

        boolean playerExists = repository.exists(uuid);


        int taskId = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {

                    if (!player.isOnline() || sessionService.isLogged(uuid)) {
                        sessionService.cancelTitleTask(uuid);
                        player.clearTitle();
                        return;
                    }

                    if (!playerExists) {
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

        sessionService.registerTitleTask(uuid, taskId);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        sessionService.logout(uuid);
        sessionService.cancelTitleTask(uuid);
        LoginTimeoutManager.cancel(uuid);
        repository.updateStatus(uuid, PlayerStatus.DESLOGADO);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!sessionService.isLogged(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (!sessionService.isLogged(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (sessionService.isLogged(player.getUniqueId())) return;

        String message = event.getMessage().toLowerCase();
        if (message.startsWith("/login") || message.startsWith("/register")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!sessionService.isLogged(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!sessionService.isLogged(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
