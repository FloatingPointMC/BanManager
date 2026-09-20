package io.github.floatingpointmc.banmanager.api.management;

import io.github.floatingpointmc.banmanager.api.punishment.Punishment;

public interface PunishManager {
    Punishment queryPunishment(int id);
}
