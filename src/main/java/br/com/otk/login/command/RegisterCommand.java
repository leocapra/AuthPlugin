package br.com.otk.login.command;

import br.com.otk.login.LoginPlugin;
import br.com.otk.login.database.PlayerRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class RegisterCommand implements CommandExecutor {

    private final PlayerRepository repository;

    public RegisterCommand(LoginPlugin plugin) {
        this.repository = new PlayerRepository(
                plugin.getDatabaseManager().getConnection()
        );
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§cUso correto: /register <senha> <senha>");
            return true;
        }

        UUID uuid = player.getUniqueId();
        String password = args[0];
        String confirmPassword = args[1];

        if (!password.equals(confirmPassword)) {
            sender.sendMessage("Senhas diferentes");
        }

        try {
            if (repository.exists(uuid)) {
                player.sendMessage("§cVocê já está registrado.");
                return true;
            }

            repository.save(uuid, player.getName(), password);
            player.sendMessage("§aRegistrado com sucesso! Use /login.");

        } catch (SQLException e) {
            player.sendMessage("§cErro ao registrar. Contate um admin.");
            e.printStackTrace();
        }

        return true;
    }
}
