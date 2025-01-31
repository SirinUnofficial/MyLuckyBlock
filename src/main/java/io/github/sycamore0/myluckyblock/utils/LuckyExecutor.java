package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class LuckyExecutor {
    public static void executeLuckyFunction(World world, PlayerEntity player, BlockPos blockPos, LuckyEventReader function) {
        // Drop Items
        if (function.hasDropItems()) {
            for (LuckyEventReader.DropItem dropItem : function.getDropItems()) {
                int posSrc = dropItem.getPosSrc();
                Vec3d dropItemPos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                double x, y, z;
                switch (posSrc) {
                    case 0:
                        x = dropItemPos.getX() + dropItem.getOffset().getX();
                        y = dropItemPos.getY() + dropItem.getOffset().getY();
                        z = dropItemPos.getZ() + dropItem.getOffset().getZ();
                        dropItemPos = new Vec3d(x, y, z);
                        break;
                    case 1:
                        x = playerPos.getX() + dropItem.getOffset().getX();
                        y = playerPos.getY() + dropItem.getOffset().getY();
                        z = playerPos.getZ() + dropItem.getOffset().getZ();
                        dropItemPos = new Vec3d(x, y, z);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: DropItems Invalid Pos Src: {}", posSrc);
                        break;
                }

                boolean isUseRandom = dropItem.isUseRandom();
                int count;
                if (isUseRandom) {
                    int min = dropItem.getRandomNum().getMin();
                    int max = dropItem.getRandomNum().getMax();
                    count = getRandomNumber(min, max);
                } else {
                    count = dropItem.getNum();
                }

                Item itemId = Registries.ITEM.get(Identifier.of(dropItem.getId()));
                ItemStack itemStack = new ItemStack(itemId, count);

                if (itemId == Items.AIR) {
                    MyLuckyBlock.LOGGER.warn("Error: DropItems Invalid Item ID: {}", dropItem.getId());
                }
                LuckyFunctions.dropItems(world, dropItemPos, itemStack);
            }
        }

        // Place Blocks
        if (function.hasPlaceBlocks()) {
            for (LuckyEventReader.PlaceBlock placeBlock : function.getPlaceBlocks()) {
                int posSrc = placeBlock.getPosSrc();
                Vec3d placeBlockPos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                double x, y, z;
                switch (posSrc) {
                    case 0:
                        x = placeBlockPos.getX() + placeBlock.getOffset().getX();
                        y = placeBlockPos.getY() + placeBlock.getOffset().getY();
                        z = placeBlockPos.getZ() + placeBlock.getOffset().getZ();
                        placeBlockPos = new Vec3d(x, y, z);
                        break;
                    case 1:
                        x = playerPos.getX() + placeBlock.getOffset().getX();
                        y = playerPos.getY() + placeBlock.getOffset().getY();
                        z = playerPos.getZ() + placeBlock.getOffset().getZ();
                        placeBlockPos = new Vec3d(x, y, z);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: PlaceBlocks Invalid Pos Src: {}", posSrc);
                        break;
                }

                Block blockId = Registries.BLOCK.get(Identifier.of(placeBlock.getId()));
                LuckyFunctions.placeBlock(world, PosHelper.parseVec3d(placeBlockPos), blockId);
            }
        }

        // Place Chests
        if (function.hasPlaceChests()) {
            for (LuckyEventReader.PlaceChest placeChest : function.getPlaceChests()) {
                int posSrc = placeChest.getPosSrc();
                Vec3d placeChestPos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                double x, y, z;
                switch (posSrc) {
                    case 0:
                        x = placeChestPos.getX() + placeChest.getOffset().getX();
                        y = placeChestPos.getY() + placeChest.getOffset().getY();
                        z = placeChestPos.getZ() + placeChest.getOffset().getZ();
                        placeChestPos = new Vec3d(x, y, z);
                        break;
                    case 1:
                        x = playerPos.getX() + placeChest.getOffset().getX();
                        y = playerPos.getY() + placeChest.getOffset().getY();
                        z = playerPos.getZ() + placeChest.getOffset().getZ();
                        placeChestPos = new Vec3d(x, y, z);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: PlaceChests Invalid Pos Src: {}", posSrc);
                        break;
                }

                int type = placeChest.getType();
                String lootTableId = placeChest.getId();

                RegistryKey<LootTable> lootTable = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(lootTableId));
                LuckyFunctions.placeChest(world, PosHelper.parseVec3d(placeChestPos), type, lootTable);
            }
        }

        // Fall Blocks
        if (function.hasFallBlocks()) {
            for (LuckyEventReader.FallBlock fallBlock : function.getFallBlocks()) {
                int posSrc = fallBlock.getPosSrc();
                Vec3d fallBlockPos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                double x, y, z;
                switch (posSrc) {
                    case 0:
                        x = fallBlockPos.getX() + fallBlock.getOffset().getX();
                        y = fallBlockPos.getY() + fallBlock.getOffset().getY();
                        z = fallBlockPos.getZ() + fallBlock.getOffset().getZ();
                        fallBlockPos = new Vec3d(x, y, z);
                        break;
                    case 1:
                        x = playerPos.getX() + fallBlock.getOffset().getX();
                        y = playerPos.getY() + fallBlock.getOffset().getY();
                        z = playerPos.getZ() + fallBlock.getOffset().getZ();
                        fallBlockPos = new Vec3d(x, y, z);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: FallBlocks Invalid Pos Src: {}", posSrc);
                        break;
                }

                Block blockId = Registries.BLOCK.get(Identifier.of(fallBlock.getId()));
                LuckyFunctions.fallBlock(world, PosHelper.parseVec3d(fallBlockPos), blockId);
            }
        }

        // Give Potion Effects
        if (function.hasGivePotionEffects()) {
            for (LuckyEventReader.GivePotionEffect givePotionEffect : function.getGivePotionEffects()) {
                int amplifier = givePotionEffect.getAmplifier();
                int duration = givePotionEffect.getDuration();
                RegistryEntry.Reference<StatusEffect> effect = Registries.STATUS_EFFECT.getEntry(Identifier.of(givePotionEffect.getId())).orElseThrow();
                LuckyFunctions.givePotionEffect(player, effect, duration, amplifier);
            }
        }

        // Spawn Mobs
        if (function.hasSpawnMobs()) {
            for (LuckyEventReader.SpawnMob spawnMob : function.getSpawnMobs()) {
                EntityType<?> entityType = Registries.ENTITY_TYPE.get(Identifier.of(spawnMob.getId()));
                String name = spawnMob.getName();
                boolean nameVisible = spawnMob.isNameVisible();
                boolean isUseRandom = spawnMob.isUseRandom();
                boolean isBaby = spawnMob.isBaby();
                String nbtString = spawnMob.getNbt();
                int posSrc = spawnMob.getPosSrc();
                int count;
                if (isUseRandom) {
                    count = getRandomNumber(spawnMob.getRandomNum().getMin(), spawnMob.getRandomNum().getMax());
                } else {
                    count = spawnMob.getNum();
                }
                for (int i = 0; i < count; i++) {
                    Vec3d spawnMobPos = PosHelper.parseBlockPos(blockPos);
                    Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                    double x, y, z;
                    switch (posSrc) {
                        case 0:
                            x = spawnMobPos.getX() + spawnMob.getOffset().getX();
                            y = spawnMobPos.getY() + spawnMob.getOffset().getY();
                            z = spawnMobPos.getZ() + spawnMob.getOffset().getZ();
                            spawnMobPos = new Vec3d(x, y, z);
                            break;
                        case 1:
                            x = playerPos.getX() + spawnMob.getOffset().getX();
                            y = playerPos.getY() + spawnMob.getOffset().getY();
                            z = playerPos.getZ() + spawnMob.getOffset().getZ();
                            spawnMobPos = new Vec3d(x, y, z);
                            break;
                        default:
                            MyLuckyBlock.LOGGER.error("Error: SpawnMobs Invalid Pos Src: {}", posSrc);
                            break;
                    }

                    if (entityType == EntityType.ITEM) {
                        LuckyFunctions.dropItemsByNbt(world, spawnMobPos, name, nameVisible, nbtString);
                    }
                    else if (entityType == EntityType.LIGHTNING_BOLT) {
                        LuckyFunctions.spawnLightning(world, spawnMobPos);
                    }
                    else if (entityType == EntityType.FIREBALL) {
                        LuckyFunctions.spawnFireball(world, spawnMobPos, nbtString);
                    }
                    else {
                        if (nbtString != null) {
                            LuckyFunctions.spawnMob(world, spawnMobPos, entityType, name, nameVisible, nbtString);
                        } else {
                            LuckyFunctions.spawnMob(world, spawnMobPos, entityType, name, nameVisible, isBaby);
                        }
                    }
                }
            }
        }

        // Send Messages
        if (function.hasSendMessages()) {
            for (LuckyEventReader.SendMessage sendMessage : function.getSendMessages()) {
                LuckyFunctions.sendMessage(player, sendMessage.getMsg(), sendMessage.getReceiver());
            }
        }

        // Create Explosions
        if (function.hasCreateExplosions()) {
            for (LuckyEventReader.CreateExplosion createExplosion : function.getCreateExplosions()) {
                int posSrc = createExplosion.getPosSrc();
                Vec3d createExplosionPos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                double x, y, z;
                switch (posSrc) {
                    case 0:
                        x = createExplosionPos.getX() + createExplosion.getOffset().getX();
                        y = createExplosionPos.getY() + createExplosion.getOffset().getY();
                        z = createExplosionPos.getZ() + createExplosion.getOffset().getZ();
                        createExplosionPos = new Vec3d(x, y, z);
                        break;
                    case 1:
                        x = playerPos.getX() + createExplosion.getOffset().getX();
                        y = playerPos.getY() + createExplosion.getOffset().getY();
                        z = playerPos.getZ() + createExplosion.getOffset().getZ();
                        createExplosionPos = new Vec3d(x, y, z);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: CreateExplosions Invalid Pos Src: {}", posSrc);
                        break;
                }

                float power = createExplosion.getPower();
                LuckyFunctions.createExplosion(world, createExplosionPos, power);
            }
        }

        // Add Particles
        if (function.hasAddParticles()) {
            for (LuckyEventReader.AddParticle addParticle : function.getAddParticles()) {
                int posSrc = addParticle.getPosSrc();
                Vec3d addParticlePos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                double x, y, z;
                switch (posSrc) {
                    case 0:
                        x = addParticlePos.getX() + addParticle.getOffset().getX();
                        y = addParticlePos.getY() + addParticle.getOffset().getY();
                        z = addParticlePos.getZ() + addParticle.getOffset().getZ();
                        addParticlePos = new Vec3d(x, y, z);
                        break;
                    case 1:
                        x = playerPos.getX() + addParticle.getOffset().getX();
                        y = playerPos.getY() + addParticle.getOffset().getY();
                        z = playerPos.getZ() + addParticle.getOffset().getZ();
                        addParticlePos = new Vec3d(x, y, z);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: AddParticles Invalid Pos Src: {}", posSrc);
                        break;
                }

                ParticleType<?> particleType = Registries.PARTICLE_TYPE.get(Identifier.of(addParticle.getId()));
                ParticleEffect particle = (ParticleEffect) particleType;
                int count = addParticle.getCount();
                double speed = addParticle.getSpeed();

                LuckyFunctions.addParticles(world, particle, addParticlePos, count, addParticle.getVelocity().getX(), addParticle.getVelocity().getY(), addParticle.getVelocity().getZ(), speed);
            }
        }

        // Load Structures
        if (function.hasLoadStructures()) {
            for (LuckyEventReader.LoadStructure loadStructure : function.getLoadStructures()) {
                int posSrc = loadStructure.getPosSrc();
                Vec3d loadStructurePos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                double x, y, z;
                switch (posSrc) {
                    case 0:
                        x = loadStructurePos.getX() + loadStructure.getOffset().getX();
                        y = loadStructurePos.getY() + loadStructure.getOffset().getY();
                        z = loadStructurePos.getZ() + loadStructure.getOffset().getZ();
                        loadStructurePos = new Vec3d(x, y, z);
                        break;
                    case 1:
                        x = playerPos.getX() + loadStructure.getOffset().getX();
                        y = playerPos.getY() + loadStructure.getOffset().getY();
                        z = playerPos.getZ() + loadStructure.getOffset().getZ();
                        loadStructurePos = new Vec3d(x, y, z);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: LoadStructures Invalid Pos Src: {}", posSrc);
                        break;
                }
                LuckyFunctions.loadStructure(world, PosHelper.parseVec3d(loadStructurePos), loadStructure.getId());
            }
        }

        // Execute Commands
        if (function.hasExecuteCommands()) {
            for (LuckyEventReader.ExecuteCommand executeCommand: function.getExecuteCommands()) {
                int posSrc = executeCommand.getPosSrc();
                Vec3d loadStructurePos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                double x, y, z;
                switch (posSrc) {
                    case 0:
                        x = loadStructurePos.getX() + executeCommand.getOffset().getX();
                        y = loadStructurePos.getY() + executeCommand.getOffset().getY();
                        z = loadStructurePos.getZ() + executeCommand.getOffset().getZ();
                        loadStructurePos = new Vec3d(x, y, z);
                        break;
                    case 1:
                        x = playerPos.getX() + executeCommand.getOffset().getX();
                        y = playerPos.getY() + executeCommand.getOffset().getY();
                        z = playerPos.getZ() + executeCommand.getOffset().getZ();
                        loadStructurePos = new Vec3d(x, y, z);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: ExecuteCommands Invalid Pos Src: {}", posSrc);
                        break;
                }
                LuckyFunctions.executeCommand(world, loadStructurePos, executeCommand.getCommand());
            }
        }
    }

    private static int getRandomNumber(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }
}
