package io.github.floatingpointmc.banmanager.spigot;

import io.github.floatingpointmc.banmanager.api.BanManagerAPI;
import io.github.floatingpointmc.banmanager.core.BanManagerCore;
import io.github.floatingpointmc.banmanager.core.config.DatabaseConfig;
import io.github.floatingpointmc.banmanager.spigot.bridge.BanManagerBridge;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotMain extends JavaPlugin {
    private BanManagerCore core;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        String mode = getConfig().getString("mode");
        if (mode.equalsIgnoreCase("standalone")) {
            DatabaseConfig databaseConfig = loadDatabaseConfig(getConfig());
            core = new BanManagerCore(databaseConfig);
            getLogger().info("BanManager is running in standalone mode.");
        } else {
            getLogger().warning("BanManager is running under bridge mode, no features available.");
            BanManagerAPI.register(new BanManagerBridge());
        }
    }

    @Override
    public void onDisable() {
        if (core != null) {
            core.shutdown();
            core = null;
        }
    }

    private DatabaseConfig loadDatabaseConfig(FileConfiguration config) {
        return DatabaseConfig.builder()
                .driver(config.getString("database.driver", "com.mysql.cj.jdbc.Driver"))
                .host(config.getString("database.host", "localhost"))
                .port(config.getInt("database.port", 3306))
                .database(config.getString("database.database", "banmanager"))
                .user(config.getString("database.user", "root"))
                .password(config.getString("database.password", ""))
                .build();
    }
}