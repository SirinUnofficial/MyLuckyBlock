package io.github.sycamore0.myluckyblock;

public class CommonClass {
    public static void init() {
    }

    public static void addEventPackGroup(String eventPackGroupName) {
        if (!Constants.EVENT_PACK_GROUP_LIST.contains(eventPackGroupName)) {
            Constants.EVENT_PACK_GROUP_LIST.add(eventPackGroupName);
        }
    }

    /**
     * @deprecated {@link #addEventPackGroup(String eventPackGroupName)}
     */
    @Deprecated(
            forRemoval = true
    )
    public static void addEventPackId(String eventPackId) {
        addEventPackGroup(eventPackId);
        String caller = Thread.currentThread().getStackTrace()[2].getClassName();
        Constants.LOG.error("CommonClass.addEventPackId(String) is deprecated, use addEventPackGroup instead! AT {}", caller);
    }
}
