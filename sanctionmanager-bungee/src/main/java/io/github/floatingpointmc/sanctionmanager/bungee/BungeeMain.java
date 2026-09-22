package io.github.floatingpointmc.sanctionmanager.bungee;

import io.github.floatingpointmc.sanctionmanager.api.SanctionManagerAPI;
import io.github.floatingpointmc.sanctionmanager.bungee.command.BungeeCommandSender;
import io.github.floatingpointmc.sanctionmanager.bungee.config.Config;
import io.github.floatingpointmc.sanctionmanager.bungee.listener.PlayerListener;
import io.github.floatingpointmc.sanctionmanager.minecraft.MinecraftSanctionManager;
import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommand;
import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommandSender;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageContext;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.bstats.bungeecord.Metrics;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bungee.BungeeCommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;

import java.io.File;
import java.util.ArrayList;

public class BungeeMain extends Plugin {
    private static final int PLUGIN_ID = 34183;
    private Config config;
    private MinecraftSanctionManager manager;

    @Override
    public void onLoad() {
        config = new Config(getDataFolder(), getLogger());
    }

    @Override
    public void onEnable() {
        config.saveDefaultConfig();
        saveDefaultMessages();
        new Metrics(this, PLUGIN_ID);
        Configuration cfg = this.config.getConfig();
        String mode = cfg.getString("mode");
        MessageConfig messageConfig = loadMessageConfig();
        MessageContext contextTemplate = MessageContext.builder()
                .pluginName(getDescription().getName())
                .pluginVersion(getDescription().getVersion())
                .build();

        manager = new MinecraftSanctionManager(
                cfg.getString("database.driver", "com.mysql.cj.jdbc.Driver"),
                cfg.getString("database.host", "localhost"),
                cfg.getInt("database.port", 3306),
                cfg.getString("database.database", "sanctionmanager"),
                cfg.getString("database.user", "root"),
                cfg.getString("database.password", ""));

        if ("standalone".equals(mode)) {
            BungeeCommandManager<SanctionCommandSender> commandManager =
                    new BungeeCommandManager<>(this, ExecutionCoordinator.asyncCoordinator(),
                            SenderMapper.create(
                                    BungeeCommandSender::new,
                                    mapped -> ((BungeeCommandSender) mapped).commandSender
                            ));
            new SanctionCommand(commandManager, messageConfig, contextTemplate).buildCommands();
            getLogger().info("SanctionManager is running in standalone mode.");
        } else {
            getLogger().info("SanctionManager is running in proxy mode, no commands available.");
        }
        getProxy().getPluginManager().registerListener(this,
                new PlayerListener(SanctionManagerAPI.getAPI().getPunishManager(), messageConfig, contextTemplate));
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