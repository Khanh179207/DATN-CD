import i18n, { formatLocaleDate } from '@/i18n'

/**
 * Format time to a relative string based on the current locale.
 * @param {string|Date} dateString - Date to format
 * @returns {string} Relative time in the current locale
 */
export function formatTimeAgo(dateString) {
    if (!dateString) return '';

    const date = new Date(dateString);
    const now = new Date();
    const secondsAgo = Math.floor((now - date) / 1000);
    const locale = i18n.global.locale.value === 'en' ? 'en' : 'vi';
    const relativeFormatter = new Intl.RelativeTimeFormat(locale, { numeric: 'auto' });

    if (secondsAgo < 60) {
        return i18n.global.t('common.just_now');
    }

    if (secondsAgo < 3600) {
        const minutes = Math.floor(secondsAgo / 60);
        return relativeFormatter.format(-minutes, 'minute');
    }

    if (secondsAgo < 86400) {
        const hours = Math.floor(secondsAgo / 3600);
        return relativeFormatter.format(-hours, 'hour');
    }

    if (secondsAgo < 604800) {
        const days = Math.floor(secondsAgo / 86400);
        return relativeFormatter.format(-days, 'day');
    }

    return formatDate(date);
}

/**
 * Format date based on the current locale.
 * @param {Date} date
 * @returns {string}
 */
export function formatDate(date) {
    return formatLocaleDate(date);
}

/**
 * Get notification content based on the current locale.
 * @param {string} type - Notification type (COMMENT, FOLLOW, RATING, etc.)
 * @param {string} originalContent - Original content from backend
 * @returns {string} Localized content
 */
export function getVietnameseNotificationContent(type, originalContent = '') {
    const translations = {
        COMMENT: i18n.global.t('admin.notifications.activities.comment'),
        FOLLOW: i18n.global.t('admin.notifications.activities.follow'),
        RATING: i18n.global.t('admin.notifications.activities.rating'),
        LIKE: i18n.global.t('admin.notifications.activities.like'),
        EVENT_VOTE: i18n.global.t('admin.notifications.activities.event_vote'),
        POST_APPROVED: i18n.global.t('admin.notifications.activities.post_approved'),
        POST_REJECTED: i18n.global.t('admin.notifications.activities.post_rejected'),
        POST_DISABLED: i18n.global.t('admin.notifications.activities.post_disabled'),
        POST_PENDING_APPROVAL: i18n.global.t('admin.notifications.activities.post_pending_approval'),
        TICKET: i18n.global.t('admin.notifications.activities.ticket'),
        FEEDBACK: i18n.global.t('admin.notifications.activities.feedback'),
        REPORT: i18n.global.t('admin.notifications.activities.report'),
        MESSAGE: i18n.global.t('admin.notifications.activities.message')
    };

    return translations[type] || originalContent || i18n.global.t('notifications.fallback_activity');
}

/**
 * Get notification title/type based on the current locale.
 * @param {string} type - Notification type
 * @returns {string} Localized title
 */
export function getVietnameseNotificationTitle(type) {
    const titles = {
        COMMENT: i18n.global.t('admin.notifications.titles.comment'),
        FOLLOW: i18n.global.t('admin.notifications.titles.follow'),
        RATING: i18n.global.t('admin.notifications.titles.rating'),
        LIKE: i18n.global.t('admin.notifications.titles.like'),
        EVENT_VOTE: i18n.global.t('admin.notifications.titles.event_vote'),
        POST_APPROVED: i18n.global.t('admin.notifications.titles.post_approved'),
        POST_REJECTED: i18n.global.t('admin.notifications.titles.post_rejected'),
        POST_DISABLED: i18n.global.t('admin.notifications.titles.post_disabled'),
        POST_PENDING_APPROVAL: i18n.global.t('admin.notifications.titles.post_pending_approval'),
        TICKET: i18n.global.t('admin.notifications.titles.ticket'),
        FEEDBACK: i18n.global.t('admin.notifications.titles.feedback'),
        REPORT: i18n.global.t('admin.notifications.titles.report'),
        MESSAGE: i18n.global.t('admin.notifications.titles.message')
    };

    return titles[type] || i18n.global.t('notifications.fallback_title');
}
