package com.gogo.mcp_java_server.kafka;

import com.gogo.base_domaine_service.event.EventStatus;
import com.gogo.base_domaine_service.event.OrderEventDto;
import com.gogo.base_domaine_service.event.ProductItemEventDto;
import com.gogo.mcp_java_server.model.Product;
import com.gogo.mcp_java_server.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UpdatedProductConsumer {
    @Autowired
    private StockService stockService;


    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatedProductConsumer.class);

    @KafkaListener(
            topics = "${spring.kafka.topic.billing.name}",
            groupId = "${spring.kafka.update.product.consumer.group-id}"
    )
    public void consumeProductStatus(OrderEventDto event) {
        LOGGER.info("📦 Order event received in Inventory service => {}", event);

        // Création : on diminue les quantités
        if (EventStatus.CREATED.name().equalsIgnoreCase(event.getStatus())) {
            for (ProductItemEventDto item : event.getProductItemEventDtos()) {
                Product product = stockService.findProductById(item.getProductIdEvent());

                if (product == null) {
                    LOGGER.warn("🚫 Produit introuvable: {}", item.getProductIdEvent());
                    continue;
                }

                int qtyBefore = product.getQty();
                int qtyUsed = item.getQty();
                int qtyAfter = stockService.qtyRestante(qtyBefore, qtyUsed, EventStatus.CREATED.name());

                if (qtyAfter >= 0) {
                    stockService.updateProductQty(item.getProductIdEvent(), qtyAfter);
                    LOGGER.info("✅ Stock décrémenté pour {}: {} -> {}", item.getProductIdEvent(), qtyBefore, qtyAfter);
                } else {
                    LOGGER.error("❌ Stock insuffisant pour le produit {} (dispo={}, demandé={})", item.getProductIdEvent(), qtyBefore, qtyUsed);
                    throw new RuntimeException("Quantité insuffisante pour le produit : " + item.getProductIdEvent());
                }
            }
        }

        // Annulation : on restaure les quantités
        else if (EventStatus.CANCELED.name().equalsIgnoreCase(event.getStatus())) {
            for (ProductItemEventDto item : event.getProductItemEventDtos()) {
                Product product = stockService.findProductById(item.getProductIdEvent());

                if (product == null) {
                    LOGGER.warn("🚫 Produit introuvable: {}", item.getProductIdEvent());
                    continue;
                }

                int qtyBefore = product.getQty();
                int qtyRestored = item.getQty();
                int qtyAfter = stockService.qtyRestante(qtyBefore, qtyRestored, EventStatus.CANCELED.name());

                stockService.updateProductQty(item.getProductIdEvent(), qtyAfter);
                LOGGER.info("🔄 Stock restauré pour {}: {} -> {}", item.getProductIdEvent(), qtyBefore, qtyAfter);
            }
        }
    }

}
