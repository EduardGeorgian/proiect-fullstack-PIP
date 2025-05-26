import { useEffect, useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { User } from "@/lib/types";
import { toast } from "sonner";
import { addFriend, getAllUsers } from "@/services/userService";

type Props = {
  currentUserEmail: string;
  currentUserId?: number;
  friends?: User[]; // Optional, if needed for friend requests
};

export default function FriendSearch({
  currentUserEmail,
  currentUserId,
  friends = [],
}: Props) {
  const [users, setUsers] = useState<User[]>([]);
  const [search, setSearch] = useState("");
  const [filtered, setFiltered] = useState<User[]>([]);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const res = await getAllUsers(currentUserEmail);
        setUsers(res.data ?? res); // fallback if no .data wrapper
      } catch (err) {
        console.error(err);
        toast.error("Could not load users.");
      }
    };

    fetchUsers();
  }, [currentUserEmail]);

  useEffect(() => {
    if (search.trim().length > 0) {
      const lower = search.toLowerCase();

      const safeFriends = Array.isArray(friends) ? friends : [];
      const friendIds = new Set(safeFriends.map((f) => f.id));

      const matches = users.filter(
        (user) =>
          user.id !== currentUserId &&
          !friendIds.has(user.id) &&
          (user.username.toLowerCase().includes(lower) ||
            user.email.toLowerCase().includes(lower))
      );

      setFiltered(matches);
      setOpen(true);
    } else {
      setFiltered([]);
      setOpen(false);
    }
  }, [search, users, friends, currentUserId]);

  const handleAddFriend = async (userId: number) => {
    if (typeof currentUserId === "undefined") {
      toast.error("Current user ID is missing.");
      return;
    }
    try {
      const res = await addFriend(currentUserId, userId);
      if (res.status !== 200 && res.status !== 201) {
        toast.error("Failed to send friend request.");
        return;
      }
      toast.success("✅ Friend request sent!");
    } catch (err) {
      toast.error("❌ Could not send friend request.");
      console.error(err);
    }
  };

  const handleClose = () => {
    setSearch("");
    setFiltered([]);
    setOpen(false);
  };

  if (!open) {
    return (
      <div className="max-w-xl mx-auto mt-4">
        <Input
          placeholder="Search users to add as friends..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>
    );
  }

  return (
    <>
      <div
        className="fixed inset-0 bg-black/30 backdrop-blur-xs z-40"
        onClick={handleClose}
      />
      <div className="fixed top-1/4 left-1/2 -translate-x-1/2 w-full max-w-xl bg-white rounded-xl shadow-xl p-6 z-50">
        <Input
          placeholder="Search users..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="mb-4"
          autoFocus
        />
        <ul className="space-y-2 max-h-96 overflow-y-auto">
          {filtered.length === 0 && (
            <li className="text-gray-500">No users found.</li>
          )}
          {filtered.map((user) => (
            <li
              key={user.id}
              className="flex justify-between items-center border-b pb-2"
            >
              <div>
                <div className="font-medium">{user.username}</div>
                <div className="text-sm text-muted-foreground">
                  {user.email}
                </div>
              </div>
              <Button
                size="sm"
                className="bg-blue-600 hover:bg-blue-700 text-white"
                onClick={() => handleAddFriend(user.id)}
              >
                Add Friend
              </Button>
            </li>
          ))}
        </ul>
        <div className="text-right mt-4">
          <Button variant="ghost" onClick={handleClose}>
            Close
          </Button>
        </div>
      </div>
    </>
  );
}
