package io.github.floatingpointmc.sanctionmanager.minecraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.UUID;

public interface MinecraftProvider {
    @Unmodifiable
    @NotNull
    Collection<String> getPlayerNames();

    @Unmodifiable
    @NotNull
    Collection<UUID> getPlayerUUIDs();

    @Nullable SanctionPlayer getPlayer(@NotNull UUID uuid);

    @Nullable SanctionPlayer getPlayer(@NotNull String name);
}