package dk.sdu.mmmi.cbse.asteroid;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.Random;

public class AsteroidControlSystem implements IEntityProcessingService {

    private Random random = new Random();
    private float timer = 0;

    @Override
    public void process(GameData gameData, World world) {
        timer += gameData.getDelta();

        if (timer >= 1.5f && world.getEntities(Asteroid.class).size() < 10) {
            world.addEntity(createAsteroid(gameData));
            timer = 0;
        }
    }

    private Entity createAsteroid(GameData gameData) {
        Entity asteroid = new Asteroid();

        // Random size between 10 and 30
        int size = random.nextInt(20) + 10;
        asteroid.setPolygonCoordinates(size, -size, -size, -size, -size, size, size, size);
        asteroid.setRadius(size / 2f);
        asteroid.setColor(javafx.scene.paint.Color.BLACK);

        // Spawn asteroid on one of the screen edges
        int side = random.nextInt(4);
        float x = 0, y = 0, rotation = 0;

        if (side == 0) { // Left
            x = 0;
            y = random.nextInt(gameData.getDisplayHeight());
            rotation = random.nextInt(120) - 60;
        } else if (side == 1) { // Right
            x = gameData.getDisplayWidth();
            y = random.nextInt(gameData.getDisplayHeight());
            rotation = 180 + random.nextInt(120) - 60;
        } else if (side == 2) { // Top
            x = random.nextInt(gameData.getDisplayWidth());
            y = 0;
            rotation = 90 + random.nextInt(120) - 60;
        } else { // Bottom
            x = random.nextInt(gameData.getDisplayWidth());
            y = gameData.getDisplayHeight();
            rotation = 270 + random.nextInt(120) - 60;
        }

        asteroid.setX(x);
        asteroid.setY(y);
        asteroid.setRotation(rotation);

        return asteroid;
    }
}
