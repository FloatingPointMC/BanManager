package io.github.floatingpointmc.sanctionmanager.core;

import io.github.floatingpointmc.sanctionmanager.api.SanctionManager;
import io.github.floatingpointmc.sanctionmanager.api.SanctionManagerAPI;
import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import io.github.floatingpointmc.sanctionmanager.core.cache.LocalPunishmentCache;
import io.github.floatingpointmc.sanctionmanager.core.cache.PunishmentCache;
import io.github.floatingpointmc.sanctionmanager.core.cache.PunishmentSerializer;
import io.github.floatingpointmc.sanctionmanager.core.cache.RedisPunishmentCache;
import io.github.floatingpointmc.sanctionmanager.core.config.StorageConfig;
import io.github.floatingpointmc.sanctionmanager.core.management.PunishmentManager;
import io.github.floatingpointmc.sanctionmanager.core.repository.BinaryPunishmentRepository;
import io.github.floatingpointmc.sanctionmanager.core.repository.HikariPunishmentRepository;
import io.github.floatingpointmc.sanctionmanager.core.repository.PunishmentRepository;
import io.github.floatingpointmc.sanctionmanager.core.service.PunishmentService;
import io.github.vlouboos.standaloneevent.api.ApiProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.RedisClient;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SanctionManagerCore implements SanctionManager {
    private final @NotNull PunishmentManager punishmentManager;
    private final @NotNull PunishmentRepository repository;
    private final @NotNull PunishmentCache cache;
    private final @Nullable RedisClient redisClient;

    public SanctionManagerCore(@NotNull StorageConfig storageConfig) {
        ApiProvider.injectApi(false);

        if (storageConfig.isDatabaseEnabled()) {
            this.repository = new HikariPunishmentRepository(storageConfig.getDatabaseConfig());
        } else {
            Path dataDir = Paths.get(storageConfig.getBinaryDataDir());
            this.repository = new BinaryPunishmentRepository(dataDir);
        }

        if (storageConfig.isRedisEnabled()) {
            this.redisClient = RedisClient.create(
                    "redis://" +
                    (storageConfig.getRedisConfig().getPassword().isEmpty() ? "" :
                            ":" + storageConfig.getRedisConfig().getPassword() + "@") +
                    storageConfig.getRedisConfig().getHost() + ":" +
                    storageConfig.getRedisConfig().getPort());
            this.cache = new RedisPunishmentCache(this.redisClient, new PunishmentSerializer());
        } else {
            this.redisClient = null;
            this.cache = new LocalPunishmentCache();
        }

        PunishmentService service = new PunishmentService(cache, repository);
        this.punishmentManager = new PunishmentManager(service);
        SanctionManagerAPI.register(this);
    }

    public SanctionManagerCore(@NotNull PunishmentCache cache, @NotNull PunishmentRepository repository) {
        ApiProvider.injectApi(false);
        this.cache = cache;
        this.repository = repository;
        this.redisClient = null;
        PunishmentService service = new PunishmentService(cache, repository);
        this.punishmentManager = new PunishmentManager(service);
        SanctionManagerAPI.register(this);
    }

    @Override
    public @NotNull PunishmentManagerAPI getPunishManager() {
        return punishmentManager;
    }

    public @NotNull PunishmentRepository getRepository() {
        return repository;
    }

    public @NotNull PunishmentCache getCache() {
        return cache;
    }

    public boolean isRedisEnabled() {
        return redisClient != null;
    }

    public boolean isDatabaseEnabled() {
        return repository instanceof HikariPunishmentRepository;
    }

    public void shutdown() {
        if (repository instanceof AutoCloseable) {
            try {
                ((AutoCloseable) repository).close();
            } catch (Exception ignored) {
            }
        }
        if (redisClient != null) {
            redisClient.close();
        }
    }
}