package jamsam.shellexample.demo.services;

import jamsam.shellexample.demo.config.SinkConnectorConfig;
import jamsam.shellexample.demo.model.ConsumerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@RequiredArgsConstructor
public class Consumer {

    @Autowired
    private ConnectorUtils connectorUtils;
    private final SinkConnectorConfig sinkConnectorConfig;

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

        if (connectorUtils.checkConnectorExists(connectorName)) {
            // Create (replaces) connector
            response = connectorUtils.updateConnector(connectorName, configJson.toString());
            response = "Updated sink connector: " + response;
        } else {
            response = connectorUtils.createConnector(json);
            log.info("Created connector response: {}", response);
        }
        return response;
    }
}
