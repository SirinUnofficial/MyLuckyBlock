package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
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
                // Get ItemStack
                String itemId = dropItem.getId();
                boolean isUseRandom = dropItem.isUseRandom();
                int count;
                if (isUseRandom) {
                    int min = dropItem.getRandomNum().getMin();
                    int max = dropItem.getRandomNum().getMax();
                    count = getRandomNumber(min, max);
                } else {
                    count = dropItem.getNum();
                }

                // Get Position
                int posSrc = dropItem.getPosSrc();
                Vec3d dropItemPos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                Vec3d offset = dropItem.getOffset();
                switch (posSrc) {
                    case 0:
                        dropItemPos = PosHelper.calcOffset(dropItemPos, offset);
                        break;
                    case 1:
                        dropItemPos = PosHelper.calcOffset(playerPos, offset);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: DropItems Invalid Pos Src: {}", posSrc);
                        break;
                }

                // Get NBT
                String nbtString = dropItem.getNbt();

                LuckyFunctions.dropItems(world, dropItemPos, itemId, count, nbtString);
            }
        }

        // Place Blocks
        if (function.hasPlaceBlocks()) {
            for (LuckyEventReader.PlaceBlock placeBlock : function.getPlaceBlocks()) {
                // Get Block ID
                String blockId = placeBlock.getId();

                // Get Position
                int posSrc = placeBlock.getPosSrc();
                Vec3d placeBlockPos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                Vec3d offset = placeBlock.getOffset();
                switch (posSrc) {
                    case 0:
                        placeBlockPos = PosHelper.calcOffset(placeBlockPos, offset);
                        break;
                    case 1:
                        placeBlockPos = PosHelper.calcOffset(playerPos, offset);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: PlaceBlocks Invalid Pos Src: {}", posSrc);
                        break;
                }

                LuckyFunctions.placeBlock(world, placeBlockPos, blockId);
            }
        }

        // Place Chests
        if (function.hasPlaceChests()) {
            for (LuckyEventReader.PlaceChest placeChest : function.getPlaceChests()) {
                int posSrc = placeChest.getPosSrc();
                Vec3d placeChestPos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                Vec3d offset = placeChest.getOffset();
                switch (posSrc) {
                    case 0:
                        placeChestPos = PosHelper.calcOffset(placeChestPos, offset);
                        break;
                    case 1:
                        placeChestPos = PosHelper.calcOffset(playerPos, offset);
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
                Vec3d velocity = fallBlock.getVelocity();
                Vec3d offset = fallBlock.getOffset();
                switch (posSrc) {
                    case 0:
                        fallBlockPos = PosHelper.calcOffset(fallBlockPos, offset);
                        break;
                    case 1:
                        fallBlockPos = PosHelper.calcOffset(playerPos, offset);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: FallBlocks Invalid Pos Src: {}", posSrc);
                        break;
                }

                Block blockId = Registries.BLOCK.get(Identifier.of(fallBlock.getId()));
                LuckyFunctions.fallBlock(world, PosHelper.parseVec3d(fallBlockPos), blockId, velocity);
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
                String mobId = spawnMob.getId();
                EntityType<?> entityType = Registries.ENTITY_TYPE.get(Identifier.of(mobId));

                String name = spawnMob.getName();
                boolean nameVisible = spawnMob.isNameVisible();

                boolean isBaby = spawnMob.isBaby();
                String nbtString = spawnMob.getNbt();

                boolean isUseRandom = spawnMob.isUseRandom();
                int count;
                if (isUseRandom) {
                    count = getRandomNumber(spawnMob.getRandomNum().getMin(), spawnMob.getRandomNum().getMax());
                } else {
                    count = spawnMob.getNum();
                }

                int posSrc = spawnMob.getPosSrc();
                Vec3d spawnMobPos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                Vec3d offset = spawnMob.getOffset();
                switch (posSrc) {
                    case 0:
                        spawnMobPos = PosHelper.calcOffset(spawnMobPos, offset);
                        break;
                    case 1:
                        spawnMobPos = PosHelper.calcOffset(playerPos, offset);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: SpawnMobs Invalid Pos Src: {}", posSrc);
                        break;
                }

                Vec3d velocity = spawnMob.getVelocity();

                for (int i = 0; i < count; i++) {
                    if (entityType == EntityType.ITEM) {
                        LuckyFunctions.dropItemsByNbt(world, spawnMobPos, name, nameVisible, nbtString);
                    }
                    else {
                        if (nbtString != null) {
                            LuckyFunctions.spawnMob(world, spawnMobPos, entityType, name, nameVisible, nbtString, velocity);
                        } else {
                            LuckyFunctions.spawnMob(world, spawnMobPos, entityType, name, nameVisible, isBaby, velocity);
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
                // Get Position
                int posSrc = createExplosion.getPosSrc();
                Vec3d createExplosionPos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                Vec3d offset = createExplosion.getOffset();
                switch (posSrc) {
                    case 0:
                        createExplosionPos = PosHelper.calcOffset(createExplosionPos, offset);
                        break;
                    case 1:
                        createExplosionPos = PosHelper.calcOffset(playerPos, offset);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: CreateExplosions Invalid Pos Src: {}", posSrc);
                        break;
                }

                float power = createExplosion.getPower();
                boolean createFire = createExplosion.isCreateFire();

                LuckyFunctions.createExplosion(world, createExplosionPos, power, createFire);
            }
        }

        // Add Particles
        if (function.hasAddParticles()) {
            for (LuckyEventReader.AddParticle addParticle : function.getAddParticles()) {
                ParticleType<?> particleType = Registries.PARTICLE_TYPE.get(Identifier.of(addParticle.getId()));
                ParticleEffect particle = (ParticleEffect) particleType;

                // Get Position
                int posSrc = addParticle.getPosSrc();
                Vec3d addParticlePos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                Vec3d offset = addParticle.getOffset();
                switch (posSrc) {
                    case 0:
                        addParticlePos = PosHelper.calcOffset(addParticlePos, offset);
                        break;
                    case 1:
                        addParticlePos = PosHelper.calcOffset(playerPos, offset);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: AddParticles Invalid Pos Src: {}", posSrc);
                        break;
                }

                int count = addParticle.getCount();
                double speed = addParticle.getSpeed();

                LuckyFunctions.addParticles(world, particle, addParticlePos, count, addParticle.getVelocity().getX(), addParticle.getVelocity().getY(), addParticle.getVelocity().getZ(), speed);
            }
        }

        // Load Structures
        if (function.hasLoadStructures()) {
            for (LuckyEventReader.LoadStructure loadStructure : function.getLoadStructures()) {
                // Get Structure ID
                String modId = loadStructure.getModId();
                String structureId = loadStructure.getId();

                // Get Position
                int posSrc = loadStructure.getPosSrc();
                Vec3d loadStructurePos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                Vec3d offset = loadStructure.getOffset();
                switch (posSrc) {
                    case 0:
                        loadStructurePos = PosHelper.calcOffset(loadStructurePos, offset);
                        break;
                    case 1:
                        loadStructurePos = PosHelper.calcOffset(playerPos, offset);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: LoadStructures Invalid Pos Src: {}", posSrc);
                        break;
                }

                LuckyFunctions.loadStructure(world, PosHelper.parseVec3d(loadStructurePos), modId, structureId);
            }
        }

        // Execute Commands
        if (function.hasExecuteCommands()) {
            for (LuckyEventReader.ExecuteCommand executeCommand: function.getExecuteCommands()) {
                // Get Command
                String command = executeCommand.getCommand();

                // Get Position
                int posSrc = executeCommand.getPosSrc();
                Vec3d loadStructurePos = PosHelper.parseBlockPos(blockPos);
                Vec3d playerPos = PosHelper.parseBlockPos(player.getBlockPos());
                Vec3d offset = executeCommand.getOffset();
                switch (posSrc) {
                    case 0:
                        loadStructurePos = PosHelper.calcOffset(loadStructurePos, offset);
                        break;
                    case 1:
                        loadStructurePos = PosHelper.calcOffset(playerPos, offset);
                        break;
                    default:
                        MyLuckyBlock.LOGGER.error("Error: ExecuteCommands Invalid Pos Src: {}", posSrc);
                        break;
                }

                LuckyFunctions.executeCommand(world, loadStructurePos, command);
            }
        }
    }

    private static int getRandomNumber(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }
}
