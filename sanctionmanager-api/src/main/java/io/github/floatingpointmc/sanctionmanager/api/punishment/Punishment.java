package io.github.floatingpointmc.sanctionmanager.api.punishment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Punishment {
    int getId();

    @NotNull UUID getTarget();

    @Nullable UUID getExecutor();

    @NotNull String getOperatorName();

    @NotNull LocalDateTime getExecutingTime();

    @Nullable LocalDateTime getExpiryTime();

    boolean isOverridden();

    @Nullable Punishment getOverriddenBy();

    boolean isOverriding();

    @Nullable Punishment getOverriddenPunishment();

    boolean isWithdrawn();

    @Nullable UUID getWithdrawnBy();

    @Nullable String getReason();

    @NotNull Type getType();
}