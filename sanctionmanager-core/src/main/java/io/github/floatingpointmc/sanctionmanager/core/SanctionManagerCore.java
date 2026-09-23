package io.github.floatingpointmc.sanctionmanager.core;

import io.github.floatingpointmc.sanctionmanager.api.SanctionManager;
import io.github.floatingpointmc.sanctionmanager.api.SanctionManagerAPI;
import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import io.github.floatingpointmc.sanctionmanager.core.cache.LocalPunishmentCache;
import io.github.floatingpointmc.sanctionmanager.core.cache.PunishmentCache;
import io.github.floatingpointmc.sanctionmanager.core.config.DatabaseConfig;
import io.github.floatingpointmc.sanctionmanager.core.management.PunishmentManager;
import io.github.floatingpointmc.sanctionmanager.core.repository.HikariPunishmentRepository;
import io.github.floatingpointmc.sanctionmanager.core.repository.PunishmentRepository;
import io.github.floatingpointmc.sanctionmanager.core.service.PunishmentService;
import io.github.vlouboos.standaloneevent.api.ApiProvider;
import org.jetbrains.annotations.NotNull;

public class SanctionManagerCore implements SanctionManager {
    private final @NotNull PunishmentManager punishmentManager;
    private final @NotNull PunishmentRepository repository;

    public SanctionManagerCore(@NotNull DatabaseConfig databaseConfig) {
        this(new LocalPunishmentCache(), new HikariPunishmentRepository(databaseConfig));
    }

    public SanctionManagerCore(@NotNull PunishmentCache cache, @NotNull PunishmentRepository repository) {
        ApiProvider.injectApi(false);
        this.repository = repository;
        PunishmentService service = new PunishmentService(cache, repository);
        this.punishmentManager = new PunishmentManager(service);
        SanctionManagerAPI.register(this);
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