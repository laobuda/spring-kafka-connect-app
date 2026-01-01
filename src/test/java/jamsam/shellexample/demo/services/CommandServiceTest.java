package jamsam.shellexample.demo.services;

import jamsam.shellexample.demo.model.ConsumerConfig;
import jamsam.shellexample.demo.model.ProducerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

    @Mock
    private Producer producer;

    @Mock
    private Consumer consumer;

    @Mock
    private ConnectorUtils connectorUtils;

    @InjectMocks
    private CommandService commandService;

    @Test
    void createProducer() {
        ProducerConfig config = new ProducerConfig();
        config.setName("test-producer");
        when(producer.createProducer(any(ProducerConfig.class))).thenReturn("Success");

        String result = commandService.createAndStartProducer(config);

        assertEquals("Success", result);
        verify(producer).createProducer(config);
    }

    @Test
    void createConsumer() {
        ConsumerConfig config = new ConsumerConfig();
        config.setName("test-consumer");
        when(consumer.createConsumer(any(ConsumerConfig.class))).thenReturn("Success");

        String result = commandService.createAndStartConsumer(config);

        assertEquals("Success", result);
        verify(consumer).createConsumer(config);
    }
}
