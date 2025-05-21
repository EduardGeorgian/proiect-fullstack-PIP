import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

export const createAccount = async (payload: {
  AccountCreateDTO: {
    currency: string | null;
    user: {
      id: number;
      username: string;
      email: string;
      password: string | null;
    };
  };
}) => {
  return await axios.post(`${API_BASE_URL}/account/create`, payload);
};

export const getAccountsByUserEmail = async (email: string) => {
  return await axios.get(`${API_BASE_URL}/account`, {
    params: { email: email },
  });
};
