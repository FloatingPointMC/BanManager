package io.github.floatingpointmc.sanctionmanager.minecraft.command;

public interface SanctionCommandSender {
    void sendMessage(String message);

    boolean hasPermission(String permission);
}