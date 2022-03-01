package io.solar.multithreading;

import io.solar.service.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StationMonitor {
    private final Map<Long, Object> stationMonitors = new HashMap<>();

    public void createMonitor(Long stationId) {

        stationMonitors.putIfAbsent(stationId, new Object());
    }

    public Object getMonitor(Long stationId) {
        Object stationMonitor = stationMonitors.get(stationId);

        if (stationMonitor == null) {
            throw new ServiceException("Cannot find station monitor by id = " + stationId);
        }

        return stationMonitor;
    }
}
