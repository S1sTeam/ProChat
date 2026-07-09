# ProChat

![ProChat](https://i.postimg.cc/TwvMRHjT/Max-a-sdelaj-iz-etogo-post-(1).png)

A chat plugin for Paper 1.21+. Replaces the boring vanilla chat with features players actually want.

Channels, animated display names, @mentions, particles, anti-spam, badges — all in one jar.

## Features

**Animated Display Names**  
Display names cycle through configurable colors. Adjustable speed and palette. Works via `{display_name_anim}` placeholder in chat format.

**3 Built-in Channels**  
- `local` — nearby players only (configurable range)  
- `global` — entire server  
- `admin` — admins only  

Toggle, rename, recolor, set range and permissions — all in config.

**Mentions**  
Type `@name` and the mentioned player gets:
- highlighted chat message (only they see it)
- actionbar notification
- configurable sound
- particles around them
- title (optional, disabled by default)
- clickable `@name` → auto-fills `/msg name`

**Badges**  
Icons before the name based on permissions. Grant `prochat.badge.vip` → shows `[VIP]`. Any name and format.

**Personal Message Sound**  
`/prochat sound ENTITY_EXPERIENCE_ORB_PICKUP` — every incoming message plays your chosen sound. Saved in sounds.yml, persists through restarts.

**Chat Particles**  
Send a message → particles spawn around you. Type, count, radius — in config.

**Anti-Spam**  
Cooldown between messages, caps detector (configurable threshold), swear filter with word list, repeat prevention.

**PlaceholderAPI**  
Automatically picks up `%player_prefix%` and `%player_suffix%` if installed.

**VoiceChat Integration**  
Automatically syncs mute/ban/freeze/jail with Simple Voice Chat. When a player is punished, they get muted in voice chat too.

**Moderation Commands**  
Built-in shortcuts: `/prochat mute <player>`, `/prochat ban <player>`, `/prochat freeze <player>`, `/prochat jail <player>`. Delegates to EssentialsX, CMI, LiteBAN, or Bukkit API automatically.

## Installation

1. Download the jar
2. Drop into `plugins/`
3. Restart the server
4. Edit `plugins/ProChat/config.yml`

No dependencies. PlaceholderAPI is optional (enables prefix/suffix support).

## Config

```yaml
format:
  chat: "{badges}{channel_prefix}{prefix}{display_name_anim}{suffix}&7: {message}"
  join: "&8[&a+&8] &7{player}"
  quit: "&8[&c-&8] &7{player}"

channels:
  local:
    enabled: true
    display: "L"
    color: "&7"
    range: 100
    permission: "prochat.channel.local"
    priority: 1
  global:
    enabled: true
    display: "G"
    color: "&a"
    range: -1
    permission: "prochat.channel.global"
    priority: 2
  admin:
    enabled: true
    display: "A"
    color: "&c"
    range: -1
    permission: "prochat.channel.admin"
    priority: 3
```

Chat format placeholders:  
`{badges}` — player badges  
`{channel_prefix}` — channel prefix e.g. `[G]`  
`{prefix}` / `{suffix}` — from PlaceholderAPI  
`{display_name}` — normal display name  
`{display_name_anim}` — animated gradient display name  
`{player}` — player name  
`{world}` — world  
`{message}` — message

### Animated Gradient

```yaml
animated_gradient:
  enabled: true
  speed: 3
  cycle_colors:
    - "#ff4444"
    - "#ff8800"
    - "#ffdd00"
    - "#44ff44"
    - "#00ddff"
    - "#8844ff"
    - "#ff44ff"
```

`speed` — ticks between gradient shifts. 3 is smooth, 1 is fast flicker.  
`cycle_colors` — ordered color list. Two to ten colors.  
Use `<anim_gradient>PlayerName</anim_gradient>` in format.  
Inline in messages: `<gradient:#ff0000:#00ff00>your text</gradient>`.

### Mentions

```yaml
mention:
  enabled: true
  format: "&b&l@{player}&r"
  permission: "prochat.mention"
  clickable: true
  sound:
    enabled: true
    type: "entity.experience_orb.pickup"
    volume: 0.5
    pitch: 1.5
  actionbar:
    enabled: true
    message: "&b&l⚡ {player} &bmentioned you!"
  title:
    enabled: false
    title: "&b&lMENTION!"
    subtitle: "&7by {player}"
    fade_in: 10
    stay: 40
    fade_out: 10
  particles:
    enabled: true
    type: "HAPPY_VILLAGER"
    count: 15
    speed: 0.2
  personal_highlight:
    enabled: true
    color: "&e&l"
```

Each section can be toggled independently. Title is off by default.  
`personal_highlight` — when you're mentioned, you see the entire message highlighted. Others see normal chat.

### Badges

```yaml
badges:
  enabled: true
  list:
    admin: "&4[ADMIN]&r "
    vip: "&6[VIP]&r "
    donator: "&a[★]&r "
    default: "&7[✦]&r "
```

Permissions: `prochat.badge.admin`, `prochat.badge.vip`, etc.  
`default` — shown when no badge is granted. Can be removed.

### Chat Particles

```yaml
chat_particles:
  enabled: true
  type: "HEART"
  count: 8
  speed: 0.05
  spread: 0.6
  permission: "prochat.particles"
```

Type — any Minecraft Particle name: HEART, NOTE, FLAME, VILLAGER_HAPPY, COMPOSTER, LAVA...

### Personal Sound

```yaml
personal_sound:
  enabled: true
  default: "block.note_block.pling"
  permission: "prochat.sound"
```

Players change via `/prochat sound <name>`. TabCompleter shows popular options.

### Anti-Spam

```yaml
anti_spam:
  enabled: true
  cooldown:
    enabled: true
    seconds: 2
  caps:
    enabled: true
    min_length: 5
    threshold: 75
  swear:
    enabled: true
    words: []
    replace_char: "*"
  repeat:
    enabled: true
    max_similarity: 85
```

`threshold` — if more than this percentage of characters are uppercase, message is blocked.  
`max_similarity` — similarity percentage to previous message.  
`words` — swear word list (empty by default, fill manually).

### VoiceChat

```yaml
voicechat:
  enabled: true
  sync_mute: true
  badge: "&b[VC]&r "
```

Automatic mute/unmute sync with Simple Voice Chat. Badge shows when in voice chat.

## Commands

```
/prochat reload             — reload config
/prochat channel <name>     — switch channel
/prochat sound <sound>      — set personal message sound
/prochat mute <player>      — mute player
/prochat unmute <player>    — unmute player
/prochat ban <player>       — ban player
/prochat unban <player>     — unban player
/prochat freeze <player>    — freeze player
/prochat unfreeze <player>  — unfreeze player
/prochat jail <player>      — jail player
/prochat unjail <player>    — unjail player
/msg <player> <text>        — private message
/r <message>                — reply to last PM
/socialspy                  — spy on private messages
/ignore <player>            — ignore a player
/chatlog <player>           — view player chat log
/chatlog clear              — clear chat log
/muteall [time] [reason]    — mute entire chat
/clear (/cc)                — clear chat
```

Tab completion works for all commands.

## Permissions

```
prochat.*                        — everything
prochat.reload                   — reload config
prochat.channel.local            — local channel
prochat.channel.global           — global channel
prochat.channel.admin            — admin channel
prochat.mention                  — receive @mentions
prochat.color                    — color codes
prochat.rgb                      — hex colors
prochat.gradient                 — gradients
prochat.bypass.antispam          — bypass anti-spam
prochat.sound                    — personal message sound
prochat.particles                — chat particles
prochat.badge.<name>             — specific badge
prochat.mod.mute                 — mute/unmute commands
prochat.mod.ban                  — ban/unban commands
prochat.mod.freeze               — freeze/unfreeze commands
prochat.mod.jail                 — jail/unjail commands
```

Default permissions grant what regular players need. Admin, RGB, gradients, anti-spam bypass — operator only.

## Localization

Languages: English, Russian, German, French.  
Set `locale` in config.yml: `en`, `ru`, `de`, `fr`.  
Language files are in `plugins/ProChat/lang/`.

## Build

```bash
mvn clean package
```

Requires Paper API 1.21.3. For older servers, adjust version in pom.xml.  
Does not build below 1.21 — Adventure API is not available at platform level.

## Why not [other plugin]

I tried about five chat plugins. Each was missing something: no gradients, awkward mentions, broken anti-spam. This plugin I wrote for myself — to work properly and not annoy. If you like it too, use it.

## License

MIT. Do whatever you want — fork, reverse engineer, embed. Just don't claim you wrote it.

---

Branches: [EN](/) · [RU](https://github.com/S1sTeam/ProChat/tree/RU) · [DE](https://github.com/S1sTeam/ProChat/tree/DE) · [FR](https://github.com/S1sTeam/ProChat/tree/FR)
