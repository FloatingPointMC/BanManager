package io.github.floatingpointmc.sanctionmanager.minecraft.command.impl;

import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommand;
import org.incendo.cloud.permission.Permission;
import org.jetbrains.annotations.NotNull;

public abstract class AdminCommand implements SanctionCommand {
    public abstract @NotNull String getPermission();

    @Override
    public @NotNull Permission getRequiredPermission() {
        return Permission.of("sanctionmanager.admin").or(Permission.of(getPermission()));
    }
}