package io.github.floatingpointmc.sanctionmanager.minecraft.command;

import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

public interface SanctionCommand {
    @NotNull String getName();

    void execute(@NotNull CommandContext<SanctionCommandSender> context);

    @NotNull Command.Builder<SanctionCommandSender> arguments(Command.Builder<SanctionCommandSender> sanctionCommandSenderBuilder);
}
