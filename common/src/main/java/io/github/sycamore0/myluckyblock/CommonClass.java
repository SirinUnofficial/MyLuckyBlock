package io.github.sycamore0.myluckyblock;

public class CommonClass {
    public static void init() {
        addEventPackGroup(Constants.MOD_ID);
    }

    public static void addEventPackGroup(String eventPackGroupName) {
        if (!Constants.EVENT_PACK_GROUP_LIST.contains(eventPackGroupName)) {
            Constants.EVENT_PACK_GROUP_LIST.add(eventPackGroupName);
        }
    }
}
