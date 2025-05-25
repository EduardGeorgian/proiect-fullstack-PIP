package org.pipproject.pip_project.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.pipproject.pip_project.business.TransferRequestService;
import org.pipproject.pip_project.dto.TransferRequestDTO;
import org.pipproject.pip_project.model.TransferRequest;
import org.pipproject.pip_project.model.TransferStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Date;

@WebMvcTest(controllers = org.pipproject.pip_project.controllers.TransferRequestController.class)
class TransferRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferRequestService transferRequestService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TransferRequest sampleRequest;

    @BeforeEach
    void setup() {
        sampleRequest = new TransferRequest();
        sampleRequest.setAmount(100);
        sampleRequest.setStatus(TransferStatus.WAITING);
        sampleRequest.setDate(new Date());
    }

    @Test
    void addTransferRequest_success() throws Exception {
        TransferRequestDTO dto = new TransferRequestDTO();
        dto.setRequesterEmail("req@example.com");
        dto.setRecipientEmail("rec@example.com");
        dto.setAmount(100);
        dto.setDescription("desc");
        dto.setSourceAccountId(1L);

        when(transferRequestService.createRequest(anyString(), anyString(), anyDouble(), anyString(), anyLong())).thenReturn(sampleRequest);

        mockMvc.perform(post("/api/requests/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getReceivedTransferRequests_success() throws Exception {
        when(transferRequestService.getReceivedRequests("rec@example.com")).thenReturn(List.of(sampleRequest));

        mockMvc.perform(get("/api/requests/received")
                        .param("recipientEmail", "rec@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100));
    }

    @Test
    void getSentTransferRequests_success() throws Exception {
        when(transferRequestService.getSentRequests("req@example.com")).thenReturn(List.of(sampleRequest));

        mockMvc.perform(get("/api/requests/sent")
                        .param("requesterEmail", "req@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100));
    }

    @Test
    void acceptTransferRequest_success() throws Exception {
        TransferRequestDTO dto = new TransferRequestDTO();
        dto.setRequesterEmail("req@example.com");
        dto.setRecipientEmail("rec@example.com");
        dto.setAmount(100);
        dto.setSourceAccountId(1L);

        when(transferRequestService.acceptRequest(eq(1L), any())).thenReturn(sampleRequest);

        mockMvc.perform(post("/api/requests/accept/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100));
    }

    @Test
    void rejectTransferRequest_success() throws Exception {
        TransferRequestDTO dto = new TransferRequestDTO();

        when(transferRequestService.rejectRequest(eq(1L), any())).thenReturn(sampleRequest);

        mockMvc.perform(post("/api/requests/reject/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTransferRequest_success() throws Exception {
        doNothing().when(transferRequestService).deleteRequest(1L);

        mockMvc.perform(post("/api/requests/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok"));
    }
}
