package io.github.floatingpointmc.sanctionmanager.spigot.command;

import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommandSender;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
public class SpigotCommandSender implements SanctionCommandSender {
    public final CommandSender commandSender;

    @Override
    public void sendMessage(String message) {
        commandSender.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }
}