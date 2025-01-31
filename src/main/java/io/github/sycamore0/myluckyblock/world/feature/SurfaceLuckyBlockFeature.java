package io.github.sycamore0.myluckyblock.world.feature;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SurfaceLuckyBlockFeature extends Feature<DefaultFeatureConfig> {
    private final Block block;

    public SurfaceLuckyBlockFeature(Block block) {
        super(DefaultFeatureConfig.CODEC);
        this.block = block;
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        Random random = context.getRandom();

        boolean success = false;
        int x = origin.getX() + random.nextInt(16);
        int z = origin.getZ() + random.nextInt(16);
        int y = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, x, z);

        BlockPos pos = new BlockPos(x, y, z);
        if (world.getBlockState(pos).isAir() &&
                world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
            world.setBlockState(pos, block.getDefaultState(), 3);
            success = true;
        }
        return success;
    }
}