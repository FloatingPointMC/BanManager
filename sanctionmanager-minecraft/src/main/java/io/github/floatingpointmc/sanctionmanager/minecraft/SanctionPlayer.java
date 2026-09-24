package io.github.floatingpointmc.sanctionmanager.minecraft;

import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface SanctionPlayer extends SanctionCommandSender {
    @NotNull
    UUID getUniqueId();

    @NotNull
    String getName();

    boolean isOnline();

    void kick(@NotNull String reason);

    default void kick() {
        kick("You have been kicked from the server.");
    }
}