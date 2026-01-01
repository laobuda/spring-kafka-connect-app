package jamsam.shellexample.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Topics {

    private String url = "http://localhost:8082/topics";

    public Topics() {
    }

    public List<String> getAllTopics() {
        RestTemplate rt = new RestTemplate();
        String topics;
        List<String> result = new ArrayList<>();
        List<String> stringList  = new ArrayList<>();
        try {
            topics = rt.getForObject(url , String.class);
            result = Arrays.asList(topics.split(","));
            stringList = result.stream()
                    .filter(topic -> !topic.contains("\"__confluent.support.metrics\""))
                    .filter(topic -> !topic.contains("_confluent-ksql-default__command_topic"))
                    .filter(topic -> !topic.contains("_schemas"))
                    .filter(topic -> !topic.contains("\"docker-connect-configs\""))
                    .filter(topic -> !topic.contains("\"docker-connect-offsets\""))
                    .filter(topic -> !topic.contains("docker-connect-status"))
                    .map(topic -> topic.replace("]", " "))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            stringList.add(ex.getMessage());
            return stringList;
        }

        if(stringList.isEmpty() || stringList.contains("404")){
            stringList.add("Resource does not exist");
            return stringList;
        } else {
            return stringList;
        }
    }

}
