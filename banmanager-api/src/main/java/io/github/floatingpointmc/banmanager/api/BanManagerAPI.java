package io.github.floatingpointmc.banmanager.api;

import org.jetbrains.annotations.NotNull;

public class BanManagerAPI {
    private static BanManager instance;

    protected static void injectBanManager(@NotNull BanManager manager) {
        instance = manager;
    }

    public static @NotNull BanManager getAPI() {
        return instance;
    }
}
