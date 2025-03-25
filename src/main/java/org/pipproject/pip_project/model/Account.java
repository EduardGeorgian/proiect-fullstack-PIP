package org.pipproject.pip_project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    /*
        Generam id ul automat, si nu folosim acelasi id de la utilizator pentru a putea avea mai multe
        conturi asociate unui singur utilizator
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Double balance;

    @Enumerated(EnumType.STRING)
    private Currency currency; //Folosim enum-ul currency

    @ManyToOne
    private User user; //Relatie many to one intre account si utilizator
}
