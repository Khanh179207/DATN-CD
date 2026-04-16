/**
 * Format time to relative format in Vietnamese
 * Returns human-readable text like "1 phút trước", "2 giờ trước", etc.
 * @param {string|Date} dateString - Date to format
 * @returns {string} Relative time in Vietnamese
 */
export function formatTimeAgo(dateString) {
    if (!dateString) return '';

    const date = new Date(dateString);
    const now = new Date();
    const secondsAgo = Math.floor((now - date) / 1000);

    // Less than a minute
    if (secondsAgo < 60) {
        return 'Vừa xong';
    }

    // Less than 60 minutes
    if (secondsAgo < 3600) {
        const minutes = Math.floor(secondsAgo / 60);
        return `${minutes} phút trước`;
    }

    // Less than 24 hours
    if (secondsAgo < 86400) {
        const hours = Math.floor(secondsAgo / 3600);
        return `${hours} giờ trước`;
    }

    // Less than 7 days
    if (secondsAgo < 604800) {
        const days = Math.floor(secondsAgo / 86400);
        return `${days} ngày trước`;
    }

    // Fallback to formatted date (DD/MM/YYYY)
    return formatDate(date);
}

/**
 * Format date to "DD/MM/YYYY" format
 * @param {Date} date
 * @returns {string}
 */
export function formatDate(date) {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

/**
 * Get notification content in Vietnamese based on type and original content
 * @param {string} type - Notification type (COMMENT, FOLLOW, RATING, etc.)
 * @param {string} originalContent - Original content from backend
 * @returns {string} Vietnamese translated content
 */
export function getVietnameseNotificationContent(type, originalContent = '') {
    const translations = {
        COMMENT: 'đã bình luận bài viết của bạn',
        FOLLOW: 'đã theo dõi bạn',
        RATING: 'đã đánh giá bài viết của bạn',
        LIKE: 'đã thích bài viết của bạn',
        EVENT_VOTE: 'đã bình chọn bài viết của bạn trong sự kiện',
        POST_APPROVED: 'Bài viết của bạn đã được phê duyệt',
        POST_REJECTED: 'Bài viết của bạn đã bị từ chối',
        POST_DISABLED: 'Bài viết của bạn đã bị vô hiệu hóa',
        POST_PENDING_APPROVAL: 'bài viết mới cần phê duyệt',
        TICKET: 'đã gửi một yêu cầu hỗ trợ',
        FEEDBACK: 'đã gửi phản hồi',
        REPORT: 'đã gửi báo cáo',
        MESSAGE: 'đã gửi tin nhắn cho bạn'
    };

    return translations[type] || originalContent || 'có hoạt động mới';
}

/**
 * Get notification title/type in Vietnamese
 * @param {string} type - Notification type
 * @returns {string} Vietnamese title
 */
export function getVietnameseNotificationTitle(type) {
    const titles = {
        COMMENT: 'Bình luận mới',
        FOLLOW: 'Người theo dõi mới',
        RATING: 'Đánh giá mới',
        LIKE: 'Thích mới',
        EVENT_VOTE: 'Bình chọn sự kiện',
        POST_APPROVED: 'Bài viết được phê duyệt',
        POST_REJECTED: 'Bài viết bị từ chối',
        POST_DISABLED: 'Bài viết bị vô hiệu hóa',
        POST_PENDING_APPROVAL: 'Bài viết chờ phê duyệt',
        TICKET: 'Yêu cầu hỗ trợ mới',
        FEEDBACK: 'Phản hồi mới',
        REPORT: 'Báo cáo mới',
        MESSAGE: 'Tin nhắn mới'
    };

    return titles[type] || 'Thông báo mới';
}
