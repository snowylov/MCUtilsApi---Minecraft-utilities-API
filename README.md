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
	•	✅ JSON-driven tag and recipe content for datapack automation(see the latest update)