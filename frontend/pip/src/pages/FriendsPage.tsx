import { Skeleton } from "@/components/ui/skeleton";
import FriendCard from "@/components/user/FriendCard";
import UserProfileCard from "@/components/user/UserProfileCard";
import { User } from "@/lib/types";
import { getUserFriends } from "@/services/userService";
import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";

export default function FriendsPage() {
  const [friends, setFriends] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const [id, setId] = useState<string | null>(null);
  const { pathname } = useLocation(); // for refresh on route change

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    setId(user?.id);
  }, [pathname]); // update when path changes

  useEffect(() => {
    const fetchFriends = async () => {
      if (!id) return;

      try {
        const res = await getUserFriends(id);
        setFriends(res.data);
      } catch (err) {
        console.error("Failed to fetch friends:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchFriends();
  });
  //empty commit
  return (
    <>
      <UserProfileCard
        username={user.username}
        email={user.email}
      ></UserProfileCard>
      <h2 className="text-2xl font-bold mt-4">Friends</h2>

      {loading ? (
        <Skeleton className="w-full h-32" />
      ) : friends.length > 0 ? (
        friends.map((friend) => (
          <div
            className="hover:shadow-lg transition-shadow cursor-pointer mb-4 "
            key={friend.id}
          >
            <FriendCard
              username={friend.username}
              email={friend.email}
            ></FriendCard>
          </div>
        ))
      ) : (
        <p className="text-muted-foreground">No friends found.</p>
      )}
    </>
  );
}
