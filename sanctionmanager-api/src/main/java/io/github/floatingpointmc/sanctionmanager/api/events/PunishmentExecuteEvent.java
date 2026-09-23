package io.github.floatingpointmc.sanctionmanager.api.events;

import io.github.vlouboos.standaloneevent.api.Event;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Punishment;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class PunishmentExecuteEvent extends Event {
    public @NotNull Punishment punishment;
}