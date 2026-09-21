package io.github.floatingpointmc.sanctionmanager.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageConfig {
    private @NotNull List<String> description;
    private @NotNull List<String> banPermanent;
    private @NotNull List<String> banTemporary;
    private @NotNull List<String> mutePermanent;
    private @NotNull List<String> muteTemporary;

    public static @NotNull MessageConfig defaults() {
        return MessageConfig.builder()
                .description(new ArrayList<>())
                .banPermanent(new ArrayList<>())
                .banTemporary(new ArrayList<>())
                .mutePermanent(new ArrayList<>())
                .muteTemporary(new ArrayList<>())
                .build();
    }
}