package dk.sdu.mmmi.cbse.collisionsystem;

import dk.sdu.mmmi.cbse.common.asteroids.Asteroid;
import dk.sdu.mmmi.cbse.common.asteroids.IAsteroidSplitter;
import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

import java.util.ServiceLoader;

public class CollisionDetector implements IPostEntityProcessingService {

    private final ServiceLoader<IAsteroidSplitter> splitterLoader = ServiceLoader.load(IAsteroidSplitter.class);

    @Override
    public void process(GameData gameData, World world) {
        for (Entity e1 : world.getEntities()) {
            for (Entity e2 : world.getEntities()) {
                if (e1.getID().equals(e2.getID())) continue;

                if (collides(e1, e2)) {
                    handleCollision(e1, e2, world);
                }
            }
        }
    }

    private boolean collides(Entity e1, Entity e2) {
        float dx = (float) (e1.getX() - e2.getX());
        float dy = (float) (e1.getY() - e2.getY());
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < (e1.getRadius() + e2.getRadius());
    }

    private void handleCollision(Entity e1, Entity e2, World world) {
        String c1 = e1.getClass().getSimpleName();
        String c2 = e2.getClass().getSimpleName();

        boolean isBullet = e1 instanceof Bullet || e2 instanceof Bullet;
        boolean isAsteroid = e1 instanceof Asteroid || e2 instanceof Asteroid;
        boolean isPlayer = c1.equals("Player") || c2.equals("Player");
        boolean isEnemy = c1.equals("Enemy") || c2.equals("Enemy");

        // Bullet hits asteroid
        if (isBullet && isAsteroid) {
            Entity asteroid = e1 instanceof Asteroid ? e1 : e2;
            createAsteroidSplit(asteroid, world);
            world.removeEntity(asteroid);
            world.removeEntity(e1);
            world.removeEntity(e2);
        }

        // Bullet hits player or enemy
        else if (isBullet && (isEnemy || isPlayer)) {
            Entity target = e1 instanceof Bullet ? e2 : e1;
            target.decreaseHp(1);

            Entity bullet = e1 instanceof Bullet ? e1 : e2;
            world.removeEntity(bullet);

            if (target.getHp() <= 0) {
                world.removeEntity(target);
            }
        }

        // Player hits asteroid = only player dies
        else if (isAsteroid && isPlayer) {
            Entity player = c1.equals("Player") ? e1 : e2;
            world.removeEntity(player);
        }

        // Enemy hits asteroid  = both are removed
        else if (isAsteroid && isEnemy) {
            world.removeEntity(e1);
            world.removeEntity(e2);
        }

        // Enemy and player collide  = both are removed
        else if (isEnemy && isPlayer) {
            world.removeEntity(e1);
            world.removeEntity(e2);
        }
    }

    private void createAsteroidSplit(Entity asteroid, World world) {
        for (IAsteroidSplitter splitter : splitterLoader) {
            splitter.createSplitAsteroid(asteroid, world);
        }
    }
}
