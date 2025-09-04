package com.gogo.mcp_java_server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String paymentId;
    private String customerId;
    private String orderId;
    private String customerName;
    private String customerMail;
    private double amount;
    private LocalDateTime timeStamp;
    private String paymentStatus;

}
