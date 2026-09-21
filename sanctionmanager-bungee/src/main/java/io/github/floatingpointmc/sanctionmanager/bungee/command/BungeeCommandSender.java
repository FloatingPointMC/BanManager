package io.github.floatingpointmc.sanctionmanager.bungee.command;

import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommandSender;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.CommandSender;

@AllArgsConstructor
public class BungeeCommandSender implements SanctionCommandSender {
    public final CommandSender commandSender;
}
