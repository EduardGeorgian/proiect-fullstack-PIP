package org.pipproject.pip_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Entity representing a financial transaction.
 * Stores details about amount, type, date, involved accounts, status, and initiator.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    /**
     * Unique identifier for the transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Amount involved in the transaction.
     */
    private double amount;

    /**
     * Email of the user who initiated the transaction.
     */
    private String initiatorEmail;

    /**
     * Type of the transaction (e.g., SEND, DEPOSIT, WITHDRAW).
     */
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    /**
     * Date and time when the transaction occurred.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /**
     * Source account for the transaction (nullable in deposits).
     */
    @ManyToOne
    @JoinColumn(name="source_account_id")
    private Account sourceAccount;

    /**
     * Destination account for the transaction (nullable in withdrawals).
     */
    @ManyToOne
    @JoinColumn(name="destination_account_id")
    private Account destinationAccount;

    /**
     * Current status of the transaction (e.g., COMPLETED, FAILED, PENDING).
     */
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    /**
     * Constructor for creating a transaction with all key fields.
     */
    public Transaction(String initiatorEmail, TransactionType type, Date date, double amount, Account sourceAccount, Account destinationAccount, TransactionStatus status) {
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.status = status;
        this.initiatorEmail = initiatorEmail;
    }
}
