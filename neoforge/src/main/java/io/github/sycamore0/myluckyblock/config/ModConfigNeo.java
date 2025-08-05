package io.github.sycamore0.myluckyblock.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfigNeo {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue generate_lucky_block;

    static {
        generate_lucky_block = BUILDER
                .comment("generate lucky block")
                .define("generate_lucky_block", true);

        SPEC = BUILDER.build(); // 构建 ModConfigSpec
    }
}