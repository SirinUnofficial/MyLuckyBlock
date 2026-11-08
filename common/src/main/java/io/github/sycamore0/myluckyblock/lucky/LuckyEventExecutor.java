package io.github.sycamore0.myluckyblock.lucky;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import io.github.sycamore0.myluckyblock.lucky.reader.RandomEventDataReader;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class LuckyEventExecutor {
    public static void executeLuckyFunction(ServerLevel serverLevel, @Nullable Player player, BlockPos blockPos, RandomEventDataReader function) {
        ActionContext context = new ActionContext(serverLevel, player, blockPos);
        try {
            runActions(context, function);

            runDropItems(context, function.getDropItems());
            runPlaceBlocks(context, function.getPlaceBlocks());
            runPlaceChests(context, function.getPlaceChests());
            runDropLoots(context, function.getDropLoots());
            runFallBlocks(context, function.getFallBlocks());
            runGivePotionEffects(context, function.getGivePotionEffects());
            runSpawnMobs(context, function.getSpawnMobs());
            runSendMessages(context, function.getSendMessages());
            runDisplayMessages(context, function.getDisplayMessages());
            runCreateExplosions(context, function.getCreateExplosions());
            runAddParticles(context, function.getAddParticles());
            runPlaySounds(context, function.getPlaySounds());
            runLoadStructures(context, function.getLoadStructures());
            runExecuteCommands(context, function.getExecuteCommands());
        } catch (Exception e) {
            Constants.LOG.error("Event {} execution aborted due to fatal error {}", function.getEventId(), e);
        }
    }

    private static void runActions(ActionContext context, RandomEventDataReader function) {
        List<JsonObject> steps = function.getActions();
        for (int i = 0; i < steps.size(); i++) {
            JsonObject step = steps.get(i);
            if (step == null) continue;

            JsonElement typeElement = step.get("type");
            if (typeElement == null || !typeElement.isJsonPrimitive()) {
                Constants.LOG.warn("Event {} action #{} has no 'type', skipping", function.getEventId(), i + 1);
                continue;
            }

            String type = typeElement.getAsString();
            LuckyAction action = LuckyActions.get(type);
            if (action == null) {
                Constants.LOG.warn("Event {} action #{} uses unknown type '{}', skipping", function.getEventId(), i + 1, type);
                continue;
            }

            try {
                action.execute(context, step);
            } catch (Exception e) {
                Constants.LOG.error("Event {} action #{} ('{}') failed: {}", function.getEventId(), i + 1, type, e.toString());
            }
        }
    }

    public static void runDropItems(ActionContext context, @Nullable List<RandomEventDataReader.DropItem> items) {
        if (items == null || items.isEmpty()) return;
        for (RandomEventDataReader.DropItem dropItem : items) {
            boolean isUseRandom = dropItem.isUseRandom();
            int count;
            if (isUseRandom) {
                count = getRandomNumber(dropItem.getRandomNum().getMin(), dropItem.getRandomNum().getMax());
            } else {
                count = dropItem.getNum();
            }

            Vec3 pos = withSpread(context.level(), context.resolve(dropItem.getPosSrc(), dropItem.getOffset()), dropItem.getSpread());
            LuckyEventFunctions.dropItems(context.level(), pos, dropItem.getId(), count, dropItem.getName(), dropItem.isNameVisible(), dropItem.getDesc(), dropItem.getNbt(), dropItem.getDataComponents());
        }
    }

    public static void runPlaceBlocks(ActionContext context, @Nullable List<RandomEventDataReader.PlaceBlock> blocks) {
        if (blocks == null || blocks.isEmpty()) return;
        for (RandomEventDataReader.PlaceBlock placeBlock : blocks) {
            LuckyEventFunctions.placeBlock(context.level(), context.resolve(placeBlock.getPosSrc(), placeBlock.getOffset()), placeBlock.getId(), placeBlock.getState(), placeBlock.getNbt(), placeBlock.getDataComponents());
        }
    }

    public static void runPlaceChests(ActionContext context, @Nullable List<RandomEventDataReader.PlaceChest> chests) {
        if (chests == null || chests.isEmpty()) return;
        for (RandomEventDataReader.PlaceChest placeChest : chests) {
            LuckyEventFunctions.placeChest(context.level(), PosHelper.parseVec3d(context.resolve(placeChest.getPosSrc(), placeChest.getOffset())), placeChest.getChestId(), placeChest.getId(), placeChest.getSeed());
        }
    }

    public static void runDropLoots(ActionContext context, @Nullable List<RandomEventDataReader.DropLoots> loots) {
        if (loots == null || loots.isEmpty()) return;
        for (RandomEventDataReader.DropLoots dropLoots : loots) {
            LuckyEventFunctions.dropLoots(context.level(), context.resolve(dropLoots.getPosSrc(), dropLoots.getOffset()), dropLoots.getId(), dropLoots.getSeed());
        }
    }

    public static void runFallBlocks(ActionContext context, @Nullable List<RandomEventDataReader.FallBlock> blocks) {
        if (blocks == null || blocks.isEmpty()) return;
        for (RandomEventDataReader.FallBlock fallBlock : blocks) {
            LuckyEventFunctions.fallBlock(context.level(), PosHelper.parseVec3d(context.resolve(fallBlock.getPosSrc(), fallBlock.getOffset())), fallBlock.getId(), fallBlock.getVelocity());
        }
    }

    public static void runGivePotionEffects(ActionContext context, @Nullable List<RandomEventDataReader.GivePotionEffect> effects) {
        if (effects == null || effects.isEmpty() || context.player() == null) return;
        for (RandomEventDataReader.GivePotionEffect givePotionEffect : effects) {
            LuckyEventFunctions.givePotionEffect(context.player(), givePotionEffect.getId(), givePotionEffect.getDuration(), givePotionEffect.getAmplifier());
        }
    }

    public static void runSpawnMobs(ActionContext context, @Nullable List<RandomEventDataReader.SpawnMob> mobs) {
        if (mobs == null || mobs.isEmpty()) return;
        for (RandomEventDataReader.SpawnMob spawnMob : mobs) {
            boolean isUseRandom = spawnMob.isUseRandom();
            int count;
            if (isUseRandom) {
                count = getRandomNumber(spawnMob.getRandomNum().getMin(), spawnMob.getRandomNum().getMax());
            } else {
                count = spawnMob.getNum();
            }
            String nbtString = spawnMob.getNbt();

            for (int i = 0; i < Mth.clamp(count, 0, 64); i++) {
                Vec3 pos = withSpread(context.level(), context.resolve(spawnMob.getPosSrc(), spawnMob.getOffset()), spawnMob.getSpread());
                if (Objects.equals(spawnMob.getId(), "minecraft:item") || Objects.equals(spawnMob.getId(), "item")) {
                    Constants.LOG.warn("dropItemsByNbt is depreciated! Please use drop_items! nbt: {}", nbtString);
                    try {
                        LuckyEventFunctions.dropItems(context.level(), pos, spawnMob.getId(), count, spawnMob.getName(), spawnMob.isNameVisible(), spawnMob.getDesc(), nbtString);
                    } catch (Exception e) {
                        Constants.LOG.error("nbt: {}, e: {}", nbtString, e.toString());
                    }
                } else {
                    LuckyEventFunctions.spawnMob(context.level(), pos, spawnMob.getId(), spawnMob.getRandomize(), spawnMob.getName(), spawnMob.isNameVisible(), spawnMob.isBaby(), spawnMob.getVelocity(), nbtString, spawnMob.getVehicleId(), spawnMob.getVehicleNbt());
                }
            }
        }
    }

    public static void runSendMessages(ActionContext context, @Nullable List<RandomEventDataReader.SendMessage> messages) {
        if (messages == null || messages.isEmpty() || context.player() == null) return;
        for (RandomEventDataReader.SendMessage sendMessage : messages) {
            LuckyEventFunctions.sendMessage(context.player(), sendMessage.getMsg());
        }
    }

    public static void runDisplayMessages(ActionContext context, @Nullable List<RandomEventDataReader.DisplayMessage> messages) {
        if (messages == null || messages.isEmpty() || context.player() == null) return;
        for (RandomEventDataReader.DisplayMessage sendMessage : messages) {
            LuckyEventFunctions.displayClientMessage(context.player(), sendMessage.getMsg(), sendMessage.getOverlay());
        }
    }

    public static void runCreateExplosions(ActionContext context, @Nullable List<RandomEventDataReader.CreateExplosion> explosions) {
        if (explosions == null || explosions.isEmpty()) return;
        for (RandomEventDataReader.CreateExplosion createExplosion : explosions) {
            LuckyEventFunctions.createExplosion(context.level(), context.resolve(createExplosion.getPosSrc(), createExplosion.getOffset()), createExplosion.getPower(), createExplosion.isCreateFire());
        }
    }

    public static void runAddParticles(ActionContext context, @Nullable List<RandomEventDataReader.AddParticle> particles) {
        if (particles == null || particles.isEmpty()) return;
        for (RandomEventDataReader.AddParticle addParticle : particles) {
            LuckyEventFunctions.addParticles(context.level(), addParticle.getId(), context.resolve(addParticle.getPosSrc(), addParticle.getOffset()), addParticle.getCount(), addParticle.getDelta().getX(), addParticle.getDelta().getY(), addParticle.getDelta().getZ(), addParticle.getSpeed());
        }
    }

    public static void runPlaySounds(ActionContext context, @Nullable List<RandomEventDataReader.PlaySound> sounds) {
        if (sounds == null || sounds.isEmpty() || context.player() == null) return;
        for (RandomEventDataReader.PlaySound playSound : sounds) {
            LuckyEventFunctions.playSound(context.player(), context.level(), PosHelper.parseBlockPos(context.pos()), playSound.getId(), playSound.getVolume(), playSound.getPitch());
        }
    }

    public static void runLoadStructures(ActionContext context, @Nullable List<RandomEventDataReader.LoadStructure> structures) {
        if (structures == null || structures.isEmpty()) return;
        for (RandomEventDataReader.LoadStructure loadStructure : structures) {
            LuckyEventFunctions.loadStructure(context.level(), PosHelper.parseVec3d(context.resolve(loadStructure.getPosSrc(), loadStructure.getOffset())), loadStructure.getModId(), loadStructure.getId());
        }
    }

    public static void runExecuteCommands(ActionContext context, @Nullable List<RandomEventDataReader.ExecuteCommand> commands) {
        if (commands == null || commands.isEmpty()) return;
        for (RandomEventDataReader.ExecuteCommand executeCommand : commands) {
            LuckyEventFunctions.executeCommand(context.level(), context.resolve(executeCommand.getPosSrc(), executeCommand.getOffset()), executeCommand.getCommand());
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

    public static Vec3 resolvePosition(BlockPos blockPos, @Nullable Player player, PosSrc posSrc, Vec3 offset) {
        Vec3 basePos;
        if (posSrc == PosSrc.PLAYER && player != null) {
            basePos = player.position();
        } else {
            basePos = PosHelper.parseBlockPos(blockPos);
        }
        return PosHelper.calcOffset(basePos, offset);
    }

    private static Vec3 withSpread(ServerLevel level, Vec3 pos, RandomEventDataReader.Vector3 spread) {
        double x = Math.abs(spread.getX());
        double y = Math.abs(spread.getY());
        double z = Math.abs(spread.getZ());
        if (x == 0.0 && y == 0.0 && z == 0.0) {
            return pos;
        }
        return pos.add(
                Mth.randomBetween(level.getRandom(), (float) -x, (float) x),
                Mth.randomBetween(level.getRandom(), (float) -y, (float) y),
                Mth.randomBetween(level.getRandom(), (float) -z, (float) z));
    }
}
