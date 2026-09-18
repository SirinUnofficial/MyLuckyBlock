package io.github.sycamore0.myluckyblock.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.sycamore0.myluckyblock.Constants;
import io.github.sycamore0.myluckyblock.event.BreakLuckyBlock;
import io.github.sycamore0.myluckyblock.lucky.EventType;
import io.github.sycamore0.myluckyblock.lucky.LuckyEventDataManager;
import io.github.sycamore0.myluckyblock.lucky.LuckyEventExecutor;
import io.github.sycamore0.myluckyblock.lucky.reader.EventPackDataReader;
import io.github.sycamore0.myluckyblock.utils.helper.StringHelper;
import io.github.sycamore0.myluckyblock.lucky.reader.RandomEventDataReader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyLuckyBlockCommand {
    private static final SimpleCommandExceptionType ERR_NOT_LOADED = new SimpleCommandExceptionType(Component.translatable("commands.myluckyblock.not_loaded"));
    private static final SimpleCommandExceptionType ERR_NO_SUCH_EVENT = new SimpleCommandExceptionType(Component.translatable("commands.myluckyblock.no_such_event"));

    private MyLuckyBlockCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("myluckyblock")
                        .then(Commands.literal("debug")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.argument("fullEventPackId", StringArgumentType.string())
                                        .suggests((ctx, builder) -> {
                                            LuckyEventDataManager m = BreakLuckyBlock.manager;
                                            if (m == null) return builder.buildFuture();
                                            m.ensureAllGroupsLoaded();
                                            for (String packId : m.getAllPackIds()) {
                                                builder.suggest(StringHelper.quote(packId));
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("eventId", IntegerArgumentType.integer(1))
                                                .suggests((ctx, builder) -> {
                                                    LuckyEventDataManager m = BreakLuckyBlock.manager;
                                                    if (m == null) return builder.buildFuture();
                                                    m.ensureAllGroupsLoaded();
                                                    String fullId = StringArgumentType.getString(ctx, "fullEventPackId");
                                                    for (int id : m.getEventIdsByFullId(fullId)) {
                                                        builder.suggest(id);
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(MyLuckyBlockCommand::execute))))

                        .then(Commands.literal("list")
                                .requires(src -> src.hasPermission(1))
                                .executes(MyLuckyBlockCommand::executeList))
        );
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();

        LuckyEventDataManager manager = BreakLuckyBlock.manager;
        if (manager == null) {
            throw ERR_NOT_LOADED.create();
        }
        manager.ensureAllGroupsLoaded();

        String fullEventPackId = StringArgumentType.getString(ctx, "fullEventPackId");
        int eventId = IntegerArgumentType.getInteger(ctx, "eventId");

        RandomEventDataReader event = manager.getEventByFullId(fullEventPackId, eventId);
        if (event == null) {
            Constants.LOG.warn("no such event {}:{}", fullEventPackId, eventId);
            throw ERR_NO_SUCH_EVENT.create();
        }

        @Nullable Player player = source.getPlayer();
        BlockPos pos = BlockPos.containing(source.getPosition());

        LuckyEventExecutor.executeLuckyFunction(level, player, pos, event);

        if (player != null) {
            player.displayClientMessage(
                    Component.translatable("commands.myluckyblock.triggered", fullEventPackId, eventId),
                    false
            );
        }
        return 1;
    }

    private static int executeList(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();

        LuckyEventDataManager manager = BreakLuckyBlock.manager;
        if (manager == null) {
            throw ERR_NOT_LOADED.create();
        }
        manager.ensureAllGroupsLoaded();

        List<LuckyEventDataManager.LoadedPack> loadedPacks = manager.getAllLoadedPacks();

        Component header = Component.translatable("commands.myluckyblock.list", loadedPacks.size());
        source.sendSuccess(() -> header, false);

        if (loadedPacks.isEmpty()) {
            source.sendSuccess(
                    () -> Component.literal("  (none)").withStyle(ChatFormatting.GRAY),
                    true
            );
            return 0;
        }

        for (LuckyEventDataManager.LoadedPack pack : loadedPacks) {
            for (Component line : buildPackLines(pack)) {
                source.sendSuccess(() -> line, true);
            }
        }
        return loadedPacks.size();
    }

    private static List<Component> buildPackLines(LuckyEventDataManager.LoadedPack pack) {
        EventPackDataReader meta = pack.eventPackData();
        int luckyCount = pack.eventCountOf(EventType.LUCKY);
        int unluckyCount = pack.eventCountOf(EventType.UNLUCKY);
        int commonCount = pack.eventCountOf(EventType.COMMON);
        int totalCount = pack.eventCount();

        String stat = String.format("(total=%d, lucky=%d, unlucky=%d, common=%d)", totalCount, luckyCount, unluckyCount, commonCount);

        List<Component> lines = new ArrayList<>(4);
        lines.add(Component.literal(pack.eventPackId() + " " + stat).withStyle(ChatFormatting.AQUA));
        lines.add(Component.literal("Name: " + meta.getName()).withStyle(ChatFormatting.GRAY));
        lines.add(Component.literal("Version: " + meta.getVersion()).withStyle(ChatFormatting.GRAY));
        lines.add(Component.literal("Info: " + meta.getInfo()).withStyle(ChatFormatting.GRAY));
        return lines;
    }
}