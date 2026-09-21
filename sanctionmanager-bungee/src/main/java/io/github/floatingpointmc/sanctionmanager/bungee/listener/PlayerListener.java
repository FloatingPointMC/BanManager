package io.github.floatingpointmc.sanctionmanager.bungee.listener;

import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Punishment;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Type;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageConfig;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final PunishmentManagerAPI punishManager;
    private final MessageConfig messageConfig;

    public PlayerListener(PunishmentManagerAPI punishManager, MessageConfig messageConfig) {
        this.punishManager = punishManager;
        this.messageConfig = messageConfig;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(LoginEvent event) {
        UUID uuid = event.getConnection().getUniqueId();
        Collection<Punishment> active = punishManager.queryActivePunishments(uuid);

        for (Punishment p : active) {
            if (p.getType() == Type.BAN) {
                List<String> lines = isTemporary(p) ? messageConfig.getBanTemporary() : messageConfig.getBanPermanent();
                event.setCancelled(true);
                event.setReason(new TextComponent(format(lines, p)));
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            UUID uuid = player.getUniqueId();
            Collection<Punishment> active = punishManager.queryActivePunishments(uuid);
            for (Punishment p : active) {
                if (p.getType() == Type.MUTE) {
                    List<String> lines = isTemporary(p) ? messageConfig.getMuteTemporary() : messageConfig.getMutePermanent();
                    for (String line : lines) {
                        player.sendMessage(new TextComponent(replaceVariables(line, p)));
                    }
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private boolean isTemporary(Punishment p) {
        return p.getExpiryTime() != null;
    }

    private String format(List<String> lines, Punishment p) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(replaceVariables(line, p));
        }
        return sb.toString();
    }

    private String replaceVariables(String line, Punishment p) {
        String result = line;
        result = result.replace("%id%", String.valueOf(p.getId()));
        result = result.replace("%reason%", "");
        if (p.getExpiryTime() != null) {
            result = result.replace("%time%", p.getExpiryTime().toString());
            Duration duration = Duration.between(LocalDateTime.now(), p.getExpiryTime());
            result = result.replace("%duration%", formatDuration(duration));
        } else {
            result = result.replace("%time%", "permanent");
            result = result.replace("%duration%", "permanent");
        }
        return result;
    }

    private String formatDuration(Duration duration) {
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