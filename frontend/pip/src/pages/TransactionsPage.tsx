import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Transaction } from "@/lib/types";
import { useEffect, useState } from "react";
import { format } from "date-fns";
import { getTransactionsByUserEmail } from "@/services/userService";
import { useLocation } from "react-router-dom";
import UserProfileCard from "@/components/user/UserProfileCard";

export default function TransactionsPage() {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading, setLoading] = useState(true);
  const [email, setEmail] = useState<string | null>(null);
  const { pathname } = useLocation(); // for refresh on route change
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    setEmail(user?.email);
  }, [pathname]); // update when path changes

  useEffect(() => {
    const fetchTransactions = async () => {
      if (!email) return;

      try {
        const res = await getTransactionsByUserEmail(email);
        setTransactions(res.data);
      } catch (err) {
        console.error("Failed to fetch transactions:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, [email]); // run only when email is set

  return (
    <>
      <UserProfileCard
        username={user.username}
        email={user.email}
      ></UserProfileCard>

      <h2 className="text-2xl font-bold mt-4">Transactions</h2>

      {loading ? (
        <Skeleton className="w-full h-32" />
      ) : transactions.length > 0 ? (
        transactions.map((tx) => (
          <Card
            className="hover:shadow-lg transition-shadow cursor-pointer mb-4 "
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
                {tx.amount} {tx.sourceAccount.currency}
              </p>
              <p className="text-sm">
                From Account <strong>#{tx.sourceAccount.id}</strong> → To
                Account <strong>#{tx.destinationAccount.id}</strong>
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
        <p>No transactions found.</p>
      )}
    </>
  );
}
