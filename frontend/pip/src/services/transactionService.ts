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
