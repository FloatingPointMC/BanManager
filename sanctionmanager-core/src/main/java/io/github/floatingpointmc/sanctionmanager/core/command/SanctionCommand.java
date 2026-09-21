package io.github.floatingpointmc.sanctionmanager.core.command;

import io.github.floatingpointmc.sanctionmanager.core.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageFormatter;
import lombok.RequiredArgsConstructor;
import org.incendo.cloud.CommandManager;

@RequiredArgsConstructor
public class SanctionCommand {
    private final CommandManager<SanctionCommandSender> commandManager;
    private final MessageConfig messageConfig;
    private final MessageContext messageContext;

    public void buildCommands() {
        commandManager.command(
                commandManager.commandBuilder("sanction")
                        .handler(context -> {
                            for (String line : MessageFormatter.formatLines(messageConfig.getDescription(), messageContext)) {
                                context.sender().sendMessage(line);
                            }
                        })
        );
    }
}