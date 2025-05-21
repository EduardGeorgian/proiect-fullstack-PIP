import { useEffect, useState } from "react";
import { getUserDashboard } from "@/services/userService";
import { User, Account } from "@/lib/types";
import UserProfileCard from "@/components/user/UserProfileCard";
import AccountList from "@/components/account/AccountList";
import CreateAccountDialog from "@/components/user/CreateAccountDialog";

const Dashboard = () => {
  const [user, setUser] = useState<User | null>(null);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);

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
            setAccounts((prev) => [...prev, ...newAccounts]);
          }}
        />
      </div>
      {!accounts.length ? (
        <p className="text-muted-foreground mt-2">No accounts found.</p>
      ) : (
        <AccountList accounts={accounts} />
      )}
    </>
  );
};

export default Dashboard;
