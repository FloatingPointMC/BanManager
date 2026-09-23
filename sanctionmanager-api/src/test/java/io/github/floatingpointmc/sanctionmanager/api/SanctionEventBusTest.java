package io.github.floatingpointmc.sanctionmanager.api;

import io.github.floatingpointmc.sanctionmanager.api.events.PunishmentRemoveEvent;
import io.github.vlouboos.standaloneevent.api.ApiProvider;
import io.github.vlouboos.standaloneevent.api.EventHandler;
import io.github.vlouboos.standaloneevent.api.StandaloneEventAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class SanctionEventBusTest {

    @BeforeEach
    void setUp() {
        ApiProvider.injectApi(false);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCallWithNoListenersDoesNotThrow() {
        PunishmentRemoveEvent event = new PunishmentRemoveEvent(1);
        assertDoesNotThrow(() -> StandaloneEventAPI.getApi().call(event));
    }

    @Test
    void testListenerReceivesEvent() {
        AtomicReference<PunishmentRemoveEvent> received = new AtomicReference<>();
        ReceiveListener listener = new ReceiveListener(received);
        StandaloneEventAPI.getApi().register(listener);

        PunishmentRemoveEvent event = new PunishmentRemoveEvent(42);
        StandaloneEventAPI.getApi().call(event);

        assertNotNull(received.get());
        assertEquals(42, received.get().id);

        StandaloneEventAPI.getApi().unregister(listener);
    }

    @Test
    void testEventCancellation() {
        CancelListener listener = new CancelListener();
        StandaloneEventAPI.getApi().register(listener);

        PunishmentRemoveEvent event = new PunishmentRemoveEvent(99);
        StandaloneEventAPI.getApi().call(event);

        assertTrue(event.canceled);

        StandaloneEventAPI.getApi().unregister(listener);
    }

    @Test
    void testListenerSwap() {
        AtomicBoolean first = new AtomicBoolean(false);
        AtomicBoolean second = new AtomicBoolean(false);

        FlagListener firstListener = new FlagListener(first);
        StandaloneEventAPI.getApi().register(firstListener);
        StandaloneEventAPI.getApi().call(new PunishmentRemoveEvent(1));
        assertTrue(first.get());

        StandaloneEventAPI.getApi().unregister(firstListener);

        FlagListener secondListener = new FlagListener(second);
        StandaloneEventAPI.getApi().register(secondListener);
        StandaloneEventAPI.getApi().call(new PunishmentRemoveEvent(2));
        assertTrue(second.get());

        StandaloneEventAPI.getApi().unregister(secondListener);
    }

    public static class ReceiveListener {
        private final AtomicReference<PunishmentRemoveEvent> ref;

        public ReceiveListener(AtomicReference<PunishmentRemoveEvent> ref) {
            this.ref = ref;
        }

        @EventHandler
        public void onPunishmentRemove(PunishmentRemoveEvent event) {
            ref.set(event);
        }
    }

    public static class CancelListener {
        @EventHandler
        public void onCancel(PunishmentRemoveEvent event) {
            event.canceled = true;
        }
    }

    public static class FlagListener {
        private final AtomicBoolean flag;

        public FlagListener(AtomicBoolean flag) {
            this.flag = flag;
        }

        @EventHandler
        public void onEvent(PunishmentRemoveEvent event) {
            flag.set(true);
        }
    }
}