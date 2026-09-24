package io.github.floatingpointmc.sanctionmanager.bungee.command;

import io.github.floatingpointmc.sanctionmanager.minecraft.SanctionPlayer;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
public class BungeeSanctionPlayer implements SanctionPlayer {
    public final ProxiedPlayer player;

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
        return player.isConnected();
    }

    @Override
    public void kick(@NotNull String reason) {
        player.disconnect(new TextComponent(reason));
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(new TextComponent(message));
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }
}