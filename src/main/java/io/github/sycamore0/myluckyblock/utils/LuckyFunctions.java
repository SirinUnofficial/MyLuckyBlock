package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.util.Optional;

public class LuckyFunctions {
    public static void dropItems(World world, Vec3d pos, ItemStack itemStack) {
        world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack));
    }

    public static void dropItemsByNbt(World world, Vec3d pos, String name, boolean nameVisible, @Nullable String nbtString) {
        ItemStack itemStack = new ItemStack(Items.AIR);
        ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);

        NbtCompound nbt = NbtHelper.generateNbt(nbtString);
        item.readNbt(nbt);
        item.saveNbt(nbt);

        if (name != null) {
            item.setCustomName(Text.translatable(name));
            item.setCustomNameVisible(nameVisible);
        }

        item.setPosition(pos);
        world.spawnEntity(item);
    }

    public static void placeBlock(World world, BlockPos blockPos, Block block) {
        BlockState blockState = block.getDefaultState();
        world.setBlockState(blockPos, blockState);
    }

    public static void placeChest(World world, BlockPos blockPos, int type, RegistryKey<LootTable> lootTableId) {
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
                break;
            default:
                MyLuckyBlock.LOGGER.error("Error: PlaceChests Invalid Type: {}", type);
                break;
        }
        BlockState blockState = block.getDefaultState();
        world.setBlockState(blockPos, blockState);
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof LootableInventory lootableInventory) {
            lootableInventory.setLootTable(lootTableId);
        }
    }

    public static void fallBlock(World world, BlockPos blockPos, Block block, Vec3d velocity) {
        BlockState blockState = block.getDefaultState();
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, blockPos, blockState);
        fallingBlockEntity.addVelocity(velocity);
    }

    public static void spawnMob(World world, Vec3d pos, EntityType<?> entityType, @Nullable String name, Boolean nameVisible, @Nullable String nbtString, Vec3d velocity) {
        Entity entity = entityType.create(world);
        if (entity == null) return;
        NbtCompound nbt = NbtHelper.generateNbt(nbtString);
        entity.readNbt(nbt);
        entity.saveNbt(nbt);
        if (name != null) {
            entity.setCustomName(Text.translatable(name));
            entity.setCustomNameVisible(nameVisible);
        }
        entity.setPosition(pos);
        entity.addVelocity(velocity);
        world.spawnEntity(entity);
    }

    public static void spawnMob(World world, Vec3d pos, EntityType<?> entityType, @Nullable String name, Boolean nameVisible, boolean isBaby, Vec3d velocity) {
        Entity entity = entityType.create(world);
        if (entity == null) return;
        if (name != null) {
            entity.setCustomName(Text.translatable(name));
            entity.setCustomNameVisible(nameVisible);
        }
        if (entity instanceof MobEntity mobEntity) {
            mobEntity.setBaby(isBaby);
        }
        entity.setPosition(pos);
        entity.addVelocity(velocity);
        world.spawnEntity(entity);
    }

    public static void createExplosion(World world, Vec3d pos, float power, boolean createFire) {
        world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), power, createFire, World.ExplosionSourceType.BLOCK);
    }

    public static void sendMessage(PlayerEntity player, String message, int receiver) {
        if (receiver == 0) {
            player.sendMessage(Text.translatable(message), true);
        } else if (receiver == 1) {
            player.sendMessage(Text.translatable(message), false);
        } else {
            MyLuckyBlock.LOGGER.error("Error: SendMessages Invalid Receiver: {}", receiver);
        }
    }

    public static void givePotionEffect(PlayerEntity player, RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
        if (player != null) {
            player.addStatusEffect(new StatusEffectInstance(effect, duration, amplifier, false, true));
        }
    }

    public static void addParticles(World world, ParticleEffect particleEffect, Vec3d pos, int count, double velocityX, double velocityY, double velocityZ, double speed) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(particleEffect, pos.getX(), pos.getY(), pos.getZ(), count, velocityX, velocityY, velocityZ, speed);
        }
    }

    public static void loadStructure(World world, BlockPos pos, String structureName) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        MinecraftServer server = serverWorld.getServer();
        StructureTemplateManager manager = serverWorld.getStructureTemplateManager();
        Identifier structureId = Identifier.of(MyLuckyBlock.MOD_ID, structureName);

        try {
            Optional<StructureTemplate> template = manager.getTemplate(structureId);

            if (template.isEmpty()) {
                Identifier resourcePath = Identifier.of(
                        MyLuckyBlock.MOD_ID,
                        "structure/" + structureName + ".nbt"
                );

                ResourceManager resourceManager = server.getResourceManager();
                Resource resource = resourceManager.getResource(resourcePath).orElseThrow();
                try (InputStream stream = resource.getInputStream()) {
                    NbtCompound nbt = NbtIo.readCompressed(stream, NbtSizeTracker.ofUnlimitedBytes());
                    StructureTemplate structure = manager.createTemplate(nbt);

                    template = Optional.of(structure);
                } catch (NoSuchFileException e) {
                    MyLuckyBlock.LOGGER.error("Structure file not exist: {}", resourcePath);
                    return;
                }
            }

            StructureTemplate structure = template.get();
            StructurePlacementData placement = new StructurePlacementData()
                    .setIgnoreEntities(false)
                    .setUpdateNeighbors(true)
                    .setRotation(BlockRotation.NONE)
                    .setMirror(BlockMirror.NONE);

            structure.place(serverWorld, pos, pos, placement, serverWorld.getRandom(), 3);
        } catch (Exception e) {
            MyLuckyBlock.LOGGER.error("Catch error when loading structure: ", e);
        }
    }

    // Experimental
    public static void playSound(PlayerEntity player, World world, Vec3d pos, SoundCategory soundCategory, float volume, float pitch) {
        world.playSound(player, PosHelper.parseVec3d(pos), SoundEvents.BLOCK_CHERRY_LEAVES_BREAK, soundCategory, volume, pitch);
    }

    // Experimental
    // 提高自定义程度
    public static void executeCommand(World world, Vec3d pos, String command) {
        if (world instanceof ServerWorld serverWorld) {
            CommandBlockMinecartEntity cBMinecart = new CommandBlockMinecartEntity(serverWorld, pos.getX(), pos.getY(), pos.getZ());
            cBMinecart.setCustomName(Text.translatable(MyLuckyBlock.MOD_ID));
            cBMinecart.getCommandExecutor().setCommand(command);
            cBMinecart.getCommandExecutor().execute(serverWorld);
            cBMinecart.setPos(pos.getX(), pos.getY(), pos.getZ());
            serverWorld.spawnEntity(cBMinecart);
            cBMinecart.discard();
        }
    }
}
