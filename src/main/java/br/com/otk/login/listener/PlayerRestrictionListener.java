package br.com.otk.login.listener;

import br.com.otk.login.database.PlayerRepository;
import br.com.otk.login.model.PlayerStatus;
import br.com.otk.login.session.SessionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;

import javax.swing.*;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerRestrictionListener implements Listener {

    private final PlayerRepository repository;

    public PlayerRestrictionListener(PlayerRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        UUID uuid = event.getPlayer().getUniqueId();
        SessionManager.clear(uuid);

        try {
            repository.updateStatus(uuid, PlayerStatus.DESLOGADO);
        } catch (SQLException e) {
            throw new RuntimeException("Não foi possivel mudar o status do jogador " + uuid + " para deslogado");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!SessionManager.isLogged(uuid)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
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
