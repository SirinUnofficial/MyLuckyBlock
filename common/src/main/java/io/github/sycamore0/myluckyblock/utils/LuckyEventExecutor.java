package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import io.github.sycamore0.myluckyblock.utils.reader.RandomEventDataReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class LuckyEventExecutor {
    public static void executeLuckyFunction(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        // Drop Items
        if (function.hasDropItems()) {
            for (RandomEventDataReader.DropItem dropItem : function.getDropItems()) {
                boolean isUseRandom = dropItem.isUseRandom();
                int count;
                if (isUseRandom) {
                    int min = dropItem.getRandomNum().getMin();
                    int max = dropItem.getRandomNum().getMax();
                    count = getRandomNumber(min, max);
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
                ResourceKey<LootTable> lootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(placeChest.getId()));

                Vec3 placeChestPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, placeChest.getOffset(), "PlaceChests");
                if (player != null && placeChest.getPosSrc() != PosSrc.BLOCK) {
                    placeChestPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), placeChest.getPosSrc(), placeChest.getOffset(), "PlaceChests");
                }

                LuckyEventFunctions.placeChest(serverLevel, PosHelper.parseVec3d(placeChestPos), placeChest.getChestId(), lootTable);
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
                    Holder.Reference<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(givePotionEffect.getId())).orElseThrow();
                    LuckyEventFunctions.givePotionEffect(player, effect, givePotionEffect.getDuration(), givePotionEffect.getAmplifier());
                }
            }
        }

        // Spawn Mobs
        if (function.hasSpawnMobs()) {
            for (RandomEventDataReader.SpawnMob spawnMob : function.getSpawnMobs()) {
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(spawnMob.getId()));

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
                    if (entityType == EntityType.ITEM) {
                        LuckyEventFunctions.dropItemsByNbt(serverLevel, spawnMobPos, spawnMob.getName(), spawnMob.isNameVisible(), spawnMob.getDesc(), nbtString);
                    } else {
                        if (nbtString != null) {
                            LuckyEventFunctions.spawnMob(serverLevel, spawnMobPos, entityType, spawnMob.getRandomize(), spawnMob.getName(), spawnMob.isNameVisible(), spawnMob.getVelocity(), nbtString);
                        } else {
                            LuckyEventFunctions.spawnMob(serverLevel, spawnMobPos, entityType, spawnMob.getRandomize(), spawnMob.getName(), spawnMob.isNameVisible(), spawnMob.isBaby(), spawnMob.getVelocity());
                        }
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
                ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(addParticle.getId()));
                ParticleOptions particleOptions;
                if (particleType instanceof SimpleParticleType simpleType) {
                    particleOptions = simpleType;
                }
                else if (particleType instanceof ParticleOptions) {
                    particleOptions = (ParticleOptions) particleType;
                } else {
                    continue;
                }

                Vec3 addParticlePos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(blockPos), PosSrc.BLOCK, addParticle.getOffset(), "AddParticles");
                if (player != null && addParticle.getPosSrc() != PosSrc.BLOCK) {
                    addParticlePos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), addParticle.getPosSrc(), addParticle.getOffset(), "AddParticles");
                }

                LuckyEventFunctions.addParticles(serverLevel, particleOptions, addParticlePos, addParticle.getCount(), addParticle.getVelocity().getX(), addParticle.getVelocity().getY(), addParticle.getVelocity().getZ(), addParticle.getSpeed());
            }
        }

        // Play Sounds
        if (function.hasPlaySounds()) {
            for (RandomEventDataReader.PlaySound playSound : function.getPlaySounds()) {
                if (player != null) {
                    SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(playSound.getId()));
                    LuckyEventFunctions.playSound(player, serverLevel, PosHelper.parseBlockPos(blockPos), soundEvent, playSound.getVolume(), playSound.getPitch());
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
