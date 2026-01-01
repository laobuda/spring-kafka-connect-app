package jamsam.shellexample.demo.services;

import jamsam.shellexample.demo.config.SourceConnectorConfig;
import jamsam.shellexample.demo.config.WebClientConfig;
import jamsam.shellexample.demo.model.ProducerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@RequiredArgsConstructor
public class Producer {

    private String url = "http://localhost:8083/connectors";
    @Autowired
    private final WebClientConfig webClient;
    private final SourceConnectorConfig connectorConfig;

  public boolean checkConnectorExists(String name) {

      String response = webClient.connectorWebClient().get()
              .uri(url + "/" + name)
              .retrieve()
              .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
              .bodyToMono(String.class)
              .block();

      return response != null;
  }

    public String deleteConnector(ProducerConfig producerConfig) {
        String result;
        if (checkConnectorExists(producerConfig.getName())) {
            webClient.connectorWebClient().delete()
                    .uri(url + "/" + producerConfig.getName())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            result = "The Source connector " + producerConfig.getName() + " has been successfully deleted.";
        } else {
            result = "No Source connector with name " + producerConfig.getName() + " exists.";
        }
        return result;
    }

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

        if (checkConnectorExists(producerConfig.getName())) {
            response = webClient.connectorWebClient().put()
                    .uri(url + "/" + producerConfig.getName() + "/config")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(configJson.toString())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            response = "Updated the source connector " + response;
        } else {
            response = webClient.connectorWebClient().post()
                    .uri(url + "/" )
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(json)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("The response >>>>>>>>>>>>>>>>>>>>>>>>>>> {}", response);
        }
        return response;
    }

}
