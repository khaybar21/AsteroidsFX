package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.asteroids.IAsteroidSplitter;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

public class AsteroidProcessor implements IEntityProcessingService {

    private IAsteroidSplitter splitter = new AsteroidSplitterImpl();

    @Override
    public void process(GameData gameData, World world) {
        for (Entity asteroid : world.getEntities(Asteroid.class)) {
            // Move asteroid forward based on its rotation
            double dx = Math.cos(Math.toRadians(asteroid.getRotation()));
            double dy = Math.sin(Math.toRadians(asteroid.getRotation()));
            asteroid.setX(asteroid.getX() + dx * 0.5);
            asteroid.setY(asteroid.getY() + dy * 0.5);


            wrapPosition(asteroid, gameData);
        }
    }

    private void wrapPosition(Entity e, GameData data) {
        if (e.getX() < 0) e.setX(e.getX() + data.getDisplayWidth());
        if (e.getX() > data.getDisplayWidth()) e.setX(e.getX() % data.getDisplayWidth());
        if (e.getY() < 0) e.setY(e.getY() + data.getDisplayHeight());
        if (e.getY() > data.getDisplayHeight()) e.setY(e.getY() % data.getDisplayHeight());
    }

    public void setAsteroidSplitter(IAsteroidSplitter splitter) {
        this.splitter = splitter;
    }

    public void removeAsteroidSplitter(IAsteroidSplitter splitter) {
        this.splitter = null;
    }
}
