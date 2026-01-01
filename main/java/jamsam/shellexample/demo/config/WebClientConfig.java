package jamsam.shellexample.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
@Bean
public WebClient connectorWebClient() {
    return WebClient.builder().baseUrl("http://localhost:8083/connectors").build();
}
}
