package com.selfcoders.matterbukkit;

import com.google.gson.Gson;
import com.selfcoders.matterbukkit.matterbridgeapi.API;
import com.selfcoders.matterbukkit.matterbridgeapi.Message;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

class APIMessageReader {
    private final API api;
    private final MatterBukkit plugin;
    private final FileConfiguration config;
    private final Logger logger;
    private API.Response response = null;
    private Thread thread = null;

    APIMessageReader(API api, MatterBukkit plugin) {
        super();

        this.api = api;
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.logger = plugin.getLogger();
    }

    void start() {
        if (thread != null) {
            stop();
        }

        logger.info("Starting API listener thread");

        try {
            // Ensure old messages are removed
            api.clearMessages();

            response = api.streamMessages();

            InputStream stream = response.getContent();

            if (stream == null) {
                logger.severe("Unable to open API stream");
                return;
            }

            thread = new Thread(null, () -> {
                String line;
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
                    Gson gson = new Gson();

                    try {
                        while (true) {
                            line = bufferedReader.readLine();
                            if (line != null) {
                                Message message = gson.fromJson(line, Message.class);

                                // Chat messages have an empty event property, everything else should be ignored (e.g. "api_connected")
                                if (!message.getEvent().isEmpty()) {
                                    continue;
                                }

                                // Skip messages with an empty text property (e.g. in case a picture or file has been sent)
                                if (message.getText().isEmpty()) {
                                    continue;
                                }

                                String messageString = config.getString("incoming.format");

                                messageString = messageString.replaceAll("%username%", message.getUsername())
                                        .replaceAll("%text%", message.getText());

                                logger.info("Incoming message: " + messageString);

                                for (Player player : plugin.getServer().getOnlinePlayers()) {
                                    player.sendMessage(messageString);
                                }
                            }
                        }
                    } catch (SocketException exception) {
                        logger.info(exception.toString());
                    } catch (IOException exception) {
                        logger.log(Level.SEVERE, exception.toString(), exception);
                    } finally {
                        try {
                            stream.close();
                        } catch (IOException exception) {
                            logger.log(Level.SEVERE, exception.toString(), exception);
                        }
                    }
                } catch (IOException exception) {
                    logger.log(Level.SEVERE, exception.toString(), exception);
                }
            }, "MatterBridge-API-Listener");

            thread.start();
        } catch (Exception exception) {
            logger.log(Level.SEVERE, exception.toString(), exception);
        }
    }

    void stop() {
        if (response != null) {
            response.getHttpUriRequest().abort();
            response = null;
        }

        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        }

        thread = null;
    }

    void restart() {
        stop();
        start();
    }

    boolean isRunning() {
        if (thread == null) {
            return false;
        }

        return thread.isAlive();
    }
}
