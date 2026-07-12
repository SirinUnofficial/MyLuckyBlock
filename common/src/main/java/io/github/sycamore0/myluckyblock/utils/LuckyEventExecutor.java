package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import io.github.sycamore0.myluckyblock.utils.reader.RandomEventDataReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Random;

public class LuckyEventExecutor {
    public static void executeLuckyFunction(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        // Drop Items
        if (function.hasDropItems()) {
            for (RandomEventDataReader.DropItem dropItem : function.getDropItems()) {
                boolean isUseRandom = dropItem.isUseRandom();
                int count;
                if (isUseRandom) {
                    count = getRandomNumber(dropItem.getRandomNum().getMin(), dropItem.getRandomNum().getMax());
                } else {
                    count = dropItem.getNum();
                }

                Vec3 dropItemPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, dropItem.getOffset(), "DropItems");
                if (player != null && dropItem.getPosSrc() != PosSrc.BLOCK) {
                    dropItemPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), dropItem.getPosSrc(), dropItem.getOffset(), "DropItems");
                }

                LuckyEventFunctions.dropItems(serverLevel, dropItemPos, dropItem.getId(), count, dropItem.getName(), dropItem.isNameVisible(), dropItem.getDesc(), dropItem.getNbt());
            }
        }

        // Place Blocks
        if (function.hasPlaceBlocks()) {
            for (RandomEventDataReader.PlaceBlock placeBlock : function.getPlaceBlocks()) {
                Vec3 placeBlockPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, placeBlock.getOffset(), "PlaceBlocks");
                if (player != null && placeBlock.getPosSrc() != PosSrc.BLOCK) {
                    placeBlockPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), placeBlock.getPosSrc(), placeBlock.getOffset(), "PlaceBlocks");
                }

                LuckyEventFunctions.placeBlock(serverLevel, placeBlockPos, placeBlock.getId());
            }
        }

        // Place Chests
        if (function.hasPlaceChests()) {
            for (RandomEventDataReader.PlaceChest placeChest : function.getPlaceChests()) {
                Vec3 placeChestPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, placeChest.getOffset(), "PlaceChests");
                if (player != null && placeChest.getPosSrc() != PosSrc.BLOCK) {
                    placeChestPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), placeChest.getPosSrc(), placeChest.getOffset(), "PlaceChests");
                }

                LuckyEventFunctions.placeChest(serverLevel, PosHelper.parseVec3d(placeChestPos), placeChest.getChestId(), placeChest.getId(), placeChest.getSeed());
            }
        }

        // Drop Loots
        if (function.hasDropLoots()) {
            for (RandomEventDataReader.DropLoots dropLoots : function.getDropLoots()) {
                Vec3 dropLootsPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, dropLoots.getOffset(), "PlaceChests");
                if (player != null && dropLoots.getPosSrc() != PosSrc.BLOCK) {
                    dropLootsPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), dropLoots.getPosSrc(), dropLoots.getOffset(), "PlaceChests");
                }

                LuckyEventFunctions.dropLoots(serverLevel, dropLootsPos, dropLoots.getId(), dropLoots.getSeed());
            }
        }

        // Fall Blocks
        if (function.hasFallBlocks()) {
            for (RandomEventDataReader.FallBlock fallBlock : function.getFallBlocks()) {
                Block blockId = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(fallBlock.getId()));

                Vec3 fallBlockPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, fallBlock.getOffset(), "FallBlocks");
                if (player != null && fallBlock.getPosSrc() != PosSrc.BLOCK) {
                    fallBlockPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), fallBlock.getPosSrc(), fallBlock.getOffset(), "FallBlocks");
                }

                LuckyEventFunctions.fallBlock(serverLevel, PosHelper.parseVec3d(fallBlockPos), blockId, fallBlock.getVelocity());
            }
        }

        // Give Potion Effects
        if (function.hasGivePotionEffects()) {
            for (RandomEventDataReader.GivePotionEffect givePotionEffect : function.getGivePotionEffects()) {
                if (player != null) {
                    LuckyEventFunctions.givePotionEffect(player, givePotionEffect.getId(), givePotionEffect.getDuration(), givePotionEffect.getAmplifier());
                }
            }
        }

        // Spawn Mobs
        if (function.hasSpawnMobs()) {
            for (RandomEventDataReader.SpawnMob spawnMob : function.getSpawnMobs()) {
                boolean isUseRandom = spawnMob.isUseRandom();
                int count;
                if (isUseRandom) {
                    count = getRandomNumber(spawnMob.getRandomNum().getMin(), spawnMob.getRandomNum().getMax());
                } else {
                    count = spawnMob.getNum();
                }

                Vec3 spawnMobPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, spawnMob.getOffset(), "SpawnMobs");
                if (player != null && spawnMob.getPosSrc() != PosSrc.BLOCK) {
                    spawnMobPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), spawnMob.getPosSrc(), spawnMob.getOffset(), "SpawnMobs");
                }

                String nbtString = spawnMob.getNbt();

                for (int i = 0; i < count; i++) {
                    if (Objects.equals(spawnMob.getId(), "minecraft:item")) {
                        Constants.LOG.warn("dropItemsByNbt is depreciated! Please use drop_items! nbt: {}", nbtString);
                        try {
                            LuckyEventFunctions.dropItems(serverLevel, spawnMobPos, spawnMob.getId(), count, spawnMob.getName(), spawnMob.isNameVisible(), spawnMob.getDesc(), nbtString);
                        } catch (Exception e) {
                            Constants.LOG.error("nbt: {}, e: {}", nbtString, e.toString());
                        }
                    } else {
                        LuckyEventFunctions.spawnMob(serverLevel, spawnMobPos, spawnMob.getId(), spawnMob.getRandomize(), spawnMob.getName(), spawnMob.isNameVisible(), spawnMob.isBaby(), spawnMob.getVelocity(), nbtString);
                    }
                }
            }
        }

        // Send Messages
        if (function.hasSendMessages()) {
            for (RandomEventDataReader.SendMessage sendMessage : function.getSendMessages()) {
                if (player != null) {
                    LuckyEventFunctions.sendMessage(player, sendMessage.getMsg());
                }
            }
        }

        // Display Messages
        if (function.hasDisplayMessages()) {
            for (RandomEventDataReader.DisplayMessage sendMessage : function.getDisplayMessages()) {
                if (player != null) {
                    LuckyEventFunctions.displayClientMessage(player, sendMessage.getMsg(), sendMessage.getOverlay());
                }
            }
        }

        // Create Explosions
        if (function.hasCreateExplosions()) {
            for (RandomEventDataReader.CreateExplosion createExplosion : function.getCreateExplosions()) {
                Vec3 createExplosionPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, createExplosion.getOffset(), "CreateExplosions");
                if (player != null && createExplosion.getPosSrc() != PosSrc.BLOCK) {
                    createExplosionPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), createExplosion.getPosSrc(), createExplosion.getOffset(), "CreateExplosions");
                }

                LuckyEventFunctions.createExplosion(serverLevel, createExplosionPos, createExplosion.getPower(), createExplosion.isCreateFire());
            }
        }

        // Add Particles
        if (function.hasAddParticles()) {
            for (RandomEventDataReader.AddParticle addParticle : function.getAddParticles()) {
                Vec3 addParticlePos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, addParticle.getOffset(), "AddParticles");
                if (player != null && addParticle.getPosSrc() != PosSrc.BLOCK) {
                    addParticlePos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), addParticle.getPosSrc(), addParticle.getOffset(), "AddParticles");
                }

                LuckyEventFunctions.addParticles(serverLevel, addParticle.getId(), addParticlePos, addParticle.getCount(), addParticle.getVelocity().getX(), addParticle.getVelocity().getY(), addParticle.getVelocity().getZ(), addParticle.getSpeed());
            }
        }

        // Play Sounds
        if (function.hasPlaySounds()) {
            for (RandomEventDataReader.PlaySound playSound : function.getPlaySounds()) {
                if (player != null) {
                    LuckyEventFunctions.playSound(player, serverLevel, PosHelper.parseBlockPos(blockPos), playSound.getId(), playSound.getVolume(), playSound.getPitch());
                }
            }
        }

        // Load Structures
        if (function.hasLoadStructures()) {
            for (RandomEventDataReader.LoadStructure loadStructure : function.getLoadStructures()) {
                Vec3 loadStructurePos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, loadStructure.getOffset(), "LoadStructures");
                if (player != null && loadStructure.getPosSrc() != PosSrc.BLOCK) {
                    loadStructurePos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), loadStructure.getPosSrc(), loadStructure.getOffset(), "LoadStructures");
                }

                LuckyEventFunctions.loadStructure(serverLevel, PosHelper.parseVec3d(loadStructurePos), loadStructure.getModId(), loadStructure.getId());
            }
        }

        // Execute Commands
        if (function.hasExecuteCommands()) {
            for (RandomEventDataReader.ExecuteCommand executeCommand : function.getExecuteCommands()) {
                Vec3 executeCommandPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, executeCommand.getOffset(), "ExecuteCommands");
                if (player != null && executeCommand.getPosSrc() != PosSrc.BLOCK) {
                    executeCommandPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), executeCommand.getPosSrc(), executeCommand.getOffset(), "ExecuteCommands");
                }

                LuckyEventFunctions.executeCommand(serverLevel, executeCommandPos, executeCommand.getCommand());
            }
        }
    }

    private static int getRandomNumber(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }
}
