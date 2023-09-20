package cz.cvut.fel.camunda.workshops.developer.congif;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@PropertySource("classpath:secret.properties")
public class AppConfig {
    @Value("${api-key}")
    private String apiKey;

    public String getApiKey() {
        byte[] decodedBytes = Base64.getDecoder().decode(apiKey);

        // Convert the decoded bytes to a UTF-8 string
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}