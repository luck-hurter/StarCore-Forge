# StarCore Forge

A NeoForge mod for Minecraft 1.21.1 that adds StarCore resources to the game.

## Features

### StarCore Ore
- **StarCore Ore** — Found in stone, drops **StarCore** when mined
- **Deepslate StarCore Ore** — Found in deepslate, drops **StarCore** when mined
- **StarCore** — A rare crystalline material with a blue-purple hue

### Mechanics
- Requires **Iron Pickaxe** or better to mine
- Affected by **Fortune** enchantment (more drops per level)
- **Silk Touch** compatible (drops the ore block itself)
- Registered in common ore tags (`c:ores`) for cross-mod compatibility

## Setup

### Prerequisites
- **Java 21** or later
- **Gradle 9.2.1** (wrapper included)

### Build
```bash
./gradlew build
```
The compiled JAR will be in `build/libs/`.

### Run Client
```bash
./gradlew runClient
```

### Generate Data
```bash
./gradlew runData
```

## Project Structure
```
src/main/java/com/starcore/forge/
├── StarCoreForge.java       # Main @Mod class
├── ModCreativeTab.java      # Creative tab registration
├── block/
│   └── ModBlocks.java       # Block registration
└── item/
    └── ModItems.java        # Item registration
```

## License
MIT License
