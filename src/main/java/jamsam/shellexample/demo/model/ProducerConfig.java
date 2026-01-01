package jamsam.shellexample.demo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ProducerConfig {

    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "Table whitelist is required")
    public String tableWhitelist;

    public String tables;

    @NotBlank(message = "Topic prefix is required")
    public String topicPrefix;

    @NotBlank(message = "Mode is required")
    public String mode;

    public String timestampColumnName;
    public String query;
    public String tableTypes;
    public String incrementingColumnName;

}