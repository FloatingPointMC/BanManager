package io.github.floatingpointmc.sanctionmanager.api.events;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PunishmentRemoveEvent extends SanctionEvent {
    public int id;
}