package jamsam.shellexample.demo.model;

import java.util.List;

public class Command {
    private String name;
    private List<String> status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

}
