import { MessageCategory } from "./MessageCategory";

export interface NotificationLog {
    id: number;
    category: MessageCategory;
    channel: string;
    userId: string;
    userName: string;
    message: string;
    delivered: boolean;
    details?: string;
    timestamp: string;
}