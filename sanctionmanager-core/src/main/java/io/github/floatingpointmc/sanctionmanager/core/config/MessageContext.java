package io.github.floatingpointmc.sanctionmanager.core.config;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageContext {
    private @NotNull String pluginName;
    private @NotNull String pluginVersion;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Punishment {
        private int id;
        private @NotNull UUID target;
        private @Nullable String targetName;
        private @NotNull UUID executor;
        private @Nullable String operatorName;
        private @NotNull LocalDateTime executingTime;
        private @Nullable LocalDateTime expiryTime;
        private @Nullable String reason;
        private @NotNull String pluginName;
        private @NotNull String pluginVersion;

        public static PunishmentBuilder builder() {
            return new PunishmentBuilder();
        }

        public static class PunishmentBuilder {
            private int id;
            private @NotNull UUID target;
            private @Nullable String targetName;
            private @NotNull UUID executor;
            private @Nullable String operatorName;
            private @NotNull LocalDateTime executingTime;
            private @Nullable LocalDateTime expiryTime;
            private @Nullable String reason;
            private @NotNull String pluginName = "SanctionManager";
            private @NotNull String pluginVersion = "unknown";

            public PunishmentBuilder id(int id) {
                this.id = id;
                return this;
            }

            public PunishmentBuilder target(@NotNull UUID target) {
                this.target = target;
                return this;
            }

            public PunishmentBuilder targetName(@Nullable String targetName) {
                this.targetName = targetName;
                return this;
            }

            public PunishmentBuilder executor(@NotNull UUID executor) {
                this.executor = executor;
                return this;
            }

            public PunishmentBuilder operatorName(@Nullable String operatorName) {
                this.operatorName = operatorName;
                return this;
            }

            public PunishmentBuilder executingTime(@NotNull LocalDateTime executingTime) {
                this.executingTime = executingTime;
                return this;
            }

            public PunishmentBuilder expiryTime(@Nullable LocalDateTime expiryTime) {
                this.expiryTime = expiryTime;
                return this;
            }

            public PunishmentBuilder reason(@Nullable String reason) {
                this.reason = reason;
                return this;
            }

            public PunishmentBuilder pluginName(@NotNull String pluginName) {
                this.pluginName = pluginName;
                return this;
            }

            public PunishmentBuilder pluginVersion(@NotNull String pluginVersion) {
                this.pluginVersion = pluginVersion;
                return this;
            }

            public Punishment build() {
                return new Punishment(id, target, targetName, executor, operatorName, executingTime, expiryTime, reason, pluginName, pluginVersion);
            }
        }
    }

    public static MessageContextBuilder builder() {
        return new MessageContextBuilder();
    }

    public static class MessageContextBuilder {
        private @NotNull String pluginName = "SanctionManager";
        private @NotNull String pluginVersion = "unknown";

        public MessageContextBuilder pluginName(@NotNull String pluginName) {
            this.pluginName = pluginName;
            return this;
        }

        public MessageContextBuilder pluginVersion(@NotNull String pluginVersion) {
            this.pluginVersion = pluginVersion;
            return this;
        }

        public MessageContext build() {
            return new MessageContext(pluginName, pluginVersion);
        }
    }
}