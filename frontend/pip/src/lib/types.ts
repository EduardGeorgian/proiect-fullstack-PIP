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
