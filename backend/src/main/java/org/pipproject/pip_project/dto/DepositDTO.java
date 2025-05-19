package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepositDTO {
    private long accountId;
    private String userEmail;
    private double amount;
}
