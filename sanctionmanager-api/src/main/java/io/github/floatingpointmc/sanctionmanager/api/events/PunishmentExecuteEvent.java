package io.github.floatingpointmc.sanctionmanager.api.events;

import io.github.floatingpointmc.sanctionmanager.api.punishment.Punishment;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class PunishmentExecuteEvent extends SanctionEvent {
    public @NotNull Punishment punishment;
}