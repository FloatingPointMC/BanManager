package io.github.floatingpointmc.sanctionmanager.minecraft.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TranslationConfig {
    private final Map<String, Object> data;

    public TranslationConfig() {
        this.data = new HashMap<>();
    }

    public TranslationConfig(@NotNull Map<String, Object> data) {
        this.data = data;
    }

    public @NotNull String get(@NotNull String path) {
        return get(path, path);
    }

    public @NotNull String get(@NotNull String path, @NotNull String defaultValue) {
        Object value = getNested(path);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof List) {
            StringBuilder sb = new StringBuilder();
            for (Object item : (List<?>) value) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(item != null ? String.valueOf(item) : "");
            }
            return sb.toString();
        }
        return String.valueOf(value);
    }

    public @NotNull List<String> getStringList(@NotNull String path) {
        Object value = getNested(path);
        if (value instanceof List) {
            List<String> result = new ArrayList<>();
            for (Object item : (List<?>) value) {
                result.add(item != null ? String.valueOf(item) : "");
            }
            return result;
        }
        if (value != null) {
            return Collections.singletonList(String.valueOf(value));
        }
        return Collections.emptyList();
    }

    public @NotNull String getCommandUsage(@NotNull String commandName) {
        return get("command." + commandName + ".usage");
    }

    public @NotNull String getCommandDescription(@NotNull String commandName) {
        return get("command." + commandName + ".desc");
    }

    public @NotNull String getCommandArgumentDescription(@NotNull String commandName, @NotNull String argumentName) {
        return get("command." + commandName + ".arg." + argumentName);
    }

    @SuppressWarnings("unchecked")
    private @Nullable Object getNested(@NotNull String path) {
        String[] keys = path.split("\\.");
        Object current = data;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(key);
                if (current == null) return null;
            } else {
                return null;
            }
        }
        return current;
    }

    public static @NotNull TranslationConfig defaults() {
        return new TranslationConfig();
    }
}