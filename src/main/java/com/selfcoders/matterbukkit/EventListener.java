package com.selfcoders.matterbukkit;

import com.selfcoders.matterbukkit.matterbridgeapi.API;
import com.selfcoders.matterbukkit.matterbridgeapi.Message;
import org.bukkit.ChatColor;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.io.IOException;
import java.util.logging.Level;

class EventListener implements Listener {
    private final MatterBukkit plugin;
    private final API matterBridgeApi;
    private final String avatarUrl;

    EventListener(MatterBukkit plugin, API matterBridgeApi, String avatarUrl) {
        this.plugin = plugin;
        this.matterBridgeApi = matterBridgeApi;
        this.avatarUrl = avatarUrl;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("outgoing.chat.enable")) {
            return;
        }

        try {
            Player player = event.getPlayer();
            String playerAvatarUrl = avatarUrl;
            if (playerAvatarUrl != null) {
                playerAvatarUrl = playerAvatarUrl.replaceAll("%playername%", player.getName());
                playerAvatarUrl = playerAvatarUrl.replaceAll("%uuid%", player.getUniqueId().toString());
            }

            matterBridgeApi.sendMessage(new Message(player.getName(), event.getMessage(), playerAvatarUrl));
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        FileConfiguration config = plugin.getConfig();

        if (!config.getBoolean("outgoing.death.enable")) {
            return;
        }

        String text = config.getString("outgoing.death.format");

        Player player = event.getEntity();

        text = text.replaceAll("%playername%", player.getName())
                .replaceAll("%username%", player.getName())
                .replaceAll("%death-message%", event.getDeathMessage());

        try {
            matterBridgeApi.sendMessage(text);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        FileConfiguration config = plugin.getConfig();

        if (!config.getBoolean("outgoing.advancement.enable")) {
            return;
        }

        AdvancementDisplay advancementDisplay = event.getAdvancement().getDisplay();

        if (advancementDisplay == null) {
            return;
        }

        if (!advancementDisplay.shouldAnnounceChat()) {
            return;
        }

        String text = config.getString("outgoing.advancement.format");

        text = text.replaceAll("%playername%", event.getPlayer().getName())
                .replaceAll("%advancement%", advancementDisplay.getTitle());

        try {
            matterBridgeApi.sendMessage(text);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }

    @EventHandler
    public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
        int oldLevel = event.getOldLevel();
        int newLevel = event.getNewLevel();

        if (newLevel < oldLevel) {
            return;
        }

        FileConfiguration config = plugin.getConfig();

        if (!config.getBoolean("outgoing.level-up.enable")) {
            return;
        }

        if (newLevel < config.getInt("outgoing.level-up.minimum-level")) {
            return;
        }

        int modulus = config.getInt("outgoing.level-up.modulus");
        if (modulus > 0 && newLevel % modulus != 0) {
            return;
        }

        String text = config.getString("outgoing.level-up.format");

        text = text.replaceAll("%playername%", event.getPlayer().getName())
                .replaceAll("%old-level%", String.valueOf(oldLevel))
                .replaceAll("%new-level%", String.valueOf(newLevel));

        try {
            matterBridgeApi.sendMessage(text);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("outgoing.join.enable")) {
            return;
        }

        try {
            matterBridgeApi.sendMessage(ChatColor.stripColor(event.getJoinMessage()));
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.getConfig().getBoolean("outgoing.quit.enable")) {
            return;
        }

        try {
            matterBridgeApi.sendMessage(ChatColor.stripColor(event.getQuitMessage()));
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, exception.toString(), exception);
        }
    }
}
