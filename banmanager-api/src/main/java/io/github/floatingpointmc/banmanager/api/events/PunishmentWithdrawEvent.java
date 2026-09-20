package io.github.floatingpointmc.banmanager.api.events;

import io.github.vlouboos.standaloneevent.api.Event;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@AllArgsConstructor
public class PunishmentWithdrawEvent extends Event {
    public int id;
    public @Nullable UUID withdrawnBy;
}