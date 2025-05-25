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
  createAccount,
  getAccountsByUserEmail,
} from "@/services/accountService";
import { toast } from "sonner";
import { useState } from "react";
import { Account, AccountCreateDTO } from "@/lib/types";

type Props = {
  accountCreateDTO: AccountCreateDTO;
  onSuccess: (accounts: Account[]) => void;
};

export default function CreateAccountDialog({
  accountCreateDTO,
  onSuccess,
}: Props) {
  const [loading, setLoading] = useState(false);
  const [password, setPassword] = useState("");

  const handleCreate = async () => {
    if (!accountCreateDTO.user) {
      toast.error("User data is missing.", {
        icon: "❌",
      });
      return;
    }

    accountCreateDTO.user.password = password;
    console.log(accountCreateDTO);

    setLoading(true);
    try {
      await createAccount(accountCreateDTO);
      const res = await getAccountsByUserEmail(accountCreateDTO.user.email);
      const accounts = res.data;
      console.log(accounts);
      onSuccess(accounts);
      toast.success("Account created successfully.", {
        icon: "✅",
      });

      return;
    } catch (err: any) {
      console.error(err);
      toast.error(err.response?.data?.error || "Failed to create account.", {
        icon: "❌",
      });
    } finally {
      setLoading(false);
    }
  };
  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>
        <Button variant="outline">Create Account</Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Confirm Account Creation</AlertDialogTitle>
          <AlertDialogDescription>
            Are you sure you want to create an account with the currency{" "}
            {accountCreateDTO.currency}?
          </AlertDialogDescription>
        </AlertDialogHeader>
        <div className="my-4">
          <label
            htmlFor="account-password"
            className="block mb-2 text-sm font-medium"
          >
            Enter your password
          </label>
          <input
            id="account-password"
            type="password"
            className="w-full px-3 py-2 border rounded"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            autoComplete="current-password"
          />
        </div>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction
            onClick={handleCreate}
            disabled={loading}
            className="bg-green-500 hover:bg-green-600 text-white"
          >
            {loading ? "Creating..." : "Create"}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
