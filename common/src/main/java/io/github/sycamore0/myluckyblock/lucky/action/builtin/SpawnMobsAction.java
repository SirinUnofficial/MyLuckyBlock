package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.config.ConfigManager;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.ActionSupport;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.EntityNode;
import io.github.sycamore0.myluckyblock.lucky.action.spec.SpawnMobSpec;
import io.github.sycamore0.myluckyblock.utils.helper.NbtHelper;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class SpawnMobsAction implements LuckyAction {
    private static final int MAX_COUNT = 64;

    private static int maxPassengerDepth() {
        return Mth.clamp(ConfigManager.get().maxPassengerDepth, 1, 64);
    }

    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (SpawnMobSpec mob : LuckyActions.spec(step, SpawnMobSpec.class)) {
            int count = Mth.clamp(ActionSupport.roll(mob.isUseRandom(), mob.getRandomNum(), mob.getNum()), 0, MAX_COUNT);
            for (int i = 0; i < count; i++) {
                Vec3 pos = context.resolve(mob.getPos());
                spawn(context.level(), mob, pos);
            }
        }
    }

    @Override
    public void validate(JsonObject step) {
        for (SpawnMobSpec mob : LuckyActions.spec(step, SpawnMobSpec.class)) {
            if (mob.getNum() < 0 || mob.getNum() > MAX_COUNT) {
                Constants.LOG.warn("spawn_mobs: 'num' {} is out of range 0..{}, it will be clamped", mob.getNum(), MAX_COUNT);
            }
            validateNode(mob.getId(), 0);
            validatePassengers(mob.getPassengers(), 1);
        }
    }

    private static void validateNode(@Nullable String entityId, int depth) {
        if (entityId == null || entityId.isBlank()) {
            return;
        }
        ResourceLocation id = ResourceLocation.tryParse(entityId);
        if (id == null) {
            Constants.LOG.warn("spawn_mobs: invalid entity id '{}' at depth {}", entityId, depth);
        } else if (BuiltInRegistries.ENTITY_TYPE.getOptional(id).isEmpty()) {
            Constants.LOG.warn("spawn_mobs: unknown entity id '{}' at depth {}", entityId, depth);
        }
    }

    private static void validatePassengers(List<EntityNode> passengers, int depth) {
        if (passengers.isEmpty()) {
            return;
        }
        int maxDepth = maxPassengerDepth();
        if (depth > maxDepth) {
            Constants.LOG.warn("spawn_mobs: passenger chain deeper than {} levels, the extra levels will be ignored", maxDepth);
            return;
        }
        for (EntityNode passenger : passengers) {
            validateNode(passenger.getId(), depth);
            validatePassengers(passenger.getPassengers(), depth + 1);
        }
    }

    private static void spawn(ServerLevel serverLevel, SpawnMobSpec spec, Vec3 pos) {
        Entity root = createEntity(spec.getId(), serverLevel);
        if (root == null) {
            return;
        }
        applyCommon(spec.getNbt(), spec.getName(), spec.isNameVisible(), spec.isBaby(), spec.getRandomize(), root, serverLevel, pos);
        root.setPos(pos);
        root.push(spec.getVelocity());

        mountPassengers(root, spec.getPassengers(), serverLevel, pos, 1);

        serverLevel.tryAddFreshEntityWithPassengers(root);
    }

    private static void mountPassengers(Entity vehicle, List<EntityNode> passengers, ServerLevel serverLevel, Vec3 pos, int depth) {
        if (passengers.isEmpty()) {
            return;
        }
        int maxDepth = maxPassengerDepth();
        if (depth > maxDepth) {
            Constants.LOG.warn("spawn_mobs: passenger chain deeper than {} levels, ignoring the rest", maxDepth);
            return;
        }
        for (EntityNode node : passengers) {
            Entity rider = createEntity(node.getId(), serverLevel);
            if (rider == null) {
                continue;
            }
            applyCommon(node.getNbt(), node.getName(), node.isNameVisible(), node.isBaby(), false, rider, serverLevel, pos);
            rider.setPos(vehicle.position());

            if (!rider.startRiding(vehicle, true)) {
                Constants.LOG.warn("spawn_mobs: {} cannot ride {}", node.getId(), vehicle.getType());
                rider.discard();
                continue;
            }
            mountPassengers(rider, node.getPassengers(), serverLevel, pos, depth + 1);
        }
    }

    @Nullable
    private static Entity createEntity(@Nullable String entityId, ServerLevel serverLevel) {
        if (entityId == null || entityId.isBlank()) {
            Constants.LOG.warn("spawn_mobs: entry has no entity id, skipping");
            return null;
        }
        ResourceLocation id = ResourceLocation.tryParse(entityId);
        if (id == null) {
            Constants.LOG.warn("spawn_mobs: invalid entity id '{}', skipping", entityId);
            return null;
        }
        var type = BuiltInRegistries.ENTITY_TYPE.getOptional(id);
        if (type.isEmpty()) {
            Constants.LOG.warn("spawn_mobs: unknown entity id '{}', skipping", entityId);
            return null;
        }
        Entity entity = type.get().create(serverLevel);
        if (entity == null) {
            Constants.LOG.warn("spawn_mobs: entity '{}' could not be created, skipping", entityId);
        }
        return entity;
    }

    private static void applyCommon(@Nullable String nbtString, @Nullable String name, boolean nameVisible, boolean isBaby, boolean randomize, Entity entity, ServerLevel serverLevel, Vec3 pos) {
        if (randomize && entity instanceof Mob mob) {
            mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(PosHelper.parseVec3d(pos)), MobSpawnType.NATURAL, null);
        }

        CompoundTag tag = parseNbt(nbtString);
        if (tag != null) {
            tag.remove("Passengers");
            tag.remove("Pos");
            tag.remove("Motion");
            tag.remove("UUID");
            entity.load(tag);
        }

        if (entity instanceof Mob mob) {
            mob.setBaby(isBaby);
        }

        if (name != null) {
            entity.setCustomName(Component.translatableEscape(name));
            entity.setCustomNameVisible(nameVisible);
        }
    }

    @Nullable
    private static CompoundTag parseNbt(@Nullable String nbtString) {
        if (nbtString == null || nbtString.isBlank()) {
            return null;
        }
        try {
            return NbtHelper.generateNbt(nbtString);
        } catch (IllegalArgumentException e) {
            Constants.LOG.warn("spawn_mobs: invalid 'nbt' {}, ignoring it", nbtString);
            return null;
        }
    }
}
