package io.github.floatingpointmc.sanctionmanager.spigot.bridge;

import io.github.floatingpointmc.sanctionmanager.api.BanManager;
import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import org.jetbrains.annotations.NotNull;

public class BanManagerBridge implements BanManager {
    @Override
    public @NotNull PunishmentManagerAPI getPunishManager() {
        throw new UnsupportedOperationException("Operation is not supported under bridge mode.");
    }
}