package io.github.floatingpointmc.sanctionmanager.bungee.command;

import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommandSender;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

@AllArgsConstructor
public class BungeeCommandSender implements SanctionCommandSender {
    public final CommandSender commandSender;

    @Override
    public void sendMessage(String message) {
        commandSender.sendMessage(new TextComponent(message));
    }
}
