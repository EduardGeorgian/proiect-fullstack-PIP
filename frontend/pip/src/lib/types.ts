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
