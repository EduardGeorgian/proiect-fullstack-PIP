import { useNavigate, useLocation } from "react-router-dom";
import { useState } from "react";
import { depositToAccount } from "@/services/accountService";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export default function MockPaymentPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);

  const accountId = Number(searchParams.get("accountId"));
  const userEmail = searchParams.get("userEmail") || "";
  const userId = Number(searchParams.get("userId"));

  const [cardNumber, setCardNumber] = useState("");
  const [expiry, setExpiry] = useState("");
  const [cvv, setCvv] = useState("");
  const [amount, setAmount] = useState("");

  const isValidCardNumber = (value: string) => /^\d{16}$/.test(value);
  const isValidExpiry = (value: string) =>
    /^(0[1-9]|1[0-2])\/\d{2}$/.test(value);
  const isValidCVV = (value: string) => /^\d{3}$/.test(value);
  const isValidAmount = (value: string) => Number(value) > 0;

  const handleMockPayment = async () => {
    if (!isValidCardNumber(cardNumber)) {
      toast.error("Invalid card number. Must be 16 digits.");
      return;
    }

    if (!isValidExpiry(expiry)) {
      toast.error("Invalid expiry date. Use MM/YY format.");
      return;
    }

    if (!isValidCVV(cvv)) {
      toast.error("Invalid CVV. Must be 3 digits.");
      return;
    }

    if (!isValidAmount(amount)) {
      toast.error("Invalid amount. Must be greater than 0.");
      return;
    }

    try {
      await depositToAccount({ accountId, userEmail, amount: Number(amount) });
      toast.success("Payment successful! ✅");
      navigate(`/dashboard/${userId}`);
    } catch (error) {
      console.error("Payment failed", error);
      toast.error("Payment failed ❌");
    }
  };

  return (
    <div className="max-w-md mx-auto mt-10 p-6 border rounded-lg shadow">
      <h1 className="text-xl font-semibold mb-4">Mock Payment</h1>

      <div className="space-y-4">
        <Input
          placeholder="Card Number (16 digits)"
          value={cardNumber}
          onChange={(e) => setCardNumber(e.target.value)}
          maxLength={16}
        />
        <Input
          placeholder="Expiry (MM/YY)"
          value={expiry}
          onChange={(e) => setExpiry(e.target.value)}
          maxLength={5}
        />
        <Input
          placeholder="CVV (3 digits)"
          value={cvv}
          onChange={(e) => setCvv(e.target.value)}
          maxLength={3}
        />
        <Input
          placeholder="Amount"
          type="number"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />

        <Button
          className="w-full bg-green-600 hover:bg-green-700 text-white"
          onClick={handleMockPayment}
        >
          Pay & Deposit
        </Button>
      </div>
    </div>
  );
}
