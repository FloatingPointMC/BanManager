package io.github.floatingpointmc.sanctionmanager.minecraft.command;

import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageFormatter;
import lombok.RequiredArgsConstructor;
import org.incendo.cloud.CommandManager;

@RequiredArgsConstructor
public class SanctionCommandManager {
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

    public void buildServiceCommands() {

    }

    private void buildCommand(SanctionCommand command) {
        commandManager.command(
                commandManager.commandBuilder(command.getName())
                        .apply(builder -> {
                            return builder;
                        })
                        .apply(command::arguments)
                        .handler(command::execute)
        );
    }
}