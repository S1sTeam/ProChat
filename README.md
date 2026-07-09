# ProChat

![ProChat](https://i.postimg.cc/TwvMRHjT/Max-a-sdelaj-iz-etogo-post-(1).png)

Un plugin de chat pour Paper 1.21+. Remplace le chat vanilla ennuyeux par des fonctionnalités que les joueurs veulent vraiment.

Canaux, noms animés, @mentions, particules, anti-spam, badges — tout dans un seul jar.

## Fonctionnalités

**Noms d'affichage animés**  
Les noms défilent à travers des couleurs configurables. Vitesse et palette réglables. Fonctionne via le placeholder `{display_name_anim}` dans le format du chat.

**3 canaux intégrés**  
- local — joueurs à proximité uniquement (portée configurable)  
- global — tout le serveur  
- admin — admins uniquement  

Activation/désactivation, renommage, couleur, portée et permissions — tout dans la config.

**Mentions**  
Tapez `@nom` et le joueur mentionné reçoit :
- message en surbrillance (visible uniquement par lui)
- notification dans l'actionbar
- son configurable
- particules autour de lui
- titre optionnel (désactivé par défaut)
- `@nom` cliquable → `/msg nom` s'insère

**Badges**  
Icônes devant le nom selon les permissions. `prochat.badge.vip` → affiche `[VIP]`. N'importe quel nom et format.

**Son personnel pour les messages**  
`/prochat sound ENTITY_EXPERIENCE_ORB_PICKUP` — à chaque message reçu, ce son est joué. Sauvegardé dans sounds.yml, persiste après redémarrage.

**Particules lors de l'envoi**  
Envoyer un message → des particules apparaissent autour de vous. Type, nombre, rayon — dans la config.

**Anti-Spam**  
Cooldown entre les messages, détection de majuscules (seuil configurable), filtre de mots interdits, prévention des répétitions.

**PlaceholderAPI**  
Détecte automatiquement `%player_prefix%` et `%player_suffix%` si installé.

**Intégration VoiceChat**  
Synchronisation automatique du mute/ban/freeze/jail avec Simple Voice Chat. Quand un joueur est puni, il est aussi rendu muet dans le chat vocal.

**Commandes de modération**  
Raccourcis intégrés : `/prochat mute <joueur>`, `/prochat ban <joueur>`, `/prochat freeze <joueur>`, `/prochat jail <joueur>`. Délègue automatiquement à EssentialsX, CMI, LiteBAN ou l'API Bukkit.

## Installation

1. Télécharger le jar
2. Mettre dans `plugins/`
3. Redémarrer le serveur
4. Modifier `plugins/ProChat/config.yml`

Aucune dépendance. PlaceholderAPI est optionnel (active les préfixes/suffixes).

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

Placeholders du format du chat :  
`{badges}` — badges du joueur  
`{channel_prefix}` — préfixe du canal ex. `[G]`  
`{prefix}` / `{suffix}` — de PlaceholderAPI  
`{display_name}` — nom d'affichage normal  
`{display_name_anim}` — nom avec gradient animé  
`{player}` — nom du joueur  
`{world}` — monde  
`{message}` — message

### Gradient animé

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

`speed` — ticks entre les changements de couleur. 3 est fluide, 1 est rapide.  
`cycle_colors` — liste ordonnée de couleurs. De deux à dix couleurs.  
Utiliser `<anim_gradient>NomJoueur</anim_gradient>` dans le format.  
Dans les messages : `<gradient:#ff0000:#00ff00>votre texte</gradient>`.

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

Chaque section peut être activée/désactivée indépendamment. Titre désactivé par défaut.  
`personal_highlight` — quand vous êtes mentionné, vous voyez tout le message en surbrillance. Les autres voient le chat normal.

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

Permissions : `prochat.badge.admin`, `prochat.badge.vip`, etc.  
`default` — affiché quand aucun badge n'est accordé. Peut être supprimé.

### Particules de chat

```yaml
chat_particles:
  enabled: true
  type: "HEART"
  count: 8
  speed: 0.05
  spread: 0.6
  permission: "prochat.particles"
```

Type — n'importe quel nom de Particle Minecraft : HEART, NOTE, FLAME, VILLAGER_HAPPY, COMPOSTER, LAVA...

### Son personnel

```yaml
personal_sound:
  enabled: true
  default: "block.note_block.pling"
  permission: "prochat.sound"
```

Les joueurs changent via `/prochat sound <nom>`. TabCompleter montre les options populaires.

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

`threshold` — si plus de ce pourcentage de caractères sont en majuscules, le message est bloqué.  
`max_similarity` — pourcentage de similarité avec le message précédent.  
`words` — liste de mots interdits (vide par défaut, à remplir manuellement).

### VoiceChat

```yaml
voicechat:
  enabled: true
  sync_mute: true
  badge: "&b[VC]&r "
```

Synchronisation automatique mute/unmute avec Simple Voice Chat. Badge affiché dans le chat vocal.

## Commandes

```
/prochat reload             — recharger la config
/prochat channel <nom>      — changer de canal
/prochat sound <son>        — définir son personnel
/prochat mute <joueur>      — rendre muet
/prochat unmute <joueur>    — réactiver le son
/prochat ban <joueur>       — bannir
/prochat unban <joueur>     — débannir
/prochat freeze <joueur>    — figer
/prochat unfreeze <joueur>  — défiger
/prochat jail <joueur>      — emprisonner
/prochat unjail <joueur>    — libérer
/msg <joueur> <texte>       — message privé
/r <message>                — répondre au dernier MP
/socialspy                  — espionner les MP
/ignore <joueur>            — ignorer un joueur
/chatlog <joueur>           — voir l'historique du joueur
/chatlog clear              — effacer l'historique
/muteall [temps] [raison]   — rendre tout le chat muet
/clear (/cc)                — effacer le chat
```

La complétion par tabulation fonctionne pour toutes les commandes.

## Permissions

```
prochat.*                        — tout
prochat.reload                   — recharger la config
prochat.channel.local            — canal local
prochat.channel.global           — canal global
prochat.channel.admin            — canal admin
prochat.mention                  — recevoir les @mentions
prochat.color                    — codes couleur
prochat.rgb                      — couleurs hex
prochat.gradient                 — dégradés
prochat.bypass.antispam          — contourner l'anti-spam
prochat.sound                    — son personnel
prochat.particles                — particules de chat
prochat.badge.<name>             — badge spécifique
prochat.mod.mute                 — commandes mute/unmute
prochat.mod.ban                  — commandes ban/unban
prochat.mod.freeze               — commandes freeze/unfreeze
prochat.mod.jail                 — commandes jail/unjail
```

Les permissions par défaut donnent ce dont les joueurs normaux ont besoin. Admin, RGB, dégradés, anti-spam — opérateur uniquement.

## Localisation

Langues : français, anglais, russe, allemand.  
Définissez `locale` dans config.yml : `fr`, `en`, `ru`, `de`.  
Fichiers de langue dans `plugins/ProChat/lang/`.

## Compilation

```bash
mvn clean package
```

Nécessite Paper API 1.21.3. Pour les serveurs plus anciens, ajustez la version dans pom.xml.  
Ne compile pas sous 1.21 — l'API Adventure n'est pas disponible au niveau de la plateforme.

## Pourquoi pas [autre plugin]

J'ai essayé environ cinq plugins de chat. Chacun manquait de quelque chose : pas de dégradés, mentions maladroites, anti-spam cassé. Ce plugin, je l'ai écrit pour moi — pour qu'il fonctionne et ne m'agace pas. S'il vous plaît aussi, utilisez-le.

## Licence

MIT. Faites ce que vous voulez — fork, rétro-ingénierie, intégration. Ne dites juste pas que vous l'avez écrit.

---

Branches : [EN](https://github.com/S1sTeam/ProChat/tree/EN) · [RU](https://github.com/S1sTeam/ProChat/tree/RU) · [DE](https://github.com/S1sTeam/ProChat/tree/DE) · [FR](/)
