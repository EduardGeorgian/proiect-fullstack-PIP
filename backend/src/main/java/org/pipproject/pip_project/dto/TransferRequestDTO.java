package org.pipproject.pip_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for transfer request details.
 * Includes amount, description, requester and recipient emails, and source account ID.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDTO {
    /**
     * Amount requested for transfer.
     */
    private double amount;

    /**
     * Description or reason for the transfer request.
     */
    private String description;

    /**
     * Email of the user making the request.
     */
    private String requesterEmail;

    /**
     * Email of the user receiving the request.
     */
    private String recipientEmail;

    /**
     * ID of the source account related to the transfer request.
     */
    private long sourceAccountId;
}
