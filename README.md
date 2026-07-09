# ProChat

Ein Chat-Plugin für Paper 1.21+. Ersetzt den langweiligen Standard-Chat mit Funktionen, die Spieler wirklich wollen.

Kanäle, animierte Namen, @Erwähnungen, Partikel, Anti-Spam, Abzeichen — alles in einer Jar.

## Funktionen

**Animierte Anzeigenamen**  
Namen wechseln durch konfigurierbare Farben. Einstellbare Geschwindigkeit und Palette. Funktioniert über den `{display_name_anim}`-Platzhalter im Chat-Format.

**3 eingebaute Kanäle**  
- local — nur nahe Spieler (einstellbare Reichweite)  
- global — gesamter Server  
- admin — nur für Admins  

Ein-/ausschaltbar, umbenennbar, einfärbbar, Reichweite und Berechtigungen — alles in der Config.

**Erwähnungen**  
`@Name` eingeben und der Spieler erhält:
- hervorgehobene Chat-Nachricht (nur für ihn sichtbar)
- Actionbar-Benachrichtigung
- konfigurierbaren Sound
- Partikel um ihn herum
- optionalen Titel (standardmäßig deaktiviert)
- klickbaren `@Name` → `/msg Name` wird eingefügt

**Abzeichen**  
Symbole vor dem Namen basierend auf Berechtigungen. `prochat.badge.vip` → zeigt `[VIP]`. Beliebiges Format und Name.

**Persönlicher Nachrichten-Sound**  
`/prochat sound ENTITY_EXPERIENCE_ORB_PICKUP` — bei jeder eingehenden Nachricht wird dieser Sound abgespielt. Gespeichert in sounds.yml, überlebt Neustarts.

**Chat-Partikel**  
Nachricht senden → Partikel erscheinen um dich herum. Typ, Anzahl, Radius — in der Config.

**Anti-Spam**  
Cooldown zwischen Nachrichten, Großbuchstaben-Erkennung (einstellbarer Schwellwert), Wortfilter, Wiederholungs-Schutz.

**PlaceholderAPI**  
Erkennt automatisch `%player_prefix%` und `%player_suffix%` falls installiert.

**VoiceChat-Integration**  
Automatische Synchronisation von Mute/Ban/Freeze/Jail mit Simple Voice Chat. Wenn ein Spieler bestraft wird, wird er auch im Sprachchat stummgeschaltet.

**Moderations-Befehle**  
Eingebaute Abkürzungen: `/prochat mute <spieler>`, `/prochat ban <spieler>`, `/prochat freeze <spieler>`, `/prochat jail <spieler>`. Delegiert automatisch an EssentialsX, CMI, LiteBAN oder Bukkit API.

## Installation

1. Jar herunterladen
2. In `plugins/` ablegen
3. Server neustarten
4. `plugins/ProChat/config.yml` bearbeiten

Keine Abhängigkeiten. PlaceholderAPI ist optional (ermöglicht Prefix/Suffix).

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

Chat-Format-Platzhalter:  
`{badges}` — Spieler-Abzeichen  
`{channel_prefix}` — Kanal-Präfix z.B. `[G]`  
`{prefix}` / `{suffix}` — von PlaceholderAPI  
`{display_name}` — normaler Anzeigename  
`{display_name_anim}` — animierter Gradienten-Name  
`{player}` — Spielername  
`{world}` — Welt  
`{message}` — Nachricht

### Animierter Gradient

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

`speed` — Ticks zwischen Farbwechseln. 3 ist flüssig, 1 ist schnelles Flackern.  
`cycle_colors` — geordnete Farbliste. Zwei bis zehn Farben.  
`<anim_gradient>SpielerName</anim_gradient>` im Format verwenden.  
Inline in Nachrichten: `<gradient:#ff0000:#00ff00>dein text</gradient>`.

### Erwähnungen

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

Jeder Abschnitt kann einzeln deaktiviert werden. Titel ist standardmäßig aus.  
`personal_highlight` — wenn du erwähnt wirst, siehst du die gesamte Nachricht hervorgehoben. Andere sehen normalen Chat.

### Abzeichen

```yaml
badges:
  enabled: true
  list:
    admin: "&4[ADMIN]&r "
    vip: "&6[VIP]&r "
    donator: "&a[★]&r "
    default: "&7[✦]&r "
```

Berechtigungen: `prochat.badge.admin`, `prochat.badge.vip`, usw.  
`default` — wird angezeigt wenn kein Abzeichen vergeben wurde. Kann entfernt werden.

### Chat-Partikel

```yaml
chat_particles:
  enabled: true
  type: "HEART"
  count: 8
  speed: 0.05
  spread: 0.6
  permission: "prochat.particles"
```

Typ — jeder Minecraft Particle-Name: HEART, NOTE, FLAME, VILLAGER_HAPPY, COMPOSTER, LAVA...

### Persönlicher Sound

```yaml
personal_sound:
  enabled: true
  default: "block.note_block.pling"
  permission: "prochat.sound"
```

Spieler ändern via `/prochat sound <name>`. TabCompleter zeigt beliebte Optionen.

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

`threshold` — wenn mehr als dieser Prozentsatz der Zeichen Großbuchstaben sind, wird die Nachricht blockiert.  
`max_similarity` — Ähnlichkeitsprozentsatz zur vorherigen Nachricht.  
`words` — Wortliste (standardmäßig leer, manuell ausfüllen).

### VoiceChat

```yaml
voicechat:
  enabled: true
  sync_mute: true
  badge: "&b[VC]&r "
```

Automatische Mute/Unmute-Synchronisation mit Simple Voice Chat. Abzeichen wird im Sprachchat angezeigt.

## Befehle

```
/prochat reload             — Config neu laden
/prochat channel <name>     — Kanal wechseln
/prochat sound <sound>      — persönlichen Sound setzen
/prochat mute <spieler>     — Spieler stummschalten
/prochat unmute <spieler>   — Stummschaltung aufheben
/prochat ban <spieler>      — Spieler bannen
/prochat unban <spieler>    — Bann aufheben
/prochat freeze <spieler>   — Spieler einfrieren
/prochat unfreeze <spieler> — Auftauen
/prochat jail <spieler>     — Spieler einsperren
/prochat unjail <spieler>   — Entlassen
/msg <spieler> <text>       — Privatnachricht
```

Tab-Vervollständigung funktioniert für alle Befehle.

## Berechtigungen

```
prochat.*                        — alles
prochat.reload                   — Config neuladen
prochat.channel.local            — lokaler Kanal
prochat.channel.global           — globaler Kanal
prochat.channel.admin            — Admin-Kanal
prochat.mention                  — @Erwähnungen empfangen
prochat.color                    — Farbcodes
prochat.rgb                      — Hex-Farben
prochat.gradient                 — Gradienten
prochat.bypass.antispam          — Anti-Spam umgehen
prochat.sound                    — persönlicher Sound
prochat.particles                — Chat-Partikel
prochat.badge.<name>             — bestimmtes Abzeichen
prochat.mod.mute                 — mute/unmute Befehle
prochat.mod.ban                  — ban/unban Befehle
prochat.mod.freeze               — freeze/unfreeze Befehle
prochat.mod.jail                 — jail/unjail Befehle
```

Standardberechtigungen geben, was normale Spieler brauchen. Admin, RGB, Gradienten, Anti-Spam-Bypass — nur für Operatoren.

## Lokalisierung

Sprachen: Deutsch, Englisch, Russisch, Französisch.  
`locale` in config.yml setzen: `de`, `en`, `ru`, `fr`.  
Sprachdateien in `plugins/ProChat/lang/`.

## Build

```bash
mvn clean package
```

Erfordert Paper API 1.21.3. Für ältere Server Version in pom.xml anpassen.  
Nicht unter 1.21 baubar — Adventure API ist auf Plattformebene nicht verfügbar.

## Warum nicht [anderes Plugin]

Ich habe etwa fünf Chat-Plugins ausprobiert. Bei jedem fehlte etwas: keine Gradienten, umständliche Erwähnungen, kaputter Anti-Spam. Dieses Plugin habe ich für mich selbst geschrieben — damit es funktioniert und nicht nervt. Wenn es dir auch gefällt, nutze es.

## Lizenz

MIT. Mach was du willst — fork, reverse engineering, einbetten. Sag nur nicht, du hättest es geschrieben.

---

Branches: [EN](https://github.com/S1sTeam/ProChat/tree/EN) · [RU](https://github.com/S1sTeam/ProChat/tree/RU) · [DE](/) · [FR](https://github.com/S1sTeam/ProChat/tree/FR)
