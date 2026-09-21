package io.github.floatingpointmc.sanctionmanager.spigot.listener;

import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Punishment;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Type;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final PunishmentManagerAPI punishManager;
    private final MessageConfig messageConfig;
    private final MessageContext contextTemplate;

    public PlayerListener(PunishmentManagerAPI punishManager, MessageConfig messageConfig, MessageContext contextTemplate) {
        this.punishManager = punishManager;
        this.messageConfig = messageConfig;
        this.contextTemplate = contextTemplate;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        Collection<Punishment> active = punishManager.queryActivePunishments(uuid);

        for (Punishment p : active) {
            if (p.getType() == Type.BAN) {
                List<String> lines = MessageFormatter.isTemporary(toContext(p, event.getName())) ? messageConfig.getBanTemporary() : messageConfig.getBanPermanent();
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, MessageFormatter.format(lines, toContext(p, event.getName())));
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Collection<Punishment> active = punishManager.queryActivePunishments(uuid);
        for (Punishment p : active) {
            if (p.getType() == Type.MUTE) {
                List<String> lines = MessageFormatter.isTemporary(toContext(p, event.getPlayer().getName())) ? messageConfig.getMuteTemporary() : messageConfig.getMutePermanent();
                for (String line : MessageFormatter.formatLines(lines, toContext(p, event.getPlayer().getName()))) {
                    event.getPlayer().sendMessage(line);
                }
                event.setCancelled(true);
                return;
            }
        }
    }

    private MessageContext.Punishment toContext(Punishment p, String targetName) {
        return MessageContext.Punishment.builder()
                .id(p.getId())
                .target(p.getTarget())
                .targetName(targetName)
                .executor(p.getExecutor() != null ? p.getExecutor() : new UUID(0, 0))
                .operatorName(p.getOperatorName())
                .executingTime(p.getExecutingTime())
                .expiryTime(p.getExpiryTime())
                .reason(p.getReason())
                .pluginName(contextTemplate.getPluginName())
                .pluginVersion(contextTemplate.getPluginVersion())
                .build();
    }
}