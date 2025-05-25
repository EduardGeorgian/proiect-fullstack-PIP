import { Card, CardContent } from "@/components/ui/card";
import { Account } from "@/lib/types";
import { Button } from "@/components/ui/button";

interface AccountListProps {
  accounts: Account[];
  userEmail: string;
  userId: number;
  onDeleteAccount: (accountId: number) => void;
  onDeposit: (accountId: number) => void;
}

export default function AccountList({
  accounts,

  onDeleteAccount,
  onDeposit,
}: AccountListProps) {
  return (
    <div className="grid gap-4 sm:grid-cols-2 md:grid-cols-3">
      {accounts.length === 0 && (
        <div className="col-span-full text-start text-gray-500">
          No accounts available. Please create one.
        </div>
      )}
      {accounts.map((account) => (
        <Card
          key={account.id}
          className="hover:shadow-lg transition-shadow cursor-pointer"
        >
          <CardContent className="p-4 pb-2 flex flex-row justify-between items-start gap-4">
            <div className="flex flex-col">
              <div className="text-sm text-muted-foreground">Account ID</div>
              <div className="text-lg font-semibold mb-2">{account.id}</div>
              <div className="text-sm text-muted-foreground">Balance</div>
              <div className="text-2xl font-bold text-green-600">
                {account.balance.toFixed(2)} {account.currency}
              </div>
            </div>
            <div className="flex items-end justify-between">
              <div className="flex flex-col gap-10">
                <Button
                  className="bg-green-600 hover:bg-green-700 text-white"
                  onClick={() => onDeposit(account.id)}
                >
                  Deposit
                </Button>
                <Button
                  variant="destructive"
                  onClick={() => onDeleteAccount(account.id)}
                  className="bg-red-600 hover:bg-red-700 text-white"
                >
                  Delete
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
