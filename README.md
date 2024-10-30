# ImageOnMap

This repository is a fork of [a fork](https://github.com/TehBrian/ImageOnMap) of [a fork](https://github.com/okocraft/ImageOnMap) of [ImageOnMap](https://github.com/zDevelopers/ImageOnMap).

The rationale for this fork (alongside most of the others, I assume) is the amount of server errors 
popping up left and right due to changes in the MC codebase over the years. In one specific server I contribute in, which also has the excellent [ImageFrame](https://github.com/LOOHP/ImageFrame), converting the mods to IF has unfortunately resulted in some issues — leaving it stuck in 1.20.1 — and I was voluntarily commissioned to make this fork work for 1.21. *Et voilà...*

Contributions are welcome! In theory this works, but still needs some more testing.

Below are the README from all the other forks this derived frm.

---

I created this fork because, frankly, I'm fed up with the amount of errors in my server log
when using this plugin, but I still love this plugin and I'd like to keep it alive.

As I sift through all the plugins' code and ensure that it's up-to-date (i.e., replacing ChatColor with Component)
and visibly error free, I'll move it over to my own package namespace to keep track.

I hope this goes without saying, but this repository is totally experimental and no stability is ensured. Still, though,
if you need help, feel free to ask on my [Discord server](https://thbn.me/discord).

Below is the old README. I'll revamp this whole README later if I actually manage to make this plugin any better.

---

## This repository was hard-forked by OKOCRAFT

Repo for ImageOnMap, a paper plugin.

This repository was hard-forked by OKOCRAFT to fix bugs and support the latest version.

A lot of modifications have been made to the internal code. Make sure to back up your server before use. 

If you are find any bugs, please feel free to report bugs to [issues](https://github.com/okocraft/ImageOnMap/issues).

### Limitations

- Only works on [Paper server](https://github.com/PaperMC/Paper) or its forked server.
- Java 17 or later required.
- Migration feature from older than version v3 has been removed.

The plugin Jar is available on [releases](https://github.com/okocraft/ImageOnMap/releases).

---

### Features

ImageOnMap allows you to load a picture from the Internet to a Minecraft map.

- Loads an image from a URL onto a map. PNG, JPEG and static GIF are supported.
- These images will be saved on your server and reloaded at restart.
- Big pictures will be cut automatically into several parts, to be rendered over multiple maps so they can cover whole
  walls! As example a 1024x1024 picture will be cut in 16 maps.
- Your image will be centered.
- You can put your map in an item frame, or in multiple ones at once—ImageOnMap handles the placement for you!

This plugin is a free software licenced under the [CeCILL-B licence](https://cecill.info/licences/Licence_CeCILL-B_V1-en.html)
(BSD-style in French law).


## Quick guide

- Ensure that you have a free slot in your inventory, as ImageOnMap will give you a map.
- Type `/tomap URL`, where URL is a link to the picture you want to render (see the section below).
- Enjoy your picture! You can place it in an item frame to make a nice poster if you want.


## Commands and Permissions

### `/tomap <url>`

Renders an image and gives a map to the player with it.

- This command can only be used by a player.
- The link must be complete, do not forget that the chat limit is 240 characters.
- You can use an URL shortener like tinyURL or bitly.
- If you want a picture in one map, type resize after the link.
- Permission: `imageonmap.new` (or `imageonmap.userender`—legacy, but will be kept in the plugin).


### `/maps [PlayerName]`

Opens a GUI to see, retrieve and manage the user's maps, can be used to see other user's maps.

- This command can only be used by a player.
- Opens a GUI listing all the maps in a paginated view.
- A book is displayed too to see some usage statistics (maps created, quotas).
- An user can retrieve a map by left-clicking it, or manage it by right-clicking.
- Maps can be renamed (for organization), deleted (but they won't render in game anymore!), or partially retrieved (for posters maps containing more than one map).
- Permissions: `imageonmap.explore`,`imageonmap.list`, plus `imageonmap.get`, `imageonmap.rename` and `imageonmap.delete` for actions into the GUI, for moderation usage`imageonmap.exploreother`, `imageonmap.listother`,`imageonmap.getother`,`imageonmap.deleteother`.

### `/givemap PlayerName [PlayerFrom]:<MapName>`

Give to a player a map from a player map store (if not specified will take from the player map store).
- Can be used by a command block and by server console (but you need to specify PlayerFrom)
Examples:
    - `/givemap Vlammar "A very cool map name"` Will give the map named "A very cool map name" to the player Vlammar
    - `/givemap Vlammar AmauryPi:"A very cool map name"` Same as above but will use AmauryPi's map store instead of the one of the player that runs the command
- Permission: `imageonmap.give`

### `/maptool update [PlayerName]:<MapName> <new url> [stretched|covered] `

Update a specified map (the field PlayerName is optional, by default it will look in the command sender store) 
- Can be used by a command block and by server console, if so the field Playername becomes mandatory
Examples:
    - `/maptool update "A very cool map name" https://www.numerama.com/wp-content/uploads/2020/09/never-gonna-give-you-up-clip-1024x581.jpg ` Will update the map named "A very cool map name" 
    - `/maptool update AmauryPi:"A very cool map name" https://www.numerama.com/wp-content/uploads/2020/09/never-gonna-give-you-up-clip-1024x581.jpg covered` Will update AmauryPi's map and set it to covered
- Permissions: `imageonmap.update`, `imageonmap.updateother`
### `/maptool <new|list|get|delete|explore|update|give|rename|migrate>`

Main command to manage the maps. The less used in everyday usage, too.

- The commands names are pretty obvious.
- `/maptool new` is an alias of `/tomap`.
- `/maptool explore` is an alias of `/maps`.
- `/maptool give` is an alias of `/givemap`.
- `/maptool update` allow to update a specific map.
- `/maptool migrate` migrates the old maps when you upgrade from IoM <= 2.7 to IoM 3.0. You HAVE TO execute this command to retrieve all maps when you do such a migration.
- the followings commands come with an extra permission `imageonmap.CMDNAMEother`:
  - `/maptool list|get|delete|explore|update`
- Permissions:
  - `imageonmap.new` for `/maptool new`;
  - `imageonmap.list` for both `/maptool list` and `/maptool explore`;
  - `imageonmap.get` for `/maptool get`;
  - `imageonmap.delete` for `/maptool delete`;
  - `imageonmap.administrative` for `/maptool migrate`.
  - `imageonmap.explore` for `/maptool explore`;
  - `imageonmap.update` for `/maptool update`;
  - `imageonmap.give` for `/maptool give`.
  

### About the permissions

All permissions are by default granted to everyone, with the exception of `imageonmap.administrative`, `imageonmap.give` and the ones that used the suffix `other` . We believe that in most cases, servers administrators want to give the availability to create images on maps to every player.  
Negate a permission using a plugin manager to remove it, if you want to restrict this possibility to a set of users.

You can grant `imageonmap.*` to users, as this permission is a shortcut for all _user_ permissions (excluding `imageonmap.administrative` , `imageonmap.give` and every permission with the prefix `other` that are intended for moderation usage).


## Configuration

```yaml
# Plugin language. Empty: system language.
# Available: en-US (default, fallback), fr-FR, ru-RU, de-DE, zh-CN, ja-JP.
lang:


# Allows collection of anonymous statistics on plugin environment and usage
# The statistics are publicly visible here: http://mcstats.org/plugin/ImageOnMap
collect-data: true


# Images rendered on maps consume Minecraft maps ID, and there are only 32 767 of them.
# You can limit the maximum number of maps a player, or the whole server, can use with ImageOnMap.
# 0 means unlimited.
map-global-limit: 0
map-player-limit: 0


# Maximum size in pixels for an image to be. 0 is unlimited.
limit-map-size-x: 0
limit-map-size-y: 0


# Should the full image be saved when a map is rendered?
save-full-image: false
```

## Changelog

### 3.0 — The From-Scratch Update

The 3.0 release is a complete rewrite of the original ImageOnMap plugin, now based on QuartzLib, which adds many feature and fixes many bugs.

This new version is not compatible with the older ones, so your older maps will not be loaded. Run the `/maptool migrate` command (as op or in console) in order to get them back in this new version.

You will find amongst the new features:

- New Splatter maps, making it easy to deploy and remove big posters in one click !
- No more item tags when maps are put in item frames !
- Internationalization support (only french and english are supported for now, contributions are welcome)
- Map Quotas (for players and the whole server)
- A new map Manager (based on an inventory interface), to list, rename, get and delete your maps
- Improvements on the commands system (integrated help and autocompletion)
- Asynchronous maps rendering (your server won't freeze anymore when rendering big maps, and you can queue multiple map
  renderings !)
- UUID management (which requires to run `/maptool migrate`)

### 3.1 — The Permissions Update

- Fixed permissions support by adding a full set of permissions for every action of the plugin.

### 4.0 — Subtle Comfort

This version is a bit light in content, but we have unified part of the plugin (splatter map) and we prepared upcoming
changes with required zLib features. The next update should be bigger and will add more stuff : thumbnail, optimization,
possibility to deploy and place item frames in creative mode, creating interactive map that can run a command if you
click on a specific frame…
             
**This version is only compatible with Minecraft 1.15+.** Compatibility for 1.14 and below is dropped for now, but in
the future we will try to bring it back. Use 4.0 pre1 for now, if you require 1.13.2 – 1.14.4 compatibility. As for the
upcoming Minecraft 1.16 version, an update will add compatibility soon after its release.

- **You can now place a map on the ground or on a ceiling.**
- Languages with non-english characters now display correctly (fixed UTF-8 encoding bug).
- Splatter maps no longer throw an exception when placed.
- When a player place a splatter map, other players in the same area see it entirely, including the bottom-left corner.
- Added Russian and German translations (thx to Danechek and squeezer).

### 4.1 — Moderation Update

*4.1 — Moderation Update* gives mods or admins commands to see maps for other players, to give maps, but also to update maps already placed in the world. You can also use ImageOnMap commands from the console or from command blocks, opening a whole new realm of automation around ImageOnMap (read: commands executed from skripts and data-packs should work).

We also fixed some bugs that were reported by lots of people.

**This version is only compatible with Minecraft 1.15+.**

- Added `/maptool update` to change the image attached to a map.
- Added `/givemap` to give a map of a specific player to another.
- All command can now be executed for other players. As example, `/maps username` will allow you to see all `username` maps.
- You can now use ImageOnMap commands from command blocks or from the console.
- Commands now support map name between quotes: "A nice name to have for a map".
- Added `/maptool rename` if you don't want or cannot use the GUI.
- Size limit (in frame).
- Bug fixes & optimizations.
  - Various optimizations.
  - Fixed AWT memory leak—can happen if you do a lot of 50x50 renders.
  - `/maptool list` no longer throw an exception.
  - Fixed an issue where the bottom left map don't render like it should.
  - Fixed the gui rename issue that was the cause of inventory loss.
  
### 4.1.1 — Moderation Update Too

This version fix bugs introduced with 4.1, and others fixed on QuartzLib side,
as this update the QuartzLib version we use to 0.0.3.

- Fixed update message sent even if the plugin is updated.
- Fixed weird update message (raw JSON instead of formatted text).   
- Fixed rare inventory bugs, causing items to be duplicated or barrier blocks to be obtained.
- Fixed possibility to get XP from the glowing effect applied on maps.
- Fixed `/maps` not working on Java 15+ due to JavaScript engine being unavailable.
- Fixed plugin not working on Minehut host due to Javascript engines being restricted.

### 4.1.2 — Moderation Update Three

This version fixes some small console spam bugs and improves the performance of the translations of the plugin.

- Updates to [QuartzLib 0.0.4](https://github.com/zDevelopers/QuartzLib/blob/master/CHANGELOG.md#quartzlib-004),
  where all these improvements were made.

### 5.0.1 — Hard Forked by Okocraft Team

This version removed NMS dependence code and old stuff. And, now only supports paper server.

- Removed QuartzLib dependency and copied it in src directly to maintain easily.
- Removed MANY redundant code and nms code in copied QuartzLib.
- Removed migration feature from v3
- Fixed bug that maps already placed in world do not render image.

## Data collection

We use metrics to collect [basic information about the usage of this plugin](https://bstats.org/plugin/bukkit/ImageOnMap).
This is 100% anonymous (you can check the source code or the network traffic), but can of course be disabled by setting
`collect-data` to false in `config.yml`.
