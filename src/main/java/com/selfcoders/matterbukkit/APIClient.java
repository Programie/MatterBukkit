package com.selfcoders.matterbukkit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class APIClient {
    private final String gateway;
    private final String systemUsername;
    private final String systemAvatarUrl;
    private final MatterBukkit plugin;
    private final FileConfiguration config;
    private final Logger logger;
    private final Gson gson;
    private final WebSocketClient webSocketClient;
    private boolean allowReconnect = true;

    public APIClient(String url, String gateway, String systemUsername, String systemAvatarUrl, String token, MatterBukkit plugin) throws URISyntaxException {
        Map<String, String> headers = new HashMap<>();

        if (token != null) {
            headers.put("Authorization", "Bearer " + token);
        }

        this.gateway = gateway;
        this.systemUsername = systemUsername;
        this.systemAvatarUrl = systemAvatarUrl;
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.logger = plugin.getLogger();
        this.gson = new Gson();

        webSocketClient = new WebSocketClient(getWebsocketURI(url), headers) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                logger.info("Connected to MatterBridge Websocket: " + getURI());
            }

            @Override
            public void onMessage(String message) {
                if (!config.getBoolean("incoming.enable")) {
                    return;
                }

                APIClient.this.onMessage(gson.fromJson(message, Message.class));
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                logger.info("Disconnected from MatterBridge Websocket: " + getURI() + "; Code: " + code + " " + reason);

                if (allowReconnect) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);

                            if (allowReconnect) {
                                reconnect();
                            }
                        } catch (InterruptedException ignored) {
                        }
                    }).start();
                }
            }

            @Override
            public void onError(Exception exception) {
                logger.severe("Exception occurred in Websocket connection: " + exception);
            }
        };
    }

    public void connect() {
        allowReconnect = true;
        webSocketClient.connect();
    }

    public void close() {
        allowReconnect = false;
        webSocketClient.close();
    }

    public void onMessage(Message message) {
        // Chat messages have an empty event property, everything else should be ignored (e.g. "api_connected")
        if (!message.getEvent().isEmpty()) {
            return;
        }

        // Skip messages with an empty text property (e.g. in case a picture or file has been sent)
        if (message.getText().isEmpty()) {
            return;
        }

        String messageString = config.getString("incoming.format");
        if (messageString == null) {
            messageString = message.getText();
        } else {
            messageString = messageString.replaceAll("%username%", message.getUsername())
                    .replaceAll("%text%", message.getText());
        }

        logger.info("Incoming message: " + messageString);

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(messageString);
        }
    }

    public void sendMessage(String username, String text, String avatar) {
        Gson gson = new Gson();

        JsonObject messageJson = new JsonObject();
        messageJson.addProperty("gateway", gateway);
        messageJson.addProperty("username", username);
        messageJson.addProperty("text", text);
        messageJson.addProperty("avatar", avatar);

        webSocketClient.send(gson.toJson(messageJson));
    }

    public void sendMessage(String text) {
        sendMessage(systemUsername, text, systemAvatarUrl);
    }

    private static URI getWebsocketURI(String url) throws URISyntaxException {
        URI uri = new URI(url);

        String scheme = uri.getScheme();

        switch (scheme) {
            case "http":
                scheme = "ws";
                break;
            case "https":
                scheme = "wss";
                break;
            case "ws":
            case "wss":
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI scheme: " + scheme);
        }

        return new URI(
                scheme,
                uri.getUserInfo(),
                uri.getHost(),
                uri.getPort(),
                uri.getPath() + "/api/websocket",
                uri.getQuery(),
                uri.getFragment()
        );
    }
}
