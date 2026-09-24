package io.github.floatingpointmc.sanctionmanager.minecraft;

import io.github.floatingpointmc.sanctionmanager.minecraft.command.SanctionCommandSender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SanctionCommandArgument<T> {
    private final @NotNull String literal; // TODO: Read messages.yml -> arguments-{LITERAL}
    private final @NotNull ParserDescriptor<SanctionCommandSender, T> parser;
    private @Nullable SuggestionProvider<SanctionCommandSender> suggestionProvider;
    private boolean optional;

    public static <T> @NotNull SanctionCommandArgument<T> build(@NotNull String literal, @NotNull ParserDescriptor<SanctionCommandSender, T> parser) {
        return new SanctionCommandArgument<>(literal, parser);
    }

    public static <T> @NotNull SanctionCommandArgument<T> build(@NotNull String literal, Class<T> typeClass, @NotNull ArgumentParser<SanctionCommandSender, T> parser) {
        return new SanctionCommandArgument<>(literal, ParserDescriptor.of(parser, typeClass));
    }

    public SanctionCommandArgument<T> suggestionProvider(SuggestionProvider<SanctionCommandSender> suggestionProvider) {
        this.suggestionProvider = suggestionProvider;
        return this;
    }

    public SanctionCommandArgument<T> optional() {
        this.optional = true;
        return this;
    }
}
