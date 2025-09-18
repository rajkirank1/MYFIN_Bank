package org.training.transactions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.training.transactions.model.entity.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccount(String fromAccount);

    List<Transaction> findByToAccount(String toAccount);

    List<Transaction> findByFromAccountOrToAccount(String fromAccount, String toAccount);

    List<Transaction> findByReferenceId(String referenceId);

    List<Transaction> findByBatchReference(String batchReference);
}
