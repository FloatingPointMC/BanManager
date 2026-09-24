package io.github.floatingpointmc.sanctionmanager.minecraft.command;

import io.github.floatingpointmc.sanctionmanager.minecraft.SanctionCommandArgument;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface SanctionCommand {
    void execute(@NotNull CommandContext<SanctionCommandSender> context);

    @NotNull String getName();

    @Nullable Collection<SanctionCommandArgument<?>> getArguments();

    @NotNull Permission getRequiredPermission();
}
