package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TransactionProcessingService {

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    private final RestTemplate restTemplate;
    @Autowired
    public TransactionProcessingService(UserRepository userRepository, TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }
    @Transactional
    public void processTransaction(Transaction transaction) {
        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());
        if(senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return;
        }
        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        if(sender.getBalance() < transaction.getAmount()){
            return;
        }
        String url ="http://localhost:8080/incentive";
        Incentive  incentive = restTemplate.postForObject(url, transaction,Incentive.class);
        float incentiveAmount = (incentive!=null)?incentive.getAmount():0;

        sender.setBalance(sender.getBalance() - transaction.getAmount());
        recipient.setBalance(incentiveAmount + recipient.getBalance() + transaction.getAmount());
        userRepository.save(sender);
        userRepository.save(recipient);
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount());
        transactionRecord.setIncentive(incentiveAmount);
        transactionRepository.save(transactionRecord);

    }
}
