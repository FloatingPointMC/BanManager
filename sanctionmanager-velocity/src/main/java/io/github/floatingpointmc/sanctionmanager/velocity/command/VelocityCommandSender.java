package io.github.floatingpointmc.sanctionmanager.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommandSender;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class VelocityCommandSender implements SanctionCommandSender {
    public final CommandSource commandSource;

    @Override
    public void sendMessage(@NotNull String message) {
        commandSource.sendMessage(Component.text(message));
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return commandSource.hasPermission(permission);
    }
}