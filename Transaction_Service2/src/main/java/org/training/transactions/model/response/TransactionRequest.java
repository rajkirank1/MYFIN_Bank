package org.training.transactions.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Outbound DTO representing saved transaction details.
 */
public class TransactionRequest {

    private String referenceId;
    private String batchReference;
    private String accountId;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime localDateTime;
    private String transactionStatus;
    private String comments;

    public TransactionRequest() {}

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getBatchReference() { return batchReference; }
    public void setBatchReference(String batchReference) { this.batchReference = batchReference; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getLocalDateTime() { return localDateTime; }
    public void setLocalDateTime(LocalDateTime localDateTime) { this.localDateTime = localDateTime; }

    public String getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(String transactionStatus) { this.transactionStatus = transactionStatus; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionRequest)) return false;
        TransactionRequest that = (TransactionRequest) o;
        return Objects.equals(referenceId, that.referenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referenceId);
    }
}
