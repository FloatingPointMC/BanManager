package io.github.floatingpointmc.sanctionmanager.api.events;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@AllArgsConstructor
public class PunishmentWithdrawEvent extends SanctionEvent {
    public int id;
    public @Nullable UUID withdrawnBy;
}