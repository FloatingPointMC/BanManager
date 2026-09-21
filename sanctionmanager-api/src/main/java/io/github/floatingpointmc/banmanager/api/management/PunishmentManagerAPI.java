package io.github.floatingpointmc.banmanager.api.management;

import io.github.floatingpointmc.banmanager.api.punishment.Punishment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface PunishmentManagerAPI {
    @Nullable Punishment queryPunishment(int id);

    @NotNull Collection<Punishment> queryActivePunishments(@NotNull UUID target);

    void addPunishment(@NotNull Punishment punishment);

    void withdrawPunishment(int id, @NotNull UUID withdrawnBy);

    void removePunishment(int id);
}