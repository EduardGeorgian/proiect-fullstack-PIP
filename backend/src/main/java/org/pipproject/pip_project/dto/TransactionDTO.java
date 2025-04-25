package org.pipproject.pip_project.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.TransactionType;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private String initiatorEmail;
    private TransactionType type;
    private double amount;
    private Long sourceAccountId;
    private Long destinationAccountId;
}
