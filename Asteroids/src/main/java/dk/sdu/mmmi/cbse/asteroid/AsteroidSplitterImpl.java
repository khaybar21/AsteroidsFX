package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.asteroids.IAsteroidSplitter;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.World;

public class AsteroidSplitterImpl implements IAsteroidSplitter {

    @Override
    public void createSplitAsteroid(Entity original, World world) {
        float radius = original.getRadius();

        // don't split if asteroid is already too small
        if (radius <= 5) return;

        float newRadius = 4;

        for (int i = 0; i < 2; i++) {
            Entity split = new Asteroid();

            split.setX(original.getX());
            split.setY(original.getY());
            split.setRotation(original.getRotation() + (i == 0 ? 20 : -20));
            split.setRadius(newRadius);
            split.setPolygonCoordinates(
                    newRadius, -newRadius,
                    -newRadius, -newRadius,
                    -newRadius, newRadius,
                    newRadius, newRadius
            );
            split.setColor(original.getColor());

            world.addEntity(split);
        }
    }
}
