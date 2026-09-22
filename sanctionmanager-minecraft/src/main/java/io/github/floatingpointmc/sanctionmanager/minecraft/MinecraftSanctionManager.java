package io.github.floatingpointmc.sanctionmanager.minecraft;

import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import io.github.floatingpointmc.sanctionmanager.core.SanctionManagerCore;
import io.github.floatingpointmc.sanctionmanager.core.config.DatabaseConfig;
import org.jetbrains.annotations.NotNull;

public class MinecraftSanctionManager {
    private final @NotNull SanctionManagerCore core;

    public MinecraftSanctionManager(@NotNull String driver, @NotNull String host, int port,
                                    @NotNull String database, @NotNull String user, @NotNull String password) {
        DatabaseConfig config = DatabaseConfig.builder()
                .driver(driver)
                .host(host)
                .port(port)
                .database(database)
                .user(user)
                .password(password)
                .build();
        this.core = new SanctionManagerCore(config);
    }

    public @NotNull PunishmentManagerAPI getPunishmentManager() {
        return core.getPunishManager();
    }

    public void shutdown() {
        core.shutdown();
    }
}