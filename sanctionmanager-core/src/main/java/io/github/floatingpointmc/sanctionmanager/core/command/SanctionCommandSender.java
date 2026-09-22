package io.github.floatingpointmc.sanctionmanager.core.command;

import org.jetbrains.annotations.NotNull;

public interface SanctionCommandSender {
    void sendMessage(@NotNull String message);

    boolean hasPermission(@NotNull String permission);
}
