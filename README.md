# StarCore Forge

A NeoForge mod for Minecraft 1.21.1 that adds StarCore resources to the game.

## Features

### StarCore Ore
- **StarCore Ore** — Found in stone, drops **StarCore** when mined
- **Deepslate StarCore Ore** — Found in deepslate, drops **StarCore** when mined
- **StarCore** — A rare crystalline material with a blue-purple hue
- **StarCore Ingot** — Smelted from StarCore in a furnace or blast furnace

### Material Variants (Batch Registration)
Automatically registered for **Iron, Gold, Copper, Netherite, StarCore**:
- **Plate** — Square plate, tilted 30° right
- **Rod** — Thin rod, 135° diagonal
- **Gear** — Toothed gear with 8 teeth
- **Dust** — Diamond-shaped pile (redstone dust style)

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

## Batch Material Registration API

Add new material variants with one line of code:

### 1. Define the material in `BuiltInMaterials.java`

```java
public static final MaterialConfig SILVER = new MaterialConfig(
    "silver",      // Registry name suffix (e.g., "silver_plate")
    "银",           // Chinese name
    "Silver",       // English name
    0xC0C0D0,       // Primary color (RGB hex)
    0xD0D0E0        // Secondary color (RGB hex)
);
```

### 2. Add to the `ALL` list

```java
public static final List<MaterialConfig> ALL = List.of(
    IRON, GOLD, COPPER, NETHERITE, STARCORE,
    SILVER  // ← Add here
);
```

### 3. Run the client

```bash
./gradlew runClient
```

This automatically generates:
- **4 items** (`silver_plate`, `silver_rod`, `silver_gear`, `silver_dust`)
- **4 textures** (16×16 PNG with the material's color)
- **4 model JSONs** (in `models/item/`)
- **Translations** (Chinese + English, merged into `zh_cn.json` / `en_us.json`)
- **Creative tab** entries (auto-populated)

### External Mod Integration

Other mods can call the API directly:

```java
BatchMaterialRegistrar.registerMaterial(
    new MaterialConfig("bronze", "青铜", "Bronze", 0xCD7F32, 0xD4A060),
    MaterialVariantType.PLATE,
    MaterialVariantType.ROD,
    MaterialVariantType.GEAR,
    MaterialVariantType.DUST
);
```

## Project Structure
```
src/main/java/com/starcore/forge/
├── StarCoreForge.java         # Main @Mod class
├── ModCreativeTab.java        # Creative tab registration
├── block/
│   └── ModBlocks.java         # Block registration
├── item/
│   └── ModItems.java          # Item registration
└── api/
    ├── MaterialVariantType.java    # Shape masks for plate/rod/gear/dust
    ├── MaterialConfig.java         # Material definition record
    ├── BuiltInMaterials.java       # Predefined materials (add new ones here)
    ├── BatchMaterialRegistrar.java  # Batch registration API
    └── TextureGenUtil.java         # Automatic PNG texture generation
```

## License
MIT License
