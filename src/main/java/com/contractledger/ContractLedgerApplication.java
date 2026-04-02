package com.contractledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ContractLedgerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContractLedgerApplication.class, args);
    }
}
