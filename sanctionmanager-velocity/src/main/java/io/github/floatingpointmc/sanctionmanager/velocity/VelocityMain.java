package io.github.floatingpointmc.sanctionmanager.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.floatingpointmc.sanctionmanager.api.SanctionManagerAPI;
import io.github.floatingpointmc.sanctionmanager.core.SanctionManagerCore;
import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommand;
import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommandSender;
import io.github.floatingpointmc.sanctionmanager.core.config.DatabaseConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.velocity.command.VelocityCommandSender;
import io.github.floatingpointmc.sanctionmanager.velocity.config.Config;
import io.github.floatingpointmc.sanctionmanager.velocity.listener.PlayerListener;
import org.bstats.velocity.Metrics;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;

public class VelocityMain {
    private static final int PLUGIN_ID = 34184;
    private final ProxyServer proxy;
    private final Logger logger;
    private final Path dataDirectory;
    private final PluginContainer pluginContainer;
    private final Metrics.Factory metricsFactory;
    private SanctionManagerCore core;

    @Inject
    public VelocityMain(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory, PluginContainer pluginContainer, Metrics.Factory metricsFactory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.pluginContainer = pluginContainer;
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        Config config = new Config(dataDirectory, logger);
        config.saveDefaultConfig();
        config.saveDefaultMessages();
        metricsFactory.make(this, PLUGIN_ID);

        String mode = config.getString("mode");
        MessageConfig messageConfig = loadMessageConfig(config);
        MessageContext contextTemplate = MessageContext.builder()
                .pluginName(pluginContainer.getDescription().getName().orElse("SanctionManager"))
                .pluginVersion(pluginContainer.getDescription().getVersion().orElse("unknown"))
                .build();

        if ("standalone".equals(mode)) {
            DatabaseConfig databaseConfig = loadDatabaseConfig(config);
            core = new SanctionManagerCore(databaseConfig);

            new SanctionCommand(new VelocityCommandManager<>(
                    pluginContainer,
                    proxy,
                    ExecutionCoordinator.asyncCoordinator(),
                    new SenderMapper<>() {
                        @Override
                        public @NonNull SanctionCommandSender map(@NonNull CommandSource base) {
                            return new VelocityCommandSender(base);
                        }

                        @Override
                        public @NonNull CommandSource reverse(@NonNull SanctionCommandSender mapped) {
                            return ((VelocityCommandSender) mapped).commandSource;
                        }
                    }
            ), messageConfig, contextTemplate).buildCommands();

            proxy.getEventManager().register(this, new PlayerListener(
                    SanctionManagerAPI.getAPI().getPunishManager(), messageConfig, contextTemplate));

            logger.info("SanctionManager is running in standalone mode.");
        } else {
            logger.warn("SanctionManager is running in proxy mode, no commands available.");
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (core != null) {
            core.shutdown();
            core = null;
        }
    }

    private DatabaseConfig loadDatabaseConfig(Config config) {
        return DatabaseConfig.builder()
                .driver(config.getString("database.driver", "com.mysql.cj.jdbc.Driver"))
                .host(config.getString("database.host", "localhost"))
                .port(config.getInt("database.port", 3306))
                .database(config.getString("database.database", "sanctionmanager"))
                .user(config.getString("database.user", "root"))
                .password(config.getString("database.password", ""))
                .build();
    }

    private MessageConfig loadMessageConfig(Config config) {
        return MessageConfig.builder()
                .description(new ArrayList<>(config.getStringList("description")))
                .banPermanent(new ArrayList<>(config.getStringList("ban.permanent")))
                .banTemporary(new ArrayList<>(config.getStringList("ban.temporary")))
                .mutePermanent(new ArrayList<>(config.getStringList("mute.permanent")))
                .muteTemporary(new ArrayList<>(config.getStringList("mute.temporary")))
                .build();
    }
}