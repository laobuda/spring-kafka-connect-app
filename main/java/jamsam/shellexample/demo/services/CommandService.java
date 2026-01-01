package jamsam.shellexample.demo.services;

import jamsam.shellexample.demo.model.ConsumerConfig;
import jamsam.shellexample.demo.model.ProducerConfig;
import jamsam.shellexample.demo.utils.JavaCurl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandService {

    private final Producer producer;
    private final Consumer consumer;

    public String createAndStartProducer(ProducerConfig producerConfig) {
        log.info("Starting producer with config: {}", producerConfig);
        return producer.createProducer(producerConfig);
    }

    public String createAndStartConsumer(ConsumerConfig consumerConfig) {
        log.info("Starting consumer with config: {}", consumerConfig);
        return consumer.createConsumer(consumerConfig);
    }

    public String executeCurl(String command) throws IOException {
        log.info("Executing command: {}", command);
        ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
        Process process = processBuilder.start();
        return JavaCurl.inputStreamToString(process.getInputStream());
    }
}
