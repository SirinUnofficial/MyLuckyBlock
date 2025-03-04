package io.github.sycamore0.myluckyblock.utils;

import io.github.sycamore0.myluckyblock.MyLuckyBlock;
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
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class LuckyExecutor {
    public static void executeLuckyFunction(Level level, Player player, BlockPos blockPos, LuckyEventReader function) {
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
                Vec3 dropItemPos = PosHelper.parseBlockPos(blockPos);
                Vec3 playerPos = PosHelper.parseBlockPos(player.blockPosition());
                Vec3 offset = dropItem.getOffset();
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

                LuckyFunctions.dropItems(level, dropItemPos, itemId, count, nbtString);
            }
        }

        // Place Blocks
        if (function.hasPlaceBlocks()) {
            for (LuckyEventReader.PlaceBlock placeBlock : function.getPlaceBlocks()) {
                // Get Block ID
                String blockId = placeBlock.getId();

                // Get Position
                int posSrc = placeBlock.getPosSrc();
                Vec3 placeBlockPos = PosHelper.parseBlockPos(blockPos);
                Vec3 playerPos = PosHelper.parseBlockPos(player.blockPosition());
                Vec3 offset = placeBlock.getOffset();
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

                LuckyFunctions.placeBlock(level, placeBlockPos, blockId);
            }
        }

        // Place Chests
        if (function.hasPlaceChests()) {
            for (LuckyEventReader.PlaceChest placeChest : function.getPlaceChests()) {
                int posSrc = placeChest.getPosSrc();
                Vec3 placeChestPos = PosHelper.parseBlockPos(blockPos);
                Vec3 playerPos = PosHelper.parseBlockPos(player.blockPosition());
                Vec3 offset = placeChest.getOffset();
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

                ResourceKey<LootTable> lootTable = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(lootTableId));
                LuckyFunctions.placeChest(level, PosHelper.parseVec3d(placeChestPos), type, lootTable);
            }
        }

        // Fall Blocks
        if (function.hasFallBlocks()) {
            for (LuckyEventReader.FallBlock fallBlock : function.getFallBlocks()) {
                int posSrc = fallBlock.getPosSrc();
                Vec3 fallBlockPos = PosHelper.parseBlockPos(blockPos);
                Vec3 playerPos = PosHelper.parseBlockPos(player.blockPosition());
                Vec3 velocity = fallBlock.getVelocity();
                Vec3 offset = fallBlock.getOffset();
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

                Block blockId = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(fallBlock.getId()));
                LuckyFunctions.fallBlock(level, PosHelper.parseVec3d(fallBlockPos), blockId, velocity);
            }
        }

        // Give Potion Effects
        if (function.hasGivePotionEffects()) {
            for (LuckyEventReader.GivePotionEffect givePotionEffect : function.getGivePotionEffects()) {
                int amplifier = givePotionEffect.getAmplifier();
                int duration = givePotionEffect.getDuration();
                Holder.Reference<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.getHolder(ResourceLocation.parse(givePotionEffect.getId())).orElseThrow();
                LuckyFunctions.givePotionEffect(player, effect, duration, amplifier);
            }
        }

        // Spawn Mobs
        if (function.hasSpawnMobs()) {
            for (LuckyEventReader.SpawnMob spawnMob : function.getSpawnMobs()) {
                String mobId = spawnMob.getId();
                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(mobId));

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
                Vec3 spawnMobPos = PosHelper.parseBlockPos(blockPos);
                Vec3 playerPos = PosHelper.parseBlockPos(player.blockPosition());
                Vec3 offset = spawnMob.getOffset();
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

                Vec3 velocity = spawnMob.getVelocity();

                for (int i = 0; i < count; i++) {
                    if (entityType == EntityType.ITEM) {
                        LuckyFunctions.dropItemsByNbt(level, spawnMobPos, name, nameVisible, nbtString);
                    }
                    else {
                        if (nbtString != null) {
                            LuckyFunctions.spawnMob(level, spawnMobPos, entityType, name, nameVisible, nbtString, velocity);
                        } else {
                            LuckyFunctions.spawnMob(level, spawnMobPos, entityType, name, nameVisible, isBaby, velocity);
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
                Vec3 createExplosionPos = PosHelper.parseBlockPos(blockPos);
                Vec3 playerPos = PosHelper.parseBlockPos(player.blockPosition());
                Vec3 offset = createExplosion.getOffset();
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

                LuckyFunctions.createExplosion(level, createExplosionPos, power, createFire);
            }
        }

        // Add Particles
        if (function.hasAddParticles()) {
            for (LuckyEventReader.AddParticle addParticle : function.getAddParticles()) {
                ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.get(ResourceLocation.parse(addParticle.getId()));
                ParticleOptions particle = (ParticleOptions) particleType;

                // Get Position
                int posSrc = addParticle.getPosSrc();
                Vec3 addParticlePos = PosHelper.parseBlockPos(blockPos);
                Vec3 playerPos = PosHelper.parseBlockPos(player.blockPosition());
                Vec3 offset = addParticle.getOffset();
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

                LuckyFunctions.addParticles(level, particle, addParticlePos, count, addParticle.getVelocity().getX(), addParticle.getVelocity().getY(), addParticle.getVelocity().getZ(), speed);
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
                Vec3 loadStructurePos = PosHelper.parseBlockPos(blockPos);
                Vec3 playerPos = PosHelper.parseBlockPos(player.blockPosition());
                Vec3 offset = loadStructure.getOffset();
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

                LuckyFunctions.loadStructure(level, PosHelper.parseVec3d(loadStructurePos), modId, structureId);
            }
        }

        // Execute Commands
        if (function.hasExecuteCommands()) {
            for (LuckyEventReader.ExecuteCommand executeCommand: function.getExecuteCommands()) {
                // Get Command
                String command = executeCommand.getCommand();

                // Get Position
                int posSrc = executeCommand.getPosSrc();
                Vec3 loadStructurePos = PosHelper.parseBlockPos(blockPos);
                Vec3 playerPos = PosHelper.parseBlockPos(player.blockPosition());
                Vec3 offset = executeCommand.getOffset();
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

                LuckyFunctions.executeCommand(level, loadStructurePos, command);
            }
        }
    }

    private static int getRandomNumber(int min, int max) {
        return min + new Random().nextInt(max - min + 1);
    }
}
