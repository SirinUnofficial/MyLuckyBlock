package io.github.sycamore0.myluckyblock.block;

import io.github.sycamore0.myluckyblock.Constants;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class LuckyBlock extends Block {
    private String modId = Constants.MOD_ID; // path: data/myluckyblock/lucky_events/%modId%/
    private boolean includeBuiltIn = false; // if include built-in lucky events(include lucky_events/my_lucky_block/)

    public LuckyBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public LuckyBlock(BlockBehaviour.Properties settings, String modId) {
        super(settings);
        this.modId = modId;
        this.includeBuiltIn = false;
    }

    public LuckyBlock(BlockBehaviour.Properties settings, String modId, boolean includeBuiltIn) {
        super(settings);
        this.modId = modId;
        this.includeBuiltIn = includeBuiltIn;
    }

    public String getModId() {
        return modId;
    }

    public boolean includeBuiltIn() {
        return includeBuiltIn;
    }
}
