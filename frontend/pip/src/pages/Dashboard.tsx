import { useEffect, useState } from "react";
import { getUserDashboard } from "@/services/userService";
import { User, Account } from "@/lib/types";
import UserProfileCard from "@/components/user/UserProfileCard";
import AccountList from "@/components/account/AccountList";
import CreateAccountDialog from "@/components/user/CreateAccountDialog";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";
import { deleteAccount } from "@/services/accountService";

const Dashboard = () => {
  const [user, setUser] = useState<User | null>(null);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();
  useEffect(() => {
    const stored = localStorage.getItem("user");
    if (stored) {
      const parsedUser = JSON.parse(stored);
      setUser(parsedUser);

      // Dupa ce am setat utilizatorul, incercam sa fetch-uim dashboard-ul
      fetchDashboard(parsedUser.id);
    } else {
      setLoading(false);
    }

    async function fetchDashboard(userId: string) {
      try {
        const data = await getUserDashboard(userId);
        setAccounts(data.accounts);
      } catch (error) {
        console.error("Eroare la fetch dashboard:", error);
      } finally {
        setLoading(false);
      }
    }
  }, []);

  const handleDeleteAccount = async (accountId: number) => {
    const account = accounts.find((acc) => acc.id === accountId);
    if (!account) return;

    if (account.balance > 0) {
      toast.error(
        `Cannot delete account ID: ${accountId} with a positive balance.`,
        {
          icon: "❌",
        }
      );
      return;
    }
    try {
      await deleteAccount(accountId);
      toast.success(`Account ID: ${accountId} deleted successfully.`, {
        icon: "✅",
      });
      setAccounts((prev) => prev.filter((acc) => acc.id !== accountId));
    } catch (err) {
      console.error("Error deleting account:", err);
      toast.error(`Failed to delete account ID: ${accountId}.`, { icon: "❌" });
    }
  };

  const handleDeposit = (accountId: number) => {
    if (!user) {
      toast.error("User information is missing.", { icon: "❌" });
      return;
    }
    navigate(
      `/mock-payment?accountId=${accountId}&userEmail=${user.email}&userId=${user.id}`
    );
  };

  if (loading) return <div>Se încarcă...</div>;
  if (!user) return <div>Utilizator inexistent.</div>;

  return (
    <>
      <UserProfileCard username={user.username} email={user.email} />
      <div className="flex justify-between items-center mt-4 mb-2">
        <h2 className="text-2xl font-bold">Accounts</h2>
        <CreateAccountDialog
          accountCreateDTO={{
            currency: "RON",
            user: {
              id: user.id,
              email: user.email,
              username: user.username,
              password: null,
            },
          }}
          onSuccess={(newAccounts) => {
            setAccounts((prev) => {
              const existingIds = new Set(prev.map((acc) => acc.id));
              const filteredNew = newAccounts.filter(
                (acc) => !existingIds.has(acc.id)
              );
              return [...prev, ...filteredNew];
            });
          }}
        />
      </div>
      {accounts.length > 0 && (
        <AccountList
          accounts={accounts}
          userEmail={user.email}
          userId={user.id}
          onDeleteAccount={handleDeleteAccount}
          onDeposit={handleDeposit}
        />
      )}
      {accounts.length === 0 && (
        <div className="text-gray-500">
          No accounts available. Please create one.
        </div>
      )}
    </>
  );
};

export default Dashboard;
