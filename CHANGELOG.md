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

#### Features
- One build for NeoForge, Forge, Fabric and Quilt: loader-neutral code in `common`, loader code behind `platform.Services`.
- Asset engine: the blockstates, models, item models, names, loot tables, recipes, advancements and tags of block families are made in memory at load time, from shape specs in the jar, in a pack at the bottom of the pack list (`caliberlib:generated`), so mods and resource packs can still replace any file. Textures come from the source block's own model. Tall-door textures are made from the source door's.

- Ridge caps for 22 and 67 roofs: a cap takes the pitch of the roof below it, continues that roof's shingles to the ridge, and carries the hips at the ends of a ridge as on 45 roofs.
- Generated shapes copy their source block's flammability, on every loader; tall doors and cut-out families get the cut-out render layer, tinted families their colours; roof_67 models render on Fabric and Quilt.

#### Bugfixes
- Starts on every Forge 47 and on NeoForge 1.20.1 (0.2.9 needed Forge 47.3 or newer).
- 22 and 67 roofs and roof caps block sky light, as stairs do; the space under a closed roof is dark.
- Signs of the library's wood types get their textures on Fabric and Quilt, as on Forge.

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





















