package io.github.floatingpointmc.sanctionmanager.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StorageConfig {
    private boolean databaseEnabled;
    private boolean redisEnabled;
    private @NotNull DatabaseConfig databaseConfig;
    private @NotNull RedisConfig redisConfig;
    private @NotNull String binaryDataDir;

    public static @NotNull StorageConfig defaults() {
        return StorageConfig.builder()
                .databaseEnabled(true)
                .redisEnabled(false)
                .databaseConfig(DatabaseConfig.defaults())
                .redisConfig(RedisConfig.defaults())
                .binaryDataDir("data")
                .build();
    }
}