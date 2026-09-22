package io.github.floatingpointmc.sanctionmanager.api;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public final class SanctionManagerAPI {
    private static final AtomicReference<SanctionManager> INSTANCE = new AtomicReference<>();

    private SanctionManagerAPI() {
    }

    public static void register(@NotNull SanctionManager manager) {
        INSTANCE.set(manager);
    }

    public static @NotNull SanctionManager getAPI() {
        SanctionManager instance = INSTANCE.get();
        if (instance == null) {
            throw new IllegalStateException("SanctionManager has not been registered");
        }
        return instance;
    }
}