package io.github.floatingpointmc.sanctionmanager.minecraft.command.impl.admin;

import io.github.floatingpointmc.sanctionmanager.minecraft.MinecraftSanctionManager;
import io.github.floatingpointmc.sanctionmanager.minecraft.SanctionCommandArgument;
import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommandSender;
import io.github.floatingpointmc.sanctionmanager.minecraft.command.impl.AdminCommand;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public class BanCommand extends AdminCommand {
    @Override
    public void execute(@NotNull CommandContext<SanctionCommandSender> context) {

    }

    @Override
    public @NotNull String getName() {
        return "ban";
    }

    @Override
    public @Nullable Collection<SanctionCommandArgument<?>> getArguments() {
        return Arrays.asList(
                SanctionCommandArgument.build("player", StringParser.stringParser()).suggestionProvider(SuggestionProvider.suggestingStrings(MinecraftSanctionManager.provider.getPlayerNames())),
                SanctionCommandArgument.build("duration", StringParser.stringParser()).optional(),
                SanctionCommandArgument.build("reason", StringParser.stringParser()).optional()
        );
    }

    @Override
    public @NotNull String getPermission() {
        return "sanctionmanager.ban";
    }
}
