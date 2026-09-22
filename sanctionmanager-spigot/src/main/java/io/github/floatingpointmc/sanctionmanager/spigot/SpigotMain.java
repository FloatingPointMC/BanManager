package io.github.floatingpointmc.sanctionmanager.spigot;

import io.github.floatingpointmc.sanctionmanager.api.BanManagerAPI;
import io.github.floatingpointmc.sanctionmanager.core.BanManagerCore;
import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommand;
import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommandSender;
import io.github.floatingpointmc.sanctionmanager.core.config.DatabaseConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.spigot.bridge.BanManagerBridge;
import io.github.floatingpointmc.sanctionmanager.spigot.command.SpigotCommandSender;
import io.github.floatingpointmc.sanctionmanager.spigot.listener.PlayerListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class SpigotMain extends JavaPlugin {
    private static final int PLUGIN_ID = 34182;
    private BanManagerCore core;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveDefaultMessages();
        new Metrics(this, PLUGIN_ID);
        String mode = getConfig().getString("mode");
        if (mode.equalsIgnoreCase("standalone")) {
            DatabaseConfig databaseConfig = loadDatabaseConfig(getConfig());
            MessageConfig messageConfig = loadMessageConfig();
            MessageContext contextTemplate = MessageContext.builder()
                    .pluginName(getDescription().getName())
                    .pluginVersion(getDescription().getVersion())
                    .build();
            core = new BanManagerCore(databaseConfig);
            new SanctionCommand(new LegacyPaperCommandManager<>(this, ExecutionCoordinator.asyncCoordinator(), new SenderMapper<CommandSender, SanctionCommandSender>() {
                @Override
                public @NotNull SanctionCommandSender map(@NotNull CommandSender base) {
                    return new SpigotCommandSender(base);
                }

                @Override
                public @NotNull CommandSender reverse(@NotNull SanctionCommandSender mapped) {
                    return ((SpigotCommandSender) mapped).commandSender;
                }
            }), messageConfig, contextTemplate);
            getServer().getPluginManager().registerEvents(
                    new PlayerListener(BanManagerAPI.getAPI().getPunishManager(), messageConfig, contextTemplate), this);
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

    private MessageConfig loadMessageConfig() {
        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            saveResource("messages.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        return MessageConfig.builder()
                .description(new ArrayList<>(config.getStringList("description")))
                .banPermanent(new ArrayList<>(config.getStringList("ban.permanent")))
                .banTemporary(new ArrayList<>(config.getStringList("ban.temporary")))
                .mutePermanent(new ArrayList<>(config.getStringList("mute.permanent")))
                .muteTemporary(new ArrayList<>(config.getStringList("mute.temporary")))
                .build();
    }

    private void saveDefaultMessages() {
        File file = new File(getDataFolder(), "messages.yml");
        if (!file.exists()) {
            saveResource("messages.yml", false);
        }
    }
}