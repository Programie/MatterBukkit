package com.selfcoders.matterbukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URISyntaxException;

public class MatterBukkit extends JavaPlugin {
    private APIClient apiClient = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        FileConfiguration config = getConfig();

        String url = config.getString("bridge.url");
        String gateway = config.getString("bridge.gateway");
        String token = config.getString("bridge.token");

        String avatarUrl = config.getString("outgoing.avatar-url");
        String systemAvatarUrl = config.getString("outgoing.system-avatar-url");
        String systemUsername = config.getString("outgoing.system-username");

        try {
            apiClient = new APIClient(url, gateway, systemUsername, systemAvatarUrl, token, this);
            apiClient.connect();
        } catch (URISyntaxException exception) {
            throw new RuntimeException(exception);
        }

        getServer().getPluginManager().registerEvents(new EventListener(this, apiClient, avatarUrl), this);

        getLogger().info("MatterBukkit enabled");
    }

    @Override
    public void onDisable() {
        if (apiClient != null) {
            apiClient.close();
        }

        getLogger().info("MatterBukkit disabled");
    }
}
