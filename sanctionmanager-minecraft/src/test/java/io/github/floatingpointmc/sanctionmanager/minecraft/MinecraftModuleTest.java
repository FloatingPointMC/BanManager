package io.github.floatingpointmc.sanctionmanager.minecraft;

import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.minecraft.config.MessageFormatter;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MinecraftModuleTest {

    @Test
    void testMessageFormatterReplaceVariables() {
        MessageContext.Punishment context = MessageContext.Punishment.builder()
                .id(42)
                .target(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .targetName("TestPlayer")
                .executor(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                .operatorName("Admin")
                .executingTime(LocalDateTime.now())
                .reason("Hacking")
                .pluginName("SanctionManager")
                .pluginVersion("1.0")
                .build();

        String result = MessageFormatter.replaceVariables(
                "Player %name% banned for %reason% by %operator%", context);
        assertEquals("Player TestPlayer banned for Hacking by Admin", result);
    }

    @Test
    void testMessageFormatterFormatLines() {
        MessageContext context = MessageContext.builder()
                .pluginName("SanctionManager")
                .pluginVersion("2.0")
                .build();

        java.util.List<String> lines = Arrays.asList("Plugin: %plugin%", "Version: %version%");
        java.util.List<String> result = MessageFormatter.formatLines(lines, context);

        assertEquals(2, result.size());
        assertEquals("Plugin: SanctionManager", result.get(0));
        assertEquals("Version: 2.0", result.get(1));
    }

    @Test
    void testMessageFormatterIsTemporary() {
        MessageContext.Punishment permanent = MessageContext.Punishment.builder()
                .id(1)
                .target(UUID.randomUUID())
                .executor(UUID.randomUUID())
                .executingTime(LocalDateTime.now())
                .build();
        assertFalse(MessageFormatter.isTemporary(permanent));

        MessageContext.Punishment temporary = MessageContext.Punishment.builder()
                .id(2)
                .target(UUID.randomUUID())
                .executor(UUID.randomUUID())
                .executingTime(LocalDateTime.now())
                .expiryTime(LocalDateTime.now().plusDays(7))
                .build();
        assertTrue(MessageFormatter.isTemporary(temporary));
    }

    @Test
    void testMessageConfigDefaults() {
        MessageConfig defaults = MessageConfig.defaults();
        assertNotNull(defaults.getDescription());
        assertNotNull(defaults.getBanPermanent());
        assertTrue(defaults.getDescription().isEmpty());
    }

    @Test
    void testMessageFormatterFormatDuration() {
        assertEquals("permanent", MessageFormatter.formatDuration(null));
        assertEquals("1d", MessageFormatter.formatDuration(java.time.Duration.ofDays(1)));
        assertEquals("1h", MessageFormatter.formatDuration(java.time.Duration.ofHours(1)));
        assertEquals("30s", MessageFormatter.formatDuration(java.time.Duration.ofSeconds(30)));
    }
}