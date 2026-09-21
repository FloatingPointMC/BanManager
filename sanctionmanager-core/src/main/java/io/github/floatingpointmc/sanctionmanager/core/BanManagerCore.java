package io.github.floatingpointmc.sanctionmanager.core;

import io.github.floatingpointmc.sanctionmanager.api.BanManager;
import io.github.floatingpointmc.sanctionmanager.api.BanManagerAPI;
import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import io.github.floatingpointmc.sanctionmanager.core.cache.LocalPunishmentCache;
import io.github.floatingpointmc.sanctionmanager.core.cache.PunishmentCache;
import io.github.floatingpointmc.sanctionmanager.core.config.DatabaseConfig;
import io.github.floatingpointmc.sanctionmanager.core.management.PunishmentManager;
import io.github.floatingpointmc.sanctionmanager.core.repository.HikariPunishmentRepository;
import io.github.floatingpointmc.sanctionmanager.core.repository.PunishmentRepository;
import io.github.floatingpointmc.sanctionmanager.core.service.PunishmentService;
import org.jetbrains.annotations.NotNull;

public class BanManagerCore implements BanManager {
    private final @NotNull PunishmentManager punishmentManager;
    private final @NotNull PunishmentRepository repository;

    public BanManagerCore(@NotNull DatabaseConfig databaseConfig) {
        this(new LocalPunishmentCache(), new HikariPunishmentRepository(databaseConfig));
    }

    public BanManagerCore(@NotNull PunishmentCache cache, @NotNull PunishmentRepository repository) {
        this.repository = repository;
        PunishmentService service = new PunishmentService(cache, repository);
        this.punishmentManager = new PunishmentManager(service);
        BanManagerAPI.register(this);
    }

    @Override
    public @NotNull PunishmentManagerAPI getPunishManager() {
        return punishmentManager;
    }

    public void shutdown() {
        if (repository instanceof AutoCloseable) {
            try {
                ((AutoCloseable) repository).close();
            } catch (Exception ignored) {
            }
        }
    }
}