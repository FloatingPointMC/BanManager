package io.github.floatingpointmc.sanctionmanager.spigot;

import io.github.floatingpointmc.sanctionmanager.api.SanctionManagerAPI;
import io.github.floatingpointmc.sanctionmanager.minecraft.MinecraftProvider;
import io.github.floatingpointmc.sanctionmanager.minecraft.MinecraftSanctionManager;
import io.github.floatingpointmc.sanctionmanager.minecraft.SanctionPlayer;
import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommandManager;
import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommandSender;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.spigot.bridge.SanctionManagerBridge;
import io.github.floatingpointmc.sanctionmanager.spigot.command.SpigotCommandSender;
import io.github.floatingpointmc.sanctionmanager.spigot.command.SpigotSanctionPlayer;
import io.github.floatingpointmc.sanctionmanager.spigot.listener.PlayerListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpigotMain extends JavaPlugin {
    private static final int PLUGIN_ID = 34182;
    private MinecraftSanctionManager manager;

    private final MinecraftProvider provider = new MinecraftProvider() {
        @Override
        public @NotNull Collection<String> getPlayerNames() {
            return Bukkit.getOnlinePlayers().stream()
                    .map(org.bukkit.entity.Player::getName)
                    .collect(Collectors.toList());
        }

        @Override
        public @NotNull Collection<UUID> getPlayerUUIDs() {
            return Bukkit.getOnlinePlayers().stream()
                    .map(org.bukkit.entity.Player::getUniqueId)
                    .collect(Collectors.toList());
        }

        @Override
        public @Nullable SanctionPlayer getPlayer(@NotNull UUID uuid) {
            org.bukkit.entity.Player player = Bukkit.getPlayer(uuid);
            return player != null ? new SpigotSanctionPlayer(player) : null;
        }

        @Override
        public @Nullable SanctionPlayer getPlayer(@NotNull String name) {
            org.bukkit.entity.Player player = Bukkit.getPlayer(name);
            return player != null ? new SpigotSanctionPlayer(player) : null;
        }
    };

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

            String binaryDir = getDataFolder().toPath()
                    .resolve(getConfig().getString("storage.binary.directory", "data"))
                    .toString();

            manager = new MinecraftSanctionManager(
                    provider,
                    getConfig().getBoolean("storage.database.enabled", true),
                    getConfig().getString("storage.database.driver", "com.mysql.cj.jdbc.Driver"),
                    getConfig().getString("storage.database.host", "localhost"),
                    getConfig().getInt("storage.database.port", 3306),
                    getConfig().getString("storage.database.database", "sanctionmanager"),
                    getConfig().getString("storage.database.user", "root"),
                    getConfig().getString("storage.database.password", ""),
                    getConfig().getBoolean("storage.redis.enabled", false),
                    getConfig().getString("storage.redis.host", "localhost"),
                    getConfig().getInt("storage.redis.port", 6379),
                    getConfig().getString("storage.redis.password", ""),
                    binaryDir);

            LegacyPaperCommandManager<SanctionCommandSender> commandManager =
                    new LegacyPaperCommandManager<>(this, ExecutionCoordinator.asyncCoordinator(),
                            SenderMapper.create(
                                    SpigotCommandSender::new,
                                    mapped -> ((SpigotCommandSender) mapped).commandSender
                            ));
            new SanctionCommandManager(commandManager, manager, messageConfig, contextTemplate).buildCommands();

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