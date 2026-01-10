package br.com.otk.login.command;

import br.com.otk.login.LoginPlugin;
import br.com.otk.login.database.PlayerRepository;
import br.com.otk.login.model.PlayerStatus;
import br.com.otk.login.session.SessionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.UUID;

public class LoginCommand implements CommandExecutor {

    private final PlayerRepository repository;

    public LoginCommand(LoginPlugin plugin) {
        this.repository = new PlayerRepository(
                plugin.getDatabaseManager().getConnection()
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cUso correto: /login <senha>");
            return true;
        }

        UUID uuid = player.getUniqueId();
        String password = args[0];


            if (SessionManager.isLogged(uuid)) {
                player.sendMessage("Jogador já está logado!");
                return true;
            }


        try {
            if (!repository.exists(uuid)) {
                player.sendMessage("§cVocê não está registrado. Use /register.");
                return true;
            }

            String storedPassword = repository.findPassword(uuid);

            if (storedPassword == null || !storedPassword.equals(password)) {
                player.sendMessage("§cSenha incorreta.");
                return true;
            }

            repository.updateStatus(uuid, PlayerStatus.LOGADO);
            SessionManager.login(uuid);
            player.sendMessage("§aLogin realizado com sucesso!");

        } catch (SQLException e) {
            player.sendMessage("§cErro ao fazer login.");
            e.printStackTrace();
        }

        return true;
    }
}
