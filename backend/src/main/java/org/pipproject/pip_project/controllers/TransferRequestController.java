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


@RestController
@RequestMapping("api/requests")
@CrossOrigin(origins = "*")
public class TransferRequestController {
    private final TransferRequestService transferRequestService;

    @Autowired
    public TransferRequestController(TransferRequestService transferRequestService) {
        this.transferRequestService = transferRequestService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTransferRequest(@RequestBody TransferRequestDTO transferRequestDTO) {
        try{
           TransferRequest request = transferRequestService.createRequest(transferRequestDTO.getRequesterEmail(), transferRequestDTO.getRecipientEmail(),transferRequestDTO.getAmount(),transferRequestDTO.getDescription(),transferRequestDTO.getSourceAccountId());
            return ResponseEntity.status(HttpStatus.CREATED).body(request);
        }catch(Exception e){
            Map<String,String> error = new HashMap<>();
            error.put("error",e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

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

    @GetMapping("/sent")
    public ResponseEntity<?> getSentTransferRequests(@RequestParam String requesterEmail) {
        try{
            List<TransferRequest> sentTransferRequests = transferRequestService.getSentRequests(requesterEmail);
            return ResponseEntity.status(HttpStatus.OK).body(sentTransferRequests);
        }catch(Exception e){
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<?> acceptTransferRequest(@PathVariable String requestId, @RequestBody TransferRequestDTO transferRequestDTO) {
        try {
            TransferRequest request = transferRequestService.acceptRequest(Long.parseLong(requestId), transferRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(request);
        }catch(Exception e){
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
