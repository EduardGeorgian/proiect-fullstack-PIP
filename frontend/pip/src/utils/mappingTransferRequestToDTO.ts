import { TransferRequest, TransferRequestDTO } from "@/lib/types";

export function mapToTransferRequestDTO(
  req: TransferRequest
): TransferRequestDTO {
  return {
    amount: req.amount,
    description: req.description,
    requesterEmail: req.requester.email,
    recipientEmail: req.recipient.email,
    sourceAccountId: req.sourceAccount.id,
  };
}
