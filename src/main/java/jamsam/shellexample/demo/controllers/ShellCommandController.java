package jamsam.shellexample.demo.controllers;

import jamsam.shellexample.demo.model.Command;
import jamsam.shellexample.demo.model.ConsumerConfig;
import jamsam.shellexample.demo.model.ProducerConfig;
import jamsam.shellexample.demo.services.CommandService;
import jamsam.shellexample.demo.services.Connectors;
import jamsam.shellexample.demo.services.Topics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ShellCommandController {

    private final CommandService service;
    private final Topics topics;
    private final Connectors connectors;

    @GetMapping({ "", "/", "/home", "/home.html" })
    public String getHome(@ModelAttribute Command command, Model model) {
        command.setName("Home");
        command.setStatus(Collections.singletonList("Found"));
        model.addAttribute("command", command);
        return "home.html";
    }

    @PostMapping("/createConsumer")
    public String sendConsumerForm(@ModelAttribute ConsumerConfig consumerConfig, Model model) {
        model.addAttribute("ConsumerConfig", new ConsumerConfig());
        return "fragments/consumerConfigForm.html";
    }

    @PostMapping("/showConsumer")
    public String showConsumer(@ModelAttribute ConsumerConfig consumerConfig, @ModelAttribute Command command,
            Model model) {
        model.addAttribute("ConsumerConfig", consumerConfig);
        service.createAndStartConsumer(consumerConfig);
        command.setName("Create Sink Connector");
        List<String> status = new ArrayList<>();
        status.add("The sink connector " + consumerConfig.getName() + " has been successfully created.");
        command.setStatus(status);
        return "home.html";
    }

    @PostMapping("/createProducer")
    public String sendProducerForm(@ModelAttribute ProducerConfig producerConfig, Model model) {
        model.addAttribute("ProducerConfig", new ProducerConfig());
        return "fragments/producerConfigForm.html";
    }

    @PostMapping("/showProducer")
    public String showProducer(@ModelAttribute ProducerConfig producerConfig, @ModelAttribute Command command,
            Model model) {
        model.addAttribute("ProducerConfig", producerConfig);
        service.createAndStartProducer(producerConfig);
        command.setName("Create Source Connector");
        List<String> status = new ArrayList<>();
        status.add("The source connector " + producerConfig.getName() + " has been successfully created.");
        command.setStatus(status);
        return "home.html";
    }

    @GetMapping("/showTopics")
    public String showTopics(@ModelAttribute Command command) {
        List<String> myTopics = topics.getAllTopics();
        command.setName("Show Topics");
        command.setStatus(myTopics);
        return "home.html";
    }

    @GetMapping("/showAllConnectors")
    public String showAllConnectors(@ModelAttribute Command command) {
        final List<String> results = connectors.getAllConnectors();
        command.setName("Show All Connectors");
        command.setStatus(results);
        return "home.html";
    }

    @PostMapping("/deleteAllConnectors")
    public String deleteAllConnectors(@ModelAttribute Command command) throws IOException {
        final List<String> results = connectors.deleteAllConnectors();
        command.setName("Delete All Connectors");
        command.setStatus(results);
        return "home.html";
    }

    @PostMapping("/resetTopicsForConnectors")
    public String resetTopicsForConnectors(@ModelAttribute Command command) throws IOException {
        final List<String> results = connectors.resetTopicsForConnectors();
        command.setName("Reset Topics for all Connectors");
        command.setStatus(results);
        return "home.html";
    }
}
