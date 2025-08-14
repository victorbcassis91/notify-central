import type { MessageCategory } from '../models/MessageCategory';
import type { NotificationLog } from '../models/NotificationLog';
import type { PageResponse } from "../models/PageResponse";

export const fetchCategories = async (): Promise<MessageCategory[]> => {
    const res = await fetch("/api/categories");
    return await res.json();
};

export const fetchLogs = async (
    page: number = 0,
    size: number = 10
): Promise<PageResponse<NotificationLog>> => {
    const res = await fetch("/api/notifications?page=" + page + "&size=" + size);
    return await res.json();
};

export const getCacheStatus = async (
    statusOrderId: string
): Promise<string> => {
    const res = await fetch("/api/statusOrder/" + statusOrderId);
    return await res.json();
};

export const submitMessage = async (data: {
    category: MessageCategory;
    message: string;
}): Promise<string> => {
    const res = await fetch("/api/messages", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });

    return await res.text();
};