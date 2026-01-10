package br.com.otk.login;

import br.com.otk.login.command.LoginCommand;
import br.com.otk.login.command.RegisterCommand;
import br.com.otk.login.database.DatabaseManager;
import br.com.otk.login.database.PlayerRepository;
import br.com.otk.login.listener.PlayerRestrictionListener;
import br.com.otk.login.session.SessionManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class LoginPlugin extends JavaPlugin {
    private DatabaseManager databaseManager;

    @Override
        public void onEnable() {

            File dataFolder = new File("plugins/AuthPlugin");
            if (!dataFolder.exists()) {
                boolean created = dataFolder.mkdirs();
                if (created) {
                    getLogger().info("Pasta login-plugin-configuration criada com sucesso.");
                }
            }

            databaseManager = new DatabaseManager(this, dataFolder);
            databaseManager.init();

        PlayerRepository playerRepository = new PlayerRepository(databaseManager.getConnection());
            Objects.requireNonNull(getCommand("login"),
                            "O comando '/login' não foi encontrado no plugin.yml!")
                    .setExecutor(new LoginCommand(playerRepository));
            Objects.requireNonNull(getCommand("register"),
                            "O comando '/register' não foi encontrado no plugin.yml!")
                    .setExecutor(new RegisterCommand(playerRepository));

            getServer().getPluginManager().registerEvents(
                    new PlayerRestrictionListener(playerRepository, JavaPlugin.getPlugin(LoginPlugin.class)),
                    this
            );
            getLogger().info("OTK Login Plugin iniciado!");
        }

        @Override
        public void onDisable() {
            SessionManager.clearAll();

            if (databaseManager != null) {
                databaseManager.close();
            }

            getServer().getScheduler().cancelTasks(this);
        }
    }
