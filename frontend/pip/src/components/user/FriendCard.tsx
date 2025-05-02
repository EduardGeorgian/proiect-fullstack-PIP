import { Card, CardContent } from "../ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "../ui/avatar";

interface Props {
  username: string;
  email: string;
}

export default function UserProfileCard({ username, email }: Props) {
  return (
    <Card className="flex items-center justify-between gap-4 p-4 relative">
      <Avatar className="h-16 w-16">
        <AvatarImage src="./assets/images/react.svg" alt={username} />
        <AvatarFallback className="bg-gray-300 text-black">
          {username.charAt(0).toUpperCase()}
        </AvatarFallback>
      </Avatar>
      <CardContent className="flex flex-col justify-center items-center">
        <h2 className="text-lg font-semibold">{username}</h2>
        <p className="text-sm text-gray-500">{email}</p>
      </CardContent>
    </Card>
  );
}
