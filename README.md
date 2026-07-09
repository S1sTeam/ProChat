# ⚡ ProChat

**ProChat** — мощный плагин для Paper 1.21+, который полностью преображает чат на вашем сервере Minecraft.  
Анимированные ники, каналы, @упоминания со звуком и частицами, анти-спам, бейджи и многое другое.

---

## ✨ Возможности

| Фича | Описание |
|------|----------|
| **🌈 Анимированный градиент** | Никнеймы переливаются цветами радуги прямо в чате |
| **🏷️ Бейджи (Chat Badges)** | Иконки перед ником: `[ADMIN]`, `[VIP]`, `[★]` и т.д. |
| **🎨 Форматирование сообщений** | `&c`, `#RRGGBB`, `<gradient:#c1:#c2>text</gradient>` |
| **📢 Каналы чата** | Локальный, глобальный, админ-канал с настраиваемой дистанцией |
| **@Упоминания** | `@ник` → подсветка сообщения + звук + частицы + ActionBar + Title |
| **🔊 Персональный звук** | Каждый игрок выбирает свой звук для новых сообщений |
| **✨ Частицы при отправке** | Красивые частицы вокруг игрока, когда он пишет в чат |
| **🛡️ Анти-спам** | Cooldown, защита от капса, фильтр мата, блокировка повторов |
| **🔌 PlaceholderAPI** | Поддержка `%player_prefix%`, `%player_suffix%` |
| **⚙️ Гибкая настройка** | Всё в `config.yml` и `messages.yml` — цвета, звуки, частицы, форматы |

---

## 📥 Установка

1. Скачай `ProChat-0.11.jar` из [релизов](https://github.com/S1sTeam/ProChat/releases)
2. Положи в папку `plugins/` твоего сервера
3. Перезапусти сервер (или `/reload`)
4. Настрой под себя в `plugins/ProChat/config.yml`

**Зависимости:**  
- ✅ **Paper 1.21+** (рекомендуется последняя версия)  
- ⏺ PlaceholderAPI (опционально, для префиксов/суффиксов)

---

## 📝 Форматирование

```
&c — красный                        &l — жирный
&e — жёлтый                         &o — курсив
#ff0000 — hex цвет                  &n — подчёркнутый
```

Градиент:  
```
<gradient:#ff4444:#44ff44>Привет, мир!</gradient>
```

Анимированный градиент (в формате ника):  
```
<anim_gradient>Игрок</anim_gradient>
```

---

## 💬 Команды

| Команда | Описание | Право |
|---------|----------|-------|
| `/prochat reload` | Перезагрузить конфиг | `prochat.reload` |
| `/prochat channel <канал>` | Переключить канал | `prochat.reload` |
| `/prochat sound <звук>` | Установить звук сообщений | `prochat.sound` |
| `/msg <игрок> <сообщение>` | Личное сообщение | — |
| `@<игрок>` | Упомянуть игрока в чате | `prochat.mention` |

---

## 🔧 Права (Permissions)

```
prochat.*                        — Все права
prochat.reload                   — Перезагрузка конфига
prochat.channel.local            — Локальный чат
prochat.channel.global           — Глобальный чат
prochat.channel.admin            — Админ-чат
prochat.mention                  — @упоминания
prochat.color                    — Цвета (&a-&f)
prochat.rgb                      — Hex цвета (#RRGGBB)
prochat.gradient                 — Градиенты
prochat.bypass.antispam          — Обход анти-спама
prochat.sound                    — Персональный звук
prochat.particles                — Частицы при отправке
prochat.badge.<название>         — Бейдж (например prochat.badge.vip)
```

---

## 🗂️ Структура конфига

```yaml
format:
  chat: "{badges}{channel_prefix}{prefix}{display_name_anim}{suffix}&7: {message}"

animated_gradient:
  enabled: true
  speed: 3
  cycle_colors: ["#ff4444", "#ff8800", "#ffdd00", "#44ff44", "#00ddff", "#8844ff", "#ff44ff"]

chat_particles:
  enabled: true
  type: HEART
  count: 8
  spread: 0.6

mention:
  actionbar:
    enabled: true
  title:
    enabled: false
  particles:
    enabled: true
  personal_highlight:
    enabled: true
    color: "&e&l"
```

Полный пример — в `plugins/ProChat/config.yml`.

---

## 📜 Лицензия

MIT License — делай что хочешь, но не выдавай за своё.

---

## 👨‍💻 Разработчик

**S1sTeam**  
[github.com/S1sTeam/ProChat](https://github.com/S1sTeam/ProChat)
