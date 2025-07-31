package com.jpmc.midascore.Controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class BalanceQueryController {
    private UserRepository userRepository;
    @Autowired
    BalanceQueryController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/balance")
    public ResponseEntity<Balance> getUserBalance(@RequestParam  long userId) {
        Optional<UserRecord> userOpt =  userRepository.findById(userId);
        if(userOpt.isEmpty()){
            Balance balance = new Balance(0);
            return new ResponseEntity<>(balance,HttpStatus.OK);
        }
        UserRecord user = userOpt.get();
        Balance balance = new Balance(user.getBalance());
        return new ResponseEntity<>(balance,HttpStatus.OK);
    }
}
