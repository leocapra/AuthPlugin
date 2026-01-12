package br.com.otk.login.infrastructure.bukkit.command;

import br.com.otk.login.application.usecases.RegisterUseCase;
import br.com.otk.login.domain.valueobject.RegisterResult;
import br.com.otk.login.infrastructure.bukkit.session.LoginTimeoutManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public record RegisterCommand(RegisterUseCase registerUseCase) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cNão foi possível registra conta, você precisa de uma senha!");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage("§cUso correto: /register <senha> <senha>");
            return true;
        }


        RegisterResult result = registerUseCase.execute(player.getUniqueId(), player.getName(), args[0], args[1]);
        switch (result) {
            case SUCCESS ->{
                    player.sendMessage("§aRegistrado com sucesso!");
                    LoginTimeoutManager.cancel(player.getUniqueId());
            }
            case PASSWORD_MISMATCH ->
                    player.sendMessage("§cAs senhas não coincidem.");
            case EMPTY_PASSWORD ->
                    player.sendMessage("§cVocê precisa informar uma senha.");
            case ALREADY_EXISTS ->
                    player.sendMessage("§cVocê já está registrado.");
            case INVALID_PASSWORD ->
                player.sendMessage("§cA senha precisa ter mais que 8 caracteres.");
        }


        return true;
    }
}
