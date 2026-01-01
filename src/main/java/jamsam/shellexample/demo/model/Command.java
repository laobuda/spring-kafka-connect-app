package jamsam.shellexample.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class Command {
    private String name;
    private List<String> status;

}
