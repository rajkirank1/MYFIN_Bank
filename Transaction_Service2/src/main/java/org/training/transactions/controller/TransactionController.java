package org.training.transactions.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.training.transactions.exception.AccountStatusException;
import org.training.transactions.exception.InsufficientBalance;
import org.training.transactions.exception.ResourceNotFound;
import org.training.transactions.model.dto.TransactionDto;
import org.training.transactions.model.response.Response;
import org.training.transactions.model.response.TransactionRequest;
import org.training.transactions.service.TransactionService;

@RestController
@RequestMapping(value = "/api/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> addTransaction(@Valid @RequestBody TransactionDto dto) {
        if (dto == null) throw new ResourceNotFound("Request body is missing");
        if (dto.getFromAccount() == null || dto.getFromAccount().trim().isEmpty()) {
            throw new AccountStatusException("fromAccount is required");
        }
        if (dto.getAmount() == null || dto.getAmount().doubleValue() <= 0) {
            throw new InsufficientBalance("amount must be greater than zero");
        }

        Response resp = transactionService.addTransaction(dto);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(path = "/internal", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> internalTransaction(
            @Valid @RequestBody List<@Valid TransactionDto> dtos,
            @RequestParam(value = "reference", required = false) String reference) {

        if (dtos == null || dtos.isEmpty()) {
            throw new ResourceNotFound("transaction list cannot be empty");
        }

        for (TransactionDto dto : dtos) {
            if (dto == null) throw new ResourceNotFound("one of the transactions is null");
            if (dto.getFromAccount() == null || dto.getFromAccount().trim().isEmpty()) {
                throw new AccountStatusException("fromAccount is required for each transaction");
            }
            if (dto.getAmount() == null || dto.getAmount().doubleValue() <= 0) {
                throw new InsufficientBalance("amount must be greater than zero for each transaction");
            }
        }

        Response resp = transactionService.internalTransaction(dtos, reference);
        return ResponseEntity.ok(resp);
    }

    @GetMapping(path = "/from/{account}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionRequest>> byFromAccount(@PathVariable("account") String account) {
        if (account == null || account.trim().isEmpty()) throw new ResourceNotFound("account path variable is required");
        List<TransactionRequest> list = transactionService.getTransaction(account);
        return ResponseEntity.ok(list);
    }

    @GetMapping(path = "/reference/{ref}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TransactionRequest>> byReference(@PathVariable("ref") String ref) {
        if (ref == null || ref.trim().isEmpty()) throw new ResourceNotFound("reference id is required");
        List<TransactionRequest> list = transactionService.getTransactionByTransactionReference(ref);
        return ResponseEntity.ok(list);
    }

    @GetMapping(path = "/ping", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
