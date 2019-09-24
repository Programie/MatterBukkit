package com.selfcoders.matterbukkit;

import com.selfcoders.matterbukkit.matterbridgeapi.API;
import com.selfcoders.matterbukkit.matterbridgeapi.Message;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.stream.Collectors;

class EventListener implements Listener {
    private final MatterBukkit plugin;
    private final API matterBridgeApi;
    private final YamlConfiguration advancements;

    EventListener(MatterBukkit plugin, API matterBridgeApi) {
        this.plugin = plugin;
        this.matterBridgeApi = matterBridgeApi;

        advancements = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("advancements.yml")));
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        try {
            matterBridgeApi.sendMessage(new Message(event.getPlayer().getName(), event.getMessage()));
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("outgoing");

        if (!config.getBoolean("death.enable")) {
            return;
        }

        String text = config.getString("death.format");

        text = text.replaceAll("%username%", event.getEntity().getName())
                .replaceAll("%death-message%", event.getDeathMessage());

        try {
            matterBridgeApi.sendMessage(text);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        if (event.getAdvancement() == null || event.getAdvancement().getKey().getKey().contains("recipe/") || event.getPlayer() == null) {
            return;
        }

        // Taken from DiscordSRV
        try {
            Object craftAdvancement = ((Object) event.getAdvancement()).getClass().getMethod("getHandle").invoke(event.getAdvancement());
            Object advancementDisplay = craftAdvancement.getClass().getMethod("c").invoke(craftAdvancement);
            boolean display = (boolean) advancementDisplay.getClass().getMethod("i").invoke(advancementDisplay);

            if (!display) {
                return;
            }
        } catch (NullPointerException exception) {
            return;
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
            return;
        }

        ConfigurationSection config = plugin.getConfig().getConfigurationSection("outgoing");

        if (!config.getBoolean("advancement.enable")) {
            return;
        }

        String internalAdvancementName = event.getAdvancement().getKey().getKey();

        String advancementName = advancements.getString(internalAdvancementName);

        if (advancementName == null) {
            // Fallback if not defined in advancements.yml: turn "story/shitty_advancement_name" into "Shitty Advancement Name", taken from DiscordSRV
            advancementName = Arrays.stream(internalAdvancementName.substring(internalAdvancementName.lastIndexOf("/") + 1).toLowerCase().split("_"))
                    .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                    .collect(Collectors.joining(" "));
        }

        String text = config.getString("advancement.format");

        text = text.replaceAll("%playername%", event.getPlayer().getName())
                .replaceAll("%advancement%", advancementName);

        try {
            matterBridgeApi.sendMessage(text);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("outgoing");

        if (!config.getBoolean("join.enable")) {
            return;
        }

        try {
            matterBridgeApi.sendMessage(event.getJoinMessage());
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ConfigurationSection config = plugin.getConfig().getConfigurationSection("outgoing");

        if (!config.getBoolean("quit.enable")) {
            return;
        }

        try {
            matterBridgeApi.sendMessage(event.getQuitMessage());
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }
}
