package io.github.sycamore0.myluckyblock.lucky.action.builtin;

import com.google.gson.JsonObject;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.lucky.action.ActionContext;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyAction;
import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;
import io.github.sycamore0.myluckyblock.lucky.action.spec.LoadStructure;
import io.github.sycamore0.myluckyblock.utils.helper.PosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

public final class LoadStructuresAction implements LuckyAction {
    @Override
    public void execute(ActionContext context, JsonObject step) {
        for (LoadStructure structure : LuckyActions.spec(step, LoadStructure.class)) {
            loadStructure(context.level(), PosHelper.parseVec3d(context.resolvePos(structure.getPosSrc(), structure.getOffset())), structure.getModId(), structure.getId());
        }
    }

    private static void loadStructure(ServerLevel serverLevel, BlockPos pos, String modId, String structureName) {
        StructureTemplateManager manager = serverLevel.getStructureManager();
        ResourceLocation structureId = ResourceLocation.fromNamespaceAndPath(modId, structureName);

        try {
            Optional<StructureTemplate> template = manager.get(structureId);

            if (template.isPresent()) {
                StructureTemplate structure = template.get();
                StructurePlaceSettings placement = new StructurePlaceSettings()
                        .setIgnoreEntities(false)
                        .setKnownShape(true)
                        .setRotation(Rotation.NONE)
                        .setMirror(Mirror.NONE);

                structure.placeInWorld(serverLevel, pos, pos, placement, serverLevel.getRandom(), 3);
            } else {
                Constants.LOG.error("Structure template {} is empty", structureId);
            }
        } catch (Exception e) {
            Constants.LOG.error("Failed to load structure {}", structureId, e);
        }
    }
}
