package org.pipproject.pip_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double amount;

    private String initiatorEmail;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name="source_account_id")
    private Account sourceAccount;

    @ManyToOne
    @JoinColumn(name="destination_account_id")
    private Account destinationAccount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    public Transaction(String initiatorEmail,TransactionType type, Date date, double amount,Account sourceAccount, Account destinationAccount, TransactionStatus status) {
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.status = status;
        this.initiatorEmail = initiatorEmail;
    }
}
