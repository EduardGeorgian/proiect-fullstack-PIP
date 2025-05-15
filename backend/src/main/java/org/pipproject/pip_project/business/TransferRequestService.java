package org.pipproject.pip_project.business;

import org.pipproject.pip_project.dto.TransferRequestDTO;
import org.pipproject.pip_project.model.Account;
import org.pipproject.pip_project.model.TransferRequest;
import org.pipproject.pip_project.model.TransferStatus;
import org.pipproject.pip_project.model.User;
import org.pipproject.pip_project.repositories.AccountRepository;
import org.pipproject.pip_project.repositories.TransferRequestRepository;
import org.pipproject.pip_project.repositories.UserRepository;
import org.pipproject.pip_project.validators.TransferRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service

public class TransferRequestService {

    private final TransferRequestRepository transferRequestRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    @Autowired
    public TransferRequestService(TransferRequestRepository transferRequestRepository, UserRepository userRepository, AccountRepository accountRepository, TransactionService transactionService) {
        this.transferRequestRepository = transferRequestRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    public TransferRequest createRequest(String requesterEmail, String recipientEmail,double amount, String description, long sourceAccountId){
        User requester = userRepository.findByEmail(requesterEmail).orElseThrow(()->new RuntimeException("Requester not found"));
        User recipient = userRepository.findByEmail(recipientEmail).orElseThrow(()->new RuntimeException("Recipient not found"));
        Account sourceAccount = accountRepository.findById(sourceAccountId).orElseThrow(()->new RuntimeException("Source account not found"));

        TransferRequestValidator.validate(requesterEmail,recipientEmail,amount,description,sourceAccountId);

        TransferRequest request = new TransferRequest(amount,description,new Date(), TransferStatus.WAITING,requester,recipient,sourceAccount);
        return transferRequestRepository.save(request);
    }

    public List<TransferRequest> getReceivedRequests(String recipientEmail) {
        return transferRequestRepository.findByRecipientEmail(recipientEmail);
    }

    public List<TransferRequest> getSentRequests(String requesterEmail) {
        return transferRequestRepository.findByRequesterEmailOrderByDateDesc(requesterEmail);
    }

    public TransferRequest acceptRequest(long requestId, TransferRequestDTO transferRequestDTO) {
        TransferRequest request = transferRequestRepository.findById(requestId).orElseThrow(()->new RuntimeException("Request not found"));
        if(request.getStatus() == TransferStatus.ACCEPTED){
            throw new RuntimeException("Request already accepted");
        }
        User requester = userRepository.findByEmail(transferRequestDTO.getRequesterEmail()).orElseThrow(()->new RuntimeException("Requester not found"));
        User recipient = userRepository.findByEmail(transferRequestDTO.getRecipientEmail()).orElseThrow(()->new RuntimeException("Recipient not found"));

        Account requesterAccount = accountRepository.findById(transferRequestDTO.getSourceAccountId()).orElseThrow(()->new RuntimeException("Requester account not found"));
        Account recipientAccount = accountRepository.findFirstByUser(Optional.ofNullable(recipient));

        if(recipientAccount.getBalance()<request.getAmount()){
            throw new RuntimeException("Source account balance is lower than request amount");
        }
        request.setStatus(TransferStatus.ACCEPTED);
        transactionService.addTransfer(transferRequestDTO.getRecipientEmail(),transferRequestDTO.getAmount(),recipientAccount,requesterAccount);
        return transferRequestRepository.save(request);

    }

    public TransferRequest rejectRequest(long requestId, TransferRequestDTO transferRequestDTO) {
        TransferRequest request = transferRequestRepository.findById(requestId).orElseThrow(()->new RuntimeException("Request not found"));
        if(request.getStatus() == TransferStatus.REJECTED){
            throw new RuntimeException("Request already rejected");
        }
        request.setStatus(TransferStatus.REJECTED);
        return transferRequestRepository.save(request);
    }

    public void deleteRequest(long requestId) {
        transferRequestRepository.deleteById(requestId);
    }

}
