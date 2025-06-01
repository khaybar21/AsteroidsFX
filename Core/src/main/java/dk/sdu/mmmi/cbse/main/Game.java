/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dk.sdu.mmmi.cbse.main;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author jcs
 */
class Game {
    private int enemyKills = 0;
    private int asteroidKills = 0;

    private final GameData gameData = new GameData();
    private final World world = new World();

    private final Text asteroidKillText = new Text(10, 20, "Asteroids destroyed: 0");
    private final Text enemyKillText = new Text(10, 40, "Enemies destroyed: 0");

    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();
    private final Map<Entity, Text> hpLabels = new ConcurrentHashMap<>();

    private final Pane gameWindow = new Pane();

    private final List<IGamePluginService> gamePluginServices;
    private final List<IEntityProcessingService> entityProcessingServices;
    private final List<IPostEntityProcessingService> postEntityProcessingServices;
    private final ScoreSystem scoreSystem;


    public void render() {
        new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate > 0) {
                    float delta = (now - lastUpdate) / 1_000_000_000f;
                    gameData.setDelta(delta);
                }
                lastUpdate = now;

                update();
                draw();
                gameData.getKeys().update();
            }
        }.start();
    }

    public Game(List<IGamePluginService> pluginServices,
                List<IEntityProcessingService> entityProcessors,
                List<IPostEntityProcessingService> postProcessors,
                ScoreSystem scoreSystem) {
        this.gamePluginServices = pluginServices;
        this.entityProcessingServices = entityProcessors;
        this.postEntityProcessingServices = postProcessors;
        this.scoreSystem = scoreSystem;
    }

    public void start(Stage window) {
        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        gameWindow.getChildren().addAll(asteroidKillText, enemyKillText);


     // Start loaded plugins
        for (IGamePluginService plugin : gamePluginServices) {
            plugin.start(gameData, world);
        }

        Scene scene = new Scene(gameWindow);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) gameData.getKeys().setKey(GameKeys.LEFT, true);
            if (e.getCode() == KeyCode.RIGHT) gameData.getKeys().setKey(GameKeys.RIGHT, true);
            if (e.getCode() == KeyCode.UP) gameData.getKeys().setKey(GameKeys.UP, true);
            if (e.getCode() == KeyCode.SPACE) gameData.getKeys().setKey(GameKeys.SPACE, true);
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) gameData.getKeys().setKey(GameKeys.LEFT, false);
            if (e.getCode() == KeyCode.RIGHT) gameData.getKeys().setKey(GameKeys.RIGHT, false);
            if (e.getCode() == KeyCode.UP) gameData.getKeys().setKey(GameKeys.UP, false);
            if (e.getCode() == KeyCode.SPACE) gameData.getKeys().setKey(GameKeys.SPACE, false);
        });


        for (Entity e : world.getEntities()) {
             // Add entities to the screen
            Polygon p = new Polygon(e.getPolygonCoordinates());
            p.setFill(e.getColor());
            p.setStroke(Color.BLACK);
            polygons.put(e, p);
            gameWindow.getChildren().add(p);
        }

        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();
    }

   // movement and actions
    private void update() {
        for (IEntityProcessingService s : entityProcessingServices) {
            s.process(gameData, world);
        }

        for (IPostEntityProcessingService s : postEntityProcessingServices) {
            s.process(gameData, world);
        }
    }

    private void draw() {
        Set<Entity> removed = new HashSet<>();
    // Remove dead entities from game 
        for (Entity e : polygons.keySet()) {
            if (!world.getEntities().contains(e)) {
                String type = e.getClass().getSimpleName();

                if (type.equalsIgnoreCase("Enemy")) {
                    enemyKills++;
                    enemyKillText.setText("Enemies destroyed: " + enemyKills);
                    scoreSystem.sendKillUpdate("enemy");
                } else if (type.equalsIgnoreCase("Asteroid")) {
                    asteroidKills++;
                    asteroidKillText.setText("Asteroids destroyed: " + asteroidKills);
                    scoreSystem.sendKillUpdate("asteroid");
                }

                gameWindow.getChildren().remove(polygons.get(e));
                gameWindow.getChildren().remove(hpLabels.get(e));
                removed.add(e);
            }
        }

        for (Entity e : removed) {
            polygons.remove(e);
            hpLabels.remove(e);
        }


        // update/add shapes
        for (Entity e : world.getEntities()) {
            Polygon p = polygons.get(e);
            if (p == null) {
                p = new Polygon(e.getPolygonCoordinates());
                p.setFill(e.getColor());
                p.setStroke(Color.BLACK);
                polygons.put(e, p);
                gameWindow.getChildren().add(p);
            }

            p.setTranslateX(e.getX());
            p.setTranslateY(e.getY());
            p.setRotate(e.getRotation());

            String type = e.getClass().getSimpleName();

            if (type.equals("Player") || type.equals("Enemy")) {
                Text hp = hpLabels.get(e);
                if (hp == null) {
                    hp = new Text();
                    hpLabels.put(e, hp);
                    gameWindow.getChildren().add(hp);
                }
                hp.setText("HP: " + e.getHp());
                hp.setX(e.getX());
                hp.setY(e.getY() - 15);
            } else {
                Text old = hpLabels.remove(e);
                if (old != null) gameWindow.getChildren().remove(old);
            }
        }
    }

    public List<IGamePluginService> getGamePluginServices() {
        return gamePluginServices;
    }

    public List<IEntityProcessingService> getEntityProcessingServices() {
        return entityProcessingServices;
    }

    public List<IPostEntityProcessingService> getPostEntityProcessingServices() {
        return postEntityProcessingServices;
    }
}