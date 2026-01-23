package io.github.sycamore0.myluckyblock.utils.reader;

import com.google.gson.annotations.SerializedName;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.utils.PosSrc;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RandomEventDataReader {
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

    @SerializedName("display_messages")
    protected List<DisplayMessage> displayMessages;

    @SerializedName("create_explosions")
    protected List<CreateExplosion> createExplosions;

    @SerializedName("add_particles")
    protected List<AddParticle> addParticles;

    @SerializedName("play_sounds")
    protected List<PlaySound> playSounds;

    @SerializedName("load_structures")
    protected List<LoadStructure> loadStructures;

    @SerializedName("execute_commands")
    protected List<ExecuteCommand> executeCommands;

    public boolean hasDropItems() {
        return dropItems != null && !dropItems.isEmpty();
    }
    public boolean hasPlaceBlocks() {
        return placeBlocks != null && !placeBlocks.isEmpty();
    }
    public boolean hasPlaceChests() {
        return placeChests != null && !placeChests.isEmpty();
    }
    public boolean hasFallBlocks() {
        return fallBlocks != null && !fallBlocks.isEmpty();
    }
    public boolean hasGivePotionEffects() {
        return givePotionEffects != null && !givePotionEffects.isEmpty();
    }
    public boolean hasSpawnMobs() {
        return spawnMobs != null && !spawnMobs.isEmpty();
    }
    public boolean hasSendMessages() {
        return sendMessages != null && !sendMessages.isEmpty();
    }
    public boolean hasDisplayMessages() {
        return displayMessages != null && !displayMessages.isEmpty();
    }
    public boolean hasCreateExplosions() {
        return createExplosions != null && !createExplosions.isEmpty();
    }
    public boolean hasAddParticles() {
        return addParticles != null && !addParticles.isEmpty();
    }
    public boolean hasPlaySounds() {
        return playSounds != null && !playSounds.isEmpty();
    }
    public boolean hasLoadStructures() {
        return loadStructures != null && !loadStructures.isEmpty();
    }
    public boolean hasExecuteCommands() {
        return executeCommands != null && !executeCommands.isEmpty();
    }

    public List<DropItem> getDropItems() {
        return dropItems;
    }
    public List<PlaceBlock> getPlaceBlocks() {
        return placeBlocks;
    }
    public List<PlaceChest> getPlaceChests() {
        return placeChests;
    }
    public List<FallBlock> getFallBlocks() {
        return fallBlocks;
    }
    public List<GivePotionEffect> getGivePotionEffects() {
        return givePotionEffects;
    }
    public List<SpawnMob> getSpawnMobs() {
        return spawnMobs;
    }
    public List<SendMessage> getSendMessages() {
        return sendMessages;
    }
    public List<DisplayMessage> getDisplayMessages() {
        return displayMessages;
    }
    public List<CreateExplosion> getCreateExplosions() {
        return createExplosions;
    }
    public List<AddParticle> getAddParticles() {
        return addParticles;
    }
    public List<PlaySound> getPlaySounds() {
        return playSounds;
    }
    public List<LoadStructure> getLoadStructures() {
        return loadStructures;
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

        @SerializedName("name")
        protected String name = null;

        @SerializedName("name_visible")
        protected boolean nameVisible = false;

        @SerializedName("desc")
        protected String desc = null;

        @SerializedName("nbt")
        protected String nbt = null;

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
            return Mth.clamp(num, 1, 64);
        }

        public PosSrc getPosSrc() {
            return PosSrc.fromValue(posSrc);
        }

        public Vec3 getOffset() {
            return new Vec3(offset.getX(), offset.getY(), offset.getZ());
        }

        public String getName() {
            return name;
        }

        public boolean isNameVisible() {
            return nameVisible;
        }

        public String getDesc() {
            return desc;
        }

        public String getNbt() {
            return nbt;
        }
    }

    public static class PlaceBlock {
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

        public PosSrc getPosSrc() {
            return PosSrc.fromValue(posSrc);
        }

        public Vec3 getOffset() {
            return new Vec3(offset.getX(), offset.getY(), offset.getZ());
        }
    }

    public static class PlaceChest {
        @SerializedName("id")
        protected String id = "empty";

        @SerializedName("chest_id")
        protected String chestId = "minecraft:chest";

        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        // Getters
        public PosSrc getPosSrc() {
            return PosSrc.fromValue(posSrc);
        }

        public Vec3 getOffset() {
            return new Vec3(offset.getX(), offset.getY(), offset.getZ());
        }

        public String getId() {
            return id;
        }

        public String getChestId() {
            return chestId;
        }
    }

    public static class FallBlock {
        @SerializedName("id")
        protected String id;

        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        @SerializedName("velocity")
        protected Vec3 velocity = new Vec3(0.0, 0.0, 0.0);

        // Getters
        public PosSrc getPosSrc() {
            return PosSrc.fromValue(posSrc);
        }

        public Vec3 getOffset() {
            return new Vec3(offset.getX(), offset.getY(), offset.getZ());
        }

        public Vec3 getVelocity() {
            return velocity;
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

        @SerializedName("velocity")
        protected Vec3 velocity = new Vec3(0.0, 0.0, 0.0);

        @SerializedName("randomize")
        protected boolean randomize = true;

        @SerializedName("name")
        protected String name = null;

        @SerializedName("name_visible")
        protected boolean nameVisible = false;

        @SerializedName("desc")
        protected String desc = null;

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
        public PosSrc getPosSrc() {
            return PosSrc.fromValue(posSrc);
        }

        public Vec3 getOffset() {
            return new Vec3(offset.getX(), offset.getY(), offset.getZ());
        }

        public Vec3 getVelocity() {
            return velocity;
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

        public boolean getRandomize() {
            return randomize;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
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

    public static class DisplayMessage {
        @SerializedName("msg")
        protected String msg;

        @SerializedName("overlay")
        protected boolean overlay = true;

        // Getters
        public boolean getOverlay() {
            return overlay;
        }

        public String getMsg() {
            return msg;
        }
    }

    public static class SendMessage {
        @SerializedName("msg")
        protected String msg;

        // Getters
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
        public PosSrc getPosSrc() {
            return PosSrc.fromValue(posSrc);
        }

        public Vec3 getOffset() {
            return new Vec3(offset.getX(), offset.getY(), offset.getZ());
        }

        public int getPower() {
            return power;
        }

        public boolean isCreateFire() {
            return createFire;
        }
    }

    public static class PlaySound {
        @SerializedName("id")
        protected String id;

        @SerializedName("volume")
        protected float volume = 1.0f;

        @SerializedName("pitch")
        protected float pitch = 1.0f;

        // Getters
        public String getId() {
            return id;
        }

        public float getVolume() {
            return volume;
        }

        public float getPitch() {
            return pitch;
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

        public PosSrc getPosSrc() {
            return PosSrc.fromValue(posSrc);
        }

        public Vec3 getOffset() {
            return new Vec3(offset.getX(), offset.getY(), offset.getZ());
        }
    }

    public static class LoadStructure {
        @SerializedName("id")
        protected String id;

        @SerializedName("mod_id")
        protected String modId = Constants.MOD_ID;

        @SerializedName("pos_src")
        protected int posSrc = 0;

        @SerializedName("offset")
        protected Offset offset = new Offset(0.0, 0.0, 0.0);

        // Getters
        public String getId() {
            return id;
        }

        public String getModId() {
            return modId;
        }

        public PosSrc getPosSrc() {
            return PosSrc.fromValue(posSrc);
        }

        public Vec3 getOffset() {
            return new Vec3(offset.getX(), offset.getY(), offset.getZ());
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

        public PosSrc getPosSrc() {
            return PosSrc.fromValue(posSrc);
        }

        public Vec3 getOffset() {
            return new Vec3(offset.getX(), offset.getY(), offset.getZ());
        }
    }

    public static class RandomNum {
        @SerializedName("min")
        protected int min;

        @SerializedName("max")
        protected int max;

        public RandomNum(int min, int max) {
            this.min = Mth.clamp(min, 0, max);
            this.max = Mth.clamp(max, min, 64);
        }

        // Getters
        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }

    public static class Velocity {
        @SerializedName("x")
        protected double x;

        @SerializedName("y")
        protected double y;

        @SerializedName("z")
        protected double z;

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

    public static class Offset {
        @SerializedName("x")
        protected double x;

        @SerializedName("y")
        protected double y;

        @SerializedName("z")
        protected double z;

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
}
