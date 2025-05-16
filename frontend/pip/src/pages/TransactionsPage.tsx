import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Transaction } from "@/lib/types";
import { useEffect, useState } from "react";
import { format } from "date-fns";
import { getTransactionsByUserEmail } from "@/services/transactionService";
import UserProfileCard from "@/components/user/UserProfileCard";
import ClearTransactionsDialog from "@/components/user/ClearTransactionsDialog";

export default function TransactionsPage() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState<{ username: string; email: string } | null>(
    null
  );

  useEffect(() => {
    const stored = localStorage.getItem("user");
    if (!stored) {
      setLoading(false);
      return;
    }

    const parsedUser = JSON.parse(stored);
    setUser(parsedUser);

    const fetchTransactions = async () => {
      try {
        const res = await getTransactionsByUserEmail(parsedUser.email);
        setTransactions(res.data);
      } catch (err) {
        console.error("Failed to fetch transactions:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, []);

  if (loading) return <Skeleton className="w-full h-32" />;
  if (!user) return <p>Utilizator inexistent.</p>;

  return (
    <>
      <UserProfileCard username={user.username} email={user.email} />

      <div className="flex justify-between items-center mt-4 mb-2">
        <h2 className="text-2xl font-bold">Transactions</h2>
        <ClearTransactionsDialog
          userEmail={user.email}
          onSuccess={() => {
            setTransactions((prev) =>
              prev.filter(
                (tx) => tx.status !== "COMPLETED" && tx.status !== "FAILED"
              )
            );
          }}
        />
      </div>

      {transactions.length > 0 ? (
        transactions.map((tx) => (
          <Card
            className="hover:shadow-lg transition-shadow cursor-pointer mb-4"
            key={tx.id}
          >
            <CardContent className="p-4 space-y-1">
              <div className="flex justify-between">
                <p className="font-semibold text-primary">{tx.type}</p>
                <p className="text-sm text-muted-foreground">
                  {format(new Date(tx.date), "PPPpp")}
                </p>
              </div>

              <p className="text-lg font-bold">
                {tx.amount}{" "}
                {tx.sourceAccount?.currency ||
                  tx.destinationAccount?.currency ||
                  ""}
              </p>

              <p className="text-sm">
                {tx.sourceAccount && (
                  <>
                    From Account <strong>#{tx.sourceAccount.id}</strong>
                  </>
                )}
                {tx.sourceAccount && tx.destinationAccount && " → "}
                {tx.destinationAccount && (
                  <>
                    To Account <strong>#{tx.destinationAccount.id}</strong>
                  </>
                )}
              </p>

              <p
                className={`text-sm ${
                  tx.status === "PENDING"
                    ? "text-yellow-500"
                    : tx.status === "COMPLETED"
                    ? "text-green-600"
                    : "text-red-600"
                }`}
              >
                Status: {tx.status}
              </p>
            </CardContent>
          </Card>
        ))
      ) : (
        <p className="text-muted-foreground">No transactions found.</p>
      )}
    </>
  );
}
