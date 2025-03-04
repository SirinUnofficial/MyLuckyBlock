package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.helper.NbtHelper;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.Optional;

public class LuckyFunctions {
    public static void dropItems(Level level, Vec3 pos, String itemId, int count) {
        dropItems(level, pos, itemId, count, null);
    }

    public static void dropItems(Level level, Vec3 pos, String itemId, int count, @Nullable String nbtString) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        if (item.equals(Items.AIR)) return;
        ItemStack itemStack = new ItemStack(item, count);

        ItemEntity itemEntity = new ItemEntity(level, pos.x(), pos.y(), pos.z(), itemStack);

        if (nbtString != null) {
            CompoundTag nbt = NbtHelper.generateItemNbt(itemId, count, nbtString);
            if (nbt == null) return;
            itemEntity.load(nbt);
            itemEntity.save(nbt);
        }

        itemEntity.setPos(pos);
        level.addFreshEntity(itemEntity);
    }

    // use in spawn mob
    public static void dropItemsByNbt(Level level, Vec3 pos, @Nullable String name, boolean nameVisible, @Nullable String nbtString) {
        ItemStack itemStack = new ItemStack(Items.AIR);
        ItemEntity item = new ItemEntity(level, pos.x(), pos.y(), pos.z(), itemStack);

        if (nbtString != null) {
            CompoundTag nbt = NbtHelper.generateNbt(nbtString);
            if (nbt == null) return;
            item.load(nbt);
            item.save(nbt);
        }

        if (name != null) {
            item.setCustomName(Component.translatableEscape(name));
            item.setCustomNameVisible(nameVisible);
        }

        item.setPos(pos);
        level.addFreshEntity(item);
    }

    public static void placeBlock(Level level, Vec3 pos, String blockId) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
        BlockState blockState = block.defaultBlockState();
        BlockPos blockPos = PosHelper.parseVec3d(pos);
        level.setBlockAndUpdate(blockPos, blockState);
    }

    // TODO: not type but block id?
    public static void placeChest(Level level, BlockPos blockPos, int type, ResourceKey<LootTable> lootTableId) {
        Block block = Blocks.CHEST;
        switch (type) {
            case 1:
                block = Blocks.TRAPPED_CHEST;
                break;
            case 2:
                block = Blocks.BARREL;
                break;
            case 3:
                block = Blocks.SHULKER_BOX;
                break;
            case 0:
                // Default is Chest
                break;
            default:
                Constants.LOG.error("Error: PlaceChests Invalid Type: {}", type);
                break;
        }
        BlockState blockState = block.defaultBlockState();
        level.setBlockAndUpdate(blockPos, blockState);
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        // TODO: fix
        if (blockEntity instanceof RandomizableContainer lootableInventory) {
            lootableInventory.setLootTable(lootTableId);
        }
    }

    public static void fallBlock(Level level, BlockPos blockPos, Block block, Vec3 velocity) {
        BlockState blockState = block.defaultBlockState();
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(level, blockPos, blockState);
        fallingBlockEntity.push(velocity);
    }

    public static void spawnMob(Level level, Vec3 pos, EntityType<?> entityType, @Nullable String name, boolean nameVisible, @Nullable String nbtString, Vec3 velocity) {
        Entity entity = entityType.create(level);
        if (entity == null) return;
        CompoundTag nbt = NbtHelper.generateNbt(nbtString);
        if (nbt == null) return;
        entity.load(nbt);
        entity.save(nbt);
        if (name != null) {
            entity.setCustomName(Component.translatableEscape(name));
            entity.setCustomNameVisible(nameVisible);
        }
        entity.setPos(pos);
        entity.push(velocity);
        level.addFreshEntity(entity);
    }

    public static void spawnMob(Level level, Vec3 pos, EntityType<?> entityType, @Nullable String name, boolean nameVisible, boolean isBaby, Vec3 velocity) {
        Entity entity = entityType.create(level);
        if (entity == null) return;
        if (name != null) {
            entity.setCustomName(Component.translatableEscape(name));
            entity.setCustomNameVisible(nameVisible);
        }
        if (entity instanceof Mob mobEntity) {
            mobEntity.setBaby(isBaby);
        }
        entity.setPos(pos);
        entity.push(velocity);
        level.addFreshEntity(entity);
    }

    public static void createExplosion(Level level, Vec3 pos, float power, boolean createFire) {
        level.explode(null, pos.x(), pos.y(), pos.z(), power, createFire, Level.ExplosionInteraction.BLOCK);
    }

    public static void sendMessage(Player player, String message, boolean overlay) {
        player.displayClientMessage(Component.translatableEscape(message), overlay);
    }

    public static void givePotionEffect(Player player, Holder<MobEffect> effect, int duration, int amplifier) {
        if (player != null) {
            player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true));
        }
    }

    public static void addParticles(Level level, ParticleOptions particleEffect, Vec3 pos, int count, double velocityX, double velocityY, double velocityZ, double speed) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particleEffect, pos.x(), pos.y(), pos.z(), count, velocityX, velocityY, velocityZ, speed);
        }
    }

    public static void loadStructure(Level level, BlockPos pos, String modId, String structureName) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        MinecraftServer server = serverLevel.getServer();
        StructureTemplateManager manager = serverLevel.getStructureManager();
        ResourceLocation structureId = ResourceLocation.fromNamespaceAndPath(modId, structureName);

        try {
            Optional<StructureTemplate> template = manager.get(structureId);

            if (template.isEmpty()) {
                ResourceLocation resourcePath = ResourceLocation.fromNamespaceAndPath(
                        modId,
                        "structure/" + structureName + ".nbt"
                );

                ResourceManager resourceManager = server.getResourceManager();
                Resource resource = resourceManager.getResource(resourcePath).orElseThrow();
                try (InputStream stream = resource.open()) {
                    CompoundTag nbt = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
                    StructureTemplate structure = manager.readStructure(nbt);

                    template = Optional.of(structure);
                } catch (NoSuchFileException e) {
                    Constants.LOG.error("Structure file not exist: {}", resourcePath);
                    return;
                }
            }

            StructureTemplate structure = template.get();
            StructurePlaceSettings placement = new StructurePlaceSettings()
                    .setIgnoreEntities(false)
                    .setKnownShape(true)
                    .setRotation(Rotation.NONE)
                    .setMirror(Mirror.NONE);

            structure.placeInWorld(serverLevel, pos, pos, placement, serverLevel.getRandom(), 3);
        } catch (Exception e) {
            Constants.LOG.error("Catch error when loading structure: ", e);
        }
    }

    public static void executeCommand(Level level, Vec3 pos, String command) {
        if (level instanceof ServerLevel serverLevel) {
            MinecartCommandBlock cBMinecart = new MinecartCommandBlock(serverLevel, pos.x(), pos.y(), pos.z());
            cBMinecart.setCustomName(Component.translatableEscape(Constants.MOD_ID));
            cBMinecart.getCommandBlock().setCommand(command);
            cBMinecart.getCommandBlock().performCommand(serverLevel);
            cBMinecart.setPos(pos.x(), pos.y(), pos.z());
            serverLevel.addFreshEntity(cBMinecart);
            cBMinecart.discard();
        }
    }

    // Experimental
    public static void playSound(Player player, Level level, Vec3 pos, SoundSource soundSource, float volume, float pitch) {
        level.playSound(player, PosHelper.parseVec3d(pos), SoundEvents.CHERRY_LEAVES_BREAK, soundSource, volume, pitch);
    }
}
