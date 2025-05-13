import { Skeleton } from "@/components/ui/skeleton";
import FriendCard from "@/components/user/FriendCard";
import UserProfileCard from "@/components/user/UserProfileCard";
import { User } from "@/lib/types";
import { getUserDashboard, getUserFriends } from "@/services/userService";
import { useEffect, useState } from "react";
import SendMoneyDialog from "@/components/user/SendMoneyDialog";
import {
  getSentTransactionRequests,
  sendTransaction,
} from "@/services/transactionService";
import { toast } from "sonner";
import { CheckCircle, XCircle } from "lucide-react";
import RequestMoneyDialog from "@/components/user/RequestMoneyDialog";
import { requestTransaction } from "@/services/transactionService";
import RequestListDialog from "@/components/user/RequestListDialog";
import { TransferRequest } from "@/lib/types";

export default function FriendsPage() {
  const [user, setUser] = useState<User | null>(null);
  const [friends, setFriends] = useState<User[]>([]);
  const [selectedFriend, setSelectedFriend] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [accounts, setAccounts] = useState([]);
  const [friendAccounts, setFriendAccounts] = useState([]);
  const [transferBalance, setTransferBalance] = useState<string | null>(null);
  const [showRequestDialog, setShowRequestDialog] = useState(false);
  const [pendingMap, setPendingMap] = useState<Record<string, boolean>>({});
  const [showRequestListDialog, setShowRequestListDialog] = useState(false);
  const [requestsForFriend, setRequestsForFriend] = useState<TransferRequest[]>(
    []
  );
  const [selectedRequestFriendName, setSelectedRequestFriendName] =
    useState<string>("");
  const [sentRequests, setSentRequests] = useState<TransferRequest[]>([]);

  // Load user from localStorage and fetch friends
  useEffect(() => {
    const stored = localStorage.getItem("user");
    if (!stored) return;

    const parsedUser = JSON.parse(stored);
    setUser(parsedUser);

    const fetchFriends = async () => {
      try {
        const res = await getUserFriends(parsedUser.id);
        const friendList = res.data;
        setFriends(friendList);

        const sentRes = await getSentTransactionRequests(parsedUser.email);
        const sentPendingRequests = sentRes.data.filter(
          (r: any) => r.status === "WAITING"
        );

        sentPendingRequests.forEach((req: any) => {
          req.currency = transferBalance;
        });

        setSentRequests(sentPendingRequests);
        const map: Record<string, boolean> = {};
        for (const friend of friendList) {
          map[friend.email] = sentPendingRequests.some(
            (req: any) =>
              req.recipient?.email?.trim().toLowerCase() ===
              friend.email?.trim().toLowerCase()
          );

          console.log("Friend email:", friend.email);
          sentPendingRequests.forEach((req: any) => {
            console.log("Checking req.recipientEmail:", req.recipientEmail);
          });
        }

        console.log("Sent pending requests:", sentPendingRequests);
        console.log("Pending map:", map);

        setPendingMap(map);
      } catch (err) {
        console.error("Failed to fetch friends or pending requests:", err);
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

  const handleRequestMoney = async (
    amount: number,
    description: string,
    sourceAccountId: number
  ) => {
    if (!selectedFriend || !user?.email || !selectedFriend.email) return;

    try {
      const res = await requestTransaction({
        amount,
        description,
        requesterEmail: user.email,
        recipientEmail: selectedFriend.email,
        sourceAccountId,
      });
      setSentRequests((prev) => [...prev, res.data]);

      const recipientEmail = res.data.recipient?.email?.toLowerCase();
      if (recipientEmail) {
        setPendingMap((prev) => ({
          ...prev,
          [recipientEmail]: true,
        }));
      }

      toast.success(`Request sent to ${selectedFriend.username}`, {
        icon: <CheckCircle className="text-green-500" />,
      });

      setSelectedFriend(null);
      setShowRequestDialog(false);
    } catch (err) {
      console.error("Request failed:", err);
      toast.error("Failed to send request.", {
        icon: <XCircle className="text-red-500" />,
      });
    }
  };

  const handleViewRequests = (friend: User) => {
    if (!user?.email || !friend?.email) return;

    const requests = sentRequests.filter(
      (r: TransferRequest) =>
        r.recipient?.email?.trim().toLowerCase() ===
          friend.email?.trim().toLowerCase() && r.status === "WAITING"
    );

    setRequestsForFriend(requests);
    setSelectedRequestFriendName(friend.username);
    setShowRequestListDialog(true);
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
              onRequestClick={() => {
                setSelectedFriend(friend);
                setShowRequestDialog(true);
              }}
              onViewRequestsClick={() => handleViewRequests(friend)}
              hasPendingRequests={!!pendingMap[friend.email]}
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

      {selectedFriend && showRequestDialog && !showRequestListDialog && (
        <RequestMoneyDialog
          open={showRequestDialog}
          onClose={() => {
            setShowRequestDialog(false);
            setSelectedFriend(null);
          }}
          onRequest={handleRequestMoney}
          recipientName={selectedFriend.username}
          userAccounts={accounts}
        />
      )}

      {showRequestListDialog && (
        <RequestListDialog
          open={showRequestListDialog}
          onClose={() => setShowRequestListDialog(false)}
          friendName={selectedRequestFriendName}
          requests={requestsForFriend}
          currency={transferBalance || ""}
        />
      )}
    </>
  );
}
