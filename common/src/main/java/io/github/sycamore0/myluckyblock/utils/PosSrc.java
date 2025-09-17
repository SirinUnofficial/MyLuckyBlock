package io.github.sycamore0.myluckyblock.utils;

public enum PosSrc {
    BLOCK(0),
    PLAYER(1);

    private final int value;

    PosSrc(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static PosSrc fromValue(int value) {
        for (PosSrc posSrc : PosSrc.values()) {
            if (posSrc.getValue() == value) {
                return posSrc;
            }
        }
        return BLOCK;
    }
}
