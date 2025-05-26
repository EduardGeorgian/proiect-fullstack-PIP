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

export const getAllUsers = async (userEmail: string) => {
  const response = await axios.post(`${API_BASE_URL}/user/users`, null, {
    params: { userEmail: userEmail },
  });

  return response;
};

export const addFriend = async (userId: number, friendId: number) => {
  const response = await axios.post(
    `${API_BASE_URL}/friends/add`,
    {
      user_id: userId,
      friend_id: friendId,
    },
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return response;
};

export const getReceivedFriendRequests = async (userId: number) => {
  const response = await axios.get(
    `${API_BASE_URL}/friends/requests/received`,
    {
      params: { userId: userId },
    }
  );
  return response.data;
};

export const acceptFriendRequest = async (userId: number, senderId: number) => {
  const response = await axios.post(
    `${API_BASE_URL}/friends/requests/accept`,
    {
      user_id: userId,
      friend_id: senderId,
    },
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return response.data;
};

export const rejectFriendRequest = async (userId: number, senderId: number) => {
  const response = await axios.post(
    `${API_BASE_URL}/friends/requests/reject`,
    {
      user_id: userId,
      friend_id: senderId,
    },
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return response.data;
};

export const unfriendUser = async (userId: number, friendId: number) => {
  const response = await axios.post(
    `${API_BASE_URL}/friends/unfriend?userId=${userId}&friendId=${friendId}`,
    null, // nu trimitem body
    {
      headers: {
        "Content-Type": "application/json",
      },
    }
  );
  return response.data;
};
