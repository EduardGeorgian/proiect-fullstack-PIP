import { Card, CardContent } from "../ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "../ui/avatar";
import { Button } from "../ui/button";

interface Props {
  username: string;
  email: string;
}

export default function UserProfileCard({ username, email }: Props) {
  return (
    <Card className="flex flex-row  justify-between items-start  gap-4 p-4 relative ">
      <CardContent className="flex flex-row gap-4 items-start">
        <Avatar className="h-16 w-16">
          <AvatarImage src="./assets/images/react.svg" alt={username} />
          <AvatarFallback className="bg-gray-300 text-black">
            {username.charAt(0).toUpperCase()}
          </AvatarFallback>
        </Avatar>
        <CardContent className="flex flex-col justify-center items-start">
          <h2 className="text-lg font-semibold">{username}</h2>
          <p className="text-sm text-gray-500">{email}</p>
        </CardContent>

        <CardContent className="flex flex-row gap-5 items-center">
          <Button
            variant="outline"
            className="size-sm cursor-pointer bg-gray-200 hover:bg-green-300"
          >
            Send
          </Button>
          <Button
            variant="outline"
            className="size-sm cursor-pointer bg-gray-200 hover:bg-green-300"
          >
            Request
          </Button>
        </CardContent>
      </CardContent>
      <CardContent className="flex flex-row gap-5 items-center">
        <Button
          variant="outline"
          className="size-sm cursor-pointer bg-gray-200 hover:bg-red-300"
        >
          Unfriend
        </Button>
      </CardContent>
    </Card>
  );
}
