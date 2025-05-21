import { Card, CardContent } from "@/components/ui/card";
import { Account } from "@/lib/types";

interface AccountListProps {
  accounts: Account[];
}

export default function AccountList({ accounts }: AccountListProps) {
  return (
    <div className="grid gap-4 sm:grid-cols-2 md:grid-cols-3">
      {accounts.map((account) => (
        <Card
          key={account.id}
          className="hover:shadow-lg transition-shadow cursor-pointer"
        >
          <CardContent className="p-4">
            <div className="text-sm text-muted-foreground">Account ID</div>
            <div className="text-lg font-semibold mb-2">{account.id}</div>

            <div className="text-sm text-muted-foreground">Balance</div>
            <div className="text-2xl font-bold text-green-600">
              {account.balance.toFixed(2)} {account.currency}
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
