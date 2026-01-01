package jamsam.shellexample.demo.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ConsumerConfig {

    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "Connection URL is required")
    public String connectionUrl;

    public String connectionUser;
    public String connectionPassword;
    public String insertMode;

    @NotBlank(message = "Topics are required")
    public String topics;

}