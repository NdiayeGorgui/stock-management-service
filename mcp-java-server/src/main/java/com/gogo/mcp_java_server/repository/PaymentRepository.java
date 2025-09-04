package com.gogo.mcp_java_server.repository;

import com.gogo.mcp_java_server.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
