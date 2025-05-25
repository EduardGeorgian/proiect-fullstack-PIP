package org.pipproject.pip_project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Entity representing a user's account.
 * Holds balance, currency, associated user, and related transactions.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    /**
     * Unique identifier of the account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * Current balance of the account.
     */
    @Column(nullable = false)
    private Double balance = 0.0;

    /**
     * Currency used by the account.
     */
    @Enumerated(EnumType.STRING)
    private Currency currency;

    /**
     * Owner of the account.
     */
    @ManyToOne
    private User user;

    /**
     * List of transactions where this account is the source.
     * Marked with @JsonIgnore to avoid serialization recursion.
     */
    @OneToMany(mappedBy = "sourceAccount")
    @JsonIgnore
    private List<Transaction> transactions;

    /**
     * Constructor for creating account with currency and user.
     */
    public Account(Currency currency, User user) {
        this.currency = currency;
        this.user = user;
    }
}
