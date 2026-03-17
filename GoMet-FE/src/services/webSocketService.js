import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { useAuthStore } from '@/stores/auth';

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

        if (this.connected) {
            console.log('WebSocket already connected');
            return;
        }

        // Create SockJS connection
        const socket = new SockJS('http://localhost:8080/ws');
        this.stompClient = Stomp.over(socket);

        // Disable debug logs in production
        this.stompClient.debug = () => { };

        this.stompClient.connect(
            {}, // headers
            (frame) => {
                console.log('WebSocket connected:', frame);
                this.connected = true;
                this.setupSubscriptions();
            },
            (error) => {
                console.error('WebSocket connection error:', error);
                this.connected = false;
                // Retry connection after 5 seconds
                setTimeout(() => this.connect(), 5000);
            }
        );
    }

    /**
     * Disconnect from WebSocket server
     */
    disconnect() {
        if (this.stompClient && this.connected) {
            this.stompClient.disconnect(() => {
                console.log('WebSocket disconnected');
                this.connected = false;
                this.subscriptions.clear();
            });
        }
    }

    /**
     * Setup subscriptions for notifications
     */
    setupSubscriptions() {
        const authStore = useAuthStore();

        if (!authStore.user?.accountID) return;

        // Subscribe to user-specific notifications
        const notificationSubscription = this.stompClient.subscribe(
            `/user/queue/notifications`,
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