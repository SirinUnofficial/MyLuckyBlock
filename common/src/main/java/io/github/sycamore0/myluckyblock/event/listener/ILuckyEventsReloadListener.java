package io.github.sycamore0.myluckyblock.event.listener;

import io.github.sycamore0.myluckyblock.lucky.reader.EventPack;

import java.util.List;
import java.util.Map;

public interface ILuckyEventsReloadListener {
    Map<String, List<EventPack>> getPacksByGroup();

    void setOnReload(Runnable callback);
}
