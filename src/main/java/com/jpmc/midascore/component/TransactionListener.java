package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {
    static final Logger logger = LoggerFactory.getLogger(TransactionListener.class);

    private final TransactionProcessingService transactionProcessingService;

    TransactionListener(TransactionProcessingService transactionProcessingService) {
        this.transactionProcessingService = transactionProcessingService;
    }
    @KafkaListener(
            topics = "${general.kafka-topic}", // Reads topic name from application.yml
            groupId = "midas-core"                 // A unique ID for the consumer group
    )
    public void handleTransaction(Transaction transaction) {


//        logger.info("Received new transaction {}", transaction.getSenderId());
        transactionProcessingService.processTransaction(transaction);
    }

}
