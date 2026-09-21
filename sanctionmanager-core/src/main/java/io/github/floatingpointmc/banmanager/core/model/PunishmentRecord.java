package io.github.floatingpointmc.banmanager.core.model;

import io.github.floatingpointmc.banmanager.api.punishment.Punishment;
import io.github.floatingpointmc.banmanager.api.punishment.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PunishmentRecord implements Punishment {
    private int id;
    private @NotNull UUID target;
    private @Nullable UUID executor;
    private @NotNull LocalDateTime executingTime;
    private @Nullable LocalDateTime expiryTime;
    private boolean overridden;
    private @Nullable Punishment overriddenBy;
    private boolean overriding;
    private @Nullable Punishment overriddenPunishment;
    private boolean withdrawn;
    private @Nullable UUID withdrawnBy;
    private @NotNull Type type;
}