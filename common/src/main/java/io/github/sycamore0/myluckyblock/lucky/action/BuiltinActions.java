package io.github.sycamore0.myluckyblock.lucky.action;

import io.github.sycamore0.myluckyblock.lucky.LuckyEventExecutor;
import io.github.sycamore0.myluckyblock.lucky.reader.RandomEventDataReader;

final class BuiltinActions {
    private static boolean registered;

    private BuiltinActions() {
    }

    /*
    // You can use this method to register your own actions.
    LuckyActions.register("my_action", (context, step) -> Constants.GSON.fromJson(step.get("data"), MyPayload.class));
     */
    static synchronized void register() {
        if (registered) return;
        registered = true;

        LuckyActions.register("drop_items", (context, step) ->
                LuckyEventExecutor.runDropItems(context,
                        LuckyActions.payload(step, RandomEventDataReader.DropItem.class)));

        LuckyActions.register("place_blocks", (context, step) ->
                LuckyEventExecutor.runPlaceBlocks(context,
                        LuckyActions.payload(step, RandomEventDataReader.PlaceBlock.class)));

        LuckyActions.register("place_chests", (context, step) ->
                LuckyEventExecutor.runPlaceChests(context,
                        LuckyActions.payload(step, RandomEventDataReader.PlaceChest.class)));

        LuckyActions.register("drop_loots", (context, step) ->
                LuckyEventExecutor.runDropLoots(context,
                        LuckyActions.payload(step, RandomEventDataReader.DropLoots.class)));

        LuckyActions.register("fall_blocks", (context, step) ->
                LuckyEventExecutor.runFallBlocks(context,
                        LuckyActions.payload(step, RandomEventDataReader.FallBlock.class)));

        LuckyActions.register("give_potion_effects", (context, step) ->
                LuckyEventExecutor.runGivePotionEffects(context,
                        LuckyActions.payload(step, RandomEventDataReader.GivePotionEffect.class)));

        LuckyActions.register("spawn_mobs", (context, step) ->
                LuckyEventExecutor.runSpawnMobs(context,
                        LuckyActions.payload(step, RandomEventDataReader.SpawnMob.class)));

        LuckyActions.register("send_messages", (context, step) ->
                LuckyEventExecutor.runSendMessages(context,
                        LuckyActions.payload(step, RandomEventDataReader.SendMessage.class)));

        LuckyActions.register("display_messages", (context, step) ->
                LuckyEventExecutor.runDisplayMessages(context,
                        LuckyActions.payload(step, RandomEventDataReader.DisplayMessage.class)));

        LuckyActions.register("create_explosions", (context, step) ->
                LuckyEventExecutor.runCreateExplosions(context,
                        LuckyActions.payload(step, RandomEventDataReader.CreateExplosion.class)));

        LuckyActions.register("add_particles", (context, step) ->
                LuckyEventExecutor.runAddParticles(context,
                        LuckyActions.payload(step, RandomEventDataReader.AddParticle.class)));

        LuckyActions.register("play_sounds", (context, step) ->
                LuckyEventExecutor.runPlaySounds(context,
                        LuckyActions.payload(step, RandomEventDataReader.PlaySound.class)));

        LuckyActions.register("load_structures", (context, step) ->
                LuckyEventExecutor.runLoadStructures(context,
                        LuckyActions.payload(step, RandomEventDataReader.LoadStructure.class)));

        LuckyActions.register("execute_commands", (context, step) ->
                LuckyEventExecutor.runExecuteCommands(context,
                        LuckyActions.payload(step, RandomEventDataReader.ExecuteCommand.class)));

        LuckyActions.register("lucky_well", WellAction::execute);
    }
}
