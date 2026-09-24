package io.github.floatingpointmc.sanctionmanager.minecraft.command;

import io.github.floatingpointmc.sanctionmanager.minecraft.MinecraftSanctionManager;
import io.github.floatingpointmc.sanctionmanager.minecraft.SanctionCommandArgument;
import io.github.floatingpointmc.sanctionmanager.minecraft.command.impl.admin.BanCommand;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageFormatter;
import lombok.RequiredArgsConstructor;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.CommandComponent;

@RequiredArgsConstructor
public class SanctionCommandManager {
    private final CommandManager<SanctionCommandSender> commandManager;
    private final MinecraftSanctionManager sanctionManager;
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

        buildCommand(new BanCommand(sanctionManager, messageConfig, messageContext));
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
                                    if (argument.isOptional()) component.optional();
                                    if (argument.getSuggestionProvider() != null) component.suggestionProvider(argument.getSuggestionProvider());
                                    builder.argument(component);
                                }
                            }
                            return builder;
                        })
                        .permission(command.getRequiredPermission())
                        .handler(command::execute)
        );
    }
}