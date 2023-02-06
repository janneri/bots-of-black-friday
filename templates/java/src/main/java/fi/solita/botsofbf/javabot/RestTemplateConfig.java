package fi.solita.botsofbf.javabot;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                //.rootUri("https://bots-of-black-friday-tampere.azurewebsites.net")
                .rootUri("http://localhost:8080")
                .build();
    }
}
