package io.github.floatingpointmc.sanctionmanager.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommandSender;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public class VelocityCommandSender implements SanctionCommandSender {
    public final CommandSource commandSource;

    @Override
    public void sendMessage(String message) {
        commandSource.sendMessage(Component.text(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return commandSource.hasPermission(permission);
    }
}