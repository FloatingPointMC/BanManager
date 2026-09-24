package io.github.floatingpointmc.sanctionmanager.spigot.command;

import io.github.floatingpointmc.sanctionmanager.minecraft.SanctionPlayer;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
public class SpigotSanctionPlayer implements SanctionPlayer {
    public final Player player;

    @Override
    public @NotNull UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public @NotNull String getName() {
        return player.getName();
    }

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public void kick(@NotNull String reason) {
        player.kickPlayer(reason);
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}