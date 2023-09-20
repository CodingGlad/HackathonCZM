package cz.cvut.fel.camunda.workshops.developer.congif;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
@Configuration
@PropertySource("classpath:secret.properties")
public class AppConfig {
    @Value("${api-key}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}