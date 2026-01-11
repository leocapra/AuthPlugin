package br.com.otk.login.infrastructure.persistence;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private final File dataFolder;
    private Connection connection;

    public DatabaseManager(JavaPlugin plugin, File dataFolder) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
    }

    public void init() {
        try {
            File dbfile = new File(dataFolder, "players.db");

            String url = "jdbc:sqlite:" + dbfile.getAbsolutePath();
            connection = DriverManager.getConnection(url);

            createTable();
        } catch (SQLException e) {
            plugin.getLogger().severe("[OtkLogin]: Falha ao conectar no SQLite!");
        }
    }

    private void createTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS players (
                uuid TEXT PRIMARY KEY,
                username TEXT NOT NULL,
                password TEXT,
                status TEXT NOT NULL DEFAULT 'DESLOGADO'
                );
                """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            init();
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }catch (SQLException e){
            throw new RuntimeException("erro ao fechar banco de dados");
        }
    }

}
