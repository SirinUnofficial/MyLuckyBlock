package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class LuckyEventExecutor {
    public static void executeLuckyFunction(Level level, Player player, BlockPos blockPos, RandomEventReader function) {
        // Drop Items
        if (function.hasDropItems()) {
            for (RandomEventReader.DropItem dropItem : function.getDropItems()) {
                // Get ItemStack
                String itemId = dropItem.getId();

                String name = dropItem.getName();
                boolean nameVisible = dropItem.isNameVisible();

                String desc = dropItem.getDesc();

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
                Vec3 dropItemPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), dropItem.getPosSrc(), dropItem.getOffset(), "DropItems");

                // Get NBT
                String nbtString = dropItem.getNbt();

                LuckyEventFunctions.dropItems(level, dropItemPos, itemId, count, name, nameVisible, desc, nbtString);
            }
        }

        // Place Blocks
        if (function.hasPlaceBlocks()) {
            for (RandomEventReader.PlaceBlock placeBlock : function.getPlaceBlocks()) {
                // Get Block ID
                String blockId = placeBlock.getId();

                // Get Position
                Vec3 placeBlockPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), placeBlock.getPosSrc(), placeBlock.getOffset(), "PlaceBlocks");

                LuckyEventFunctions.placeBlock(level, placeBlockPos, blockId);
            }
        }

        // Place Chests
        if (function.hasPlaceChests()) {
            for (RandomEventReader.PlaceChest placeChest : function.getPlaceChests()) {
                String chestId = placeChest.getChestId();

                // Get Position
                Vec3 placeChestPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), placeChest.getPosSrc(), placeChest.getOffset(), "PlaceChests");

                Block chestBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(chestId));
                BlockState chestBlockState = chestBlock.defaultBlockState();

                String lootTableId = placeChest.getId();

                ResourceKey<LootTable> lootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(lootTableId));
                LuckyEventFunctions.placeChest(level, PosHelper.parseVec3d(placeChestPos), chestBlockState, lootTable);
            }
        }

        // Fall Blocks
        if (function.hasFallBlocks()) {
            for (RandomEventReader.FallBlock fallBlock : function.getFallBlocks()) {
                // Get Position
                Vec3 fallBlockPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), fallBlock.getPosSrc(), fallBlock.getOffset(), "FallBlocks");

                Vec3 velocity = fallBlock.getVelocity();

                Block blockId = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(fallBlock.getId()));
                LuckyEventFunctions.fallBlock(level, PosHelper.parseVec3d(fallBlockPos), blockId, velocity);
            }
        }

        // Give Potion Effects
        if (function.hasGivePotionEffects()) {
            for (RandomEventReader.GivePotionEffect givePotionEffect : function.getGivePotionEffects()) {
                int amplifier = givePotionEffect.getAmplifier();
                int duration = givePotionEffect.getDuration();
                Holder.Reference<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(givePotionEffect.getId())).orElseThrow();
                LuckyEventFunctions.givePotionEffect(player, effect, duration, amplifier);
            }
        }

        // Spawn Mobs
        if (function.hasSpawnMobs()) {
            for (RandomEventReader.SpawnMob spawnMob : function.getSpawnMobs()) {
                String mobId = spawnMob.getId();
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(mobId));

                String name = spawnMob.getName();
                boolean nameVisible = spawnMob.isNameVisible();

                String desc = spawnMob.getDesc();

                boolean isBaby = spawnMob.isBaby();
                String nbtString = spawnMob.getNbt();

                boolean isUseRandom = spawnMob.isUseRandom();
                int count;
                if (isUseRandom) {
                    count = getRandomNumber(spawnMob.getRandomNum().getMin(), spawnMob.getRandomNum().getMax());
                } else {
                    count = spawnMob.getNum();
                }

                // Get Position
                Vec3 spawnMobPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), spawnMob.getPosSrc(), spawnMob.getOffset(), "SpawnMobs");

                Vec3 velocity = spawnMob.getVelocity();

                for (int i = 0; i < count; i++) {
                    if (entityType == EntityType.ITEM) {
                        LuckyEventFunctions.dropItemsByNbt(level, spawnMobPos, name, nameVisible, desc, nbtString);
                    } else {
                        if (nbtString != null) {
                            LuckyEventFunctions.spawnMob(level, spawnMobPos, entityType, name, nameVisible, velocity, nbtString);
                        } else {
                            LuckyEventFunctions.spawnMob(level, spawnMobPos, entityType, name, nameVisible, isBaby, velocity);
                        }
                    }
                }
            }
        }

        // Send Messages
        if (function.hasSendMessages()) {
            for (RandomEventReader.SendMessage sendMessage : function.getSendMessages()) {
                LuckyEventFunctions.sendMessage(player, sendMessage.getMsg());
            }
        }

        // Display Messages
        if (function.hasDisplayMessages()) {
            for (RandomEventReader.DisplayMessage sendMessage : function.getDisplayMessages()) {
                LuckyEventFunctions.displayClientMessage(player, sendMessage.getMsg(), sendMessage.getOverlay());
            }
        }

        // Create Explosions
        if (function.hasCreateExplosions()) {
            for (RandomEventReader.CreateExplosion createExplosion : function.getCreateExplosions()) {
                // Get Position
                Vec3 createExplosionPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), createExplosion.getPosSrc(), createExplosion.getOffset(), "CreateExplosions");

                float power = createExplosion.getPower();
                boolean createFire = createExplosion.isCreateFire();

                LuckyEventFunctions.createExplosion(level, createExplosionPos, power, createFire);
            }
        }

        // Add Particles
        if (function.hasAddParticles()) {
            for (RandomEventReader.AddParticle addParticle : function.getAddParticles()) {
                ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(addParticle.getId()));
                ParticleOptions particle = (ParticleOptions) particleType;

                // Get Position
                Vec3 addParticlePos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), addParticle.getPosSrc(), addParticle.getOffset(), "AddParticles");

                int count = addParticle.getCount();
                double speed = addParticle.getSpeed();

                LuckyEventFunctions.addParticles(level, particle, addParticlePos, count, addParticle.getVelocity().getX(), addParticle.getVelocity().getY(), addParticle.getVelocity().getZ(), speed);
            }
        }

        // Load Structures
        if (function.hasLoadStructures()) {
            for (RandomEventReader.LoadStructure loadStructure : function.getLoadStructures()) {
                // Get Structure ID
                String modId = loadStructure.getModId();
                String structureId = loadStructure.getId();

                // Get Position
                Vec3 loadStructurePos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), loadStructure.getPosSrc(), loadStructure.getOffset(), "LoadStructures");

                LuckyEventFunctions.loadStructure(level, PosHelper.parseVec3d(loadStructurePos), modId, structureId);
            }
        }

        // Execute Commands
        if (function.hasExecuteCommands()) {
            for (RandomEventReader.ExecuteCommand executeCommand : function.getExecuteCommands()) {
                // Get Command
                String command = executeCommand.getCommand();

                // Get Position
                Vec3 executeCommandPos = PosHelper.calcPos(PosHelper.parseBlockPos(blockPos), PosHelper.parseBlockPos(player.blockPosition()), executeCommand.getPosSrc(), executeCommand.getOffset(), "ExecuteCommands");

                LuckyEventFunctions.executeCommand(level, executeCommandPos, command);
            }
        }
    }

    private static int getRandomNumber(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }
}
