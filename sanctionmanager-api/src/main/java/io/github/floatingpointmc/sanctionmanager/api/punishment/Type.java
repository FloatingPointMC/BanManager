package io.github.floatingpointmc.sanctionmanager.api.punishment;

public enum Type {
        BAN,
        MUTE;

        public int ordinary() {
            return ordinal();
        }
    }