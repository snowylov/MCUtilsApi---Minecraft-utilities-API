# MCUtilsApi---Minecraft-utilities-API
made by alex.
feel free to give me credit or just copy the src into your mod

features:
🧰 Core Features

✅ ModUtil.java
	•	Simple Identifier creation (id("my_block"))
	•	Logging utility (log("message"))
	•	Null-safety guard (notNull())

✅ RegistryHelper.java
	•	Register blocks with automatic item creation
	•	Register items directly

✅ TagUtil.java
	•	Generate block/item tag keys easily
    ✅ CreativeTabHelper.java
	•	Quickly create custom creative tabs with item icons

✅ EventBusHelper.java
	•	Register Fabric player interaction events like UseBlockCallback

⸻

📦 Block & Item Utilities

✅ RecipeHelper.java
	•	Simplified creation of shaped and shapeless recipes programmatically

✅ LootTableHelper.java
	•	Generate single-item loot tables easily

✅ LanguageHelper.java
	•	Add localization strings via code
	•	Output entire en_us.json map

⸻

🧱 Model & Texture Generators

✅ ModelRegistrationHelper.java
	•	Auto-generate:
	•	Block models (cube_all, etc.)
	•	Item models (generated)
	•	Writes to the correct JSON format

✅ BlockstateGenerator.java
	•	Generate vanilla-style blockstates with simple model references

✅ LangEntryHelper.java
	•	Build and write localization entries to lang/en_us.json

⸻

🧬 Entity Helpers

✅ EntityRegistryHelper.java
	•	Register custom EntityTypes and bind attributes in one call

✅ EntityRendererHelper.java
	•	Register client-side renderers for entities

✅ UniversalEntityRenderer.java
	•	A generic renderer that works with:
	•	Any Model instance
	•	Custom Identifier texture
	•	Ideal for quick testing or simple entity types

⸻

🐍 Optional Python Integration
	•	GUI & script-based asset generator
	•	Generates:
	•	models/item/, models/block/
	•	blockstates/
	•	lang/en_us.json
	•	Accepts config JSON for batch asset creation

🧪 New Addons Included:

✅ AutoItemRegistrar.java
	•	Register multiple items all at once

    AutoBlockRegistrar.java
	•	Register blocks + their BlockItems in bulk
    ✨ New Features in v5:
	•	✅ @RegisterItem and @RegisterBlock annotations
	•	✅ Auto-discovery via reflection using AutoAnnotationProcessor
	•	✅ JSON-driven tag and recipe content for datapack automation(see the latest update)\n\n---\n\n# Temperature API for Fabric 1.21.11

Temperature API supplies sparse, persistent temperatures for blocks, fluids, air, and items. The standard temperature is `100`; that value is omitted from world saves, item components, and client caches.

## API examples

```java
TemperatureRegistries.registerBlock(MyBlocks.STEEL_BLOCK, TemperatureProfile.metalProfile());
TemperatureRegistries.registerFluid(MyFluids.OIL,
        new TemperatureProfile(90, 2.0F, 0.25F, 0.7F, false, false));
TemperatureRegistries.registerAirProvider((world, pos, current) ->
        world.getBiome(pos).matchesKey(MyBiomes.VOLCANO) ? 180 : null);

int blockTemperature = TemperatureApi.getBlockTemperature(world, pos);
int fluidTemperature = TemperatureApi.getFluidTemperature(world, pos);
int airTemperature = TemperatureApi.getAirTemperature(world, pos);
int itemTemperature = TemperatureApi.getItemTemperature(stack);

TemperatureApi.setBlockTemperature(serverWorld, pos, 450);
TemperatureApi.setItemTemperature(stack, 450);
```

Add custom metals to `#temperature_api:metal`. Metal touching fire, a lit/any campfire, or lava becomes fully orange after 30 seconds. After that it progressively changes to red. The tint overlay is capped at 30%.

## Block texture/model predicate

Extend `TemperatureStateBlock`. Its `temperature_stage` property is synchronized like an ordinary block state:

| Stage | Meaning | Temperature |
|---:|---|---:|
| 0 | cold | below 75 |
| 1 | normal | 75–149 |
| 2 | warm | 150–399 |
| 3 | hot | 400–699 |
| 4 | glowing | 700+ |

Use normal blockstate variants such as `temperature_stage=3` to swap textures/models.

## Item model predicate

The numeric item-model property is `temperature_api:temperature` and returns the raw temperature. Example `assets/example/items/heated_ingot.json`:

```json
{
  "model": {
    "type": "minecraft:range_dispatch",
    "property": "temperature_api:temperature",
    "entries": [
      {"threshold": 150, "model": {"type": "minecraft:model", "model": "example:item/heated_ingot_warm"}},
      {"threshold": 400, "model": {"type": "minecraft:model", "model": "example:item/heated_ingot_orange"}},
      {"threshold": 700, "model": {"type": "minecraft:model", "model": "example:item/heated_ingot_red"}}
    ],
    "fallback": {"type": "minecraft:model", "model": "example:item/heated_ingot"}
  }
}
```

Temperature changes fire `TemperatureChangeCallback.EVENT`. Registration maps are additive and tag-based metal support allows cross-mod integration without a hard dependency.
