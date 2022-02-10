package io.solar.service.engine;

import io.solar.entity.objects.BasicObject;
import io.solar.service.engine.interfaces.StarMapEngine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StarMapEngineImpl implements StarMapEngine {

    @Override
    public float calculateDistanceBetweenObjects(BasicObject firstObject, BasicObject secondObject) {
        double deltaX = firstObject.getX() - secondObject.getX();
        double deltaY = firstObject.getY() - secondObject.getY();

        return (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    @Data
    @AllArgsConstructor
    public static class CoordinatePoint {

        private Double x;
        private Double y;

    }

}