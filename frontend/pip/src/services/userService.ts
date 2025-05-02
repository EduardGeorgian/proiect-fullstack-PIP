import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

export const getUserDashboard = async (id: string) => {
  const response = await axios.get(`${API_BASE_URL}/user/${id}`);
  return response.data;
};

export const loginUser = async (email: string, password: string) => {
  const response = await axios.post(`${API_BASE_URL}/user/login`, {
    email,
    password,
  });
  console.log("Login response:", response.data);
  return response.data;
};

export const getTransactionsByUserEmail = async (email: string) => {
  return await axios.get(`${API_BASE_URL}/transactions`, {
    params: { initiatorEmail: email },
  });
};
