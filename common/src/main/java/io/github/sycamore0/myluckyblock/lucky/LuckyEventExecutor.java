package io.github.sycamore0.myluckyblock.lucky;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import io.github.sycamore0.myluckyblock.lucky.reader.RandomEventDataReader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class LuckyEventExecutor {
    public static void executeLuckyFunction(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        try {
            handleDropItems(serverLevel, player, blockPos, function);
            handlePlaceBlocks(serverLevel, player, blockPos, function);
            handlePlaceChests(serverLevel, player, blockPos, function);
            handleDropLoots(serverLevel, player, blockPos, function);
            handleFallBlocks(serverLevel, player, blockPos, function);
            handleGivePotionEffects(serverLevel, player, blockPos, function);
            handleSpawnMobs(serverLevel, player, blockPos, function);
            handleSendMessages(serverLevel, player, blockPos, function);
            handleDisplayMessages(serverLevel, player, blockPos, function);
            handleCreateExplosions(serverLevel, player, blockPos, function);
            handleAddParticles(serverLevel, player, blockPos, function);
            handlePlaySounds(serverLevel, player, blockPos, function);
            handleLoadStructures(serverLevel, player, blockPos, function);
            handleExecuteCommands(serverLevel, player, blockPos, function);
        } catch (Exception e) {
            Constants.LOG.error("Event {} execution aborted due to fatal error {}", function.getEventId(), e);
        }
    }

    private static void handleDropItems(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasDropItems()) {
            for (RandomEventDataReader.DropItem dropItem : function.getDropItems()) {
                boolean isUseRandom = dropItem.isUseRandom();
                int count;
                if (isUseRandom) {
                    count = getRandomNumber(dropItem.getRandomNum().getMin(), dropItem.getRandomNum().getMax());
                } else {
                    count = dropItem.getNum();
                }

                LuckyEventFunctions.dropItems(serverLevel, resolvePosition(blockPos, player, dropItem.getPosSrc(), dropItem.getOffset()), dropItem.getId(), count, dropItem.getName(), dropItem.isNameVisible(), dropItem.getDesc(), dropItem.getNbt());
            }
        }
    }

    private static void handlePlaceBlocks(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasPlaceBlocks()) {
            for (RandomEventDataReader.PlaceBlock placeBlock : function.getPlaceBlocks()) {
                LuckyEventFunctions.placeBlock(serverLevel, resolvePosition(blockPos, player, placeBlock.getPosSrc(), placeBlock.getOffset()), placeBlock.getId());
            }
        }
    }

    private static void handlePlaceChests(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasPlaceChests()) {
            for (RandomEventDataReader.PlaceChest placeChest : function.getPlaceChests()) {
                LuckyEventFunctions.placeChest(serverLevel, PosHelper.parseVec3d(resolvePosition(blockPos, player, placeChest.getPosSrc(), placeChest.getOffset())), placeChest.getChestId(), placeChest.getId(), placeChest.getSeed());
            }
        }
    }

    private static void handleDropLoots(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasDropLoots()) {
            for (RandomEventDataReader.DropLoots dropLoots : function.getDropLoots()) {
                LuckyEventFunctions.dropLoots(serverLevel, resolvePosition(blockPos, player, dropLoots.getPosSrc(), dropLoots.getOffset()), dropLoots.getId(), dropLoots.getSeed());
            }
        }
    }

    private static void handleFallBlocks(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasFallBlocks()) {
            for (RandomEventDataReader.FallBlock fallBlock : function.getFallBlocks()) {
                LuckyEventFunctions.fallBlock(serverLevel, PosHelper.parseVec3d(resolvePosition(blockPos, player, fallBlock.getPosSrc(), fallBlock.getOffset())), fallBlock.getId(), fallBlock.getVelocity());
            }
        }
    }

    private static void handleGivePotionEffects(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasGivePotionEffects()) {
            for (RandomEventDataReader.GivePotionEffect givePotionEffect : function.getGivePotionEffects()) {
                if (player != null) {
                    LuckyEventFunctions.givePotionEffect(player, givePotionEffect.getId(), givePotionEffect.getDuration(), givePotionEffect.getAmplifier());
                }
            }
        }
    }

    private static void handleSpawnMobs(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasSpawnMobs()) {
            for (RandomEventDataReader.SpawnMob spawnMob : function.getSpawnMobs()) {
                boolean isUseRandom = spawnMob.isUseRandom();
                int count;
                if (isUseRandom) {
                    count = getRandomNumber(spawnMob.getRandomNum().getMin(), spawnMob.getRandomNum().getMax());
                } else {
                    count = spawnMob.getNum();
                }
                String nbtString = spawnMob.getNbt();

                for (int i = 0; i < Mth.clamp(count, 0, 64); i++) {
                    if (Objects.equals(spawnMob.getId(), "minecraft:item") || Objects.equals(spawnMob.getId(), "item")) {
                        Constants.LOG.warn("dropItemsByNbt is depreciated! Please use drop_items! nbt: {}", nbtString);
                        try {
                            LuckyEventFunctions.dropItems(serverLevel, resolvePosition(blockPos, player, spawnMob.getPosSrc(), spawnMob.getOffset()), spawnMob.getId(), count, spawnMob.getName(), spawnMob.isNameVisible(), spawnMob.getDesc(), nbtString);
                        } catch (Exception e) {
                            Constants.LOG.error("nbt: {}, e: {}", nbtString, e.toString());
                        }
                    } else {
                        LuckyEventFunctions.spawnMob(serverLevel, resolvePosition(blockPos, player, spawnMob.getPosSrc(), spawnMob.getOffset()), spawnMob.getId(), spawnMob.getRandomize(), spawnMob.getName(), spawnMob.isNameVisible(), spawnMob.isBaby(), spawnMob.getVelocity(), nbtString, spawnMob.getVehicleId(), spawnMob.getVehicleNbt());
                    }
                }
            }
        }
    }

    private static void handleSendMessages(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasSendMessages()) {
            for (RandomEventDataReader.SendMessage sendMessage : function.getSendMessages()) {
                if (player != null) {
                    LuckyEventFunctions.sendMessage(player, sendMessage.getMsg());
                }
            }
        }
    }

    private static void handleDisplayMessages(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasDisplayMessages()) {
            for (RandomEventDataReader.DisplayMessage sendMessage : function.getDisplayMessages()) {
                if (player != null) {
                    LuckyEventFunctions.displayClientMessage(player, sendMessage.getMsg(), sendMessage.getOverlay());
                }
            }
        }
    }

    private static void handleCreateExplosions(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasCreateExplosions()) {
            for (RandomEventDataReader.CreateExplosion createExplosion : function.getCreateExplosions()) {
                LuckyEventFunctions.createExplosion(serverLevel, resolvePosition(blockPos, player, createExplosion.getPosSrc(), createExplosion.getOffset()), createExplosion.getPower(), createExplosion.isCreateFire());
            }
        }
    }

    private static void handleAddParticles(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasAddParticles()) {
            for (RandomEventDataReader.AddParticle addParticle : function.getAddParticles()) {
                LuckyEventFunctions.addParticles(serverLevel, addParticle.getId(), resolvePosition(blockPos, player, addParticle.getPosSrc(), addParticle.getOffset()), addParticle.getCount(), addParticle.getDelta().getX(), addParticle.getDelta().getY(), addParticle.getDelta().getZ(), addParticle.getSpeed());
            }
        }
    }

    private static void handlePlaySounds(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasPlaySounds()) {
            for (RandomEventDataReader.PlaySound playSound : function.getPlaySounds()) {
                if (player != null) {
                    LuckyEventFunctions.playSound(player, serverLevel, PosHelper.parseBlockPos(blockPos), playSound.getId(), playSound.getVolume(), playSound.getPitch());
                }
            }
        }
    }

    private static void handleLoadStructures(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasLoadStructures()) {
            for (RandomEventDataReader.LoadStructure loadStructure : function.getLoadStructures()) {
                LuckyEventFunctions.loadStructure(serverLevel, PosHelper.parseVec3d(resolvePosition(blockPos, player, loadStructure.getPosSrc(), loadStructure.getOffset())), loadStructure.getModId(), loadStructure.getId());
            }
        }
    }

    private static void handleExecuteCommands(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        if (function.hasExecuteCommands()) {
            for (RandomEventDataReader.ExecuteCommand executeCommand : function.getExecuteCommands()) {
                LuckyEventFunctions.executeCommand(serverLevel, resolvePosition(blockPos, player, executeCommand.getPosSrc(), executeCommand.getOffset()), executeCommand.getCommand());
            }
        }
    }

    public static int getRandomNumber(int min, int max) {
        try {
            return min + ThreadLocalRandom.current().nextInt(max - min + 1);
        } catch (IllegalArgumentException e) {
            Constants.LOG.error(e.getMessage());
            return 1;
        }
    }

    public static Vec3 resolvePosition(BlockPos blockPos, Player player, PosSrc posSrc, Vec3 offset) {
        Vec3 basePos;
        if (posSrc == PosSrc.PLAYER && player != null) {
            basePos = player.position();
        } else {
            basePos = PosHelper.parseBlockPos(blockPos);
        }
        return PosHelper.calcOffset(basePos, offset);
    }
}
