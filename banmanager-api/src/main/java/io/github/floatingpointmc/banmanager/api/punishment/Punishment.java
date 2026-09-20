package io.github.floatingpointmc.banmanager.api.punishment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Punishment {
    int getID();

    @NotNull UUID getTarget();

    @Nullable UUID getExecutor();

    @NotNull LocalDateTime getExecutingTime();

    @Nullable LocalDateTime getExpiryTime();

    boolean isOverridden();

    @Nullable Punishment getOverriddenBy();

    boolean isOverriding();

    @Nullable Punishment getOverridden();

    boolean isWithdrawn();

    @Nullable Punishment getWithdrawnBy();

    @NotNull Type getType();
}
