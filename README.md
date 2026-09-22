# SanctionManager

**SanctionManager** is a modular punishment management system for Minecraft server networks.

It provides a shared API and core implementation for managing player sanctions across multiple Minecraft server platforms, with support for **Spigot, BungeeCord, and Velocity**.

The project is designed with a modular architecture so that platform-specific code remains separated from the common punishment and data-management logic.

## Features

* Modular architecture with a shared API and core
* Support for:

    * Spigot
    * BungeeCord
    * Velocity
* Cross-server punishment management
* Shared punishment data through a central storage layer
* Redis support for network-wide communication and data synchronization
* Connection pooling through HikariCP
* Command handling powered by Cloud
* Public API for integration with other plugins
* Java 8-compatible API and core
* Shaded and relocated runtime dependencies for platform plugins

## Architecture

SanctionManager is divided into several Gradle modules:

```text
SanctionManager
├── sanctionmanager-api
│   └── Public API and shared interfaces
│
├── sanctionmanager-core
│   └── Common implementation and data layer
│
├── sanctionmanager-spigot
│   └── Spigot/Bukkit integration
│
├── sanctionmanager-bungee
│   └── BungeeCord integration
│
└── sanctionmanager-velocity
    └── Velocity integration
```

The dependency relationship is approximately:

```text
                    ┌────────────────────┐
                    │ sanctionmanager-api│
                    └─────────┬──────────┘
                              │
                    ┌─────────▼──────────┐
                    │ sanctionmanager-core│
                    └───────┬─────┬──────┘
                            │     │
              ┌─────────────┘     └──────────────┐
              ▼                                  ▼
   ┌──────────────────┐               ┌──────────────────┐
   │ Platform Modules │               │ Network / Storage│
   │                  │               │ Infrastructure   │
   │ Spigot           │               │ Redis            │
   │ BungeeCord       │               │ HikariCP         │
   │ Velocity         │               │                  │
   └──────────────────┘               └──────────────────┘
```

## Platform Support

| Module                     | Platform                 |    Java |
| -------------------------- | ------------------------ | ------: |
| `sanctionmanager-api`      | Platform-independent API |  Java 8 |
| `sanctionmanager-core`     | Common implementation    |  Java 8 |
| `sanctionmanager-spigot`   | Spigot/Bukkit            |  Java 8 |
| `sanctionmanager-bungee`   | BungeeCord               | Java 17 |
| `sanctionmanager-velocity` | Velocity                 | Java 25 |

The Java version listed above is the version used to build the corresponding module. Your Minecraft server must also satisfy the Java requirements of the specific server software and Minecraft version you are running.

## Requirements

### Build

* JDK 25
* Gradle Wrapper

The root project uses Java 25 for the build configuration, while the API and core modules target Java 8 bytecode.

### Runtime dependencies

Depending on the platform and deployment, SanctionManager uses:

* Redis / Jedis
* HikariCP
* Cloud
* StandaloneEvent
* ServerBridge

The API and core modules declare ServerBridge and StandaloneEvent as compile-time dependencies. Core additionally uses Jedis, HikariCP, and Cloud.

## Building

Clone the repository:

```bash
git clone https://github.com/FloatingPointMC/SanctionManager.git
cd SanctionManager
```

Build the project using the Gradle Wrapper:

```bash
./gradlew build
```

On Windows:

```bat
gradlew.bat build
```

The platform modules use Shadow to produce their distributable JARs. Runtime dependencies such as Jedis, HikariCP, and bStats are relocated into SanctionManager's own namespace to reduce dependency conflicts with other plugins.

## Installation

### Spigot

Build:

```bash
./gradlew :sanctionmanager-spigot:build
```

Place the resulting JAR in the server's:

```text
plugins/
```

directory.

The Spigot module registers itself as `SanctionManager` and uses:

```text
io.github.floatingpointmc.sanctionmanager.spigot.SpigotMain
```

as its entry point.

### BungeeCord

Build:

```bash
./gradlew :sanctionmanager-bungee:build
```

Place the resulting JAR in the proxy's:

```text
plugins/
```

directory.

The BungeeCord module uses:

```text
io.github.floatingpointmc.sanctionmanager.bungee.BungeeMain
```

as its entry point.

### Velocity

Build:

```bash
./gradlew :sanctionmanager-velocity:build
```

Place the resulting JAR in Velocity's:

```text
plugins/
```

directory.

The Velocity module uses:

```text
io.github.floatingpointmc.sanctionmanager.velocity.VelocityMain
```

as its entry point.

Velocity currently declares StandaloneEvent as a required dependency and ServerBridge as an optional dependency.

## Dependencies

SanctionManager is built around several existing open-source libraries and platform APIs.

### Core

The core module currently uses:

* [Jedis](https://github.com/redis/jedis) — Redis client
* [HikariCP](https://github.com/brettwooldridge/HikariCP) — JDBC connection pooling
* [Cloud](https://github.com/Incendo/cloud) — command framework
* ServerBridge API
* StandaloneEvent API
* JetBrains Annotations
* Lombok

### Spigot

The Spigot adapter additionally uses:

* Spigot API 1.8.8
* bStats
* Cloud Paper

### BungeeCord

The BungeeCord adapter additionally uses:

* BungeeCord API
* bStats
* Cloud Bungee

### Velocity

The Velocity adapter additionally uses:

* Velocity API 4.2.0
* bStats
* Cloud Velocity
* SnakeYAML

## API

The `sanctionmanager-api` module contains the public interfaces and types intended for integration with other plugins.

If you are developing another plugin that needs to interact with SanctionManager, depend on the API rather than directly depending on the platform implementation.

This allows integrations to remain independent from the platform-specific implementation.

## Network Architecture

SanctionManager is intended to be used in Minecraft server networks where multiple servers and/or proxies need access to shared sanction data.

A typical deployment can look like:

```text
                    ┌───────────────┐
                    │    Redis      │
                    │ Shared State  │
                    └───────┬───────┘
                            │
             ┌──────────────┼──────────────┐
             │              │              │
             ▼              ▼              ▼
       ┌──────────┐   ┌──────────┐   ┌──────────┐
       │ Spigot   │   │ Spigot   │   │ Velocity │
       │ Server 1 │   │ Server 2 │   │ / Proxy  │
       └──────────┘   └──────────┘   └──────────┘
```

This allows the common SanctionManager logic to be shared while platform-specific integrations remain isolated.

## Development

The project is a Kotlin DSL-based Gradle multi-project build.

Included modules:

```text
sanctionmanager-api
sanctionmanager-core
sanctionmanager-spigot
sanctionmanager-bungee
sanctionmanager-velocity
```

To run the test suite:

```bash
./gradlew test
```

To build a specific platform:

```bash
./gradlew :sanctionmanager-spigot:build
./gradlew :sanctionmanager-bungee:build
./gradlew :sanctionmanager-velocity:build
```

## Contributing

Contributions are welcome.

Before submitting a pull request:

1. Make sure the project builds successfully.
2. Run the relevant test suite.
3. Keep platform-specific code inside its corresponding module.
4. Put platform-independent functionality in `sanctionmanager-core`.
5. Put public integration interfaces in `sanctionmanager-api`.
6. Avoid introducing unnecessary platform-specific dependencies into shared modules.
7. Document significant API or behavioral changes.

For larger changes, opening an issue before implementation is recommended so the proposed design can be discussed first.

## License

SanctionManager is free and open-source software.

Copyright © 2026 vlouboos and contributors.

See [LICENSE](LICENSE) for the complete license text.

### Third-party software

SanctionManager uses a number of third-party libraries. Each dependency remains subject to its own license.

See the corresponding project and dependency metadata for the applicable licenses.

## Authors

**vlouboos**

GitHub: https://github.com/vlouboos

Repository:

https://github.com/FloatingPointMC/SanctionManager
