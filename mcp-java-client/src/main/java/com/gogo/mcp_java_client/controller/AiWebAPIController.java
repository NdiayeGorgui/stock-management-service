package com.gogo.mcp_java_client.controller;

import com.gogo.mcp_java_client.agents.MyAIAgent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin("*")
public class AiWebAPIController {
    private MyAIAgent agent;

    public AiWebAPIController(MyAIAgent agent) {
        this.agent = agent;
    }
    @GetMapping("/chat")
    public   Flux<String> askAgent(String query) {
        return agent.prompt(query);
    }
}