# CountDownDisplay

A lightweight [Paper](https://papermc.io/) plugin that puts live **countdown timers** and a shared **hype counter** into your Minecraft world as floating `TextDisplay` entities. Perfect for map releases, event launches, and stream countdowns.

- ⏳ **Countdowns** tick down to a Unix timestamp, then fire a command and launch a firework when they hit zero.
- 🔥 **Hype counter** shows a number that anyone can bump — the count is stored remotely so it's shared across everything that points at the same API.
- 💾 **Persistent** — displays are plain entities tagged with scoreboard tags, so they survive restarts and are rediscovered automatically on startup.

## Requirements

| Dependency | Version |
|---|---|
| Paper (or a fork) | 1.20.4+ |
| [CommandAPI](https://commandapi.jorel.dev/) | 9.3.0+ |
| Java | 8+ |

## Installation

1. Install [CommandAPI](https://commandapi.jorel.dev/) on your server (it's a hard dependency).
2. Drop `CountDownDisplay.jar` into your `plugins/` folder.
3. Restart the server.

On startup the plugin waits ~15 seconds, then scans all loaded worlds for tagged `TextDisplay` entities and re-attaches to them.

## Commands

All commands require **OP**.

### `/countdown`

| Command | Description |
|---|---|
| `/countdown new <name> <timestamp> <command>` | Spawns a countdown display at your location. `timestamp` is a Unix time in **seconds**. `command` runs from the console when the timer reaches zero (pass an empty-ish command if you don't need one). |
| `/countdown list` | Lists every loaded countdown and its end command. |
| `/countdown refresh` | Rescans the world for countdown entities and redraws them. |
| `/countdown test <name>` | Runs the end command of a countdown immediately, without removing it. |

Time is shown as `1d 02h 03m 04s`, dropping leading units that are zero (e.g. `05m 09s`).

When a countdown finishes it:
1. Runs its end command as console.
2. Detonates an orange/red firework at the display's position.
3. Removes the display entity.

**Example** – countdown to a release, then announce it:

```
/countdown new release 1735689600 say The map is out! Go play it!
```

> Tip: grab a Unix timestamp at [unixtimestamp.com](https://www.unixtimestamp.com/).

### `/hype`

| Command | Description |
|---|---|
| `/hype new <name> <command>` | Spawns a hype counter display at your location. `command` runs from the console every time the counter is increased. |
| `/hype increase <name>` | Increments the counter (can be run by the console or from command blocks/functions). |
| `/hype list` | Lists every loaded hype counter and its command. |
| `/hype refresh` | Rescans the world for hype counter entities and redraws them. |

The counter polls the API every second and only ever moves upward, so the displayed number never flickers backwards.

**Example** – a button players can hit to add hype and get a sound effect:

```
/hype new launch execute as @a run playsound minecraft:entity.player.levelup master @s
```

Then hook a command block up to `/hype increase launch`.

## How it works

Each display is a vanilla `TextDisplay` entity with scoreboard tags, so you can inspect or move them with normal commands (`/data`, `/tp @e[tag=countdown]`, …):

| Tag | Meaning |
|---|---|
| `countdown` / `hypeCount` | Marks the entity type |
| `name_<name>` | The display's name |
| `time_<timestamp>` | Countdown end time (countdowns only) |

The command to run is stored in the entity's custom name. You can create displays entirely by hand (e.g. from a datapack) by spawning a `TextDisplay` with the right tags and running `/countdown refresh` or `/hype refresh`.

### Hype counter backend

The hype counter reads from and writes to a small HTTP API:

- `GET https://api.hielkemaps.com/counter/get` → `{ "count": 123 }`
- `GET https://api.hielkemaps.com/counter/increase` → `{ "count": 124 }`

The URLs are currently hard-coded in `HypeCount.java` — change them there if you want to point at your own backend.

## Building

```sh
mvn package
```

The shaded jar is written to `target/CountDownDisplay.jar`.

## License

[MIT](LICENSE) © 2022 Hielke
