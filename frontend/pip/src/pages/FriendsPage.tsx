/* eslint-disable @typescript-eslint/no-explicit-any */
import { Skeleton } from "@/components/ui/skeleton";
import FriendCard from "@/components/user/FriendCard";
import UserProfileCard from "@/components/user/UserProfileCard";
import { User } from "@/lib/types";
import {
  getReceivedFriendRequests,
  getUserDashboard,
  getUserFriends,
  unfriendUser,
} from "@/services/userService";
import { useEffect, useState } from "react";
import SendMoneyDialog from "@/components/user/SendMoneyDialog";
import {
  acceptTransferRequest,
  deleteTransferRequest,
  getReceivedTransactionRequests,
  getSentTransactionRequests,
  rejectTransferRequest,
  sendTransaction,
} from "@/services/transactionService";
import { toast } from "sonner";
import { CheckCircle, XCircle } from "lucide-react";
import RequestMoneyDialog from "@/components/user/RequestMoneyDialog";
import { requestTransaction } from "@/services/transactionService";
import RequestListDialog from "@/components/user/RequestListDialog";
import { TransferRequest } from "@/lib/types";
import TransferRequestsDialog from "@/components/user/TransferRequestsDialog";
import { mapToTransferRequestDTO } from "@/utils/mappingTransferRequestToDTO";
import FriendSearch from "@/components/user/FriendSearch";
import FriendRequests from "@/components/user/FriendRequests";
import { Bell } from "lucide-react";

export default function FriendsPage() {
  const [user, setUser] = useState<User | null>(null);
  const [friends, setFriends] = useState<User[]>([]);
  const [selectedFriend, setSelectedFriend] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [accounts, setAccounts] = useState([]);
  const [friendAccounts, setFriendAccounts] = useState([]);
  const [transferBalance, setTransferBalance] = useState<string | null>(null);
  const [showRequestDialog, setShowRequestDialog] = useState(false);
  const [showRequestListDialog, setShowRequestListDialog] = useState(false);
  const [showReceivedRequestListDialog, setShowReceivedRequestListDialog] =
    useState(false);
  const [requestsForFriend, setRequestsForFriend] = useState<TransferRequest[]>(
    []
  );
  const [requestsReceivedFromFriend, setRequestsReceivedFromFriend] = useState<
    TransferRequest[]
  >([]);
  const [selectedRequestFriendName, setSelectedRequestFriendName] =
    useState<string>("");
  const [sentRequests, setSentRequests] = useState<TransferRequest[]>([]);
  const [receivedRequests, setReceivedRequests] = useState<TransferRequest[]>(
    []
  );
  const [showFriendRequests, setShowFriendRequests] = useState(false);
  const [pendingFriendRequests, setPendingFriendRequests] = useState<number>(0);

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
        const receivedRes = await getReceivedTransactionRequests(
          parsedUser.email
        );
        const sentPendingRequests = sentRes.data.filter(
          (r: any) => r.status === "WAITING"
        );

        const receivedPendingRequests = receivedRes.data.filter(
          (r: any) => r.status === "WAITING"
        );

        sentPendingRequests.forEach((req: any) => {
          req.currency = transferBalance;
        });

        receivedPendingRequests.forEach((req: any) => {
          req.currency = transferBalance;
        });

        setSentRequests(sentPendingRequests);
        setReceivedRequests(receivedPendingRequests);
        const map: Record<string, boolean> = {};
        const map2: Record<string, boolean> = {};
        for (const friend of friendList) {
          map[friend.email] = sentPendingRequests.some(
            (req: any) =>
              req.recipient?.email?.trim().toLowerCase() ===
              friend.email?.trim().toLowerCase()
          );
        }

        for (const friend of friendList) {
          map2[friend.email] = receivedPendingRequests.some(
            (req: any) =>
              req.requester?.email?.trim().toLowerCase() ===
              friend.email?.trim().toLowerCase()
          );
        }
      } catch (err) {
        console.error("Failed to fetch friends or pending requests:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchFriends();
  }, [transferBalance]);

  useEffect(() => {
    if (!user) return;
    const fetchPendingRequests = async () => {
      try {
        const data = await getReceivedFriendRequests(user.id);
        setPendingFriendRequests(data.length);
      } catch (err) {
        console.error("Could not fetch pending friend requests", err);
      }
    };
    fetchPendingRequests();
  }, [user]);

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

  const handleViewReceivedRequests = (friend: User) => {
    if (!user?.email || !friend?.email) return;
    const requests = receivedRequests.filter(
      (r: TransferRequest) =>
        r.requester?.email?.trim().toLowerCase() ===
          friend.email?.trim().toLowerCase() && r.status === "WAITING"
    );
    setRequestsReceivedFromFriend(requests);
    setSelectedRequestFriendName(friend.username);
    setShowReceivedRequestListDialog(true);
  };

  const handleAcceptRequest = async (
    requestId: number,
    transferRequestDTO: TransferRequest
  ) => {
    if (!user?.email) return;

    try {
      await acceptTransferRequest(
        String(requestId),
        mapToTransferRequestDTO(transferRequestDTO)
      );
      setRequestsReceivedFromFriend((prev) =>
        prev.filter((req) => req.id !== requestId)
      );

      setReceivedRequests((prev) => prev.filter((req) => req.id !== requestId));

      toast.success("Request accepted successfully.", {
        icon: <CheckCircle className="text-green-500" />,
      });
    } catch (err) {
      console.error("Failed to accept request:", err);
      toast.error("Failed to accept request.", {
        icon: <XCircle className="text-red-500" />,
      });
    }
  };

  const handleRejectRequest = async (
    requestId: number,
    transferRequestDTO: TransferRequest
  ) => {
    if (!user?.email) return;

    try {
      await rejectTransferRequest(
        String(requestId),
        mapToTransferRequestDTO(transferRequestDTO)
      );
      setRequestsReceivedFromFriend((prev) =>
        prev.filter((req) => req.id !== requestId)
      );
      setReceivedRequests((prev) => prev.filter((req) => req.id !== requestId));

      toast.success("Request rejected successfully.", {
        icon: <CheckCircle className="text-green-500" />,
      });
    } catch (err) {
      console.error("Failed to reject request:", err);
      toast.error("Failed to reject request.", {
        icon: <XCircle className="text-red-500" />,
      });
    }
  };

  const handleDeleteRequest = async (requestId: number) => {
    if (!user?.email) return;

    try {
      await deleteTransferRequest(String(requestId));
      setRequestsForFriend((prev) =>
        prev.filter((req) => req.id !== requestId)
      );
      setSentRequests((prev) => prev.filter((req) => req.id !== requestId));
      toast.success("Request deleted successfully.", {
        icon: <CheckCircle className="text-green-500" />,
      });
    } catch (err) {
      console.error("Failed to delete request:", err);
      toast.error("Failed to delete request.", {
        icon: <XCircle className="text-red-500" />,
      });
    }
  };

  const handleUnfriend = async (userId: number, friendId: number) => {
    if (!user) return;
    try {
      await unfriendUser(userId, friendId);
      setFriends((prev) => prev.filter((f) => f.id !== friendId));
      toast.success("Unfriended successfully.", {
        icon: <CheckCircle className="text-green-500" />,
      });
    } catch (err) {
      console.error("Failed to unfriend:", err);
      toast.error("Failed to unfriend.", {
        icon: <XCircle className="text-red-500" />,
      });
    }
  };

  if (!user) return <div>Se încarcă...</div>;

  return (
    <>
      <UserProfileCard username={user.username} email={user.email} />

      <FriendSearch
        currentUserEmail={user.email}
        currentUserId={user.id}
        friends={friends}
      />
      <h2 className="text-2xl font-bold mt-4">Friends</h2>

      <div className="my-4 flex items-center gap-2">
        <button
          onClick={() => setShowFriendRequests(true)}
          className="flex items-center gap-2 text-blue-600 hover:underline"
        >
          <Bell className="w-5 h-5" />
          You have {pendingFriendRequests} friend request(s)
        </button>
      </div>

      {showFriendRequests && (
        <FriendRequests
          currentUserId={user.id}
          open={showFriendRequests}
          onClose={() => setShowFriendRequests(false)}
          onAcceptedOrRejected={async () => {
            try {
              const updated = await getReceivedFriendRequests(user.id);
              setPendingFriendRequests(updated.length);

              const refreshedFriends = await getUserFriends(String(user.id));
              setFriends(refreshedFriends.data);
            } catch (err) {
              console.error("Could not refresh data:", err);
            }
          }}
        />
      )}

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
              onSendClick={() => setSelectedFriend(friend)}
              onRequestClick={() => {
                setSelectedFriend(friend);
                setShowRequestDialog(true);
              }}
              onViewRequestsClick={() => handleViewRequests(friend)}
              onUnfriendClick={() => handleUnfriend(user.id, friend.id)}
              onViewReceivedRequestsClick={() =>
                handleViewReceivedRequests(friend)
              }
              hasPendingRequests={sentRequests.some(
                (req) =>
                  req.recipient?.email?.trim().toLowerCase() ===
                    friend.email?.trim().toLowerCase() &&
                  req.status === "WAITING"
              )}
              hasReceivedPendingRequests={receivedRequests.some(
                (req) =>
                  req.requester?.email?.trim().toLowerCase() ===
                    friend.email?.trim().toLowerCase() &&
                  req.status === "WAITING"
              )}
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
          onCancel={(id) => handleDeleteRequest(id)}
        />
      )}

      {showReceivedRequestListDialog && (
        <TransferRequestsDialog
          open={showReceivedRequestListDialog}
          onClose={() => setShowReceivedRequestListDialog(false)}
          friendName={selectedRequestFriendName}
          requests={requestsReceivedFromFriend}
          currency={transferBalance || ""}
          onAccept={(id) =>
            handleAcceptRequest(
              id,
              requestsReceivedFromFriend.find((r) => r.id === id)!
            )
          }
          onReject={(id) => {
            const req = requestsReceivedFromFriend.find((r) => r.id === id);
            if (req) handleRejectRequest(id, req);
          }}
        />
      )}
    </>
  );
}
