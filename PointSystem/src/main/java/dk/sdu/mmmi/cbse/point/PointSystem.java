package dk.sdu.mmmi.cbse.point;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class PointSystem {

    private int asteroidsKilled = 0;
    private int enemiesKilled = 0;

    public static void main(String[] args) {
        SpringApplication.run(PointSystem.class, args);
    }

    @GetMapping("/asteroid")
    public String addAsteroidKill() {
        asteroidsKilled++;
        return "Asteroids killed: " + asteroidsKilled;
    }

    @GetMapping("/enemy")
    public String addEnemyKill() {
        enemiesKilled++;
        return "Enemies killed: " + enemiesKilled;
    }

    @GetMapping("/")
    public String status() {
        return "<h1>Scoreboard</h1>" +
                "<p>Asteroids killed: " + asteroidsKilled + "</p>" +
                "<p>Enemies killed: " + enemiesKilled + "</p>";
    }
}