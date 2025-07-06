# Changelog

## [1.8] - 2025-07-06

* Use Websocket connection instead of raw HTTP connection for connection to MatterBridge API (fixes #6)

## [1.7] - 2023-05-24

* Fixed check whether advancement should be sent to Matterbridge
* Removed requirement of explicitly providing a list of advancements (advancements.yml)

**Note:** The plugin now requires at least Minecraft 1.19 as the new features depend on a feature only available since 1.19.

## [1.6] - 2022-06-07

Updated list of advancements from https://minecraft.gamepedia.com/Advancement to add those added in Minecraft 1.19 (thanks to @Millesimus)

## [1.5.1] - 2022-05-21

* Removed logging for avatar URL while sending messages
* Updated gson library to version 2.9.0 (fix for CVE-2022-25647)

## [1.5] - 2021-12-04

Updated list of advancements from https://minecraft.gamepedia.com/Advancement to add those added in Minecraft 1.18 (thanks to @Millesimus)

## [1.4] - 2021-06-21

Updated list of advancements from https://minecraft.gamepedia.com/Advancement to add those added in Minecraft 1.17 (thanks to @Millesimus)

## [1.3.1] - 2021-06-04

Upgraded org.apache.httpcomponents:httpclient dependency to fix security vulnerability CVE-2020-13956.

## [1.3] - 2021-04-02

Updated list of advancements from https://minecraft.gamepedia.com/Advancement (thanks to @Millesimus)

## [1.2] - 2021-03-21

* Corrected and added some advancements (thanks to @Millesimus)
* Check if outgoing.chat.enable in config.yml is enabled (thanks to @Millesimus)

## [1.1] - 2021-03-18

Fixed sending "§e" before playername in join/quit messages (fixes #4)

## [1.0] - 2020-09-02

Initial release
