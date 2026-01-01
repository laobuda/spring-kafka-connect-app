package jamsam.shellexample.demo.services;

import jamsam.shellexample.demo.config.WebClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Utility class for common Kafka connector operations.
 * Provides shared functionality for checking, creating, updating, and deleting
 * connectors.
 */
@Slf4j
@Component
public class ConnectorUtils {

    private final WebClientConfig webClient;

    @Value("${kafka.connector.url:http://localhost:8083/connectors}")
    private String connectorBaseUrl;

    public ConnectorUtils(WebClientConfig webClient) {
        this.webClient = webClient;
    }

    /**
     * Checks if a connector with the given name exists.
     *
     * @param name the connector name to check
     * @return true if the connector exists, false otherwise
     */
    public boolean checkConnectorExists(String name) {
        String response = webClient.connectorWebClient().get()
                .uri(connectorBaseUrl + "/" + name)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.empty())
                .bodyToMono(String.class)
                .block();

        return response != null;
    }

    /**
     * Deletes a connector by name.
     *
     * @param connectorName the name of the connector to delete
     * @param connectorType the type of connector (e.g., "Source", "Sink") for the
     *                      response message
     * @return a message indicating the result of the deletion
     */
    public String deleteConnector(String connectorName, String connectorType) {
        if (checkConnectorExists(connectorName)) {
            webClient.connectorWebClient().delete()
                    .uri(connectorBaseUrl + "/" + connectorName)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            return "The " + connectorType + " connector " + connectorName + " has been successfully deleted.";
        } else {
            return "No " + connectorType + " connector with name " + connectorName + " exists.";
        }
    }

    /**
     * Updates an existing connector's configuration.
     *
     * @param connectorName the name of the connector to update
     * @param configJson    the JSON configuration to apply
     * @return the response from the connector API
     */
    public String updateConnector(String connectorName, String configJson) {
        return webClient.connectorWebClient().put()
                .uri(connectorBaseUrl + "/" + connectorName + "/config")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(configJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Creates a new connector.
     *
     * @param json the full JSON payload including name and config
     * @return the response from the connector API
     */
    public String createConnector(String json) {
        return webClient.connectorWebClient().post()
                .uri(connectorBaseUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(json)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Creates or updates a connector based on whether it already exists.
     *
     * @param connectorName the name of the connector
     * @param configJson    the JSON configuration (for updates)
     * @param fullJson      the full JSON payload including name and config (for
     *                      creation)
     * @param connectorType the type of connector (e.g., "source", "sink") for
     *                      logging
     * @return the response from the connector API with appropriate prefix
     */
    public String createOrUpdateConnector(String connectorName, String configJson, String fullJson,
            String connectorType) {
        if (checkConnectorExists(connectorName)) {
            String response = updateConnector(connectorName, configJson);
            return "Updated the " + connectorType + " connector " + response;
        } else {
            String response = createConnector(fullJson);
            log.info("Created {} connector response: {}", connectorType, response);
            return response;
        }
    }

    /**
     * Gets the base URL for the connector API.
     *
     * @return the connector base URL
     */
    public String getConnectorBaseUrl() {
        return connectorBaseUrl;
    }
}
