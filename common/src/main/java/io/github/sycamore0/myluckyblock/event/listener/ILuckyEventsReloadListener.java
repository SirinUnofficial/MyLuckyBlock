package io.github.sycamore0.myluckyblock.event.listener;

import io.github.sycamore0.myluckyblock.utils.reader.EventDataReader;

import java.util.List;
import java.util.Map;

public interface ILuckyEventsReloadListener {
    Map<String, List<EventDataReader>> getData();
}
