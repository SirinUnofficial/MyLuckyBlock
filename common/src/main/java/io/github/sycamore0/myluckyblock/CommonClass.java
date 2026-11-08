package io.github.sycamore0.myluckyblock;

import io.github.sycamore0.myluckyblock.lucky.action.LuckyActions;

public class CommonClass {
    public static void init() {
        LuckyActions.registerBuiltins();
    }

    public static void addEventPackGroup(String eventPackGroupName) {
        if (!Constants.EVENT_PACK_GROUP_LIST.contains(eventPackGroupName)) {
            Constants.EVENT_PACK_GROUP_LIST.add(eventPackGroupName);
        }
    }
}
