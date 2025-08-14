import {action, makeObservable, observable} from 'mobx';
import {fetchCategories, fetchLogs, getCacheStatus, submitMessage} from '../services/NotificationApi';
import type {MessageCategory} from '../models/MessageCategory';
import type {NotificationLog} from '../models/NotificationLog';
import type {PageResponse} from "../models/PageResponse";
import React from "react";
import {StatusOrder} from "../models/StatusOrder";

export class NotificationService {

    @observable categories: MessageCategory[] = [];
    @observable logs: PageResponse<NotificationLog> | null = null;
    @observable message: string = "";
    @observable selectedCategory: MessageCategory | null = null;
    @observable notificationMessage: string | null = null;
    @observable notificationType: 'success' | 'error' | null = null;
    @observable page = 0;
    @observable rowsPerPage = 10;


    constructor() {
        makeObservable(this);
        this.initialize();
    }

    @action
    async initialize() {
        await this.fetchCategories();
        await this.fetchLogs();
    }

    @action
    handleChangePage = (_event: unknown, newPage: number) => {
        this.page = newPage;
        this.fetchLogs(newPage, this.rowsPerPage);
    };

    @action
    handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newSize = parseInt(event.target.value, 10);
        this.rowsPerPage = newSize;
        this.page = 0;
        this.fetchLogs(0, newSize);
    };

    @action
    async fetchCategories() {
        this.categories = await fetchCategories();
        if (this.categories.length > 0) {
            this.selectedCategory = this.categories[0];
        }
    }

    @action
    async fetchLogs(page: number = 0,
                    size: number = 10) {
        this.logs = await fetchLogs(page, size);
    }

    @action
    async submitMessage() {
        if (!this.message.trim() || !this.selectedCategory) {
            this.notificationMessage = "Please fill in all fields.";
            this.notificationType = "error";
            return;
        }

        try {
            const statusOrderId = await submitMessage({
                category: this.selectedCategory,
                message: this.message
            });

            this.message = "";
            this.notificationMessage = "Message sent successfully!";
            this.notificationType = "success";

            await this.pollForCacheReady(statusOrderId);
        } catch (err) {
            this.notificationMessage = "Error sending message.";
            this.notificationType = "error";
        }
    }

    async pollForCacheReady(statusOrderId: string) {
        let maxAttempts = 10;
        let attempts = 0;

        await this.wait(1000);
        while (attempts < maxAttempts) {
            const status = await getCacheStatus(statusOrderId);
            if (status === StatusOrder.SUCCESS) {
                await this.fetchLogs();
                this.notificationMessage = "Notification has sent successfully!";
                this.notificationType = "success";
                return;
            } else if (status === StatusOrder.ERROR) {
                this.notificationMessage = "Notification could not be sent.";
                this.notificationType = "error";
                return;
            }
            attempts++;
            await this.wait(1000);
        }
    }

    @action
    setMessage(msg: string) {
        this.message = msg;
    }

    @action
    setCategory(cat: MessageCategory) {
        this.selectedCategory = cat;
    }

    wait = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));
}