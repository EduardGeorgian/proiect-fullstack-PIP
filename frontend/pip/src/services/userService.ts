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

export const getUserFriends = async (id: string) => {
  return await axios.get(`${API_BASE_URL}/friends`, {
    params: { id: id },
  });
};

export const registerUser = async (
  username: string,
  email: string,
  password: string
) => {
  const response = await axios.post(`${API_BASE_URL}/user/register`, {
    username,
    email,
    password,
  });
  return response.data;
};
