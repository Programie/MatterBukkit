# MatterBukkit

A Minecraft Bukkit plugin which sends chat messages from/to [MatterBridge](https://github.com/42wim/matterbridge).

[![pipeline status](https://gitlab.com/Programie/MatterBukkit/badges/master/pipeline.svg)](https://gitlab.com/Programie/MatterBukkit/commits/master)
[![download from GitLab](https://img.shields.io/badge/download-Releases-blue?logo=gitlab)](https://gitlab.com/Programie/MatterBukkit/-/releases)
[![download from Modrinth](https://img.shields.io/badge/download-Modrinth-blue?logo=modrinth)](https://modrinth.com/mod/matterbukkit)
[![download from CurseForge](https://img.shields.io/badge/download-CurseForge-blue?logo=curseforge)](https://www.curseforge.com/minecraft/bukkit-plugins/matterbukkit)
[![view on Website](https://img.shields.io/badge/view-Website-blue)](https://selfcoders.com/projects/matterbukkit)

## Setup

First, install the plugin onto your Bukkit/Spigot/Paper server, and start it to generate [`config.yml`](https://gitlab.com/Programie/MatterBukkit/-/blob/master/src/main/resources/config.yml). Then, configure it:

- Set [`url`](https://gitlab.com/Programie/MatterBukkit/-/blob/master/src/main/resources/config.yml#L3) to the URL where you're running MatterBridge, with port 4242 or whatever you'd like to use (if you're running it on the same server, you can use `http://localhost:4242`).
- Set [`gateway`](https://gitlab.com/Programie/MatterBukkit/-/blob/master/src/main/resources/config.yml#L6) to the name of your MatterBridge gateway.
- Set a [`token`](https://gitlab.com/Programie/MatterBukkit/-/blob/master/src/main/resources/config.yml#L9) to secure the API. This is optional, but heavily recommended.
- Configure all other settings to your liking.

Add an API protocol and gateway to `matterbridge.toml`. Here is an example:

```toml
[api]
[api.minecraft]
Token="pasteTokenHere"
# Set BindAddress to "0.0.0.0:port" if your Minecraft server is running on a different server, and you're not using a reverse proxy
BindAddress="127.0.0.1:4242"
Buffer=1000
RemoteNickFormat="[{PROTOCOL}] {NICK}"

[[gateway.inout]]
account="api.minecraft"
channel="api"
```

Add any other protocols and gateways you would like to use, following [MatterBridge's documentation](https://github.com/42wim/matterbridge/wiki/How-to-create-your-config).

If necessary, open your desired port in your firewall, then start MatterBridge and restart your Minecraft server.

## Build

You can build the project in the following 2 steps:

 * Check out the repository
 * Build the jar file using maven: *mvn clean package*

**Note:** JDK 1.8 and Maven is required to build the project!