package io.github.floatingpointmc.sanctionmanage.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.floatingpointmc.sanctionmanage.velocity.command.VelocityCommandSender;
import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommand;
import io.github.floatingpointmc.sanctionmanager.core.command.SanctionCommandSender;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.VelocityCommandManager;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

public class VelocityMain {
    private final ProxyServer proxy;
    private final Path dataDirectory;

    @Inject
    public VelocityMain(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.dataDirectory = dataDirectory;
        // if mode is standalone
        new SanctionCommand(new VelocityCommandManager<>(proxy.getPluginManager().getPlugin("sanctionmanager").orElseThrow(), proxy, ExecutionCoordinator.asyncCoordinator(), new SenderMapper<CommandSource, SanctionCommandSender>() {
            @Override
            public @NonNull SanctionCommandSender map(@NonNull CommandSource base) {
                return new VelocityCommandSender(base);
            }

            @Override
            public @NonNull CommandSource reverse(@NonNull SanctionCommandSender mapped) {
                return ((VelocityCommandSender) mapped).commandSource;
            }
        }), null, null);
    }
}
