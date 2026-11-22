package io.github.sycamore0.myluckyblock.lucky.action;

import io.github.sycamore0.myluckyblock.lucky.action.builtin.AddParticlesAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.CreateExplosionsAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.DisplayMessagesAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.DropItemsAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.DropLootsAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.ExecuteCommandsAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.FallBlocksAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.GivePotionEffectsAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.LoadStructuresAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.PlaceBlocksAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.PlaceChestsAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.PlaySoundsAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.SendMessagesAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.SpawnMobsAction;
import io.github.sycamore0.myluckyblock.lucky.action.builtin.LuckyWellAction;

final class BuiltinActions {
    private static boolean registered;

    private BuiltinActions() {
    }

    // LuckyActions.register("my_action", new MyAction());
    static synchronized void register() {
        if (registered) return;
        registered = true;

        LuckyActions.register("drop_items", new DropItemsAction());
        LuckyActions.register("place_blocks", new PlaceBlocksAction());
        LuckyActions.register("place_chests", new PlaceChestsAction());
        LuckyActions.register("drop_loots", new DropLootsAction());
        LuckyActions.register("fall_blocks", new FallBlocksAction());
        LuckyActions.register("give_potion_effects", new GivePotionEffectsAction());
        LuckyActions.register("spawn_mobs", new SpawnMobsAction());
        LuckyActions.register("send_messages", new SendMessagesAction());
        LuckyActions.register("display_messages", new DisplayMessagesAction());
        LuckyActions.register("create_explosions", new CreateExplosionsAction());
        LuckyActions.register("add_particles", new AddParticlesAction());
        LuckyActions.register("play_sounds", new PlaySoundsAction());
        LuckyActions.register("load_structures", new LoadStructuresAction());
        LuckyActions.register("execute_commands", new ExecuteCommandsAction());
        LuckyActions.register("lucky_well", new LuckyWellAction());
    }
}
