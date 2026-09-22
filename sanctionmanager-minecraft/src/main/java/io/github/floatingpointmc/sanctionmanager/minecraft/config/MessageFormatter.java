package io.github.floatingpointmc.sanctionmanager.minecraft.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class MessageFormatter {

    private MessageFormatter() {
    }

    public static boolean isTemporary(@NotNull MessageContext.Punishment context) {
        return context.getExpiryTime() != null;
    }

    public static @NotNull String replaceVariables(@NotNull String line,
                                                   @NotNull MessageContext.Punishment context) {
        String result = line;
        result = result.replace("%id%", String.valueOf(context.getId()));
        result = result.replace("%reason%", context.getReason() != null ? context.getReason() : "");
        result = result.replace("%name%", context.getTargetName() != null ? context.getTargetName() : context.getTarget().toString());
        result = result.replace("%uuid%", context.getTarget().toString());
        result = result.replace("%operator%", context.getOperatorName() != null ? context.getOperatorName() : "[Console]");
        result = result.replace("%plugin%", context.getPluginName());
        result = result.replace("%version%", context.getPluginVersion());
        if (context.getExpiryTime() != null) {
            result = result.replace("%time%", context.getExpiryTime().toString());
            Duration duration = Duration.between(LocalDateTime.now(), context.getExpiryTime());
            result = result.replace("%duration%", formatDuration(duration));
        } else {
            result = result.replace("%time%", "permanent");
            result = result.replace("%duration%", "permanent");
        }
        return result;
    }

    public static @NotNull String replaceVariables(@NotNull String line,
                                                   @NotNull MessageContext context) {
        String result = line;
        result = result.replace("%plugin%", context.getPluginName());
        result = result.replace("%version%", context.getPluginVersion());
        return result;
    }

    public static @NotNull String format(@NotNull List<String> lines,
                                         @NotNull MessageContext.Punishment context) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(replaceVariables(line, context));
        }
        return sb.toString();
    }

    public static @NotNull List<String> formatLines(@NotNull List<String> lines,
                                                    @NotNull MessageContext.Punishment context) {
        List<String> result = new ArrayList<>(lines.size());
        for (String line : lines) {
            result.add(replaceVariables(line, context));
        }
        return result;
    }

    public static @NotNull List<String> formatLines(@NotNull List<String> lines,
                                                    @NotNull MessageContext context) {
        List<String> result = new ArrayList<>(lines.size());
        for (String line : lines) {
            result.add(replaceVariables(line, context));
        }
        return result;
    }

    public static @NotNull String formatDuration(@Nullable Duration duration) {
        if (duration == null) {
            return "permanent";
        }
        long seconds = duration.getSeconds();
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (secs > 0 || sb.length() == 0) sb.append(secs).append("s");
        return sb.toString().trim();
    }
}