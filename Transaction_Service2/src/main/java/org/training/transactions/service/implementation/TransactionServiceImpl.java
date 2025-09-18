package org.training.transactions.service.implementation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.training.transactions.exception.AccountStatusException;
import org.training.transactions.exception.InsufficientBalance;
import org.training.transactions.model.TransactionStatus;
import org.training.transactions.model.TransactionType;
import org.training.transactions.model.dto.TransactionDto;
import org.training.transactions.model.entity.Transaction;
import org.training.transactions.model.response.Response;
import org.training.transactions.model.response.TransactionRequest;
import org.training.transactions.repository.TransactionRepository;
import org.training.transactions.service.TransactionService;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Response addTransaction(TransactionDto transactionDto) {
        if (transactionDto == null) {
            throw new IllegalArgumentException("transactionDto cannot be null");
        }

        String from = transactionDto.getFromAccount();
        if (from == null || from.trim().isEmpty()) {
            throw new AccountStatusException("From account is invalid or inactive");
        }

        BigDecimal amount = transactionDto.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InsufficientBalance("Amount must be greater than zero");
        }

        Transaction entity = new Transaction();
        entity.setReferenceId(UUID.randomUUID().toString()); // ensure unique per-transaction
        entity.setFromAccount(from);
        entity.setToAccount(transactionDto.getToAccount());
        entity.setAmount(amount);
        entity.setComments(transactionDto.getDescription());

        String typeStr = transactionDto.getTransactionType();
        TransactionType type = TransactionType.TRANSFER;
        if (typeStr != null && !typeStr.trim().isEmpty()) {
            try {
                type = TransactionType.valueOf(typeStr.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) { }
        }
        entity.setTransactionType(type);
        entity.setStatus(TransactionStatus.SUCCESS);

        Transaction saved = transactionRepository.save(entity);

        Response resp = new Response();
        resp.setResponseCode("00");
        resp.setMessage("Transaction saved with reference: " + saved.getReferenceId());
        return resp;
    }

    @Override
    public Response internalTransaction(List<TransactionDto> transactionDtos, String transactionReference) {
        if (transactionDtos == null || transactionDtos.isEmpty()) {
            throw new IllegalArgumentException("transactionDtos cannot be null/empty");
        }

        int success = 0;
        int failed = 0;
        List<String> savedRefs = new ArrayList<>();

        String batchRef = transactionReference; // store provided reference as batchReference for grouping

        for (TransactionDto dto : transactionDtos) {
            try {
                if (dto == null) {
                    throw new IllegalArgumentException("transaction dto is null");
                }
                if (dto.getFromAccount() == null || dto.getFromAccount().trim().isEmpty()) {
                    throw new AccountStatusException("From account invalid");
                }
                BigDecimal amount = dto.getAmount();
                if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new InsufficientBalance("Invalid amount");
                }

                Transaction e = new Transaction();
                e.setReferenceId(UUID.randomUUID().toString()); // unique per-row
                e.setBatchReference(batchRef); // group id (nullable)

                e.setFromAccount(dto.getFromAccount());
                e.setToAccount(dto.getToAccount());
                e.setAmount(dto.getAmount());
                e.setComments(dto.getDescription());

                String typeStr = dto.getTransactionType();
                TransactionType type = TransactionType.TRANSFER;
                if (typeStr != null && !typeStr.trim().isEmpty()) {
                    try {
                        type = TransactionType.valueOf(typeStr.trim().toUpperCase());
                    } catch (IllegalArgumentException ignored) { }
                }
                e.setTransactionType(type);
                e.setStatus(TransactionStatus.SUCCESS);

                Transaction saved = transactionRepository.save(e);
                success++;
                savedRefs.add(saved.getReferenceId());
            } catch (org.springframework.dao.DataIntegrityViolationException dve) {
                failed++;
                // continue processing remaining items
            } catch (Throwable ex) {
                failed++;
            }
        }

        Response resp = new Response();
        resp.setResponseCode(failed == 0 ? "00" : "01");
        resp.setMessage(String.format("Processed: %d succeeded, %d failed", success, failed));
        if (!savedRefs.isEmpty()) {
            resp.setMessage(resp.getMessage() + ". Saved refs: " + String.join(",", savedRefs));
        }
        return resp;
    }

    @Override
    public List<TransactionRequest> getTransaction(String accountId) {
        if (accountId == null) throw new IllegalArgumentException("accountId cannot be null");
        List<Transaction> entities = transactionRepository.findByFromAccountOrToAccount(accountId, accountId);
        List<TransactionRequest> out = new ArrayList<>();
        for (Transaction e : entities) out.add(mapToTransactionRequest(e));
        return out;
    }

    @Override
    public List<TransactionRequest> getTransactionByTransactionReference(String transactionReference) {
        if (transactionReference == null) throw new IllegalArgumentException("transactionReference cannot be null");

        List<Transaction> byRef = transactionRepository.findByReferenceId(transactionReference);
        List<Transaction> byBatch = transactionRepository.findByBatchReference(transactionReference);

        List<Transaction> merged = new ArrayList<>();
        if (byRef != null && !byRef.isEmpty()) merged.addAll(byRef);
        if (byBatch != null && !byBatch.isEmpty()) merged.addAll(byBatch);

        List<TransactionRequest> out = new ArrayList<>();
        for (Transaction e : merged) out.add(mapToTransactionRequest(e));
        return out;
    }

    /* mapping */
    private TransactionRequest mapToTransactionRequest(Transaction e) {
        TransactionRequest tr = new TransactionRequest();
        tr.setReferenceId(e.getReferenceId());
        tr.setBatchReference(e.getBatchReference());
        tr.setAccountId(e.getFromAccount() != null ? e.getFromAccount() : e.getToAccount());
        tr.setTransactionType(e.getTransactionType() != null ? e.getTransactionType().name() : null);
        tr.setAmount(e.getAmount());
        tr.setLocalDateTime(e.getTransactionDate());
        tr.setTransactionStatus(e.getStatus() != null ? e.getStatus().name() : null);
        tr.setComments(e.getComments());
        return tr;
    }
}
