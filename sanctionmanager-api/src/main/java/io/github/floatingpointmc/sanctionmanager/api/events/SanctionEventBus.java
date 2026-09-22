package io.github.floatingpointmc.sanctionmanager.api.events;

import org.jetbrains.annotations.NotNull;

public final class SanctionEventBus {
    private static volatile Handler handler = new DefaultHandler();

    private SanctionEventBus() {
    }

    public interface Handler {
        void call(@NotNull SanctionEvent event);
    }

    private static final class DefaultHandler implements Handler {
        @Override
        public void call(@NotNull SanctionEvent event) {
        }
    }

    public static void setHandler(@NotNull Handler handler) {
        SanctionEventBus.handler = handler;
    }

    public static void call(@NotNull SanctionEvent event) {
        handler.call(event);
    }
}