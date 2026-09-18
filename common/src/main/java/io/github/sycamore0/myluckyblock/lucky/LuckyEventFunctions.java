package io.github.sycamore0.myluckyblock.lucky;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.helper.NbtHelper;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class LuckyEventFunctions {
    public static void dropItems(ServerLevel serverLevel, Vec3 pos, String itemId, int count, @Nullable String name, boolean nameVisible, @Nullable String desc, @Nullable String nbtString) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        if (item.equals(Items.AIR)) return;
        ItemStack itemStack = new ItemStack(item, count);

        if (nbtString != null) {
            itemStack = NbtHelper.createItemStackWithNBT(itemStack, nbtString, serverLevel.registryAccess());
        }

        ItemEntity itemEntity = new ItemEntity(serverLevel, pos.x(), pos.y(), pos.z(), itemStack);

        if (name != null) {
            itemEntity.setCustomName(Component.translatable(name));
            itemEntity.setCustomNameVisible(nameVisible);
            itemStack.set(DataComponents.CUSTOM_NAME, Component.translatable(name));
        }

        if (desc != null) {
            ItemLore itemLore = new ItemLore(List.of(Component.translatable(desc)));
            itemStack.set(DataComponents.LORE, itemLore);
        }

        itemEntity.setPos(pos);
        serverLevel.addFreshEntity(itemEntity);
    }

    // use in spawn mob
    @Deprecated(forRemoval = true)
    public static void dropItemsByNbt(ServerLevel serverLevel, Vec3 pos, @Nullable String name, boolean nameVisible, @Nullable String desc, @Nullable String nbtString) {
        ItemStack itemStack = new ItemStack(Items.AIR);
        ItemEntity itemEntity = new ItemEntity(serverLevel, pos.x(), pos.y(), pos.z(), itemStack);

        if (nbtString != null) {
            CompoundTag nbt = NbtHelper.generateNbt(nbtString);
            if (nbt == null) return;
            itemEntity.load(nbt);
        }

        if (name != null) {
            itemEntity.setCustomName(Component.translatable(name));
            itemEntity.setCustomNameVisible(nameVisible);
            itemEntity.getItem().set(DataComponents.CUSTOM_NAME, Component.translatable(name));
        }

        if (desc != null) {
            ItemLore itemLore = new ItemLore(List.of(Component.translatable(desc)));
            itemEntity.getItem().set(DataComponents.LORE, itemLore);
        }

        itemEntity.setPos(pos);
        serverLevel.addFreshEntity(itemEntity);
    }

    public static void placeBlock(ServerLevel serverLevel, Vec3 pos, String blockId) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
        BlockState blockState = block.defaultBlockState();
        BlockPos blockPos = PosHelper.parseVec3d(pos);
        serverLevel.setBlockAndUpdate(blockPos, blockState);
    }

    public static void placeChest(ServerLevel serverLevel, BlockPos blockPos, String chestBlockId, String lootTableId, long seed) {
        Block chestBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(chestBlockId));
        BlockState chestBlockState = chestBlock.defaultBlockState();
        serverLevel.setBlockAndUpdate(blockPos, chestBlockState);
        BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
        if (blockEntity instanceof RandomizableContainer lootableInventory) {
            ResourceKey<LootTable> lootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(lootTableId));
            lootableInventory.setLootTable(lootTable);
            lootableInventory.setLootTableSeed(seed);
        }
    }

    public static void dropLoots(ServerLevel serverLevel, Vec3 pos, String lootTableId, long seed) {
        ResourceKey<LootTable> lootTableResourceKey = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(lootTableId));
        LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(lootTableResourceKey);
        LootParams.Builder lootparams$builder = new LootParams.Builder(serverLevel).withParameter(LootContextParams.ORIGIN, pos);
        ObjectArrayList<ItemStack> itemStacks = lootTable.getRandomItems(lootparams$builder.create(LootContextParamSets.COMMAND), seed);

        for (ItemStack itemStack : itemStacks) {
            ItemEntity itemEntity = new ItemEntity(serverLevel, pos.x(), pos.y(), pos.z(), itemStack);
            itemEntity.setPos(pos);
            serverLevel.addFreshEntity(itemEntity);
        }
    }

    public static void fallBlock(ServerLevel serverLevel, BlockPos blockPos, String blockId, Vec3 velocity) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
        BlockState blockState = block.defaultBlockState();
        FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(serverLevel, blockPos, blockState);
        fallingBlockEntity.push(velocity);
    }

    @Deprecated(forRemoval = true)
    public static void spawnMob(ServerLevel serverLevel, Vec3 pos, String entityId, boolean randomize, @Nullable String name, boolean nameVisible, boolean isBaby, Vec3 velocity, @Nullable String nbtString) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityId));
        Entity entity = entityType.create(serverLevel);
        if (entity == null) return;

        if (randomize && entity instanceof Mob mobEntity) {
            mobEntity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(PosHelper.parseVec3d(pos)), MobSpawnType.NATURAL, null);
        }

        CompoundTag nbt = NbtHelper.generateNbt(nbtString);
        if (nbt != null) {
            entity.load(nbt);
        }

        if (entity instanceof Mob mobEntity) {
            mobEntity.setBaby(isBaby);
        }

        if (name != null) {
            entity.setCustomName(Component.translatableEscape(name));
            entity.setCustomNameVisible(nameVisible);
        }

        entity.setPos(pos);
        entity.push(velocity);
        serverLevel.addFreshEntity(entity);
    }

    public static void spawnMob(ServerLevel serverLevel, Vec3 pos, String entityId, boolean randomize, @Nullable String name, boolean nameVisible, boolean isBaby, Vec3 velocity, @Nullable String nbtString, @Nullable String vehicleId, @Nullable String vehicleNbtString) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityId));
        Entity entity = entityType.create(serverLevel);
        if (entity == null) return;

        if (randomize && entity instanceof Mob mobEntity) {
            mobEntity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(PosHelper.parseVec3d(pos)), MobSpawnType.NATURAL, null);
        }

        CompoundTag nbt = NbtHelper.generateNbt(nbtString);
        if (nbt != null) {
            entity.load(nbt);
        }

        if (entity instanceof Mob mobEntity) {
            mobEntity.setBaby(isBaby);
        }

        if (name != null) {
            entity.setCustomName(Component.translatableEscape(name));
            entity.setCustomNameVisible(nameVisible);
        }

        entity.setPos(pos);
        entity.push(velocity);

        if (vehicleId != null) {
            EntityType<?> vehicleEntityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(vehicleId));
            Entity vehicle = vehicleEntityType.create(serverLevel);
            if (vehicle != null) {
                CompoundTag vehicleNbt = NbtHelper.generateNbt(vehicleNbtString);
                if (vehicleNbt != null) {
                    vehicle.load(vehicleNbt);
                }

                if (randomize && vehicle instanceof Mob mobVehicleEntity) {
                    mobVehicleEntity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(PosHelper.parseVec3d(pos)), MobSpawnType.NATURAL, null);
                }

                vehicle.setPos(pos);
                vehicle.push(velocity);

                serverLevel.addFreshEntity(vehicle);
                entity.startRiding(vehicle, true);
            }
        }

        serverLevel.addFreshEntity(entity);
    }

    public static void createExplosion(ServerLevel serverLevel, Vec3 pos, float power, boolean createFire) {
        serverLevel.explode(null, pos.x(), pos.y(), pos.z(), power, createFire, ServerLevel.ExplosionInteraction.BLOCK);
    }

    public static void sendMessage(Player player, String message) {
        player.sendSystemMessage(Component.translatableEscape(message));
    }

    public static void displayClientMessage(Player player, String message, boolean overlay) {
        player.displayClientMessage(Component.translatableEscape(message), overlay);
    }

    public static void givePotionEffect(Player player, String effectId, int duration, int amplifier) {
        Optional<Holder.Reference<MobEffect>> effectOptional = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(effectId));
        if (effectOptional.isPresent()) {
            Holder.Reference<MobEffect> effect = effectOptional.get();
            if (player != null) {
                player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true));
            }
        }
    }

    public static void addParticles(ServerLevel serverLevel, String particleSpec, Vec3 pos, int count, double deltaX, double deltaY, double deltaZ, double speed) {
        try {
            String formattedParticleSpec = particleSpec.replace("%pos_x%", Double.toString(pos.x())).replace("%pos_y%", Double.toString(pos.y())).replace("%pos_z%", Double.toString(pos.z()));
            ParticleOptions particleOptions = NbtHelper.parseParticleOptions(serverLevel.registryAccess(), formattedParticleSpec);
            serverLevel.sendParticles(particleOptions, pos.x, pos.y, pos.z, count, deltaX, deltaY, deltaZ, speed);
        } catch (CommandSyntaxException e) {
            Constants.LOG.error("Invalid particle specification '{}': {}", particleSpec, e.getMessage());
        } catch (Exception e) {
            Constants.LOG.error("Failed to send particles: {}", e.getMessage());
        }
    }

    public static void playSound(Entity entity, ServerLevel serverLevel, Vec3 pos, String soundId, float volume, float pitch) {
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(soundId));
        if (soundEvent != null) {
            serverLevel.playSound(entity, PosHelper.parseVec3d(pos), soundEvent, SoundSource.BLOCKS, volume, pitch);
        } else {
            Constants.LOG.error("soundEvent is null! soundId: {}.", soundId);
        }
    }

    public static void loadStructure(ServerLevel serverLevel, BlockPos pos, String modId, String structureName) {
        StructureTemplateManager manager = serverLevel.getStructureManager();
        ResourceLocation structureId = ResourceLocation.fromNamespaceAndPath(modId, structureName);

        try {
            Optional<StructureTemplate> template = manager.get(structureId);

            if (template.isPresent()) {
                StructureTemplate structure = template.get();
                StructurePlaceSettings placement = new StructurePlaceSettings()
                        .setIgnoreEntities(false)
                        .setKnownShape(true)
                        .setRotation(Rotation.NONE)
                        .setMirror(Mirror.NONE);

                structure.placeInWorld(serverLevel, pos, pos, placement, serverLevel.getRandom(), 3);
            } else {
                Constants.LOG.error("template.isEmpty()");
            }
        } catch (Exception e) {
            Constants.LOG.error("Catch error when loading structure: ", e);
        }
    }

    public static void executeCommand(ServerLevel serverLevel, Vec3 pos, String command) {
        MinecartCommandBlock cBMinecart = new MinecartCommandBlock(serverLevel, pos.x(), pos.y(), pos.z());
        cBMinecart.setCustomName(Component.translatable(Constants.MOD_ID));
        cBMinecart.getCommandBlock().setCommand(command);
        cBMinecart.setPos(pos.x(), pos.y(), pos.z());
        serverLevel.addFreshEntity(cBMinecart);
        cBMinecart.getCommandBlock().performCommand(serverLevel);
        cBMinecart.discard();
    }
}
