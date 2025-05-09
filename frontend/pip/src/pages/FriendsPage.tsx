import { Skeleton } from "@/components/ui/skeleton";
import FriendCard from "@/components/user/FriendCard";
import UserProfileCard from "@/components/user/UserProfileCard";
import { User } from "@/lib/types";
import { getUserDashboard, getUserFriends } from "@/services/userService";
import { useEffect, useState } from "react";
import SendMoneyDialog from "@/components/user/SendMoneyDialog";
import { sendTransaction } from "@/services/transactionService";
import { toast } from "sonner";
import { CheckCircle, XCircle } from "lucide-react";

export default function FriendsPage() {
  const [user, setUser] = useState<User | null>(null);
  const [friends, setFriends] = useState<User[]>([]);
  const [selectedFriend, setSelectedFriend] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [accounts, setAccounts] = useState([]);
  const [friendAccounts, setFriendAccounts] = useState([]);
  const [transferBalance, setTransferBalance] = useState<string | null>(null);

  // Load user from localStorage and fetch friends
  useEffect(() => {
    const stored = localStorage.getItem("user");
    if (!stored) return;

    const parsedUser = JSON.parse(stored);
    setUser(parsedUser);

    const fetchFriends = async () => {
      try {
        const res = await getUserFriends(parsedUser.id);
        setFriends(res.data);
      } catch (err) {
        console.error("Failed to fetch friends:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchFriends();
  }, []);

  // Fetch account info when selecting a friend
  useEffect(() => {
    const fetchAccounts = async () => {
      if (!user || !selectedFriend) return;

      try {
        const data = await getUserDashboard(String(user.id));
        const friendData = await getUserDashboard(String(selectedFriend.id));

        setAccounts(data.accounts);
        setFriendAccounts(friendData.accounts);
        setTransferBalance(data.accounts[0]?.currency || null);
      } catch (err) {
        console.error("Eroare la fetch dashboard:", err);
      }
    };

    fetchAccounts();
  }, [selectedFriend, user]);

  const handleSendMoney = async (
    amount: number,
    sourceAccountId: number,
    destinationAccountId: number
  ) => {
    if (!selectedFriend || !user?.email) return;

    try {
      await sendTransaction({
        initiatorEmail: user.email,
        type: "TRANSFER",
        amount,
        sourceAccountId,
        destinationAccountId,
      });

      toast.success(
        `You sent ${amount} ${transferBalance} to ${selectedFriend.username}`,
        {
          icon: <CheckCircle className="text-green-500" />,
          action: {
            label: "OK",
            onClick: (
              _event: React.MouseEvent<HTMLButtonElement>,
              toastId?: string | number
            ) => {
              toast.dismiss(toastId);
            },
          },
        }
      );

      setSelectedFriend(null);
    } catch (err) {
      console.error("Error during transaction:", err);
      toast.error("Transaction failed. Please try again.", {
        icon: <XCircle className="text-red-500" />,
        action: {
          label: "OK",
          onClick: (
            _event: React.MouseEvent<HTMLButtonElement>,
            toastId?: string | number
          ) => {
            toast.dismiss(toastId);
          },
        },
      });
    }
  };

  if (!user) return <div>Se încarcă...</div>;

  return (
    <>
      <UserProfileCard username={user.username} email={user.email} />

      <h2 className="text-2xl font-bold mt-4">Friends</h2>

      {loading ? (
        <Skeleton className="w-full h-32" />
      ) : friends.length > 0 ? (
        friends.map((friend) => (
          <div
            key={friend.id}
            className="hover:shadow-lg transition-shadow cursor-pointer mb-4"
          >
            <FriendCard
              username={friend.username}
              email={friend.email}
              onSendClick={() => setSelectedFriend(friend)}
            />
          </div>
        ))
      ) : (
        <p className="text-muted-foreground">No friends found.</p>
      )}

      {selectedFriend && (
        <SendMoneyDialog
          open={!!selectedFriend}
          onClose={() => setSelectedFriend(null)}
          onSend={handleSendMoney}
          recipientName={selectedFriend.username}
          userAccounts={accounts}
          friendAccounts={friendAccounts}
        />
      )}
    </>
  );
}
