package io.github.floatingpointmc.sanctionmanage.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.nio.file.Path;

public class VelocityMain {
    private final ProxyServer proxy;
    private final Path dataDirectory;

    @Inject
    public VelocityMain(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.dataDirectory = dataDirectory;
    }
}
