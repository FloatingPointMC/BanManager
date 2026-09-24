package io.github.floatingpointmc.sanctionmanager.velocity.command;

import com.velocitypowered.api.proxy.Player;
import io.github.floatingpointmc.sanctionmanager.minecraft.SanctionPlayer;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
public class VelocitySanctionPlayer implements SanctionPlayer {
    public final Player player;

    @Override
    public @NotNull UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public @NotNull String getName() {
        return player.getUsername();
    }

    @Override
    public boolean isOnline() {
        return player.isActive();
    }

    @Override
    public void kick(@NotNull String reason) {
        player.disconnect(Component.text(reason));
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(Component.text(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}