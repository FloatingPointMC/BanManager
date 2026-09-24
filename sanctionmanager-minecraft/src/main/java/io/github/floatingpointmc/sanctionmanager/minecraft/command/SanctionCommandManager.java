package io.github.floatingpointmc.sanctionmanager.minecraft.command;

import io.github.floatingpointmc.sanctionmanager.minecraft.SanctionCommandArgument;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageFormatter;
import lombok.RequiredArgsConstructor;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.description.CommandDescription;
import org.incendo.cloud.permission.Permission;

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
                            if (command.getArguments() != null) {
                                for (SanctionCommandArgument<?> argument : command.getArguments()) {
                                    CommandComponent.Builder<SanctionCommandSender, ?> component = CommandComponent.builder(argument.getLiteral(), argument.getParser());
                                    // if (argument.getDescription() != null) component.description(argument.getDescription()); TODO: Read messages.yml -> commands-{CMD_NAME}-{ARGUMENT_LITERAL}
                                    if (argument.isOptional()) component.optional();
                                    if (argument.getSuggestionProvider() != null) component.suggestionProvider(argument.getSuggestionProvider());
                                    builder.argument(component);
                                }
                            }
                            return builder;
                        })
                        .permission(command.getRequiredPermission())
                        // .commandDescription() TODO: Read messages.yml -> commands-{CMD_NAME}
                        .handler(command::execute)
        );
    }
}