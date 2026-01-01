package jamsam.shellexample.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SinkConnectorConfig {

    @Value("${connector.sink.class}")
    public  String CONNECTOR_SINK_CLASS;
    @Value("${connection.sink.url}")
    public  String CONNECTION_SINK_URL;
    @Value("${connection.sink.user}")
    public  String CONNECTION_SINK_USER;
    @Value("${connection.sink.password}")
    public  String CONNECTION_SINK_PASSWORD;
    @Value("${insert.mode}")
    public  String INSERT_MODE;
    @Value("${auto.create}")
    public  String AUTO_CREATE;
    @Value("${auto.evolve}")
    public  String AUTO_EVOLVE;
    @Value("${max.retries}")
    public  String MAX_RETRIES;
    @Value("${max.tasks}")
    public String MAX_TASKS;
    @Value("${pk.mode}")
    public String PK_MODE;
    @Value("${pk.fields}")
    public String PK_FIELDS;
}
