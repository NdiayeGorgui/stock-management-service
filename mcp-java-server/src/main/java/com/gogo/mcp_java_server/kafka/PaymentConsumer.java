package com.gogo.mcp_java_server.kafka;

import com.gogo.base_domaine_service.event.EventStatus;
import com.gogo.base_domaine_service.event.OrderEventDto;
import com.gogo.mcp_java_server.model.Payment;
import com.gogo.mcp_java_server.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentConsumer {

    @Autowired
    private PaymentRepository paymentRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic.payment.name}",
            groupId = "${spring.kafka.consumer.payment.group-id}"
    )
    public void orderConsumer(OrderEventDto event) {
        if (EventStatus.COMPLETED.name().equalsIgnoreCase(event.getStatus())) {
            LOGGER.info("📦 MCP server received event => {}", event);

            Payment payment=new Payment();

           payment.setPaymentId(event.getPaymentId());
           payment.setOrderId(event.getId());
           payment.setCustomerId(event.getCustomerEventDto().getCustomerIdEvent());
           payment.setCustomerName(event.getCustomerEventDto().getName());
           payment.setCustomerMail(event.getCustomerEventDto().getEmail());
           payment.setTimeStamp(LocalDateTime.now());
           payment.setPaymentStatus(EventStatus.COMPLETED.name());

            paymentRepository.save(payment);

            LOGGER.info("✅ New Payment record created for order {}", event.getId());
        }


    }
}
