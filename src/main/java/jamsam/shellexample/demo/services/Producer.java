package jamsam.shellexample.demo.services;

import jamsam.shellexample.demo.config.SourceConnectorConfig;
import jamsam.shellexample.demo.model.ProducerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@RequiredArgsConstructor
public class Producer {

    @Autowired
    private ConnectorUtils connectorUtils;
    private final SourceConnectorConfig connectorConfig;


    public String createProducer(ProducerConfig producerConfig) {
        String response;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        JSONObject configJson = new JSONObject()
                .put("connector.class", connectorConfig.CONNECTOR_SOURCE_CLASS)
                .put("tasks.max", connectorConfig.MAX_TASKS)
                .put("connection.url", connectorConfig.CONNECTION_SOURCE_URL)
                .put("connection.user", connectorConfig.CONNECTION_SOURCE_USER)
                .put("connection.password", connectorConfig.CONNECTION_SOURCE_PASSWORD)
                .put("table.whitelist", producerConfig.getTableWhitelist())
                .put("tables", producerConfig.getTables())
                .put("mode", producerConfig.getMode())
                .put("topic.prefix", producerConfig.getTopicPrefix())
                .put("numeric.mapping", connectorConfig.NUMERIC_MAPPING)
                .put("poll.interval.ms", connectorConfig.POLL_INTERVAL)
                .put("batch.max.rows", connectorConfig.MAX_BATCH_ROW)
                .put("incrementing.column.name", producerConfig.getIncrementingColumnName())
                .put("timestamp.column.name", producerConfig.getTimestampColumnName())
                .put("query", producerConfig.getQuery())
                .put("table.types", producerConfig.getTableTypes());

        if (producerConfig.getMode() != null && producerConfig.getMode().equalsIgnoreCase("timestamp")) {
            configJson.put("poll.interval.ms", 5000);
        }

        String json = new JSONObject()
                .put("name", producerConfig.getName())
                .put("config", configJson)
                .toString();

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        log.info("The request >>>>>>>>>>>>>>>>>>>>>>>>>>> {}", request.getBody());

        if (connectorUtils.checkConnectorExists(producerConfig.getName())) {
            response = connectorUtils.updateConnector(producerConfig.getName(), configJson.toString());
            response = "Updated the source connector " + response;
        } else {
            response = connectorUtils.createConnector(json);
            log.info("The response >>>>>>>>>>>>>>>>>>>>>>>>>>> {}", response);
        }
        return response;
    }

}
