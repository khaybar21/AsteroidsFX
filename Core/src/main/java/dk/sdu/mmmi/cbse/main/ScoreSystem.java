package dk.sdu.mmmi.cbse.main;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ScoreSystem {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8080";

    public void sendKillUpdate(String type) {
        try {
            String url = baseUrl + "/" + type;
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("Kill update failed '" + type + "': " + e.getMessage());
        }
    }
}
