package org.pipproject.pip_project.controllers;

import org.pipproject.pip_project.business.TransferRequestService;
import org.pipproject.pip_project.dto.TransferRequestDTO;
import org.pipproject.pip_project.model.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller that handles operations related to transfer requests.
 */
@RestController
@RequestMapping("api/requests")
@CrossOrigin(origins = "*")
public class TransferRequestController {
    private final TransferRequestService transferRequestService;

    /**
     * Constructor for {@link TransferRequestController}.
     *
     * @param transferRequestService the service for transfer request operations
     */
    @Autowired
    public TransferRequestController(TransferRequestService transferRequestService) {
        this.transferRequestService = transferRequestService;
    }

    /**
     * Adds a new transfer request.
     *
     * @param transferRequestDTO transfer request data
     * @return the created {@link TransferRequest} or an error message
     */
    @PostMapping("/add")
    public ResponseEntity<?> addTransferRequest(@RequestBody TransferRequestDTO transferRequestDTO) {
        try {
            TransferRequest request = transferRequestService.createRequest(
                    transferRequestDTO.getRequesterEmail(),
                    transferRequestDTO.getRecipientEmail(),
                    transferRequestDTO.getAmount(),
                    transferRequestDTO.getDescription(),
                    transferRequestDTO.getSourceAccountId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Retrieves all received transfer requests for a given recipient.
     *
     * @param recipientEmail email of the recipient
     * @return list of {@link TransferRequest} or an error message
     */
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedTransferRequests(@RequestParam String recipientEmail) {
        try {
            List<TransferRequest> receivedTransferRequests = transferRequestService.getReceivedRequests(recipientEmail);
            return ResponseEntity.status(HttpStatus.OK).body(receivedTransferRequests);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Retrieves all sent transfer requests for a given requester.
     *
     * @param requesterEmail email of the requester
     * @return list of {@link TransferRequest} or an error message
     */
    @GetMapping("/sent")
    public ResponseEntity<?> getSentTransferRequests(@RequestParam String requesterEmail) {
        try {
            List<TransferRequest> sentTransferRequests = transferRequestService.getSentRequests(requesterEmail);
            return ResponseEntity.status(HttpStatus.OK).body(sentTransferRequests);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Accepts a transfer request.
     *
     * @param requestId          ID of the transfer request
     * @param transferRequestDTO updated transfer request data
     * @return updated {@link TransferRequest} or an error message
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptTransferRequest(@PathVariable String requestId, @RequestBody TransferRequestDTO transferRequestDTO) {
        try {
            TransferRequest request = transferRequestService.acceptRequest(Long.parseLong(requestId), transferRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(request);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Rejects a transfer request.
     *
     * @param requestId          ID of the transfer request
     * @param transferRequestDTO transfer request data
     * @return updated {@link TransferRequest} or an error message
     */
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<?> rejectTransferRequest(@PathVariable String requestId, @RequestBody TransferRequestDTO transferRequestDTO) {
        try {
            TransferRequest request = transferRequestService.rejectRequest(Long.parseLong(requestId), transferRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(request);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Deletes a transfer request.
     *
     * @param requestId ID of the transfer request to be deleted
     * @return confirmation message or error
     */
    @PostMapping("/delete/{requestId}")
    public ResponseEntity<?> deleteTransferRequest(@PathVariable String requestId) {
        try {
            transferRequestService.deleteRequest(Long.parseLong(requestId));
            return ResponseEntity.status(HttpStatus.OK).body("Ok");
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
