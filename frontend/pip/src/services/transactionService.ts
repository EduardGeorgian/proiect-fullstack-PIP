import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

export const getTransactionsByUserEmail = async (email: string) => {
  return await axios.get(`${API_BASE_URL}/transactions`, {
    params: { initiatorEmail: email },
  });
};

export const sendTransaction = async (
  initiatorEmail: string,
  transactionType: string,
  amount: number,
  sourceAccountId: number,
  destinationAccountId: number
) => {
  const response = await axios.post(`${API_BASE_URL}/transactions/send`, {
    initiatorEmail,
    transactionType,
    amount,
    sourceAccountId,
    destinationAccountId,
  });
  return response.data;
};
