# Change Log

### 1.20.1 branch (unreleased)
#### API changes
- `BlockManager.register(...)` and `BlockManager.Builder` take a `Registrar<Block>` instead of a Forge `DeferredRegister<Block>`. Create one with `Registrar.create(Registries.BLOCK, MOD_ID)` and call `finish()` once its entries are added.
- `registerBlockFunc` returns a `RegistryEntry<Block>` instead of a `RegistryObject<Block>`. `RegistryEntry` keeps `getId()`, and `Registrar` keeps `getEntries()`.
- `BlockAdditional.stateGenerator`, `stateGen(...)` and `stateGenBase(...)` are replaced by `textureFrom(...)`; the model generators moved from `ModBlockHelper` to `data.datagen.ModBlockModels` in the NeoForge module.
- `ModBlockFamily.Variant.getRegistryName(ModBlockStateProvider)` is removed.
- Loader checks go through `platform.Services` (`Services.platform().isModLoaded(...)`).
- `Services.registry().lateBlocks(modId, callback)` and `lateItems(...)` register blocks and items once every mod has registered its own, for blocks made from other mods' blocks.
- `BlockManager.Builder` gains `kind(...)`, `ingredient(...)`, `door(...)`, `displayName(...)`, `baseModel(...)`, `texture(slot, ...)`, `sibling(variant, ...)`, `cutout()` and `tint(...)`, the inputs of the asset engine. `BlockManager` gains `modId()`, `idPrefix()` and `assets()`.
- `assets.AssetEngine.generateFor(owner, namespace)` turns on load-time generation of a namespace's shapes.
- `AssetEngine` gains `dataIndex(...)`, `copyFlammability()`, `cutoutBlocks()`, `tintedBlocks()` and `ferriteCoreHint()`, which the loader modules call; `PlatformHooks` gains `copyFlammability(source, targets)`.
- `RoofPeakBlock` has a `pitch` property (`22`, `45`, `67`) again; worlds saved without it load as `45`, the only caps there were.
- `RoofPeakBlock.isHip(state)` counts outer corners of all three roof pitches.
- `ModWoodType.SIGN_TYPES` lists the wood types whose sign materials the client sets up.
- Shape specs name a shape per language (`"name": {"en_us": ...}`); other languages fall back to `en_us`.
- `BlockManager.Builder.tool(MiningTool)` sets the tool that mines a family's shapes (`PICKAXE`, `AXE`, `SHOVEL`, `HOE` or `NONE`); by default pickaxe for stone and axe for wood. `Material` gains `tool` and `miningTool()`.
- `GameView` and `PlatformHooks` gain `burns(...)`: whether fire takes a block.
- `compat.CompatLists.enable(owner, bundledFolder, configFile)` registers shapes for other mods' blocks from `calibermod-compat.json` files. Only the mod that calls it gets compat shapes; CaliberLib alone registers none.
- `config.ConfigFile`: TOML config files on every loader. Defaults are written with their comments, and keys a file lacks are added to it; everything else in the file is left as it is.
- `PlatformHelper.modFiles()` lists each loaded mod's own files, for reading data before data packs load.
- `BlockManager.Builder` gains `idPrefix(...)` (the shapes' id prefix, instead of the one taken from the family name) and `owner(...)` (the mod whose recipes and advancements a family's generated data use).

#### Features
- One build for NeoForge, Forge, Fabric and Quilt: loader-neutral code in `common`, loader code behind `platform.Services`.
- Asset engine: the blockstates, models, item models, names, loot tables, recipes, advancements and tags of block families are made in memory at load time, from shape specs in the jar, in a pack at the bottom of the pack list (`caliberlib:generated`), so mods and resource packs can still replace any file. Textures come from the source block's own model. Tall-door textures are made from the source door's. The pack stays on through every data reload: `/datapack disable "caliberlib:generated"` reloads the data with the pack still on, so the shapes keep their loot tables, recipes and tags.

- Ridge caps for 22 and 67 roofs: a cap takes the pitch of the roof below it, continues that roof's shingles to the ridge, and carries the hips at the ends of a ridge as on 45 roofs.
- Generated shapes copy their source block's flammability, on every loader; tall doors and cut-out families get the cut-out render layer; families built with `tint(...)` register block and item colour handlers; roof_67 models render on Fabric and Quilt.
- Compat lists (`calibermod-compat.json`, format 1): read from the owner's bundled folder, from `data/<modid>/calibermod-compat.json` in other mods' jars (each lists only its own blocks), and from `config/calibermod-compat/`. Entries whose block is missing are skipped. A source mod's own shape wins over a listed one with the same id. Per-mod and per-type switches and a ceiling on the number of shapes (20,000 by default) live in the owner's config file, in a `[compat]` table; Chipped is off unless turned on. Entries from bundled `released-*.json` files are read first, keep their shapes and names whatever later files say, and are never refused by the ceiling. Shapes copy their source block's behaviour as plain values (hardness, sound, light, map colour and the like), so a source whose light depends on its own state cannot break them. Wood shapes use the source mod's own block set type when it has one.
- A server and a client with different compat lists refuse the login with a readable reason, before the registries are compared: a login query on Fabric and Quilt, the `caliberlib:compat` channel version on NeoForge and Forge. Both sides log which entries differ.
- A world that holds compat shapes no longer registered (switched off, past the ceiling, or no longer listed) logs a warning naming each entry and its setting before its chunks load; the game would otherwise turn those blocks into air without a word. A small record in the world folder (`caliberlib-compat.txt`) holds what the world's last session registered.
- Game tests on Fabric and NeoForge (`runGametest`, `runGameTestServer`, part of `check`) and test jars for production servers (`gametestJar`).
- Needs Fabric API 0.83.0 or newer on Fabric and Quilt (the oldest tested).

#### Bugfixes
- Starts on every Forge 47 and on NeoForge 1.20.1 (0.2.9 needed Forge 47.3 or newer).
- 22 and 67 roofs and roof caps block sky light, as stairs do; the space under a closed roof is dark.
- Signs of the library's wood types get their textures on Fabric and Quilt, as on Forge.
- Shapes of wood that fire does not take (crimson, warped, and other mods' woods like them) are never furnace fuel (`minecraft:non_flammable_wood`).

### 0.2.0 (in progress)
#### Features
- Added support for Fabric Mod Loader
- Added Window Shutter Blocks
- Added new block states to Capital Blocks so they can connect
- Added new block states to Window and Window Half Blocks so they can have an arched top

#### Bugfixes


### 0.1.6
#### Features
- Added STONE_VARIANTS_WITHOUT_SLABS to ModBlockHelper
- Added Slabs to Stripped Wood variants
- Refactored Variant Lists for easier management of BlockManager variants
- Added fixBlockTex for Chalk Pillar and Purpur Pillar _(Pillars have "top" texture on top and bottom)_
- Added fixBlockTex for Create Variants

#### Bugfixes
- Removed non BlockManager item model data generation _(this was causing item models to appear invisible in inventory)_
- Adjusted texture alignment for Pillar models _(textures were misaligned)_


---
### 0.1.5
- Downgraded Forge Version <- 47.1.3 to allow NeoForge 1.20.1 support
- Added Window Shutter Blocks
- Added LootTable Provider Mixin
- Made changes to BlockManager to allow for Mod Compatibility blocks
- Bugfix: fixed ArchBlock class and models so textures display properly
- Bugfix: fixed Tall Doors sounds not playing
- Bugfix: fixed Talls Doors occlusion issues when in open state

---
### 0.1.4
- Added Diagonal Beam Blocks

---
### 0.1.3
- Added Buttons, Signs, Hanging Signs, Doors, Tall Doors, and Trapdoors to BlockManager
- Added Block and Item Tag Data Generation
- Refactored Slabs and Vertical Slabs to Layers and Vertical Layers (this just makes sense to avoid complications with vanilla slabs)
- Added CornerSlabLayers and VerticalCornerSlabLayers to add layers functionality to Corner Slab variants
- Added EighthLayer blocks to add layers functionality to Eighth Blocks
- Bugfix: fixed QuarterLayerBlock and VerticalQuarterLayerBlock textures

---
### 0.1.2
- Removed Pressure Plate Variant from Planks variants as it exists in Vanilla Minecraft
- Added Mud Brick to BlockProps
- Fixed block naming and textures to account for Stripped Bamboo Block Variants
- Removed and consolidated redundant Enums and ModBlockStateProperties

---
### 0.1.1
- Added BlockProps to ensure block property consistency across mods
- Added README.md

---
### 0.1.0
- Initial Release
- This mod is in the early development phase
- All classes moved from CaliberMod for use in other mods
- Added BlockState/Model DataGen for Hanging Signs
- Added Missing layer_block model





















