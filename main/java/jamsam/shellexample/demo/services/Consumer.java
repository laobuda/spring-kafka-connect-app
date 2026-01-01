/*
package jamsam.shellexample.demo.services;

import jamsam.shellexample.demo.config.SinkConnectorConfig;
import jamsam.shellexample.demo.model.ConsumerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {

    private String url = "http://localhost:8083/connectors";

    private final SinkConnectorConfig sinkConnectorConfig;
    private final Topics  topicsApi;

    public boolean checkConnectorExists(String name) {
        RestTemplate rt = new RestTemplate();
        String found = null;
        try {
            found = rt.getForObject(url + "/" + name, String.class);
        } catch (Exception ex) {
            found = ex.getMessage();
        }
        if (found == null || found.isEmpty() || found.contains("404")) {
            return false;
        } else {
            return true;
        }
    }

    public String deleteConnector(ConsumerConfig consumerConfig) {
        String result = null;
        if (checkConnectorExists(consumerConfig.getName())) {
            RestTemplate rt = new RestTemplate();
            rt.delete(url + "/" + consumerConfig.getName());
            result = "The Sink connector " + consumerConfig.getName() + " has been successfully deleted.";
        } else {
            result = "No Sink connector with name " + consumerConfig.getName() + " exists.";
        }
        return result;
    }

    public String getStringOutOfArray(String[] list) {

        String result = null;
        if (!(list.length < 1)) {
            int cnt = 0;
            for (String table : list) {
                if (list.length == 1) {
                    result = table;
                    break;
                } else if (cnt == 0) {
                    result = table + ", ";
                    cnt++;
                } else if (cnt <= list.length - 2) {
                    result = result + table + ", ";
                    cnt++;
                } else {
                    result = result + table;
                }
            }
        }
        return result;
    }

    public String createConsumer(ConsumerConfig consumerConfig) {
        RestTemplate rt = new RestTemplate();
        String response = null;
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
                .put("auto.evolve", sinkConnectorConfig.AUTO_EVOLVE);

        // Handle topics or topics.regex logic
        if (topicsInput.isEmpty()) {
            throw new IllegalArgumentException("Topics cannot be empty");
        }

        if (topicsInput.endsWith("*")) {
            // Use regex pattern for prefix matching (e.g., "inventory_*")
            String prefix = topicsInput.substring(0, topicsInput.length() - 1);
            String regexPattern = "^" + Pattern.quote(prefix) + ".*";
            configJson.put("topics.regex", regexPattern);
            log.info("Using topics.regex: {}", regexPattern);
        } else {
            // Use explicit topics list
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
        log.info("Connector config: {}", request.getBody());

        if (checkConnectorExists(consumerConfig.getName())) {
            rt.delete(url + "/" + consumerConfig.getName());
            response = rt.postForObject(url, request, String.class);
            return "Updated sink connector: " + response;
        } else {
            response = rt.postForObject(url, request, String.class);
            log.info("Created connector response: {}", response);
            return response;
        }
    }

}
*/
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
    private final Topics topicsApi;

    private static final String CONNECTOR_BASE_URL = "http://localhost:8083/connectors";

    public boolean checkConnectorExists(String name) {

        String response = webClient.connectorWebClient().get()
                .uri(CONNECTOR_BASE_URL + "/" + name)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> Mono.empty())// .error(new ResourceNotFoundException("404 NOT FOUND")))
                .bodyToMono(String.class)
                .block();

        return response != null && !response.contains("404");
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
                    .uri(connectorUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(json)
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
