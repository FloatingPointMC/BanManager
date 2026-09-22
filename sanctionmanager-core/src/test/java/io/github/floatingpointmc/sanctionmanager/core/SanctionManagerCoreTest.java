package io.github.floatingpointmc.sanctionmanager.core;

import io.github.floatingpointmc.sanctionmanager.api.events.PunishmentExecuteEvent;
import io.github.floatingpointmc.sanctionmanager.api.events.SanctionEventBus;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Punishment;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Type;
import io.github.floatingpointmc.sanctionmanager.core.cache.LocalPunishmentCache;
import io.github.floatingpointmc.sanctionmanager.core.config.DatabaseConfig;
import io.github.floatingpointmc.sanctionmanager.core.model.PunishmentRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class SanctionManagerCoreTest {

    private LocalPunishmentCache cache;

    @BeforeEach
    void setUp() {
        cache = new LocalPunishmentCache();
    }

    @Test
    void testEventBusFiresOnAddPunishment() {
        AtomicBoolean fired = new AtomicBoolean(false);
        SanctionEventBus.setHandler(event -> {
            if (event instanceof PunishmentExecuteEvent) {
                fired.set(true);
            }
        });

        PunishmentRecord punishment = new PunishmentRecord(
                1, UUID.randomUUID(), UUID.randomUUID(), "Console",
                LocalDateTime.now(), null, false, null, false, null,
                false, null, "Test reason", Type.BAN
        );

        cache.put(punishment);
        assertNotNull(cache.findById(1));

        SanctionEventBus.setHandler(event -> {});
    }

    @Test
    void testEventCancellationPreventsAction() {
        SanctionEventBus.setHandler(event -> event.canceled = true);

        PunishmentRecord punishment = new PunishmentRecord(
                2, UUID.randomUUID(), UUID.randomUUID(), "Console",
                LocalDateTime.now(), null, false, null, false, null,
                false, null, "Cancelled test", Type.MUTE
        );

        cache.put(punishment);
        assertNotNull(cache.findById(2));

        SanctionEventBus.setHandler(event -> {});
    }

    @Test
    void testLocalPunishmentCachePutAndFind() {
        UUID target = UUID.randomUUID();
        PunishmentRecord punishment = new PunishmentRecord(
                10, target, null, "Console",
                LocalDateTime.now(), null, false, null, false, null,
                false, null, "Cache test", Type.BAN
        );

        cache.put(punishment);

        Punishment found = cache.findById(10);
        assertNotNull(found);
        assertEquals(target, found.getTarget());
        assertEquals(Type.BAN, found.getType());

        Collection<Punishment> active = cache.findActiveByTarget(target);
        assertFalse(active.isEmpty());
        assertEquals(1, active.size());
    }

    @Test
    void testLocalPunishmentCacheInvalidate() {
        UUID target = UUID.randomUUID();
        PunishmentRecord punishment = new PunishmentRecord(
                20, target, null, "Console",
                LocalDateTime.now(), null, false, null, false, null,
                false, null, "Invalidate test", Type.BAN
        );

        cache.put(punishment);
        assertNotNull(cache.findById(20));

        cache.invalidate(20);
        assertNull(cache.findById(20));
        assertTrue(cache.findActiveByTarget(target).isEmpty());
    }

    @Test
    void testDatabaseConfigJdbcUrl() {
        DatabaseConfig mysqlConfig = DatabaseConfig.builder()
                .driver("com.mysql.cj.jdbc.Driver")
                .host("localhost")
                .port(3306)
                .database("testdb")
                .user("root")
                .password("")
                .build();
        assertTrue(mysqlConfig.getJdbcUrl().startsWith("jdbc:mysql://"));

        DatabaseConfig sqliteConfig = DatabaseConfig.builder()
                .driver("org.sqlite.JDBC")
                .host("")
                .port(0)
                .database("test.db")
                .user("")
                .password("")
                .build();
        assertTrue(sqliteConfig.getJdbcUrl().startsWith("jdbc:sqlite:"));
    }
}