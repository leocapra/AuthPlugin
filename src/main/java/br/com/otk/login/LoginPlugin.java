package br.com.otk.login;

import br.com.otk.login.application.session.SessionService;
import br.com.otk.login.application.usecases.LoginUseCase;
import br.com.otk.login.application.usecases.RegisterUseCase;
import br.com.otk.login.domain.repository.PlayerRepository;
import br.com.otk.login.infrastructure.bukkit.command.LoginCommand;
import br.com.otk.login.infrastructure.bukkit.command.RegisterCommand;
import br.com.otk.login.infrastructure.bukkit.session.LoginTimeoutManager;
import br.com.otk.login.infrastructure.persistence.DatabaseManager;
import br.com.otk.login.infrastructure.bukkit.listener.PlayerRestrictionListener;
import br.com.otk.login.infrastructure.persistence.SQLitePlayerRepository;
import org.bukkit.plugin.java.JavaPlugin;
import br.com.otk.login.infrastructure.bukkit.command.CommandLogFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;


import java.io.File;
import java.util.Objects;

public class LoginPlugin extends JavaPlugin {

    private SessionService sessionService;

    private void registerLogFilter() {
        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addFilter(new CommandLogFilter());
    }

    @Override
    public void onEnable() {

        registerLogFilter();
        getLogger().info("OTKLogin habilitado com filtro de logs de comandos sensíveis");

        File dataFolder = new File("plugins/OTKLogin");
        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdirs();
            if (created) {
                getLogger().info("Pasta login-plugin-configuration criada com sucesso.");
            }
        }

        DatabaseManager databaseManager = new DatabaseManager(this, dataFolder);
        databaseManager.init();

        PlayerRepository playerRepository = new SQLitePlayerRepository(databaseManager.getConnection());

        sessionService = new SessionService();
        LoginTimeoutManager loginTimeoutManager = new LoginTimeoutManager(this, sessionService);

        LoginUseCase loginUseCase =
                new LoginUseCase(playerRepository, sessionService);

        RegisterUseCase registerUseCase =
                new RegisterUseCase(playerRepository, sessionService);

        Objects.requireNonNull(getCommand("login"))
                .setExecutor(new LoginCommand(loginUseCase));

        Objects.requireNonNull(getCommand("register"))
                .setExecutor(new RegisterCommand(registerUseCase));

        getServer().getPluginManager().registerEvents(
                new PlayerRestrictionListener(playerRepository, sessionService, loginTimeoutManager, this),
                this
        );

        getLogger().info("OTK Login Plugin iniciado!");
    }

    @Override
    public void onDisable() {
        if (sessionService != null) {
            sessionService.clear();
        }
    }


}
