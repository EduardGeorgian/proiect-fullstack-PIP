import { useEffect, useState } from "react";
import { getUserDashboard } from "@/services/userService";
import { User, Account } from "@/lib/types";
import UserProfileCard from "@/components/user/UserProfileCard";
import AccountList from "@/components/account/AccountList";

const Dashboard = () => {
  const [id, setId] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const id = JSON.parse(localStorage.getItem("user") || "{}").id;
    setId(id);
    const fetchDashboard = async () => {
      try {
        if (!id) return;
        const data = await getUserDashboard(id);
        setUser(data.user);
        setAccounts(data.accounts);
      } catch (error) {
        console.error("Eroare la fetch dashboard:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, [id]);

  if (loading) return <div>Se încarcă...</div>;
  if (!user) return <div>Utilizator inexistent.</div>;

  return (
    <>
      <UserProfileCard
        username={user.username}
        email={user.email}
      ></UserProfileCard>
      <h2 className="text-2xl font-bold mt-4">Accounts</h2>
      <AccountList accounts={accounts} />
    </>
  );
};

export default Dashboard;
