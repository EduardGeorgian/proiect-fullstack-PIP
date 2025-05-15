package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDTO {
    private double amount;
    private String description;
    private String requesterEmail;
    private String recipientEmail;
    private long sourceAccountId;
}
