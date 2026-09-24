package io.github.floatingpointmc.sanctionmanager.minecraft;

import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import io.github.floatingpointmc.sanctionmanager.core.SanctionManagerCore;
import io.github.floatingpointmc.sanctionmanager.core.config.DatabaseConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.RedisConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.StorageConfig;
import org.jetbrains.annotations.NotNull;

public class MinecraftSanctionManager {
    private final @NotNull SanctionManagerCore core;
    public static @NotNull MinecraftProvider provider;

    public MinecraftSanctionManager(@NotNull MinecraftProvider provider, @NotNull StorageConfig storageConfig) {
        this.provider = provider;
        this.core = new SanctionManagerCore(storageConfig);
    }

    public MinecraftSanctionManager(@NotNull MinecraftProvider provider, boolean databaseEnabled, @NotNull String driver, @NotNull String host, int port,
                                    @NotNull String database, @NotNull String user, @NotNull String password,
                                    boolean redisEnabled, @NotNull String redisHost, int redisPort, @NotNull String redisPassword,
                                    @NotNull String binaryDataDir) {
        this.provider = provider;
        DatabaseConfig databaseConfig = DatabaseConfig.builder()
                .driver(driver)
                .host(host)
                .port(port)
                .database(database)
                .user(user)
                .password(password)
                .build();
        RedisConfig redisConfig = RedisConfig.builder()
                .host(redisHost)
                .port(redisPort)
                .password(redisPassword)
                .build();
        StorageConfig storageConfig = StorageConfig.builder()
                .databaseEnabled(databaseEnabled)
                .redisEnabled(redisEnabled)
                .databaseConfig(databaseConfig)
                .redisConfig(redisConfig)
                .binaryDataDir(binaryDataDir)
                .build();
        this.core = new SanctionManagerCore(storageConfig);
    }

    public @NotNull PunishmentManagerAPI getPunishmentManager() {
        return core.getPunishManager();
    }

    public @NotNull SanctionManagerCore getCore() {
        return core;
    }

    public void shutdown() {
        core.shutdown();
    }
}