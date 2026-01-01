package jamsam.shellexample.demo.services;

import jamsam.shellexample.demo.config.SinkConnectorConfig;
import jamsam.shellexample.demo.config.WebClientConfig;
import jamsam.shellexample.demo.model.ConsumerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {

    @Autowired
    private final WebClientConfig webClient;
    private final SinkConnectorConfig sinkConnectorConfig;

    private static final String CONNECTOR_BASE_URL = "http://localhost:8083/connectors";

    public boolean checkConnectorExists(String name) {

        String response = webClient.connectorWebClient().get()
                .uri(CONNECTOR_BASE_URL + "/" + name)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> Mono.empty())// .error(new ResourceNotFoundException("404 NOT FOUND")))
                .bodyToMono(String.class)
                .block();

        return response != null;// && !response.contains("404");
    }

    public String deleteConnector(ConsumerConfig consumerConfig) {
        if (checkConnectorExists(consumerConfig.getName())) {
            webClient.connectorWebClient().delete()
                    .uri(CONNECTOR_BASE_URL + "/" + consumerConfig.getName())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return "The Sink connector " + consumerConfig.getName() + " has been successfully deleted.";
        } else {
            return "No Sink connector with name " + consumerConfig.getName() + " exists.";
        }
    }

    public String createConsumer(ConsumerConfig consumerConfig) {
        String response;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        String topicsInput = consumerConfig.getTopics().trim();
        JSONObject configJson = new JSONObject()
                .put("connector.class", sinkConnectorConfig.CONNECTOR_SINK_CLASS)
                .put("tasks.max", sinkConnectorConfig.MAX_TASKS)
                .put("connection.url", sinkConnectorConfig.CONNECTION_SINK_URL)
                .put("connection.user", sinkConnectorConfig.CONNECTION_SINK_USER)
                .put("connection.password", sinkConnectorConfig.CONNECTION_SINK_PASSWORD)
                .put("max.retries", sinkConnectorConfig.MAX_RETRIES)
                .put("insert.mode", sinkConnectorConfig.INSERT_MODE)
                .put("auto.create", sinkConnectorConfig.AUTO_CREATE)
                .put("auto.evolve", sinkConnectorConfig.AUTO_EVOLVE)
                .put("pk.mode", sinkConnectorConfig.PK_MODE)
                .put("pk.fields", sinkConnectorConfig.PK_FIELDS);

        // Handle topics or topics.regex logic
        if (topicsInput.isEmpty()) {
            throw new IllegalArgumentException("Topics cannot be empty");
        }

        if (topicsInput.endsWith("*")) {
            log.info("The topicsInput {}", topicsInput );
            String prefix = topicsInput.substring(0, topicsInput.length() - 1);
            String regexPattern = "^" + Pattern.quote(prefix) + ".*";
            configJson.put("topics.regex", regexPattern);
            log.info("Using topics.regex: {}", regexPattern);
        } else {
            String[] topicsSplit = topicsInput.split(",");
            String topicsList = Arrays.stream(topicsSplit)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining(","));
            configJson.put("topics", topicsList);
            log.info("Using topics: {}", topicsList);
        }

        String json = new JSONObject()
                .put("name", consumerConfig.getName())
                .put("config", configJson)
                .toString();
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        log.info("The request >>>>>>>>>>>>>>>>>>>>>>>>>>> {}", request.getBody());

        String connectorName = consumerConfig.getName();
        String connectorUrl = CONNECTOR_BASE_URL + "/" + connectorName;

        if (checkConnectorExists(connectorName)) {

            // Create (replaces) connector
            response = webClient.connectorWebClient().put()
                    .uri(connectorUrl+ "/config")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(configJson.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            response = "Updated sink connector: " + response;
        } else {
            response = webClient.connectorWebClient().post()
                    .uri(CONNECTOR_BASE_URL)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Created connector response: {}", response);
        }
        return response;
    }
}
