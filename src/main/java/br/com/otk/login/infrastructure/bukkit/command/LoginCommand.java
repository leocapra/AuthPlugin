package br.com.otk.login.infrastructure.bukkit.command;

import br.com.otk.login.application.usecases.LoginUseCase;
import br.com.otk.login.domain.valueobject.LoginResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public record LoginCommand(LoginUseCase loginUseCase) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (!(sender instanceof Player player)) return true;

        LoginResult result = loginUseCase.execute(player.getUniqueId(), args[0]);
        switch (result) {
            case SUCCESS ->
                    player.sendMessage("§aLogado com sucesso!");
            case PASSWORD_MISMATCH ->
                    player.sendMessage("§cSenha incorreta!");
            case EMPTY_PASSWORD ->
                    player.sendMessage("§cVocê precisa informar uma senha.");
            case DOESNT_EXIST ->
                    player.sendMessage("§cVocê não está registrado! Use /register <senha> <senha> !");
        }


        return true;
    }
}
