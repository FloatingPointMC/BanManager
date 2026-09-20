package io.github.floatingpointmc.banmanager.api.events;

import io.github.vlouboos.standaloneevent.api.Event;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PunishmentRemoveEvent extends Event {
    public int id;
}