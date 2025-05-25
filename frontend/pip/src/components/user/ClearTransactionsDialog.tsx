/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import {
  AlertDialog,
  AlertDialogTrigger,
  AlertDialogContent,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogFooter,
  AlertDialogCancel,
  AlertDialogAction,
  AlertDialogDescription,
} from "@/components/ui/alert-dialog";
import { Button } from "@/components/ui/button";
import {
  clearCompletedOrFailedTransactions,
  getTransactionsByUserEmail,
} from "@/services/transactionService";
import { toast } from "sonner";
import { useState } from "react";

type Props = {
  userEmail: string;
  onSuccess: (transactions: any[]) => void;
};

export default function ClearTransactionsDialog({
  userEmail,
  onSuccess,
}: Props) {
  const [loading, setLoading] = useState(false);

  const handleClear = async () => {
    setLoading(true);
    try {
      await clearCompletedOrFailedTransactions(userEmail);
      const res = await getTransactionsByUserEmail(userEmail);
      onSuccess(res.data);
      if (!res || !Array.isArray(res.data) || res.data.length === 0) {
        toast.success("All completed and failed transactions were deleted.", {
          icon: "✅",
        });
        onSuccess([]);
        return;
      }
    } catch (err: any) {
      console.error(err);
      toast.error(
        err.response?.data?.error || "Failed to delete transactions.",
        {
          icon: "❌",
        }
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button className="bg-red-400 text-color-black hover:bg-red-500 text-white  cursor-pointer fontsize-sm">
          Clear Completed/Failed Transactions
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
          <AlertDialogDescription>
            This will permanently delete all <strong>completed</strong> or{" "}
            <strong>failed</strong> transactions.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={loading}>Cancel</AlertDialogCancel>
          <AlertDialogAction onClick={handleClear} disabled={loading}>
            {loading ? "Clearing..." : "Yes, clear them"}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
