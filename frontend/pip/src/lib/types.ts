export interface User {
  id: number;
  username: string;
  email: string;
}

export interface Account {
  id: number;
  balance: number;
  currency: string;
  user: User;
}

export interface Transaction {
  id: number;
  amount: number;
  type: string;
  date: string;
  sourceAccount: Account | null;
  destinationAccount: Account | null;
  status: string;
}

export interface TransferRequest {
  id: number;
  amount: number;
  description: string;
  date: string;
  status: string;
  currency: string;
  recipient: {
    email: string;
  };
  requester: {
    email: string;
  };
  sourceAccount: {
    id: number;
  };
}

export interface TransferRequestDTO {
  amount: number;
  description: string;
  requesterEmail: string;
  recipientEmail: string;
  sourceAccountId: number | null;
}
