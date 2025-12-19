package app;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class StarParticle {
    float x, y, size, speed;
    float alpha;

    StarParticle(int width, int height) {
        Random r = ThreadLocalRandom.current();
        x = r.nextInt(Math.max(1, width));
        y = r.nextInt(Math.max(1, height));
        size = 1 + r.nextFloat() * 2;
        speed = 0.2f + r.nextFloat() * 0.8f;
        alpha = 0.3f + r.nextFloat() * 0.7f;
    }

    void update(int height) {
        y += speed;
        if (y > height) {
            y = -5;
            x = ThreadLocalRandom.current().nextInt(900); // Default width approx
        }
    }
}