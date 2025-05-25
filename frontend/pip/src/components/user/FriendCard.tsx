import { Card, CardContent } from "../ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "../ui/avatar";
import { Button } from "../ui/button";
import { Badge } from "../ui/badge";

interface Props {
  username: string;
  hasPendingRequests?: boolean;
  hasReceivedPendingRequests?: boolean;
  onViewRequestsClick?: () => void;
  onViewReceivedRequestsClick?: () => void;
  onSendClick?: () => void;
  onRequestClick?: () => void;
}

export default function FriendCard({
  username,
  hasPendingRequests = false,
  hasReceivedPendingRequests = false,
  onViewRequestsClick,
  onViewReceivedRequestsClick,
  onSendClick,
  onRequestClick,
}: Props) {
  return (
    <Card className="flex flex-row justify-between items-start gap-4 p-4 relative ">
      <CardContent className=" flex flex-row gap-4 items-start">
        <Avatar className="h-16 w-16">
          <AvatarImage src="./assets/images/react.svg" alt={username} />
          <AvatarFallback className="bg-gray-300 text-black">
            {username.charAt(0).toUpperCase()}
          </AvatarFallback>
        </Avatar>
        <div className="flex flex-row gap-2 items-center">
          <h3 className="text-md font-semibold">{username}</h3>
        </div>
        <CardContent className="sm:flex-col sm:flex lg:flex lg:flex-row lg:gap-5 lg:items-center">
          <div className="relative">
            <Button
              variant="outline"
              className="size-sm cursor-pointer bg-gray-200 hover:bg-green-300"
              onClick={onSendClick}
            >
              Send
            </Button>
            {hasReceivedPendingRequests && (
              <Badge
                onClick={(e) => {
                  e.stopPropagation();
                  onViewReceivedRequestsClick?.();
                }}
                className="absolute -top-2 -right-2 text-xs bg-red-500 text-white px-2 py-0.5 rounded-full cursor-pointer hover:bg-red-600"
              >
                !
              </Badge>
            )}
          </div>

          <div className="relative">
            <Button
              variant="outline"
              className="size-sm cursor-pointer bg-gray-200 hover:bg-green-300"
              onClick={onRequestClick}
            >
              Request
            </Button>
            {hasPendingRequests && (
              <Badge
                onClick={(e) => {
                  e.stopPropagation();
                  onViewRequestsClick?.();
                }}
                className="absolute -top-2 -right-2 text-xs bg-red-500 text-white px-2 py-0.5 rounded-full cursor-pointer hover:bg-red-600"
              >
                !
              </Badge>
            )}
          </div>
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
