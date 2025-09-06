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
     String calcul = """
             Voici comment le montant ($) est calculé:
             itemDto.setQuantity(item.getQuantity());
                                     itemDto.setPrice(item.getPrice());
                                     itemDto.setDiscount(item.getDiscount());
                                     // 🧮 Calcul du montant net par produit et de la taxe (20%)
                                     double netCal= (item.getPrice() * item.getQuantity()) - item.getDiscount();
                                     double tax = Math.round(netCal * 0.20 * 100.0) / 100.0;
                                     itemDto.setTax(tax);
             
                                     return itemDto;
                                 })
                                 .collect(Collectors.toList());
             
                         double subtotal = itemDtos.stream()
                                 .mapToDouble(i -> i.getPrice() * i.getQuantity())
                                 .sum();
                         subtotal = Math.round(subtotal * 100.0) / 100.0;
             
                         double totalDiscount = itemDtos.stream()
                                 .mapToDouble(i -> {
                                     double total = i.getPrice() * i.getQuantity();
                                     if (total < 100) return 0;
                                     else if (total < 200) return 0.005 * total;
                                     else return 0.01 * total;
                                 })
                                 .sum();
                         totalDiscount = Math.round(totalDiscount * 100.0) / 100.0;
             
                         double totalTax = (subtotal - totalDiscount) * 0.20;
                         totalTax = Math.round(totalTax * 100.0) / 100.0;
             
                         double amount = (subtotal - totalDiscount) + totalTax;
                         amount = Math.round(amount * 100.0) / 100.0;
             
                         // Affecter les valeurs
                         dto.setItems(itemDtos);
                         dto.setTotalDiscount(totalDiscount);
                         dto.setTotalTax(totalTax);
                         dto.setAmount(amount);
             
                         responseList.add(dto);
             """;
        return chatClient.prompt()
                .system(calcul+
                        "Please refer to the payment list for the payment method.")
                .user(question).stream().content();
    }
}