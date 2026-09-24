# SanctionManager

SanctionManager is a standalone, platform-independent sanction management system. It provides a reusable backend for managing player sanctions — bans and mutes — with persistent storage, caching, and a public API.

Minecraft is one integration platform for SanctionManager, not its foundation. The core business logic has zero dependency on Minecraft APIs. Minecraft integrations are implemented as adapters that connect platform-specific events, commands, and messaging to the core.

## Features

* Platform-independent sanction management core
* Punishment types: ban and mute
* Persistent storage via JDBC (MySQL, MariaDB, PostgreSQL, SQLite)
* Connection pooling via HikariCP
* Local in-memory punishment cache
* Redis distributed cache via Jedis
* Cancellable event system (`PunishmentExecuteEvent`, `PunishmentWithdrawEvent`, `PunishmentRemoveEvent`)
* Platform-independent public API for external integrations
* Minecraft adapters for Spigot, BungeeCord, and Velocity
* Cloud command framework integration in the Minecraft adapter layer
* Configurable message templates with variable substitution
* Shaded and relocated runtime dependencies for platform plugins

## Architecture

SanctionManager enforces a strict, unidirectional dependency chain:

```text
sanctionmanager-spigot    ─┐
sanctionmanager-bungee    ─┤
sanctionmanager-velocity  ─┘
            │
            │ depends on
            ▼
sanctionmanager-minecraft
            │
            │ depends on
            ▼
sanctionmanager-core
            │
            │ depends on
            ▼
sanctionmanager-api
```

Platform adapters never depend on core directly. They interact with core exclusively through the `sanctionmanager-minecraft` adapter layer.

### Dependency boundaries

| From | To | Allowed |
|------|----|---------|
| Platform adapter (spigot/bungee/velocity) | `sanctionmanager-minecraft` | Yes |
| Platform adapter (spigot/bungee/velocity) | `sanctionmanager-core` | **No** |
| `sanctionmanager-minecraft` | `sanctionmanager-core` | Yes |
| `sanctionmanager-minecraft` | `sanctionmanager-api` | Yes |
| `sanctionmanager-core` | `sanctionmanager-api` | Yes |
| `sanctionmanager-core` | Minecraft / Bukkit / Cloud | **No** |

### Core

`sanctionmanager-core` is the platform-independent business core. It contains:

* `SanctionManagerCore` — bootstrap and lifecycle entry point
* `PunishmentManager` — implements `PunishmentManagerAPI`, fires events on mutation
* `PunishmentService` — cache-first query logic, delegates to repository
* `PunishmentRepository` / `HikariPunishmentRepository` — JDBC persistence with auto-schema creation
* `PunishmentCache` / `LocalPunishmentCache` / `RedisPunishmentCache` — caching abstraction
* `PunishmentSerializer` — serialization for Redis storage
* `PunishmentRecord` — domain model implementing the `Punishment` interface
* `DatabaseConfig` — database connection configuration (driver, host, port, database, credentials)
* StandaloneEvent (`io.github.vlouboos:standaloneevent-common`) — platform-independent event dispatch

Core never references `Player`, `CommandSender`, `Bukkit`, `Component`, `Cloud`, or any Minecraft-specific type.

### API

`sanctionmanager-api` defines platform-independent contracts:

* `SanctionManager` — root interface exposing `PunishmentManagerAPI`
* `SanctionManagerAPI` — static accessor for the registered `SanctionManager` instance
* `PunishmentManagerAPI` — query, add, withdraw, and remove punishments
* `Punishment` — read-only interface for punishment data (target, executor, type, times, reason, etc.)
* `Type` — enum: `BAN`, `MUTE`
* `PunishmentExecuteEvent`, `PunishmentWithdrawEvent`, `PunishmentRemoveEvent` — domain events (extend `Event` from StandaloneEvent, with cancellation support)

### Minecraft Adapter

`sanctionmanager-minecraft` is the Minecraft-specific integration layer. It bridges Minecraft-facing concerns to the core:

* `MinecraftSanctionManager` — initializes `SanctionManagerCore` and exposes `PunishmentManagerAPI`
* `SanctionCommandManager` — Cloud-based command handler for the `/sanction` command
* `SanctionCommandSender` — platform-independent command sender abstraction
* `MessageConfig` — configurable message templates (ban permanent/temporary, mute permanent/temporary, description)
* `MessageContext` — template variable context (punishment data, plugin name/version)
* `MessageFormatter` — variable substitution (`%name%`, `%reason%`, `%operator%`, `%duration%`, `%id%`, `%uuid%`, `%time%`, `%plugin%`, `%version%`)

Cloud Command Framework belongs to this layer, not to core or platform adapters.

### Platform Adapters

Each platform adapter is responsible for:

* Plugin lifecycle (enable/disable)
* Platform-specific event listeners (login, chat)
* Command sender mapping (platform `CommandSource` → `SanctionCommandSender`)
* Platform-specific `CommandManager` instantiation (Cloud Paper/Bungee/Velocity)
* Message delivery to players using platform APIs
* Configuration loading
* bStats metrics

Platform adapters do not instantiate `SanctionManagerCore` directly. They use `MinecraftSanctionManager` from the Minecraft adapter layer.

## Message System

Message presentation is a platform responsibility. Core returns business results — `Punishment` objects, event notifications — without any reference to Minecraft messaging.

The flow is:

```text
Core → Punishment / Event / Result
         │
         ▼
Minecraft Adapter → MessageFormatter → formatted String
         │
         ▼
Platform Adapter → CommandSource.sendMessage(Component)
```

Core never calls `Player.sendMessage()`, `CommandSender.sendMessage()`, or any platform messaging API.

## Command System

Commands belong to the Minecraft adapter layer, not the core business layer.

```text
Minecraft / Proxy Command Input
            │
            ▼
Cloud CommandManager (platform-specific)
            │
            ▼
SanctionCommand (sanctionmanager-minecraft)
            │
            ▼
MinecraftSanctionManager → PunishmentManagerAPI
            │
            ▼
Core → PunishmentService → Repository / Cache
```

Each platform adapter provides:

* A `CommandManager<SanctionCommandSender>` instance (Cloud Paper, Cloud Bungee, or Cloud Velocity)
* A `SanctionCommandSender` implementation that wraps the platform's `CommandSource`

The command logic itself lives in `SanctionCommandManager` within `sanctionmanager-minecraft`.

## Modules

| Module | Purpose | Java |
| ------ | ------- | ---- |
| `sanctionmanager-api` | Platform-independent public API and contracts | 8 |
| `sanctionmanager-core` | Standalone sanction management core | 8 |
| `sanctionmanager-minecraft` | Minecraft adapter layer (commands, messages, Cloud) | 8 |
| `sanctionmanager-spigot` | Spigot/Bukkit platform adapter | 8 |
| `sanctionmanager-bungee` | BungeeCord platform adapter | 17 |
| `sanctionmanager-velocity` | Velocity platform adapter | 25 |

## Supported Platforms

| Platform | Mode | Description |
| -------- | ---- | ----------- |
| Spigot | Standalone / Bridge | Full sanction management or bridge to another instance |
| BungeeCord | Standalone / Listener | Proxy-level sanction management |
| Velocity | Standalone / Listener | Proxy-level sanction management |

These are integration adapters, not the runtime environment of the core itself. The core can function as a standalone library without any Minecraft platform.

## Configuration

Each platform adapter ships a `config.yml`:

```yaml
mode: standalone

redis:
  enable: false
  host: localhost
  port: 6379
  password: ""

database:
  driver: com.mysql.cj.jdbc.Driver
  host: localhost
  port: 3306
  database: sanctionmanager
  user: root
  password: ""
```

### Mode

* `standalone` — the adapter runs its own `SanctionManagerCore` instance with local database and cache
* `bridge` (Spigot) — delegates to an external SanctionManager instance; no local features
* `listener` (BungeeCord/Velocity) — proxy listens for cross-proxy events; no local commands

### Database

Supported drivers:

* `com.mysql.cj.jdbc.Driver` — MySQL
* `org.mariadb.jdbc.Driver` — MariaDB
* `org.postgresql.Driver` — PostgreSQL
* `org.sqlite.JDBC` — SQLite

### Messages

Message templates are loaded from `messages.yml` and support the following variables:

| Variable | Description |
| -------- | ----------- |
| `%name%` | Punished player name |
| `%uuid%` | Punished player UUID |
| `%reason%` | Punishment reason |
| `%id%` | Punishment ID |
| `%operator%` | Operator who issued the punishment |
| `%duration%` | Remaining duration (or `permanent`) |
| `%time%` | Expiry timestamp (or `permanent`) |
| `%plugin%` | Plugin name |
| `%version%` | Plugin version |

## API / Integration

The `sanctionmanager-api` module provides the public interface for integrating with SanctionManager from external code.

### Accessing the API

```java
SanctionManager api = SanctionManagerAPI.getAPI();
PunishmentManagerAPI punishManager = api.getPunishManager();
```

### Querying punishments

```java
Collection<Punishment> active = punishManager.queryActivePunishments(playerUuid);
```

### Adding a punishment

```java
punishManager.addPunishment(punishmentRecord);
```

### Listening to events

SanctionManager uses [StandaloneEvent](https://github.com/MC-Azure/StandaloneEvent) (`io.github.vlouboos:standaloneevent-common`) as its event system. Register a listener object with `@EventHandler`-annotated methods:

```java
public class MyListener {
    @EventHandler
    public void onPunishmentExecute(PunishmentExecuteEvent event) {
        Punishment punishment = event.punishment;
    }
}

StandaloneEventAPI.getApi().register(new MyListener());
```

Events support cancellation: setting `event.canceled = true` prevents the action from proceeding.

### Integration beyond Minecraft

The platform-independent API is designed to allow integrations beyond Minecraft. Because core has no Minecraft dependency, it can be embedded in any Java 8+ application — web services, Discord bots, administration panels, or custom server software — that needs sanction management logic with persistent storage.

## Network Architecture

In a multi-server Minecraft network, SanctionManager can share sanction data across servers and proxies:

```text
                ┌───────────┐
                │   Redis   │
                │  Cache    │
                └─────┬─────┘
                      │
       ┌──────────────┼──────────────┐
       │              │              │
       ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐
│  Spigot  │   │  Spigot  │   │ Velocity │
│ Server 1 │   │ Server 2 │   │  Proxy   │
└──────────┘   └──────────┘   └──────────┘
```

Each node runs its own `SanctionManagerCore` instance backed by the same database. Redis provides a distributed cache layer for cross-node data consistency.

## Requirements

### Build

* JDK 25 (for the root build configuration and Velocity module)
* Gradle Wrapper (included)

### Runtime

| Module | Minimum Java |
| ------ | ------------ |
| API / Core / Minecraft / Spigot | Java 8 |
| BungeeCord | Java 17 |
| Velocity | Java 21 |

## Building

Clone the repository:

```bash
git clone https://github.com/FloatingPointMC/SanctionManager.git
cd SanctionManager
```

Build all modules:

```bash
./gradlew build
```

On Windows:

```bat
gradlew.bat build
```

Run tests:

```bash
./gradlew test
```

Build a specific platform adapter:

```bash
./gradlew :sanctionmanager-spigot:build
./gradlew :sanctionmanager-bungee:build
./gradlew :sanctionmanager-velocity:build
```

Platform adapters use the Shadow plugin to produce self-contained JARs. Runtime dependencies (Jedis, HikariCP, bStats) are relocated into SanctionManager's namespace to avoid conflicts with other plugins.

## Installation

### Spigot

1. Build: `./gradlew :sanctionmanager-spigot:build`
2. Place the JAR in the server's `plugins/` directory
3. Configure `config.yml` and `messages.yml` in `plugins/SanctionManager/`
4. Restart the server

Entry point: `io.github.floatingpointmc.sanctionmanager.spigot.SpigotMain`

### BungeeCord

1. Build: `./gradlew :sanctionmanager-bungee:build`
2. Place the JAR in the proxy's `plugins/` directory
3. Configure `config.yml` in `plugins/SanctionManager/`
4. Restart the proxy

Entry point: `io.github.floatingpointmc.sanctionmanager.bungee.BungeeMain`

### Velocity

1. Build: `./gradlew :sanctionmanager-velocity:build`
2. Place the JAR in Velocity's `plugins/` directory
3. Configure `config.yml` in the plugin's data directory
4. Restart the proxy

Entry point: `io.github.floatingpointmc.sanctionmanager.velocity.VelocityMain`

## Dependencies

### Core

* [StandaloneEvent Common](https://github.com/MC-Azure/StandaloneEvent) 1.6 — platform-independent event system
* [Jedis](https://github.com/redis/jedis) 8.0.1 — Redis client for distributed caching
* [HikariCP](https://github.com/brettwooldridge/HikariCP) 4.0.3 — JDBC connection pooling
* JetBrains Annotations 26.1.0
* Lombok 1.18.48

### Minecraft Adapter

* [Cloud Core](https://github.com/Incendo/cloud) 2.0.0 — command framework
* JetBrains Annotations, Lombok

### Spigot

* Spigot API 1.8.8-R0.1-SNAPSHOT
* [Cloud Paper](https://github.com/Incendo/cloud) 2.0.0-beta.10
* [bStats Bukkit](https://bstats.org) 3.2.1

### BungeeCord

* BungeeCord API 26.1-R0.1-SNAPSHOT
* [Cloud Bungee](https://github.com/Incendo/cloud) 2.0.0-beta.10
* [bStats BungeeCord](https://bstats.org) 3.2.1

### Velocity

* Velocity API 4.2.0
* [Cloud Velocity](https://github.com/Incendo/cloud) 2.0.0-beta.10
* [bStats Velocity](https://bstats.org) 3.2.1
* SnakeYAML 2.4

## Development

This is a Kotlin DSL Gradle multi-project build with six modules:

```text
sanctionmanager-api
sanctionmanager-core
sanctionmanager-minecraft
sanctionmanager-spigot
sanctionmanager-bungee
sanctionmanager-velocity
```

### Key design principles

* Core is platform-independent — no Minecraft types in `sanctionmanager-core`
* Platform adapters access core only through `sanctionmanager-minecraft`
* Cloud commands live in `sanctionmanager-minecraft`, not in platform adapters or core
* Message formatting is in `sanctionmanager-minecraft`; message delivery is in platform adapters
* API does not expose platform-specific types
* New features that are platform-independent go in core; Minecraft-specific behavior goes in the minecraft adapter or platform adapters

## Contributing

Contributions are welcome. Before submitting a pull request:

1. Ensure the project builds: `./gradlew build`
2. Run the test suite: `./gradlew test`
3. Keep core platform-independent — no Minecraft imports in `sanctionmanager-core`
4. Platform adapters must not depend on `sanctionmanager-core` directly; use `sanctionmanager-minecraft`
5. Put platform-independent functionality in `sanctionmanager-core`
6. Put public integration interfaces in `sanctionmanager-api`
7. Put Minecraft-facing logic (commands, messages) in `sanctionmanager-minecraft`
8. Document significant API or behavioral changes

For larger changes, open an issue before implementation so the design can be discussed.

## License

SanctionManager is licensed under the [GNU Lesser General Public License v3.0 or later](LICENSE).

Copyright &copy; 2026 vlouboos and contributors.

### Third-party software

SanctionManager uses third-party libraries, each subject to its own license. See dependency metadata for details.

## Authors

**vlouboos** — [GitHub](https://github.com/vlouboos)

Repository: [https://github.com/FloatingPointMC/SanctionManager](https://github.com/FloatingPointMC/SanctionManager)