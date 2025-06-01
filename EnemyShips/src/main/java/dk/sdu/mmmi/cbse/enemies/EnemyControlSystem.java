package dk.sdu.mmmi.cbse.enemies;

import dk.sdu.mmmi.cbse.common.bullet.BulletSPI;
import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;

import java.util.*;
import java.util.stream.Collectors;

public class EnemyControlSystem implements IEntityProcessingService {

    private final Random random = new Random();
    private final int maxEnemies = 5;
    private final float spawnInterval = 1f;
    private float spawnTimer = 0;

    // timers per enemy
    private final Map<Entity, Float> directionTimers = new HashMap<>();
    private final Map<Entity, Float> shootTimers = new HashMap<>();

    @Override
    public void process(GameData gameData, World world) {
        spawnTimer += gameData.getDelta();
        spawnEnemies(gameData, world);

        for (Entity e : world.getEntities()) {
            // only handle enemies, but avoid direct Enemy.class
            if (!e.getClass().getSimpleName().equals("Enemy")) continue;

            moveEnemy(e, gameData);
            keepInsideScreen(e, gameData);
            maybeShoot(e, gameData, world);
        }

        // clean up timers if enemy is gone
        directionTimers.keySet().removeIf(e -> !world.getEntities().contains(e));
        shootTimers.keySet().removeIf(e -> !world.getEntities().contains(e));
    }

    private void spawnEnemies(GameData gameData, World world) {
        int count = 0;
        for (Entity e : world.getEntities()) {
            if (e.getClass().getSimpleName().equals("Enemy")) {
                count++;
            }
        }

        while (count < maxEnemies && spawnTimer >= spawnInterval) {
            try {
                Entity newEnemy = new EnemyPlugin().createEnemy(gameData);
                world.addEntity(newEnemy);
                directionTimers.put(newEnemy, 0f);
                shootTimers.put(newEnemy, 0f);
                spawnTimer -= spawnInterval;
                count++;
            } catch (Exception ex) {
                // ignore if enemy can't spawn
                break;
            }
        }
    }

    private void moveEnemy(Entity e, GameData gameData) {
        float t = directionTimers.getOrDefault(e, 0f);
        t += gameData.getDelta();

        if (t >= 1.5f) {
            int dir = random.nextBoolean() ? 1 : -1;
            e.setRotation(e.getRotation() + dir * (5 + random.nextInt(6)));
            t = 0f;
        }

        directionTimers.put(e, t);

        double angle = Math.toRadians(e.getRotation());
        e.setX(e.getX() + Math.cos(angle));
        e.setY(e.getY() + Math.sin(angle));
    }

    private void keepInsideScreen(Entity e, GameData data) {
        boolean bounced = false;

        if (e.getX() < 0 || e.getX() > data.getDisplayWidth()) {
            e.setX(Math.max(0, Math.min(e.getX(), data.getDisplayWidth())));
            bounced = true;
        }

        if (e.getY() < 0 || e.getY() > data.getDisplayHeight()) {
            e.setY(Math.max(0, Math.min(e.getY(), data.getDisplayHeight())));
            bounced = true;
        }

        if (bounced) {
            e.setRotation(e.getRotation() + 180);
        }
    }

    private void maybeShoot(Entity e, GameData gameData, World world) {
        float timer = shootTimers.getOrDefault(e, 0f);
        timer += gameData.getDelta();

        if (timer >= 2.5f) {
            if (random.nextDouble() < 0.2) { // 20% chance
                getBulletSPIs().stream().findFirst().ifPresent(spi -> {
                    Entity bullet = spi.createBullet(e, gameData);
                    world.addEntity(bullet);
                });
            }
            timer = 0f;
        }

        shootTimers.put(e, timer);
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
    }
}
