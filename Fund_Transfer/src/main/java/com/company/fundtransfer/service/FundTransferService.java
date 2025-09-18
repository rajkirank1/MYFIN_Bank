package com.company.fundtransfer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.fundtransfer.dto.TransferRequest;
import com.company.fundtransfer.entity.Account;
import com.company.fundtransfer.exception.AccountUpdateException;
import com.company.fundtransfer.exception.InsufficientBalanceException;
import com.company.fundtransfer.exception.ResourceNotFoundException;
import com.company.fundtransfer.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class FundTransferService {
    private final AccountRepository accountRepository;

    public FundTransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public String transfer(TransferRequest req) {
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountUpdateException("Transfer amount must be greater than zero");
        }

        Account from = accountRepository.findByAccountNumber(req.getFromAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found: " + req.getFromAccount()));
        Account to = accountRepository.findByAccountNumber(req.getToAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found: " + req.getToAccount()));

        if (from.getBalance().compareTo(req.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account: " + req.getFromAccount());
        }

        try {
            from.setBalance(from.getBalance().subtract(req.getAmount()));
            to.setBalance(to.getBalance().add(req.getAmount()));
            accountRepository.save(from);
            accountRepository.save(to);
            return "Transfer successful";
        } catch (Exception e) {
            throw new AccountUpdateException("Failed to update accounts during transfer", e);
        }
    }
}
