package io.github.floatingpointmc.sanctionmanager.api;

import io.github.floatingpointmc.sanctionmanager.api.events.PunishmentRemoveEvent;
import io.github.floatingpointmc.sanctionmanager.api.events.SanctionEventBus;
import io.github.floatingpointmc.sanctionmanager.api.events.SanctionEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SanctionEventBusTest {

    @AfterEach
    void tearDown() {
        SanctionEventBus.setHandler(event -> {});
    }

    @Test
    void testDefaultHandlerDoesNothing() {
        SanctionEventBus.setHandler(event -> {});
        PunishmentRemoveEvent event = new PunishmentRemoveEvent(1);
        assertDoesNotThrow(() -> SanctionEventBus.call(event));
    }

    @Test
    void testCustomHandlerReceivesEvent() {
        AtomicReference<SanctionEvent> received = new AtomicReference<>();
        SanctionEventBus.setHandler(received::set);

        PunishmentRemoveEvent event = new PunishmentRemoveEvent(42);
        SanctionEventBus.call(event);

        assertNotNull(received.get());
        assertInstanceOf(PunishmentRemoveEvent.class, received.get());
        assertEquals(42, ((PunishmentRemoveEvent) received.get()).id);
    }

    @Test
    void testEventCancellation() {
        SanctionEventBus.setHandler(event -> event.canceled = true);

        PunishmentRemoveEvent event = new PunishmentRemoveEvent(99);
        SanctionEventBus.call(event);

        assertTrue(event.canceled);
    }

    @Test
    void testHandlerSwap() {
        AtomicBoolean first = new AtomicBoolean(false);
        AtomicBoolean second = new AtomicBoolean(false);

        SanctionEventBus.setHandler(event -> first.set(true));
        SanctionEventBus.call(new PunishmentRemoveEvent(1));
        assertTrue(first.get());

        SanctionEventBus.setHandler(event -> second.set(true));
        SanctionEventBus.call(new PunishmentRemoveEvent(2));
        assertTrue(second.get());
    }
}