package io.github.floatingpointmc.sanctionmanager.bungee;

import io.github.floatingpointmc.sanctionmanager.api.BanManagerAPI;
import io.github.floatingpointmc.sanctionmanager.bungee.config.Config;
import io.github.floatingpointmc.sanctionmanager.bungee.listener.PlayerListener;
import io.github.floatingpointmc.sanctionmanager.core.BanManagerCore;
import io.github.floatingpointmc.sanctionmanager.core.config.DatabaseConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageConfig;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.bstats.bungeecord.Metrics;

import java.io.File;
import java.util.ArrayList;

public class BungeeMain extends Plugin {
    private static final int PLUGIN_ID = 34183;
    private Config config;
    private BanManagerCore core;

    @Override
    public void onLoad() {
        config = new Config(getDataFolder(), getLogger());
    }

    @Override
    public void onEnable() {
        config.saveDefaultConfig();
        saveDefaultMessages();
        new Metrics(this, PLUGIN_ID);
        DatabaseConfig databaseConfig = loadDatabaseConfig(config.getConfig());
        MessageConfig messageConfig = loadMessageConfig();
        core = new BanManagerCore(databaseConfig);
        getProxy().getPluginManager().registerListener(this,
                new PlayerListener(BanManagerAPI.getAPI().getPunishManager(), messageConfig));
        getLogger().info("BanManager is running in standalone mode.");

    }

    @Override
    public void onDisable() {
        if (core != null) {
            core.shutdown();
            core = null;
        }
    }

    private DatabaseConfig loadDatabaseConfig(Configuration config) {
        return DatabaseConfig.builder()
                .driver(config.getString("database.driver", "com.mysql.cj.jdbc.Driver"))
                .host(config.getString("database.host", "localhost"))
                .port(config.getInt("database.port", 3306))
                .database(config.getString("database.database", "banmanager"))
                .user(config.getString("database.user", "root"))
                .password(config.getString("database.password", ""))
                .build();
    }

    private MessageConfig loadMessageConfig() {
        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            config.saveResource("messages.yml", false);
        }
        Configuration config = this.config.loadConfiguration(file);
        return MessageConfig.builder()
                .banPermanent(new ArrayList<>(config.getStringList("ban.permanent")))
                .banTemporary(new ArrayList<>(config.getStringList("ban.temporary")))
                .mutePermanent(new ArrayList<>(config.getStringList("mute.permanent")))
                .muteTemporary(new ArrayList<>(config.getStringList("mute.temporary")))
                .build();
    }

    private void saveDefaultMessages() {
        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            config.saveResource("messages.yml", false);
        }
    }
}
