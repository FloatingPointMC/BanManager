package io.github.floatingpointmc.sanctionmanager.bungee;

import io.github.floatingpointmc.sanctionmanager.api.SanctionManagerAPI;
import io.github.floatingpointmc.sanctionmanager.bungee.command.BungeeCommandSender;
import io.github.floatingpointmc.sanctionmanager.bungee.config.Config;
import io.github.floatingpointmc.sanctionmanager.bungee.listener.PlayerListener;
import io.github.floatingpointmc.sanctionmanager.core.SanctionManagerCore;
import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommand;
import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommandSender;
import io.github.floatingpointmc.sanctionmanager.core.config.DatabaseConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageContext;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.bstats.bungeecord.Metrics;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bungee.BungeeCommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.ArrayList;

public class BungeeMain extends Plugin {
    private static final int PLUGIN_ID = 34183;
    private Config config;
    private SanctionManagerCore core;

    @Override
    public void onLoad() {
        config = new Config(getDataFolder(), getLogger());
    }

    @Override
    public void onEnable() {
        config.saveDefaultConfig();
        saveDefaultMessages();
        new Metrics(this, PLUGIN_ID);
        Configuration config = this.config.getConfig();
        String mode = config.getString("mode");
        MessageConfig messageConfig = loadMessageConfig();
        MessageContext contextTemplate = MessageContext.builder()
                .pluginName(getDescription().getName())
                .pluginVersion(getDescription().getVersion())
                .build();
        if ("standalone".equals(mode)) {
            new SanctionCommand(new BungeeCommandManager<>(this,
                    ExecutionCoordinator.asyncCoordinator(),
                    new SenderMapper<>() {
                        @Override
                        public @NonNull SanctionCommandSender map(@NonNull CommandSender base) {
                            return new BungeeCommandSender(base);
                        }

                        @Override
                        public @NonNull CommandSender reverse(@NonNull SanctionCommandSender mapped) {
                            return ((BungeeCommandSender) mapped).commandSender;
                        }
                    }), messageConfig, contextTemplate).buildCommands();
            getLogger().info("SanctionManager is running in standalone mode.");
        } else {
            getLogger().info("SanctionManager is running in proxy mode, no commands available.");
        }
        DatabaseConfig databaseConfig = loadDatabaseConfig(config);
        core = new SanctionManagerCore(databaseConfig);
        getProxy().getPluginManager().registerListener(this,
                new PlayerListener(SanctionManagerAPI.getAPI().getPunishManager(), messageConfig, contextTemplate));
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
                .database(config.getString("database.database", "sanctionmanager"))
                .user(config.getString("database.user", "root"))
                .password(config.getString("database.password", ""))
                .build();
    }

    private MessageConfig loadMessageConfig() {
        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            config.saveResource("messages.yml", false);
        }
        Configuration cfg = this.config.loadConfiguration(file);
        return MessageConfig.builder()
                .description(new ArrayList<>(cfg.getStringList("description")))
                .banPermanent(new ArrayList<>(cfg.getStringList("ban.permanent")))
                .banTemporary(new ArrayList<>(cfg.getStringList("ban.temporary")))
                .mutePermanent(new ArrayList<>(cfg.getStringList("mute.permanent")))
                .muteTemporary(new ArrayList<>(cfg.getStringList("mute.temporary")))
                .build();
    }

    private void saveDefaultMessages() {
        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            config.saveResource("messages.yml", false);
        }
    }
}