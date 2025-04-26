import { Card, CardContent } from "../ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "../ui/avatar";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faRightFromBracket } from "@fortawesome/free-solid-svg-icons";

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
      <div className="absolute top-4 right-4 flex flex-col items-center cursor-pointer text-gray-500 hover:text-red-500 transition-colors duration-200">
        <FontAwesomeIcon
          className="text-3xl"
          icon={faRightFromBracket}
          onClick={() => {
            localStorage.removeItem("user");
            window.location.href = "/login";
          }}
        />
        <span className="text-sm mt-1">Log out</span>
      </div>
    </Card>
  );
}
