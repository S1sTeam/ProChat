# ProChat

![ProChat](https://i.postimg.cc/TwvMRHjT/Max-a-sdelaj-iz-etogo-post-(1).png)

Плагин на чат для Paper 1.21+. Заменяет стандартный скучный чат на то, что люди хотят видеть.

Каналы, анимированные ники, @упоминания, частицы, антиспам, бейджи — всё в одной банке.

## Возможности

**Формат ника с анимацией**  
Ник переливается по цветам из конфига. Можно настроить скорость и палитру. Работает через плэйсхолдер `{display_name_anim}` в формате чата.

**Три канала из коробки**  
- local — только игроки рядом (дальность настраивается)  
- global — весь сервер  
- admin — только для админов  

Включаются/выключаются, меняются имена, цвета, расстояния, права доступа — всё в конфиге.

**Упоминания**  
Пишешь `@ник` — чел получает:
- сообщение в чате с подсветкой (видит только он)
- actionbar с инфой кто тегнул
- звук (настраивается)
- частицы вокруг него
- title если включено в конфиге
- при клике на @ник подставляется `/msg ник`

**Бейджи**  
Иконки перед ником по правам. Повесил `prochat.badge.vip` — попёр `[VIP]`. Можно любое название и формат.

**Персональный звук на сообщения**  
`/prochat sound ENTITY_EXPERIENCE_ORB_PICKUP` — и теперь каждый раз когда приходит сообщение, играет этот звук. Сохраняется в sounds.yml, не слетает после рестарта.

**Частицы при отправке**  
Написал сообщение — вокруг тебя частицы. Тип, количество, радиус — в конфиге.

**Антиспам**  
Кд между сообщениями, капс-детектор (процент заглавных можно настроить), фильтр мата по списку слов, антиповтор.

**PlaceholderAPI**  
Если стоит на сервере — подхватывает `%player_prefix%` и `%player_suffix%` для формата.

**Интеграция с VoiceChat**  
Автоматическая синхронизация мьюта/бана/фриза/тюрьмы с Simple Voice Chat. Когда игрок наказан — его автоматически глушат в голосовом чате.

**Команды модерации**  
Встроенные шорткаты: `/prochat mute <игрок>`, `/prochat ban <игрок>`, `/prochat freeze <игрок>`, `/prochat jail <игрок>`. Автоматически делегирует EssentialsX, CMI, LiteBAN или Bukkit API.

## Установка

1. Скачать jar
2. Кинуть в plugins/
3. Перезапустить сервер
4. Открыть `plugins/ProChat/config.yml` и читать

Зависимостей никаких нет. PlaceholderAPI не обязателен, но с ним префиксы работают.

## Конфиг

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

Формат чата собирается из плэйсхолдеров:  
`{badges}` — бейджи игрока  
`{channel_prefix}` — префикс канала вида `[G]`  
`{prefix}` / `{suffix}` — из PlaceholderAPI  
`{display_name}` — обычный displayname  
`{display_name_anim}` — displayname с анимированным градиентом  
`{player}` — ник  
`{world}` — мир  
`{message}` — сообщение

### Анимированный градиент

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

speed — через сколько тиков сдвигается градиент. 3 норм, 1 — быстрое мельтешение.  
cycle_colors — список цветов по порядку. Можно хоть два, хоть десять.

В формате ника `<anim_gradient>S1sTeam</anim_gradient>`.  
Если надо прямо в сообщении: `<gradient:#ff0000:#00ff00>твой текст</gradient>`.

### Mention со всей обвязкой

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

Каждый блок можно вырубить отдельно. Title по дефолту выключен — слишком навязчиво.  
`personal_highlight` — когда тебя тегнули, ты видишь сообщение целиком выделенным цветом `color`. Остальные видят обычный чат.

### Бейджи

```yaml
badges:
  enabled: true
  list:
    admin: "&4[ADMIN]&r "
    vip: "&6[VIP]&r "
    donator: "&a[★]&r "
    default: "&7[✦]&r "
```

Права: `prochat.badge.admin`, `prochat.badge.vip` и т.д.  
`default` — показывается если нет ни одного бейджа. Можно убрать.

### Частицы при отправке

```yaml
chat_particles:
  enabled: true
  type: "HEART"
  count: 8
  speed: 0.05
  spread: 0.6
  permission: "prochat.particles"
```

Тип — любое название Particle из майнкрафта. HEART, NOTE, FLAME, VILLAGER_HAPPY, COMPOSTER, LAVA...

### Персональный звук

```yaml
personal_sound:
  enabled: true
  default: "block.note_block.pling"
  permission: "prochat.sound"
```

Игроки меняют через `/prochat sound <название>`. TabCompleter показывает популярные варианты.

### Антиспам

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

`threshold` — если больше этого процента букв заглавные, сообщение блокируется.  
`max_similarity` — процент совпадения с предыдущим сообщением.  
`words` — список матерных слов (пустой по умолчанию, заполнять руками).

### VoiceChat

```yaml
voicechat:
  enabled: true
  sync_mute: true
  badge: "&b[VC]&r "
```

Автоматическая синхронизация мьюта с Simple Voice Chat. Бейдж показывается когда игрок в голосовом чате.

## Команды

```
/prochat reload             — перечитать конфиг
/prochat channel <имя>      — переключиться на канал
/prochat sound <звук>       — установить звук сообщений
/prochat mute <игрок>       — замьютить игрока
/prochat unmute <игрок>     — размьютить
/prochat ban <игрок>        — забанить
/prochat unban <игрок>      — разбанить
/prochat freeze <игрок>     — заморозить
/prochat unfreeze <игрок>   — разморозить
/prochat jail <игрок>       — посадить в тюрьму
/prochat unjail <игрок>     — выпустить
/msg <игрок> <текст>        — личка
/r <сообщение>              — ответить на последнее ЛС
/socialspy                  — шпионаж за ЛС
/ignore <игрок>             — игнорировать игрока
/chatlog <игрок>            — лог сообщений игрока
/chatlog clear              — очистить лог
/muteall [время] [причина]  — заглушить весь чат
/clear (/cc)                — очистить чат
```

Табы работают.

## Права

```
prochat.*                        — всё сразу
prochat.reload                   — перезагрузка
prochat.channel.local            — локальный чат
prochat.channel.global           — глобальный
prochat.channel.admin            — админский
prochat.mention                  — принимать @упоминания
prochat.color                    — цветовые коды
prochat.rgb                      — hex цвета
prochat.gradient                 — градиенты
prochat.bypass.antispam          — игнор антиспама
prochat.sound                    — свой звук сообщений
prochat.particles                — частицы при отправке
prochat.badge.<name>             — конкретный бейдж
prochat.mod.mute                 — mute/unmute команды
prochat.mod.ban                  — ban/unban команды
prochat.mod.freeze               — freeze/unfreeze команды
prochat.mod.jail                 — jail/unjail команды
```

По дефолту всё что может быть у простых игроков — выдано. Админка, rgb, градиенты, антиспам-байпас — только опам.

## Локализация

Языки: русский, английский, немецкий, французский.  
Укажите `locale` в config.yml: `ru`, `en`, `de`, `fr`.  
Файлы языков в `plugins/ProChat/lang/`.

## Сборка

```bash
mvn clean package
```

Берёт Paper API 1.21.3. Если сервер старше — поднять версию в pom.xml.  
На версии ниже 1.21 не собирать — Adventure API там нет на уровне платформы.

## Почему не [другой плагин]

Я перепробовал штук пять чат-плагинов. В каждом чего-то не хватало: где-то нет градиентов, где-то mention через одно место, где-то антиспам кривой. Этот плагин я писал под себя — чтобы работало и не бесило. Если тебе тоже зайдет — пользуйся.

## Лицензия

MIT. Делай что хочешь, форкай, реверс-инжекть, встраивай куда хочешь. Только не говори что сам написал.

---

Ветки: [EN](https://github.com/S1sTeam/ProChat/tree/EN) · [RU](/) · [DE](https://github.com/S1sTeam/ProChat/tree/DE) · [FR](https://github.com/S1sTeam/ProChat/tree/FR)
