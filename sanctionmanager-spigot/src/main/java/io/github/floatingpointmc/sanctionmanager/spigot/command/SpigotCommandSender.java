package io.github.floatingpointmc.sanctionmanager.spigot.command;

import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommandSender;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class SpigotCommandSender implements SanctionCommandSender {
    public final CommandSender commandSender;

    @Override
    public void sendMessage(@NotNull String message) {
        commandSender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return commandSender.hasPermission(permission);
    }
}
