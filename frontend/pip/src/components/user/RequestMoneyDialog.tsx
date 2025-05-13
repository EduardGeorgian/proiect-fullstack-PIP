// components/user/RequestMoneyDialog.tsx
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import { Account } from "@/lib/types";

interface Props {
  open: boolean;
  onClose: () => void;
  onRequest: (
    amount: number,
    description: string,
    sourceAccountId: number
  ) => void;
  recipientName: string;
  userAccounts: Account[];
}

export default function RequestMoneyDialog({
  open,
  onClose,
  onRequest,
  recipientName,
  userAccounts,
}: Props) {
  const [amount, setAmount] = useState<number | null>(null);
  const [description, setDescription] = useState<string>("");
  const [sourceAccountId, setSourceAccountId] = useState<number | null>(null);

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Request money from {recipientName}</DialogTitle>
        </DialogHeader>

        {/* Amount */}
        <label className="block mb-2 mt-2">Amount:</label>
        <Input
          type="number"
          value={amount ?? ""}
          onChange={(e) => {
            const value = parseFloat(e.target.value);
            setAmount(isNaN(value) ? null : value);
          }}
          className="mb-4"
        />

        {/* Description */}
        <label className="block mb-2">Description (optional):</label>
        <Input
          type="text"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className="mb-4"
        />

        {/* Source Account */}
        <label className="block mb-2">Your account (to receive into):</label>
        <select
          className="w-full p-2 border rounded mb-4"
          onChange={(e) => setSourceAccountId(Number(e.target.value))}
          defaultValue=""
        >
          <option value="" disabled>
            Select account
          </option>
          {userAccounts.map((acc) => (
            <option key={acc.id} value={acc.id}>
              #{acc.id} — {acc.currency} (Balance: {acc.balance})
            </option>
          ))}
        </select>

        <div className="flex justify-end gap-2">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button
            disabled={!amount || !sourceAccountId}
            onClick={() => {
              if (amount && sourceAccountId)
                onRequest(amount, description, sourceAccountId);
            }}
          >
            Request
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
