package org.pipproject.pip_project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Double balance = 0.0;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "sourceAccount")
    @JsonIgnore  // Evita incarcarea tranzactiilor in raspuns
    private List<Transaction> transactions;


    public Account(Currency currency, User user) {
        this.currency = currency;
        this.user = user;
    }
}


