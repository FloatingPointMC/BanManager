package io.github.floatingpointmc.sanctionmanager.spigot;

import io.github.floatingpointmc.sanctionmanager.api.SanctionManagerAPI;
import io.github.floatingpointmc.sanctionmanager.minecraft.MinecraftSanctionManager;
import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommand;
import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommandSender;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.spigot.bridge.SanctionManagerBridge;
import io.github.floatingpointmc.sanctionmanager.spigot.command.SpigotCommandSender;
import io.github.floatingpointmc.sanctionmanager.spigot.listener.PlayerListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.io.File;
import java.util.ArrayList;

public class SpigotMain extends JavaPlugin {
    private static final int PLUGIN_ID = 34182;
    private MinecraftSanctionManager manager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveDefaultMessages();
        new Metrics(this, PLUGIN_ID);
        String mode = getConfig().getString("mode");
        if (mode.equalsIgnoreCase("standalone")) {
            MessageConfig messageConfig = loadMessageConfig();
            MessageContext contextTemplate = MessageContext.builder()
                    .pluginName(getDescription().getName())
                    .pluginVersion(getDescription().getVersion())
                    .build();

            manager = new MinecraftSanctionManager(
                    getConfig().getString("database.driver", "com.mysql.cj.jdbc.Driver"),
                    getConfig().getString("database.host", "localhost"),
                    getConfig().getInt("database.port", 3306),
                    getConfig().getString("database.database", "sanctionmanager"),
                    getConfig().getString("database.user", "root"),
                    getConfig().getString("database.password", ""));

            LegacyPaperCommandManager<SanctionCommandSender> commandManager =
                    new LegacyPaperCommandManager<>(this, ExecutionCoordinator.asyncCoordinator(),
                            SenderMapper.create(
                                    SpigotCommandSender::new,
                                    mapped -> ((SpigotCommandSender) mapped).commandSender
                            ));
            new SanctionCommand(commandManager, messageConfig, contextTemplate).buildCommands();

            getServer().getPluginManager().registerEvents(
                    new PlayerListener(SanctionManagerAPI.getAPI().getPunishManager(), messageConfig, contextTemplate), this);
            getLogger().info("SanctionManager is running in standalone mode.");
        } else {
            getLogger().warning("SanctionManager is running under bridge mode, no features available.");
            SanctionManagerAPI.register(new SanctionManagerBridge());
        }
    }

    @Override
    public void onDisable() {
        if (manager != null) {
            manager.shutdown();
            manager = null;
        }
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