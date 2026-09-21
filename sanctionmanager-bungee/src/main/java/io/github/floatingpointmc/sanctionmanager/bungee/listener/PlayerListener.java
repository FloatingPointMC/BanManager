package io.github.floatingpointmc.sanctionmanager.bungee.listener;

import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Punishment;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Type;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageFormatter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

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
    public void onPlayerJoin(LoginEvent event) {
        UUID uuid = event.getConnection().getUniqueId();
        Collection<Punishment> active = punishManager.queryActivePunishments(uuid);

        for (Punishment p : active) {
            if (p.getType() == Type.BAN) {
                List<String> lines = MessageFormatter.isTemporary(toContext(p, event.getConnection().getName())) ? messageConfig.getBanTemporary() : messageConfig.getBanPermanent();
                event.setCancelled(true);
                event.setReason(new TextComponent(MessageFormatter.format(lines, toContext(p, event.getConnection().getName()))));
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(ChatEvent event) {
        if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) event.getSender();
            UUID uuid = player.getUniqueId();
            Collection<Punishment> active = punishManager.queryActivePunishments(uuid);
            for (Punishment p : active) {
                if (p.getType() == Type.MUTE) {
                    List<String> lines = MessageFormatter.isTemporary(toContext(p, player.getName())) ? messageConfig.getMuteTemporary() : messageConfig.getMutePermanent();
                    for (String line : MessageFormatter.formatLines(lines, toContext(p, player.getName()))) {
                        player.sendMessage(new TextComponent(line));
                    }
                    event.setCancelled(true);
                    return;
                }
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