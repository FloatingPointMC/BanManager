package io.github.floatingpointmc.banmanager.api;

import io.github.floatingpointmc.banmanager.api.management.PunishmentManagerAPI;
import org.jetbrains.annotations.NotNull;

public interface BanManager {
    @NotNull PunishmentManagerAPI getPunishManager();
}
