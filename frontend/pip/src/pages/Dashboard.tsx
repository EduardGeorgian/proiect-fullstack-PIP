import { useEffect, useState } from "react";
import { getUserDashboard } from "@/services/userService";
import { User, Account } from "@/lib/types";
import UserProfileCard from "@/components/user/UserProfileCard";
import AccountList from "@/components/account/AccountList";

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
      <h2 className="text-2xl font-bold mt-4">Accounts</h2>
      <AccountList accounts={accounts} />
    </>
  );
};

export default Dashboard;
