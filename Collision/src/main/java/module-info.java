import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

module Collision {
    requires Common;
    requires static Bullet;
    requires CommonBullet;
   requires static Player;

    requires CommonAsteroids;

    uses dk.sdu.mmmi.cbse.common.asteroids.IAsteroidSplitter;

    provides dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService
            with dk.sdu.mmmi.cbse.collisionsystem.CollisionDetector;
}