package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.ActionSupport;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class SpawnMobsAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (SpawnMobSpec mob : LuckyActions.spec(step, SpawnMobSpec.class)) {
            int count = ActionSupport.roll(mob.isUseRandom(), mob.getRandomNum(), mob.getNum());
            for (int i = 0; i < Mth.clamp(count, 0, 64); i++) {
                Vec3 pos = ActionSupport.spread(context.level(), context.resolvePos(mob.getPosSrc(), mob.getOffset()), mob.getSpread());
                spawnMob(context.level(), pos, mob.getId(), mob.getRandomize(), mob.getName(), mob.isNameVisible(), mob.isBaby(), mob.getVelocity(), mob.getNbt(), mob.getVehicleId(), mob.getVehicleNbt());
            }
        }
    }

    private static void spawnMob(ServerLevel serverLevel, Vec3 pos, String entityId, boolean randomize, @Nullable String name, boolean nameVisible, boolean isBaby, Vec3 velocity, @Nullable String nbtString, @Nullable String vehicleId, @Nullable String vehicleNbtString) {
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityId));
        Entity entity = entityType.create(serverLevel);
        if (entity == null) return;

        if (randomize && entity instanceof Mob mobEntity) {
            mobEntity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(PosHelper.parseVec3d(pos)), MobSpawnType.NATURAL, null);
        }

        CompoundTag nbt = NbtHelper.generateNbt(nbtString);
        if (nbt != null) {
            entity.load(nbt);
        }

        if (entity instanceof Mob mobEntity) {
            mobEntity.setBaby(isBaby);
        }

        if (name != null) {
            entity.setCustomName(Component.translatableEscape(name));
            entity.setCustomNameVisible(nameVisible);
        }

        entity.setPos(pos);
        entity.push(velocity);

        if (vehicleId != null) {
            EntityType<?> vehicleEntityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(vehicleId));
            Entity vehicle = vehicleEntityType.create(serverLevel);
            if (vehicle != null) {
                CompoundTag vehicleNbt = NbtHelper.generateNbt(vehicleNbtString);
                if (vehicleNbt != null) {
                    vehicle.load(vehicleNbt);
                }

                if (randomize && vehicle instanceof Mob mobVehicleEntity) {
                    mobVehicleEntity.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(PosHelper.parseVec3d(pos)), MobSpawnType.NATURAL, null);
                }

                vehicle.setPos(pos);
                vehicle.push(velocity);

                serverLevel.addFreshEntity(vehicle);
                entity.startRiding(vehicle, true);
            }
        }

        serverLevel.addFreshEntity(entity);
    }
}
