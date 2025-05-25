package org.pipproject.pip_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Entity representing a transfer request between users.
 * Contains amount, description, status, date, requester, recipient, and source account.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    /**
     * Unique identifier of the transfer request.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Amount requested to be transferred.
     */
    private double amount;

    /**
     * Description or note about the transfer request.
     */
    private String description;

    /**
     * Date and time when the request was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /**
     * Current status of the transfer request (e.g., PENDING, ACCEPTED, REJECTED).
     */
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    /**
     * User who made the transfer request.
     */
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;

    /**
     * User who received the transfer request.
     */
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    /**
     * Source account related to the transfer request.
     */
    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    /**
     * Constructor with all main fields.
     */
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
