import { useEffect, useState } from "react";
import {
  acceptFriendRequest,
  getReceivedFriendRequests,
  rejectFriendRequest,
} from "@/services/userService";
import { Check, X, XCircle } from "lucide-react";
import { toast } from "sonner";

interface FriendRequest {
  id: number;
  username: string;
  email: string;
}

interface FriendRequestsProps {
  currentUserId: number;
  open: boolean;
  onClose: () => void;
  onAcceptedOrRejected?: () => void;
}

export default function FriendRequests({
  currentUserId,
  open,
  onClose,
  onAcceptedOrRejected,
}: FriendRequestsProps) {
  const [requests, setRequests] = useState<FriendRequest[]>([]);
  const [loadingIds, setLoadingIds] = useState<number[]>([]); // 👈 pentru disable

  useEffect(() => {
    if (!open) return;
    const fetchRequests = async () => {
      try {
        const data = await getReceivedFriendRequests(currentUserId);
        setRequests(data);
      } catch (err) {
        console.error("Failed to fetch friend requests:", err);
      }
    };
    fetchRequests();
  }, [open, currentUserId]);

  const handleAccept = async (id: number) => {
    setLoadingIds((prev) => [...prev, id]);
    try {
      await acceptFriendRequest(currentUserId, id);
      setRequests((prev) => prev.filter((r) => r.id !== id));
      onAcceptedOrRejected?.();
      toast.success("Friend request accepted.");
    } catch (err) {
      console.error("Accept failed:", err);
      toast.error("Failed to accept request.");
    } finally {
      setLoadingIds((prev) => prev.filter((loadingId) => loadingId !== id));
    }
  };

  const handleReject = async (id: number) => {
    setLoadingIds((prev) => [...prev, id]);
    try {
      await rejectFriendRequest(currentUserId, id);
      setRequests((prev) => prev.filter((r) => r.id !== id));
      onAcceptedOrRejected?.();
      toast.success("Friend request rejected.");
    } catch (err) {
      console.error("Reject failed:", err);
      toast.error("Failed to reject request.");
    } finally {
      setLoadingIds((prev) => prev.filter((loadingId) => loadingId !== id));
    }
  };

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 backdrop-blur-xs flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-xl max-w-md w-full p-6 relative">
        <button
          onClick={onClose}
          className="absolute top-3 right-3 text-gray-500 hover:text-red-500 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>
        <h2 className="text-xl font-bold mb-4">Friend Requests</h2>
        {requests.length === 0 ? (
          <p className="text-gray-500">No friend requests received.</p>
        ) : (
          <ul className="space-y-3">
            {requests.map((req) => (
              <li
                key={req.id}
                className="border rounded-lg p-3 bg-gray-50 flex items-center justify-between gap-2"
              >
                <span className="font-medium">
                  {req.username ?? "Unknown Sender"}
                </span>
                <div className="flex gap-2">
                  <button
                    className="text-green-600 hover:text-green-800 transition disabled:opacity-50"
                    onClick={() => handleAccept(req.id)}
                    disabled={loadingIds.includes(req.id)}
                  >
                    <Check className="w-5 h-5" />
                  </button>
                  <button
                    className="text-red-600 hover:text-red-800 transition disabled:opacity-50"
                    onClick={() => handleReject(req.id)}
                    disabled={loadingIds.includes(req.id)}
                  >
                    <XCircle className="w-5 h-5" />
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
