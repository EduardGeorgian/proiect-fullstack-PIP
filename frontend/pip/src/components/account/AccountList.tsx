import { Card, CardContent } from "@/components/ui/card";
import { Account } from "@/lib/types";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { deleteAccount } from "@/services/accountService";

interface AccountListProps {
  accounts: Account[];
}

const handleDeleteAccount = async (accountId: number) => {
  try {
    await deleteAccount(accountId);
    toast.success(`Account ID: ${accountId} deleted successfully.`, {
      icon: "✅",
    });
    // Remove the deleted account from the list
  } catch (error) {
    console.error("Error deleting account:", error);
    toast.error(
      `Failed to delete account ID: ${accountId}. Please try again.`,
      {
        icon: "❌",
      }
    );
  }
};

export default function AccountList({ accounts }: AccountListProps) {
  return (
    <div className="grid gap-4 sm:grid-cols-2 md:grid-cols-3">
      {accounts.map((account) => (
        <Card
          key={account.id}
          className="hover:shadow-lg transition-shadow cursor-pointer"
        >
          <CardContent className="p-4 pb-2 flex flex-col gap-2">
            <div className="text-sm text-muted-foreground">Account ID</div>
            <div className="text-lg font-semibold mb-2">{account.id}</div>

            <div className="text-sm text-muted-foreground">Balance</div>
            <div className="flex items-end justify-between">
              <div className="text-2xl font-bold text-green-600">
                {account.balance.toFixed(2)} {account.currency}
              </div>
              <Button
                variant="destructive"
                className="cursor-pointer"
                onClick={() => handleDeleteAccount(account.id)}
              >
                Delete
              </Button>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}
