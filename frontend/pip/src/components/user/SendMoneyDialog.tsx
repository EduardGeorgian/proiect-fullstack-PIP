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
  onSend: (
    amount: number,
    sourceAccountId: number,
    destinationAccountId: number
  ) => void;
  recipientName: string;
  userAccounts: Account[];
  friendAccounts: Account[];
}

export default function SendMoneyDialog({
  open,
  onClose,
  onSend,
  recipientName,
  userAccounts,
  friendAccounts,
}: Props) {
  const [amount, setAmount] = useState<number | null>(null);
  const [sourceAccountId, setSourceAccountId] = useState<number | null>(null);
  const [destinationAccountId, setDestinationAccountId] = useState<
    number | null
  >(null);

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Send money to {recipientName}</DialogTitle>
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

        {/* Source Account */}
        <label className="block mb-2">Your account:</label>
        <select
          className="w-full p-2 border rounded mb-4"
          onChange={(e) => setSourceAccountId(Number(e.target.value))}
          defaultValue=""
        >
          <option value="" disabled>
            Select source account
          </option>
          {userAccounts.map((acc) => (
            <option key={acc.id} value={acc.id}>
              #{acc.id} — {acc.currency} (Balance: {acc.balance})
            </option>
          ))}
        </select>

        {/* Destination Account */}
        <label className="block mb-2">Friend's account:</label>
        <select
          className="w-full p-2 border rounded mb-4"
          onChange={(e) => setDestinationAccountId(Number(e.target.value))}
          defaultValue=""
        >
          <option value="" disabled>
            Select destination account
          </option>
          {friendAccounts.map((acc) => (
            <option key={acc.id} value={acc.id}>
              #{acc.id} — {acc.currency} (Balance: {acc.balance})
            </option>
          ))}
        </select>

        <div className="flex justify-end gap-2 cursor-pointer">
          <Button
            variant="outline"
            onClick={onClose}
            className="cursor-pointer"
          >
            Cancel
          </Button>
          <Button
            disabled={!amount || !sourceAccountId || !destinationAccountId}
            onClick={() => {
              if (amount && sourceAccountId && destinationAccountId)
                onSend(amount, sourceAccountId, destinationAccountId);
            }}
          >
            Send
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
