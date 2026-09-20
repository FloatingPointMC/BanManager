package io.github.floatingpointmc.banmanager.core.cache;

import io.github.floatingpointmc.banmanager.api.punishment.Punishment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface PunishmentCache {
    @Nullable Punishment findById(int id);

    @NotNull Collection<Punishment> findActiveByTarget(@NotNull UUID target);

    void put(@NotNull Punishment punishment);

    void invalidate(int id);

    void invalidateByTarget(@NotNull UUID target);
}