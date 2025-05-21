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

/**
 * Service care gestionează cererile de transfer între utilizatori.
 * Permite crearea, acceptarea, respingerea, vizualizarea și ștergerea cererilor de transfer.
 */
@Service
public class TransferRequestService {

    private final TransferRequestRepository transferRequestRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    /**
     * Constructor pentru TransferRequestService.
     *
     * @param transferRequestRepository repository pentru entitatea TransferRequest
     * @param userRepository repository pentru entitatea User
     * @param accountRepository repository pentru entitatea Account
     * @param transactionService serviciul pentru gestionarea tranzacțiilor financiare
     */
    @Autowired
    public TransferRequestService(TransferRequestRepository transferRequestRepository, UserRepository userRepository, AccountRepository accountRepository, TransactionService transactionService) {
        this.transferRequestRepository = transferRequestRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    /**
     * Creează o nouă cerere de transfer între doi utilizatori.
     * Validează datele cererii și o salvează cu statusul WAITING.
     *
     * @param requesterEmail emailul utilizatorului care face cererea
     * @param recipientEmail emailul destinatarului cererii
     * @param amount suma solicitată pentru transfer
     * @param description descrierea cererii
     * @param sourceAccountId id-ul contului sursă de unde se va face transferul
     * @return cererea de transfer creată și salvată
     * @throws RuntimeException dacă vreun utilizator sau cont nu este găsit
     */
    public TransferRequest createRequest(String requesterEmail, String recipientEmail, double amount, String description, long sourceAccountId) {
        User requester = userRepository.findByEmail(requesterEmail).orElseThrow(() -> new RuntimeException("Requester not found"));
        User recipient = userRepository.findByEmail(recipientEmail).orElseThrow(() -> new RuntimeException("Recipient not found"));
        Account sourceAccount = accountRepository.findById(sourceAccountId).orElseThrow(() -> new RuntimeException("Source account not found"));

        TransferRequestValidator.validate(requesterEmail, recipientEmail, amount, description, sourceAccountId);

        TransferRequest request = new TransferRequest(amount, description, new Date(), TransferStatus.WAITING, requester, recipient, sourceAccount);
        return transferRequestRepository.save(request);
    }

    /**
     * Obține lista cererilor de transfer primite de un utilizator.
     *
     * @param recipientEmail emailul destinatarului cererilor
     * @return lista cererilor primite
     */
    public List<TransferRequest> getReceivedRequests(String recipientEmail) {
        return transferRequestRepository.findByRecipientEmail(recipientEmail);
    }

    /**
     * Obține lista cererilor de transfer trimise de un utilizator, ordonate descrescător după dată.
     *
     * @param requesterEmail emailul utilizatorului care a trimis cererile
     * @return lista cererilor trimise
     */
    public List<TransferRequest> getSentRequests(String requesterEmail) {
        return transferRequestRepository.findByRequesterEmailOrderByDateDesc(requesterEmail);
    }

    /**
     * Acceptă o cerere de transfer specificată prin id.
     * Verifică dacă cererea nu este deja acceptată,
     * validează conturile utilizatorilor,
     * face transferul efectiv prin serviciul TransactionService,
     * apoi actualizează statusul cererii la ACCEPTED.
     *
     * @param requestId id-ul cererii de transfer
     * @param transferRequestDTO DTO-ul care conține datele pentru procesarea cererii
     * @return cererea actualizată cu status ACCEPTED
     * @throws RuntimeException dacă cererea nu este găsită, deja acceptată,
     *                          dacă utilizatorii sau conturile nu sunt găsite,
     *                          sau dacă soldul contului destinatar este insuficient
     */
    public TransferRequest acceptRequest(long requestId, TransferRequestDTO transferRequestDTO) {
        TransferRequest request = transferRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        if (request.getStatus() == TransferStatus.ACCEPTED) {
            throw new RuntimeException("Request already accepted");
        }
        User requester = userRepository.findByEmail(transferRequestDTO.getRequesterEmail()).orElseThrow(() -> new RuntimeException("Requester not found"));
        User recipient = userRepository.findByEmail(transferRequestDTO.getRecipientEmail()).orElseThrow(() -> new RuntimeException("Recipient not found"));

        Account requesterAccount = accountRepository.findById(transferRequestDTO.getSourceAccountId()).orElseThrow(() -> new RuntimeException("Requester account not found"));
        Account recipientAccount = accountRepository.findFirstByUser(Optional.ofNullable(recipient));

        if (recipientAccount.getBalance() < request.getAmount()) {
            throw new RuntimeException("Source account balance is lower than request amount");
        }
        request.setStatus(TransferStatus.ACCEPTED);
        transactionService.addTransfer(transferRequestDTO.getRecipientEmail(), transferRequestDTO.getAmount(), recipientAccount, requesterAccount);
        return transferRequestRepository.save(request);
    }

    /**
     * Respinge o cerere de transfer specificată prin id.
     * Verifică dacă cererea nu este deja respinsă, apoi actualizează statusul la REJECTED.
     *
     * @param requestId id-ul cererii de transfer
     * @param transferRequestDTO DTO-ul cu datele cererii
     * @return cererea actualizată cu status REJECTED
     * @throws RuntimeException dacă cererea nu este găsită sau deja respinsă
     */
    public TransferRequest rejectRequest(long requestId, TransferRequestDTO transferRequestDTO) {
        TransferRequest request = transferRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        if (request.getStatus() == TransferStatus.REJECTED) {
            throw new RuntimeException("Request already rejected");
        }
        request.setStatus(TransferStatus.REJECTED);
        return transferRequestRepository.save(request);
    }

    /**
     * Șterge o cerere de transfer după id.
     *
     * @param requestId id-ul cererii care trebuie ștearsă
     */
    public void deleteRequest(long requestId) {
        transferRequestRepository.deleteById(requestId);
    }

}
