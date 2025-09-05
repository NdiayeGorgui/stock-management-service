package com.gogo.mcp_java_server.kafka;

import com.gogo.base_domaine_service.event.EventStatus;
import com.gogo.base_domaine_service.event.OrderEventDto;
import com.gogo.mcp_java_server.model.Bill;
import com.gogo.mcp_java_server.model.Order;
import com.gogo.mcp_java_server.model.Payment;
import com.gogo.mcp_java_server.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentConsumer {


    @Autowired
    private StockService stockService;

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

            stockService.savePayment(payment);

            event.setStatus(EventStatus.COMPLETED.name());
            // billingService.updateTheBillStatus(event.getId(), event.getStatus());
            List<Bill> billList=stockService.billList(event.getCustomerEventDto().getCustomerIdEvent(),EventStatus.CREATED.name());
            for (Bill bill:billList){
                if (bill.getStatus().equalsIgnoreCase(EventStatus.CREATED.name())){
                    bill.setStatus(event.getStatus());
                    stockService.saveBill(bill);
                    //   billingService.updateAllBillCustomerStatus(bill.getCustomerIdEvent(), event.getStatus());
                }
            }

            String customerId = event.getCustomerEventDto().getCustomerIdEvent();
            List<Order> orders = stockService.findByCustomer(customerId);

            for (Order order : orders) {
                if (EventStatus.CREATED.name().equalsIgnoreCase(order.getOrderStatus())) {

                    // 1. Mettre à jour l’état de la commande
                    order.setOrderStatus(EventStatus.COMPLETED.name());
                    stockService.saveOrder(order);
                }

            }

            LOGGER.info("✅ New Payment record created for order {}", event.getId());
        }


    }
}
