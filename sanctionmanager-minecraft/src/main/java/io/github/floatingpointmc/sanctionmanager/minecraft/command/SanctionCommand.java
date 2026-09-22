package io.github.floatingpointmc.sanctionmanager.minecraft.command;

import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageFormatter;
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