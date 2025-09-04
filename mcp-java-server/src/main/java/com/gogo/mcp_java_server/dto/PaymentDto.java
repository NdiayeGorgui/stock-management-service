package com.gogo.mcp_java_server.dto;

import java.time.LocalDateTime;

public record PaymentDto(String paymentId,
                         String customerId,
                         String orderId,
                         String customerName,
                         String customerMail,
                         double amount,
                         LocalDateTime timeStamp,
                         String paymentStatus) {
}
