package io.solar;

import lombok.AllArgsConstructor;

import javax.swing.*;
import java.awt.*;

public class Test {

    static class SpaceShip {
        float position;
        float speed;
        float acceleration;
    }

    public static void main(String ... args) {
        SpaceShip station = new SpaceShip();
        SpaceShip spaceShip = new SpaceShip();
        reset(spaceShip, station);
        canvas c = new canvas();
        c.getGraphics().drawOval(50, 50, 20, 20);
        System.out.println("hello");
        new Thread(() -> {
            while(true) {
                recalculate(station);
                recalculate(spaceShip);
                c.draw(station, spaceShip);
                if (Math.abs(station.position - spaceShip.position) < 10) {
                    if (Math.abs(station.speed - spaceShip.speed) < 10) {
                        System.out.println("we have a winner");
                        break;
                    } else {
                        System.err.println("position close, but speed high");
                        reset(spaceShip, station);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    //ignore
                }
            }
        }).start();

    }

    public static void reset(SpaceShip spaceShip, SpaceShip station) {
        spaceShip.position = 0;
        spaceShip.speed = 0;
        spaceShip.acceleration = 1;

        station.position = 100;
        station.speed = 1;
    }
    public static void recalculate(SpaceShip spaceShip) {
        spaceShip.position += spaceShip.speed;
        spaceShip.speed += spaceShip.acceleration;
    }
    static class canvas extends JFrame {

        Canvas c;
        // constructor
        public canvas() {
            super("canvas");
            c = new Canvas();

            // set background
            c.setBackground(Color.black);

            add(c);
            setSize(400, 300);
            setVisible(true);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }

        public void draw(SpaceShip station, SpaceShip spaceShip) {
            Graphics graphic = c.getGraphics();
            c.setBackground(Color.GRAY);
            c.setForeground(Color.BLUE);
            graphic.clearRect(0, 0, 5000,  5000);
            graphic.setColor(Color.red);
            graphic.fillOval((int)spaceShip.position, 50, 5, 5);
            graphic.setColor(Color.yellow);
            graphic.fillRect(((int)station.position), 50, 5, 5);
        }
    }
}
