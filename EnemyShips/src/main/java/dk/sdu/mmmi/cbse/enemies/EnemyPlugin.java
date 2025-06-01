package dk.sdu.mmmi.cbse.enemies;

import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import javafx.scene.paint.Color;

import java.util.Random;

public class EnemyPlugin implements IGamePluginService {

    private Entity enemy;

    @Override
    public void start(GameData gameData, World world) {
        enemy = createEnemy(gameData);
        world.addEntity(enemy);
    }

    public Entity createEnemy(GameData gameData) {
        Entity e = new Enemy();

        Random r = new Random();
        float x, y, rot;
        int side = r.nextInt(4);

        switch (side) {
            case 0: x = 0; y = r.nextInt(gameData.getDisplayHeight()); rot = r.nextInt(120) - 60; break;
            case 1: x = gameData.getDisplayWidth(); y = r.nextInt(gameData.getDisplayHeight()); rot = 180 + r.nextInt(120) - 60; break;
            case 2: x = r.nextInt(gameData.getDisplayWidth()); y = 0; rot = 90 + r.nextInt(120) - 60; break;
            case 3: x = r.nextInt(gameData.getDisplayWidth()); y = gameData.getDisplayHeight(); rot = 270 + r.nextInt(120) - 60; break;
            default: x = 300; y = 300; rot = r.nextInt(360); // fallback
        }

        e.setX(x);
        e.setY(y);
        e.setRotation(rot);
        e.setRadius(10);
        e.setPolygonCoordinates(-5, -5, 10, 0, -5, 5);
        e.setColor(Color.RED);
        e.setHp(5); // basic HP

        return e;
    }

    @Override
    public void stop(GameData gameData, World world) {
        world.removeEntity(enemy);
    }
}
