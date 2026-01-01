package jamsam.shellexample.demo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Connectors {

    private final String url = "http://localhost:8083/connectors";
    private final String curlDelete = "curl -X DELETE localhost:8083/connectors/";
    private final String curlPut = "curl -X PUT localhost:8083/connectors/";

    private final CommandService commandService;

    public List<String> getAllConnectors() {
        RestTemplate rt = new RestTemplate();
        String connectors;
        List<String> result = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        try {
            connectors = rt.getForObject(url, String.class);
            if (connectors != null) {
                result = Arrays.asList(connectors.split(","));
                stringList = result.stream()
                        .map(connector -> connector.replace("[", ""))
                        .map(connector -> connector.replace("\"", ""))
                        .map(connector -> connector.replace("]", ""))
                        .collect(Collectors.toList());
            } else {
                stringList.add("Resource does not exist");
                return stringList;
            }
        } catch (Exception ex) {
            log.error("Error fetching connectors", ex);
            stringList.add(ex.getMessage());
            return stringList;
        }

        if (stringList.isEmpty() || stringList.contains("404")) {
            stringList.add("Resource does not exist");
            return stringList;
        } else {
            return stringList;
        }
    }

    private boolean runCurlComman(String connectorName) throws IOException {
        final String command = curlDelete + connectorName;
        final String content = commandService.executeCurl(command);
        return !content.contains("40");
    }

    public String deleteConnectorByName(String connectorName) throws IOException {
        String result = "Nothing to delete";
        if (runCurlComman(connectorName)) {
            result = "Connector " + connectorName + " has been deleted successfully.";
        } else {
            result = "Could not delete Connector " + connectorName;
        }
        return result;
    }

    public List<String> deleteAllConnectors() throws IOException {
        final List<String> connectors = getAllConnectors();
        List<String> result = new ArrayList<>();

        for (String connector : connectors) {
            final boolean successfully = deleteConnectorByName(connector).contains("successfully");
            if (successfully) {
                result.add(connector + " deleted.");
            } else {
                result.add(connector + " NOT deleted.");
            }
        }
        return result;
    }

    public List<String> resetTopicsForConnectors() {
        final List<String> connectors = getAllConnectors();
        List<String> result = new ArrayList<>();
        if (connectors.size() >= 1) {
            connectors.stream().forEach(connector -> {
                try {
                    log.info("trying to run " + curlPut + connector + "/topics/reset");
                    commandService.executeCurl(curlPut + connector + "/topics/reset");
                    result.add("Topics for " + connector + " has been reset");
                } catch (IOException e) {
                    log.error("Exception trying to run " + curlPut + connector + "/topics/reset", e);
                    result.add("Could not reset Topics for " + connector);
                }
            });
        }
        if (result.isEmpty()) {
            result.add("No topics to delete");
        }

        return result;
    }
}
