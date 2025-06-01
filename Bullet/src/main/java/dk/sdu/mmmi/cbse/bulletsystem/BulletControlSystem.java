package dk.sdu.mmmi.cbse.bulletsystem;

import dk.sdu.mmmi.cbse.common.bullet.Bullet;
import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import javafx.scene.paint.Color;

import java.util.*;

public class BulletControlSystem implements IEntityProcessingService, BulletSPI {

    private static final float MAX_LIFETIME = 1.5f;
    private static final float BULLET_SPEED = 10f;

    private final Map<Entity, Float> bulletTimers = new HashMap<>();

    @Override
    public void process(GameData gameData, World world) {
        List<Entity> toRemove = new ArrayList<>();

        for (Entity bullet : world.getEntities(Bullet.class)) {
            double angle = Math.toRadians(bullet.getRotation());
            bullet.setX(bullet.getX() + Math.cos(angle) * BULLET_SPEED);
            bullet.setY(bullet.getY() + Math.sin(angle) * BULLET_SPEED);

            float lifetime = bulletTimers.getOrDefault(bullet, 0f) + gameData.getDelta();
            bulletTimers.put(bullet, lifetime);

            if (lifetime >= MAX_LIFETIME) {
                toRemove.add(bullet);
            }
        }

        for (Entity b : toRemove) {
            bulletTimers.remove(b);
            world.removeEntity(b);
        }
    }

    @Override
    public Entity createBullet(Entity shooter, GameData gameData) {
        Entity bullet = new Bullet();

        bullet.setPolygonCoordinates(0, -2, 1, -1, 1, 1, 0, 2, -1, 1, -1, -1);

        double angle = Math.toRadians(shooter.getRotation());
        bullet.setX(shooter.getX() + Math.cos(angle) * 3);
        bullet.setY(shooter.getY() + Math.sin(angle) * 3);

        bullet.setRotation(shooter.getRotation());
        bullet.setRadius(1);
        bullet.setColor(Color.YELLOW);

        bulletTimers.put(bullet, 0f);
        return bullet;
    }
}
