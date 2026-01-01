package jamsam.shellexample.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SourceConnectorConfig {

    @Value("${connector.source.class}")
    public String CONNECTOR_SOURCE_CLASS;
    @Value("${max.tasks}")
    public String MAX_TASKS;
    @Value("${connection.source.url}")
    public String CONNECTION_SOURCE_URL;
    @Value("${connection.source.user}")
    public String CONNECTION_SOURCE_USER;
    @Value("${connection.source.password}")
    public String CONNECTION_SOURCE_PASSWORD;
    @Value("${numeric.mapping}")
    public String NUMERIC_MAPPING;
    @Value("${poll.interval}")
    public String POLL_INTERVAL;
    @Value("${max.batch.row}")
    public String MAX_BATCH_ROW;

}
