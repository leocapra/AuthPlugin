package br.com.otk.login;

import br.com.otk.login.command.LoginCommand;
import br.com.otk.login.command.RegisterCommand;
import br.com.otk.login.database.DatabaseManager;
import br.com.otk.login.database.PlayerRepository;
import br.com.otk.login.listener.PlayerRestrictionListener;
import br.com.otk.login.session.SessionManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LoginPlugin extends JavaPlugin {

    private static LoginPlugin instance;
    private DatabaseManager databaseManager;
    private File dataFolder;
    private PlayerRepository playerRepository;

    public void onEnable() {
        dataFolder = new File("plugins/login-plugin-configuration");

        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdirs();
            if (created) {
                getLogger().info("Pasta login-plugin-configuration criada com sucesso.");
            }
        }



        databaseManager = new DatabaseManager(this, dataFolder);
        databaseManager.init();

        getCommand("register").setExecutor(new RegisterCommand(this));
        getCommand("login").setExecutor(new LoginCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerRestrictionListener(playerRepository), this);


        getLogger().info("OTK Login Plugin iniciado!");
    }

    public void onDisable() {
        SessionManager.clearAll();
        databaseManager.close();
        getServer().getScheduler().cancelTasks(this);
    }

    public static LoginPlugin getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public File getCustomDataFolder() {
        return dataFolder;
    }




}
