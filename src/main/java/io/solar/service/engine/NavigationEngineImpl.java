package io.solar.service.engine;

import io.solar.entity.objects.BasicObject;
import io.solar.service.engine.interfaces.NavigationEngine;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class NavigationEngineImpl implements NavigationEngine {


    @Override
    public void setRandomSpeedInRange(BasicObject basicObject, Float speedMin, Float speedMax) {
        double randomSpeed = Math.random() * (speedMax - speedMin) + speedMin;

        double maxSpeedAtAxis = randomSpeed;
        double minSpeedAtAxis = -randomSpeed;

        double speedX = Math.random() * (maxSpeedAtAxis - minSpeedAtAxis) + minSpeedAtAxis;

        double speedY = Math.sqrt(Math.pow(randomSpeed, 2) - Math.pow(speedX, 2));
        Random random = new Random();
        speedY = random.nextBoolean() ? speedY : -speedY;
        basicObject.setSpeedX((float) speedX);
        basicObject.setSpeedY((float) speedY);
    }
}