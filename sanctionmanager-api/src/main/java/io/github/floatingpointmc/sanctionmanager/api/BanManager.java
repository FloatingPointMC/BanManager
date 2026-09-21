package io.github.floatingpointmc.sanctionmanager.api;

import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import org.jetbrains.annotations.NotNull;

public interface BanManager {
    @NotNull PunishmentManagerAPI getPunishManager();
}
