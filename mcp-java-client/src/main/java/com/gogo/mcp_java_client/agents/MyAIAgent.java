package com.gogo.mcp_java_client.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class MyAIAgent {
    private ChatClient chatClient;

    public MyAIAgent(ChatClient.Builder chatClient, ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClient
                .defaultSystem("Answer the user question using provided tools")
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(MessageChatMemoryAdvisor
                        .builder(MessageWindowChatMemory.builder().build()).build())
                .build();
    }
    public  Flux<String> prompt(String question) {
        return chatClient.prompt()
                .system("Use markdown style when you answer." +
                        " Si on te pose une question sur le mode de payment, réponds ceci: " +
                        "Veuillez consulter la liste de payments pour connaitre le mode de payment")
                .user(question).stream().content();
    }
}