package io.github.floatingpointmc.sanctionmanager.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import io.github.floatingpointmc.sanctionmanager.api.management.PunishmentManagerAPI;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Punishment;
import io.github.floatingpointmc.sanctionmanager.api.punishment.Type;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageConfig;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageContext;
import io.github.floatingpointmc.sanctionmanager.core.config.MessageFormatter;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerListener {
    private final PunishmentManagerAPI punishManager;
    private final MessageConfig messageConfig;
    private final MessageContext contextTemplate;

    public PlayerListener(PunishmentManagerAPI punishManager, MessageConfig messageConfig, MessageContext contextTemplate) {
        this.punishManager = punishManager;
        this.messageConfig = messageConfig;
        this.contextTemplate = contextTemplate;
    }

    @Subscribe
    public void onPreLogin(PreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if (uuid == null) return;
        Collection<Punishment> active = punishManager.queryActivePunishments(uuid);

        for (Punishment p : active) {
            if (p.getType() == Type.BAN) {
                String targetName = event.getUsername();
                List<String> lines = MessageFormatter.isTemporary(toContext(p, targetName)) ? messageConfig.getBanTemporary() : messageConfig.getBanPermanent();
                event.setResult(PreLoginEvent.PreLoginComponentResult.denied(Component.text(MessageFormatter.format(lines, toContext(p, targetName)))));
                return;
            }
        }
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Collection<Punishment> active = punishManager.queryActivePunishments(uuid);
        for (Punishment p : active) {
            if (p.getType() == Type.MUTE) {
                List<String> lines = MessageFormatter.isTemporary(toContext(p, player.getUsername())) ? messageConfig.getMuteTemporary() : messageConfig.getMutePermanent();
                for (String line : MessageFormatter.formatLines(lines, toContext(p, player.getUsername()))) {
                    player.sendMessage(Component.text(line));
                }
                event.setResult(PlayerChatEvent.ChatResult.denied());
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