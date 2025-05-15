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
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double amount;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;



    public TransferRequest(double amount, String description, Date date, TransferStatus status, User requester, User recipient, Account sourceAccount) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.status = status;
        this.requester = requester;
        this.recipient = recipient;
        this.sourceAccount = sourceAccount;
    }

}
