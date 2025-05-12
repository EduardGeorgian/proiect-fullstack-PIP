package org.pipproject.pip_project.repositories;

import org.pipproject.pip_project.model.TransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferRequestRepository extends JpaRepository<TransferRequest, Long> {
    List<TransferRequest> findByRecipientEmail(String recipientEmail);
    List<TransferRequest> findByRequesterEmail(String requesterEmail);
}
