package io.github.sycamore0.myluckyblock.utils;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class LuckyEventReader {
    @SerializedName("id")
    protected int id;

    @SerializedName("drop_items")
    protected List<DropItem> dropItems;

    @SerializedName("place_blocks")
    protected List<PlaceBlock> placeBlocks;

    @SerializedName("place_chests")
    protected List<PlaceChest> placeChests;

    @SerializedName("fall_blocks")
    protected List<FallBlock> fallBlocks;

    @SerializedName("give_potion_effects")
    protected List<GivePotionEffect> givePotionEffects;

    @SerializedName("spawn_mobs")
    protected List<SpawnMob> spawnMobs;

    @SerializedName("send_messages")
    protected List<SendMessage> sendMessages;

    @SerializedName("create_explosions")
    protected List<CreateExplosion> createExplosions;

    @SerializedName("add_particles")
    protected List<AddParticle> addParticles;

    @SerializedName("load_structures")
    protected List<LoadStructure> loadStructures;

    @SerializedName("execute_commands")
    protected List<ExecuteCommand> executeCommands;

    // 检查 dropItems 是否为空
    public boolean hasDropItems() {
        return dropItems != null && !dropItems.isEmpty();
    }

    public List<DropItem> getDropItems() {
        return dropItems;
    }

    // 检查 placeBlocks 是否为空
    public boolean hasPlaceBlocks() {
        return placeBlocks != null && !placeBlocks.isEmpty();
    }

    public List<PlaceBlock> getPlaceBlocks() {
        return placeBlocks;
    }

    // 检查 placeChests 是否为空
    public boolean hasPlaceChests() {
        return placeChests != null && !placeChests.isEmpty();
    }

    public List<PlaceChest> getPlaceChests() {
        return placeChests;
    }

    // 检查 fallBlocks 是否为空
    public boolean hasFallBlocks() {
        return fallBlocks != null && !fallBlocks.isEmpty();
    }

    public List<FallBlock> getFallBlocks() {
        return fallBlocks;
    }

    // 检查 givePotionEffects 是否为空
    public boolean hasGivePotionEffects() {
        return givePotionEffects != null && !givePotionEffects.isEmpty();
    }

    public List<GivePotionEffect> getGivePotionEffects() {
        return givePotionEffects;
    }

    // 检查 spawnMobs 是否为空
    public boolean hasSpawnMobs() {
        return spawnMobs != null && !spawnMobs.isEmpty();
    }

    public List<SpawnMob> getSpawnMobs() {
        return spawnMobs;
    }

    // 检查 sendMessages 是否为空
    public boolean hasSendMessages() {
        return sendMessages != null && !sendMessages.isEmpty();
    }

    public List<SendMessage> getSendMessages() {
        return sendMessages;
    }

    // 检查 createExplosions 是否为空
    public boolean hasCreateExplosions() {
        return createExplosions != null && !createExplosions.isEmpty();
    }

    public List<CreateExplosion> getCreateExplosions() {
        return createExplosions;
    }

    // 检查 addParticles 是否为空
    public boolean hasAddParticles() {
        return addParticles != null && !addParticles.isEmpty();
    }

    public List<AddParticle> getAddParticles() {
        return addParticles;
    }

    // 检查 loadStructures 是否为空
    public boolean hasLoadStructures() {
        return loadStructures != null && !loadStructures.isEmpty();
    }

    public List<LoadStructure> getLoadStructures() {
        return loadStructures;
    }

    // 检查 executeCommands 是否为空
    public boolean hasExecuteCommands() {
        return executeCommands != null && !executeCommands.isEmpty();
    }

    public List<ExecuteCommand> getExecuteCommands() {
        return executeCommands;
    }

    // Getters and Setters for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class DropItem {
        @SerializedName("id")
        protected String id;

        @SerializedName("use_random")
        protected boolean useRandom = false;

        @SerializedName("random_num")
        protected RandomNum randomNum = new RandomNum(0, 1);

        @SerializedName("num")
        protected int num = 1;

        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        // Getters
        public String getId() {
            return id;
        }

        public boolean isUseRandom() {
            return useRandom;
        }

        public RandomNum getRandomNum() {
            return randomNum;
        }

        public int getNum() {
            return MathHelper.clamp(num, 1, 64);
        }

        public int getPosSrc() {
            return posSrc;
        }

        public Offset getOffset() {
            return offset;
        }
    }

    public static class PlaceBlock {
        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        @SerializedName("id")
        protected String id;

        // Getters
        public int getPosSrc() {
            return posSrc;
        }

        public Offset getOffset() {
            return offset;
        }

        public String getId() {
            return id;
        }
    }

    public static class PlaceChest {
        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        @SerializedName("id")
        protected String id = "empty";

        @SerializedName("type")
        protected int type = 0;

        // Getters
        public int getPosSrc() {
            return posSrc;
        }

        public Offset getOffset() {
            return offset;
        }

        public String getId() {
            return id;
        }

        public int getType() {
            return type;
        }
    }

    public static class FallBlock {
        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        @SerializedName("id")
        protected String id;

        // Getters
        public int getPosSrc() {
            return posSrc;
        }

        public Offset getOffset() {
            return offset;
        }

        public String getId() {
            return id;
        }
    }

    public static class GivePotionEffect {
        @SerializedName("id")
        protected String id;

        @SerializedName("amplifier")
        protected int amplifier = 0;

        @SerializedName("duration")
        protected int duration = 1;

        // Getters
        public String getId() {
            return id;
        }

        public int getAmplifier() {
            return amplifier;
        }

        public int getDuration() {
            return duration;
        }
    }

    public static class SpawnMob {
        @SerializedName("id")
        protected String id;

        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        @SerializedName("name")
        protected String name = null;

        @SerializedName("name_visible")
        protected boolean nameVisible = false;

        @SerializedName("is_baby")
        protected boolean isBaby = false;

        @SerializedName("use_random")
        protected boolean useRandom = false;

        @SerializedName("random_num")
        protected RandomNum randomNum = new RandomNum(0, 1);

        @SerializedName("num")
        protected int num = 1;

        @SerializedName("nbt")
        protected String nbt = null;

        // Getters
        public int getPosSrc() {
            return posSrc;
        }

        public Offset getOffset() {
            return offset;
        }

        public String getId() {
            return id;
        }

        public boolean isUseRandom() {
            return useRandom;
        }

        public RandomNum getRandomNum() {
            return randomNum;
        }

        public String getName() {
            return name;
        }

        public Boolean isNameVisible() {
            return nameVisible;
        }

        public Boolean isBaby() {
            return isBaby;
        }

        public int getNum() {
            return num;
        }

        public String getNbt() {
            return nbt;
        }
    }

    public static class SendMessage {
        @SerializedName("msg")
        protected String msg;

        @SerializedName("receiver")
        protected int receiver = 0;

        // Getters
        public int getReceiver() {
            return receiver;
        }

        public String getMsg() {
            return msg;
        }
    }

    public static class CreateExplosion {
        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        @SerializedName("power")
        protected int power = 1;

        @SerializedName("create_fire")
        protected boolean createFire = false;

        // Getters
        public int getPosSrc() {
            return posSrc;
        }

        public Offset getOffset() {
            return offset;
        }

        public int getPower() {
            return power;
        }

        public boolean isCreateFire() {
            return createFire;
        }
    }

    public static class AddParticle {
        @SerializedName("id")
        protected String id;

        @SerializedName("count")
        protected int count = 1;

        @SerializedName("velocity")
        protected Velocity velocity = new Velocity(1.0, 1.0, 1.0);

        @SerializedName("speed")
        protected double speed = 0.0;

        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        // Getters
        public String getId() {
            return id;
        }

        public int getCount() {
            return count;
        }

        public Velocity getVelocity() {
            return velocity;
        }

        public double getSpeed() {
            return speed;
        }

        public int getPosSrc() {
            return posSrc;
        }

        public Offset getOffset() {
            return offset;
        }
    }

    public static class LoadStructure {
        @SerializedName("id")
        protected String id;

        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        // Getters
        public String getId() {
            return id;
        }

        public int getPosSrc() {
            return posSrc;
        }

        public Offset getOffset() {
            return offset;
        }
    }

    public static class ExecuteCommand {
        @SerializedName("command")
        protected String command;

        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        // Getters
        public String getCommand() {
            return command;
        }

        public int getPosSrc() {
            return posSrc;
        }

        public Offset getOffset() {
            return offset;
        }
    }

    public static class RandomNum {
        @SerializedName("min")
        protected int min;

        @SerializedName("max")
        protected int max;

        // Default Constructor
        public RandomNum(int min, int max) {
            this.min = MathHelper.clamp(min, 0, max);
            this.max = MathHelper.clamp(max, min, 64);
        }

        // Getters
        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }

    public static class Offset {
        @SerializedName("x")
        protected double x;

        @SerializedName("y")
        protected double y;

        @SerializedName("z")
        protected double z;

        // Default Constructor
        public Offset(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        // Getters
        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }

    public static class Velocity {
        @SerializedName("x")
        protected double x;

        @SerializedName("y")
        protected double y;

        @SerializedName("z")
        protected double z;

        // Default Constructor
        public Velocity(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        // Getters
        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }
}
