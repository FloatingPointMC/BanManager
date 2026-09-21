package io.github.floatingpointmc.sanctionmanager.core.command;

import lombok.RequiredArgsConstructor;
import org.incendo.cloud.CommandManager;

@RequiredArgsConstructor
public class SanctionCommand {
    private final CommandManager<SanctionCommandSender> commandManager;

    public void buildCommands() {
        commandManager.command(
                commandManager.commandBuilder("sanction")
                        .handler(context -> {})
        );
    }
}
