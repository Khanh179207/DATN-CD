import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { useAuthStore } from '@/stores/auth';

// 🚀 1. KHỞI TẠO ÂM THANH (PRE-LOAD) Ở ĐẦU FILE
const globalMsgSound = new Audio('/sounds/ting.mp3');

class WebSocketService {
    constructor() {
        this.stompClient = null;
        this.connected = false;
        this.subscriptions = new Map();
    }

    /**
     * Connect to WebSocket server
     */
    connect() {
        const authStore = useAuthStore();

        if (!authStore.isAuthenticated || !authStore.user?.accountID) {
            console.warn('Cannot connect to WebSocket: User not authenticated');
            return;
        }

        if (this.connected && this.stompClient) {
            console.log('WebSocket already connected');
            return;
        }

        try {
            // Create SockJS connection
            const socket = new SockJS('http://localhost:8080/ws');
            this.stompClient = Stomp.over(socket);

            // Disable debug logs in production
            this.stompClient.debug = () => { };

            this.stompClient.connect(
                {}, // headers
                (frame) => {
                    console.log('✅ WebSocket connected successfully');
                    this.connected = true;
                    // Setup subscriptions after a brief delay to ensure full connection
                    this.setupSubscriptions();
                },
                (error) => {
                    console.error('❌ WebSocket connection error:', error);
                    this.connected = false;

                    // Retry connection after 5 seconds
                    console.log('⏳ Retrying connection in 5 seconds...');
                    setTimeout(() => {
                        console.log('🔄 Attempting to reconnect...');
                        this.connect();
                    }, 5000);
                }
            );
        } catch (error) {
            console.error('❌ Error creating WebSocket connection:', error);
            this.connected = false;

            // Retry after 5 seconds
            setTimeout(() => this.connect(), 5000);
        }
    }

    /**
     * Disconnect from WebSocket server
     */
    disconnect() {
        try {
            if (this.stompClient && this.connected) {
                this.stompClient.disconnect(() => {
                    console.log('✅ WebSocket disconnected');
                    this.connected = false;
                    this.subscriptions.clear();
                    this.stompClient = null;
                });
            } else if (this.stompClient) {
                this.stompClient.disconnect();
                this.connected = false;
                this.subscriptions.clear();
                this.stompClient = null;
            }
        } catch (error) {
            console.error('Error disconnecting WebSocket:', error);
            this.connected = false;
            this.subscriptions.clear();
            this.stompClient = null;
        }
    }

    /**
     * Setup subscriptions for notifications
     */
    setupSubscriptions() {
        const authStore = useAuthStore();

        if (!authStore.user?.accountID) {
            console.warn('No user authenticated, cannot setup subscriptions');
            return;
        }

        const accountId = authStore.user.accountID;

        // Add a small delay to ensure the connection is fully established
        // before sending subscription requests
        setTimeout(() => {
            try {
                // ==========================================
                // 🚀 2. KÊNH LẮNG NGHE CHAT TOÀN CẦU (BÁO ÂM THANH)
                // ==========================================
                const globalChatTopic = `/topic/global-chat/${accountId}`;
                const chatSubscription = this.stompClient.subscribe(
                    globalChatTopic,
                    (message) => {
                        try {
                            const chatData = JSON.parse(message.body);
                            console.log('💬 Nhận được tin nhắn ở chế độ Global:', chatData);
                            
                            // PHÁT ÂM THANH
                            globalMsgSound.currentTime = 0; // Trả về đầu bài để phát liên tục
                            globalMsgSound.play().catch((err) => {
                                console.warn("Trình duyệt chặn autoplay âm thanh:", err);
                            });

                            // Phát CustomEvent nếu cần làm Badge báo tin nhắn mới
                            const event = new CustomEvent('global-chat-alert', { detail: chatData });
                            window.dispatchEvent(event);
                            
                        } catch (error) {
                            console.error('Error parsing global chat alert:', error);
                        }
                    }
                );
                this.subscriptions.set('global-chat', chatSubscription);
                console.log('Subscribed to: ' + globalChatTopic);

                // ==========================================
                // CODE CŨ - GIỮ NGUYÊN HOÀN TOÀN
                // ==========================================

                // Subscribe to user-specific notifications using topic-based approach
                const notificationTopic = `/topic/notifications/${accountId}`;
                const notificationSubscription = this.stompClient.subscribe(
                    notificationTopic,
                    (message) => {
                        try {
                            const notification = JSON.parse(message.body);
                            console.log('Received notification:', notification);
                            this.handleNotification(notification);
                        } catch (error) {
                            console.error('Error parsing notification:', error);
                        }
                    }
                );
                this.subscriptions.set('notifications', notificationSubscription);
                console.log('Subscribed to: ' + notificationTopic);

                // Subscribe to admin notifications (for admin users)
                const adminNotificationTopic = `/topic/admin-notifications/${accountId}`;
                const adminNotificationSubscription = this.stompClient.subscribe(
                    adminNotificationTopic,
                    (message) => {
                        try {
                            const notification = JSON.parse(message.body);
                            console.log('Received admin notification:', notification);
                            this.handleAdminNotification(notification);
                        } catch (error) {
                            console.error('Error parsing admin notification:', error);
                        }
                    }
                );
                this.subscriptions.set('admin-notifications', adminNotificationSubscription);
                console.log('Subscribed to: ' + adminNotificationTopic);

                // Subscribe to broadcast admin alerts (all admins get these)
                const adminAlertSubscription = this.stompClient.subscribe(
                    `/topic/admin-alerts`,
                    (message) => {
                        try {
                            const alert = JSON.parse(message.body);
                            console.log('Received admin alert (broadcast):', alert);
                            this.handleAdminAlert(alert);
                        } catch (error) {
                            console.error('Error parsing admin alert:', error);
                        }
                    }
                );
                this.subscriptions.set('admin-alerts', adminAlertSubscription);
                console.log('Subscribed to: /topic/admin-alerts');

                console.log('All WebSocket subscriptions setup successfully');
            } catch (error) {
                console.error('Error during subscription setup:', error);
                // Retry subscriptions after 2 seconds
                setTimeout(() => this.setupSubscriptions(), 2000);
            }
        }, 200); // Wait 200ms for connection to fully stabilize
    }

    /**
     * Handle incoming notification
     */
    handleNotification(notificationDTO) {
        // Emit custom event for components to listen to
        const event = new CustomEvent('realtime-notification', {
            detail: notificationDTO
        });
        window.dispatchEvent(event);
    }

    /**
     * Handle incoming admin notification (user-specific)
     */
    handleAdminNotification(notificationDTO) {
        // Emit custom event for admin components to listen to
        const event = new CustomEvent('admin-notification', {
            detail: notificationDTO
        });
        window.dispatchEvent(event);
    }

    /**
     * Handle incoming admin alert (broadcast to all admins)
     */
    handleAdminAlert(alertData) {
        // Emit custom event for admin components to listen to
        const event = new CustomEvent('admin-alert', {
            detail: alertData
        });
        window.dispatchEvent(event);
    }

    /**
     * Send a message to a destination
     */
    send(destination, body = {}) {
        if (this.stompClient && this.connected) {
            this.stompClient.send(destination, {}, JSON.stringify(body));
        } else {
            console.warn('WebSocket not connected, cannot send message');
        }
    }

    /**
     * Check if WebSocket is connected
     */
    isConnected() {
        return this.connected;
    }
}

// Create singleton instance
const webSocketService = new WebSocketService();

export default webSocketService;