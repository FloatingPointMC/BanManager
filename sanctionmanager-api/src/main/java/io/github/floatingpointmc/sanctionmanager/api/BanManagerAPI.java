package io.github.floatingpointmc.sanctionmanager.api;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public final class BanManagerAPI {
    private static final AtomicReference<BanManager> INSTANCE = new AtomicReference<>();

    private BanManagerAPI() {
    }

    public static void register(@NotNull BanManager manager) {
        INSTANCE.set(manager);
    }

    public static @NotNull BanManager getAPI() {
        BanManager instance = INSTANCE.get();
        if (instance == null) {
            throw new IllegalStateException("BanManager has not been registered");
        }
        return instance;
    }
}