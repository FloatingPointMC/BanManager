package io.github.floatingpointmc.banmanager.spigot.bridge;

import io.github.floatingpointmc.banmanager.api.BanManager;
import io.github.floatingpointmc.banmanager.api.management.PunishmentManagerAPI;
import org.jetbrains.annotations.NotNull;

public class BanManagerBridge implements BanManager {
    @Override
    public @NotNull PunishmentManagerAPI getPunishManager() {
        throw new UnsupportedOperationException("Operation is not supported under bridge mode.");
    }
}