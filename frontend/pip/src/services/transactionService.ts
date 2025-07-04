import { TransferRequestDTO } from "@/lib/types";
import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

export const getTransactionsByUserEmail = async (email: string) => {
  return await axios.get(`${API_BASE_URL}/transactions`, {
    params: { initiatorEmail: email },
  });
};

export const sendTransaction = async (payload: {
  initiatorEmail: string;
  type: "TRANSFER";
  amount: number;
  sourceAccountId: number;
  destinationAccountId: number;
}) => {
  return await axios.post(`${API_BASE_URL}/transactions/send`, payload);
};

export const requestTransaction = async (payload: {
  amount: number;
  description: string;
  requesterEmail: string;
  recipientEmail: string;
  sourceAccountId: number | null;
}) => {
  return await axios.post(`${API_BASE_URL}/requests/add`, payload);
};

export const getSentTransactionRequests = async (email: string) => {
  return await axios.get(`${API_BASE_URL}/requests/sent`, {
    params: { requesterEmail: email },
  });
};

export const getReceivedTransactionRequests = async (email: string) => {
  return await axios.get(`${API_BASE_URL}/requests/received`, {
    params: { recipientEmail: email },
  });
};

export const clearCompletedOrFailedTransactions = async (
  initiatorEmail: string
) => {
  const response = await axios.delete(`${API_BASE_URL}/transactions/clear`, {
    params: { initiatorEmail },
  });
  return response.data;
};

export const acceptTransferRequest = (
  requestId: string,
  transferRequestDTO: TransferRequestDTO
) => {
  return axios.post(
    `${API_BASE_URL}/requests/accept/${requestId}`,
    transferRequestDTO
  );
};

export const rejectTransferRequest = (
  requestId: string,
  transferRequestDTO: TransferRequestDTO
) => {
  return axios.post(`${API_BASE_URL}/requests/reject/${requestId}`, {
    data: transferRequestDTO,
  });
};

export const deleteTransferRequest = (requestId: string) => {
  return axios.post(`${API_BASE_URL}/requests/delete/${requestId}`);
};
