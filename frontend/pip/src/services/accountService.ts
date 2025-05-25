import { AccountCreateDTO } from "@/lib/types";
import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

export const createAccount = async (payload: AccountCreateDTO) => {
  return await axios.post(`${API_BASE_URL}/account/create`, payload);
};

export const getAccountsByUserEmail = async (email: string) => {
  return await axios.get(`${API_BASE_URL}/account`, {
    params: { email: email },
  });
};

export const deleteAccount = async (accountId: number) => {
  return await axios.post(
    `${API_BASE_URL}/account/delete?accountId=${accountId}`
  );
};
