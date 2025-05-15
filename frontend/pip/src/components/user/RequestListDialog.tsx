import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import { ScrollArea } from "../ui/scroll-area";
import { TransferRequest } from "@/lib/types";

interface RequestListProps {
  open: boolean;
  onClose: () => void;
  requests: TransferRequest[];
  friendName: string;
  currency: string;
  onCancel: (requestId: number) => void;
}

export default function RequestListDialog({
  open,
  onClose,
  requests,
  friendName,
  onCancel,
  currency,
}: RequestListProps) {
  console.log("Requests for dialog:", requests);

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="backdrop-blur-sm">
        <DialogHeader>
          <DialogTitle className="text-lg">
            Requests to {friendName}
          </DialogTitle>
        </DialogHeader>
        <ScrollArea className="h-64 w-full">
          {requests.length === 0 ? (
            <p className="text-muted-foreground">No pending requests.</p>
          ) : (
            requests.map((req) => (
              <div key={req.id} className="border-b py-2">
                <p>
                  <span className="font-semibold">Amount:</span> {req.amount}{" "}
                  {currency}
                </p>
                <p>
                  <span className="font-semibold">From:</span>{" "}
                  {req.requester.email}
                </p>
                <p>
                  <span className="font-semibold">To:</span>{" "}
                  {req.recipient.email}
                </p>
                <p>
                  <span className="font-semibold">Description:</span>{" "}
                  {req.description || "No description provided"}
                </p>
                <p>
                  <span className="font-semibold">Status:</span>{" "}
                  <span className="text-yellow-500">{req.status}</span>
                </p>
                <p>
                  <span className="font-semibold">Date:</span>{" "}
                  {new Date(req.date).toLocaleString(undefined, {
                    year: "numeric",
                    month: "2-digit",
                    day: "2-digit",
                    hour: "2-digit",
                    minute: "2-digit",
                    hour12: false,
                  })}
                </p>
                <div className="flex justify-end space-x-2 mt-2">
                  <button
                    onClick={() => onCancel(req.id)}
                    className="bg-yellow-300 text-black px-4 py-2 rounded"
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))
          )}
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
