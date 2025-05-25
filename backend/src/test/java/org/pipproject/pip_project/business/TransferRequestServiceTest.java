package org.pipproject.pip_project.business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.pipproject.pip_project.business.TransferRequestService;
import org.pipproject.pip_project.dto.TransferRequestDTO;
import org.pipproject.pip_project.model.*;
import org.pipproject.pip_project.repositories.*;

import java.util.*;

class TransferRequestServiceTest {

    @Mock
    private TransferRequestRepository transferRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransferRequestService transferRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createRequest_success() {
        User requester = new User();
        requester.setEmail("requester@example.com");

        User recipient = new User();
        recipient.setEmail("recipient@example.com");

        Account account = new Account();
        account.setId(1L);

        TransferRequest savedRequest = new TransferRequest(100, "desc", new Date(), TransferStatus.WAITING, requester, recipient, account);

        when(userRepository.findByEmail("requester@example.com")).thenReturn(Optional.of(requester));
        when(userRepository.findByEmail("recipient@example.com")).thenReturn(Optional.of(recipient));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transferRequestRepository.save(any())).thenReturn(savedRequest);

        TransferRequest result = transferRequestService.createRequest(
                "requester@example.com",
                "recipient@example.com",
                100,
                "desc",
                1L
        );

        assertEquals(TransferStatus.WAITING, result.getStatus());
        assertEquals(100, result.getAmount());
        verify(transferRequestRepository).save(any());
    }

    @Test
    void createRequest_userNotFound_throws() {
        when(userRepository.findByEmail("requester@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            transferRequestService.createRequest("requester@example.com", "recipient@example.com", 100, "desc", 1L);
        });

        assertEquals("Requester not found", ex.getMessage());
    }

    @Test
    void acceptRequest_success() {
        TransferRequest request = new TransferRequest();
        request.setStatus(TransferStatus.WAITING);
        request.setAmount(50);

        User requester = new User();
        requester.setEmail("requester@example.com");

        User recipient = new User();
        recipient.setEmail("recipient@example.com");

        Account requesterAccount = new Account();
        requesterAccount.setId(1L);

        Account recipientAccount = new Account();
        recipientAccount.setBalance(100.0);

        TransferRequestDTO dto = new TransferRequestDTO();
        dto.setRequesterEmail("requester@example.com");
        dto.setRecipientEmail("recipient@example.com");
        dto.setSourceAccountId(1L);
        dto.setAmount(50);

        when(transferRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(userRepository.findByEmail("requester@example.com")).thenReturn(Optional.of(requester));
        when(userRepository.findByEmail("recipient@example.com")).thenReturn(Optional.of(recipient));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(requesterAccount));
        when(accountRepository.findFirstByUser(Optional.of(recipient))).thenReturn(recipientAccount);
        when(transferRequestRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionService.addTransfer(anyString(), anyDouble(), any(Account.class), any(Account.class))).thenReturn(new Transaction());

        TransferRequest result = transferRequestService.acceptRequest(1L, dto);

        assertEquals(TransferStatus.ACCEPTED, result.getStatus());
        verify(transactionService).addTransfer(eq(dto.getRecipientEmail()), eq(dto.getAmount()), eq(recipientAccount), eq(requesterAccount));
    }

    @Test
    void acceptRequest_alreadyAccepted_throws() {
        TransferRequest request = new TransferRequest();
        request.setStatus(TransferStatus.ACCEPTED);

        when(transferRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            transferRequestService.acceptRequest(1L, new TransferRequestDTO());
        });

        assertEquals("Request already accepted", ex.getMessage());
    }

    @Test
    void rejectRequest_success() {
        TransferRequest request = new TransferRequest();
        request.setStatus(TransferStatus.WAITING);

        when(transferRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(transferRequestRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        TransferRequestDTO dto = new TransferRequestDTO();

        TransferRequest result = transferRequestService.rejectRequest(1L, dto);

        assertEquals(TransferStatus.REJECTED, result.getStatus());
    }

    @Test
    void rejectRequest_alreadyRejected_throws() {
        TransferRequest request = new TransferRequest();
        request.setStatus(TransferStatus.REJECTED);

        when(transferRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            transferRequestService.rejectRequest(1L, new TransferRequestDTO());
        });

        assertEquals("Request already rejected", ex.getMessage());
    }

    @Test
    void deleteRequest_invokesRepository() {
        doNothing().when(transferRequestRepository).deleteById(1L);
        transferRequestService.deleteRequest(1L);
        verify(transferRequestRepository).deleteById(1L);
    }
}
