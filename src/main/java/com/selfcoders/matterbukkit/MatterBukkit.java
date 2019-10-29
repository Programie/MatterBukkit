package com.selfcoders.matterbukkit;

import com.selfcoders.matterbukkit.matterbridgeapi.API;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class MatterBukkit extends JavaPlugin {
    private APIMessageReader apiMessageReader = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ConfigurationSection bridgeConfig = getConfig().getConfigurationSection("bridge");

        String url = bridgeConfig.getString("url");
        String gateway = bridgeConfig.getString("gateway");
        String token = bridgeConfig.getString("token");

        String systemUsername = getConfig().getString("outgoing.system-username");

        API matterBridgeApi = new API(url, gateway, systemUsername, token);

        getServer().getPluginManager().registerEvents(new EventListener(this, matterBridgeApi), this);

        if (getConfig().getBoolean("incoming.enable")) {
            apiMessageReader = new APIMessageReader(matterBridgeApi, this);
            apiMessageReader.start();

            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
                if (!apiMessageReader.isRunning()) {
                    getLogger().info("Restarting API listener");

                    apiMessageReader.restart();
                }
            }, 100, 100);
        }

        getLogger().info("MatterBukkit enabled");
    }

    @Override
    public void onDisable() {
        if (apiMessageReader != null) {
            apiMessageReader.stop();
        }

        getLogger().info("MatterBukkit disabled");
    }
}
