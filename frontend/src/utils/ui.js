/**
 * Common UI utility functions and constants.
 * Fixes: Duplicated Code smell across Vue components.
 * Single Responsibility: Provide reusable UI helper functions.
 */

/**
 * Common UI constants.
 * Fixes: Magic numbers and duplicated constants.
 */
export const UI_CONSTANTS = {
    PAGINATION: {
        DEFAULT_PAGE_SIZE: 10,
        PAGE_SIZE_OPTIONS: [5, 10, 25, 50],
    },
    DEBOUNCE: {
        SEARCH_DELAY: 300,
        INPUT_DELAY: 500,
    },
    TIMEOUTS: {
        SUCCESS_MESSAGE: 3000,
        ERROR_MESSAGE: 5000,
    },
    BREAKPOINTS: {
        MOBILE: 768,
        TABLET: 1024,
    }
};

/**
 * Debounce utility function.
 * Fixes: Duplicated debounce implementations.
 */
export function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Format date for display.
 * Fixes: Duplicated date formatting code.
 */
export function formatDate(date, options = {}) {
    if (!date) return '';

    const defaultOptions = {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        ...options
    };

    try {
        const dateObj = typeof date === 'string' ? new Date(date) : date;
        return dateObj.toLocaleDateString('en-US', defaultOptions);
    } catch (error) {
        console.error('Date formatting error:', error);
        return 'Invalid Date';
    }
}

/**
 * Format date and time for display.
 */
export function formatDateTime(date) {
    return formatDate(date, {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

/**
 * Truncate text with ellipsis.
 * Fixes: Duplicated text truncation code.
 */
export function truncateText(text, maxLength = 100) {
    if (!text || text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

/**
 * Capitalize first letter of each word.
 * Fixes: Duplicated text transformation code.
 */
export function capitalizeWords(text) {
    if (!text) return '';
    return text.replace(/\w\S*/g, (txt) =>
        txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase()
    );
}

/**
 * Generate initials from name.
 * Fixes: Duplicated initial generation code.
 */
export function getInitials(firstName, lastName) {
    const first = firstName ? firstName.charAt(0).toUpperCase() : '';
    const last = lastName ? lastName.charAt(0).toUpperCase() : '';
    return first + last;
}

/**
 * Pagination utility class.
 * Fixes: Duplicated pagination logic across components.
 */
export class Pagination {
    constructor(totalItems = 0, pageSize = UI_CONSTANTS.PAGINATION.DEFAULT_PAGE_SIZE) {
        this.totalItems = totalItems;
        this.pageSize = pageSize;
        this.currentPage = 1;
    }

    get totalPages() {
        return Math.ceil(this.totalItems / this.pageSize);
    }

    get startIndex() {
        return (this.currentPage - 1) * this.pageSize;
    }

    get endIndex() {
        return Math.min(this.startIndex + this.pageSize, this.totalItems);
    }

    get hasNext() {
        return this.currentPage < this.totalPages;
    }

    get hasPrevious() {
        return this.currentPage > 1;
    }

    goToPage(page) {
        if (page >= 1 && page <= this.totalPages) {
            this.currentPage = page;
        }
    }

    nextPage() {
        if (this.hasNext) {
            this.currentPage++;
        }
    }

    previousPage() {
        if (this.hasPrevious) {
            this.currentPage--;
        }
    }

    getPageNumbers(maxVisible = 5) {
        const pages = [];
        const start = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
        const end = Math.min(this.totalPages, start + maxVisible - 1);

        for (let i = start; i <= end; i++) {
            pages.push(i);
        }

        return pages;
    }
}

/**
 * Loading state manager.
 * Fixes: Duplicated loading state management.
 */
export class LoadingManager {
    constructor() {
        this.loadingStates = new Map();
    }

    setLoading(key, isLoading = true) {
        this.loadingStates.set(key, isLoading);
    }

    isLoading(key) {
        return this.loadingStates.get(key) || false;
    }

    isAnyLoading() {
        return Array.from(this.loadingStates.values()).some(loading => loading);
    }

    clearAll() {
        this.loadingStates.clear();
    }
}

/**
 * Message manager for notifications.
 * Fixes: Duplicated message handling code.
 */
export class MessageManager {
    constructor() {
        this.messages = [];
        this.nextId = 1;
    }

    addMessage(text, type = 'info', duration = UI_CONSTANTS.TIMEOUTS.SUCCESS_MESSAGE) {
        const message = {
            id: this.nextId++,
            text,
            type, // success, error, warning, info
            timestamp: Date.now(),
            duration
        };

        this.messages.push(message);

        if (duration > 0) {
            setTimeout(() => {
                this.removeMessage(message.id);
            }, duration);
        }

        return message.id;
    }

    removeMessage(id) {
        const index = this.messages.findIndex(msg => msg.id === id);
        if (index !== -1) {
            this.messages.splice(index, 1);
        }
    }

    clearAll() {
        this.messages.length = 0;
    }

    success(text, duration) {
        return this.addMessage(text, 'success', duration);
    }

    error(text, duration = UI_CONSTANTS.TIMEOUTS.ERROR_MESSAGE) {
        return this.addMessage(text, 'error', duration);
    }

    warning(text, duration) {
        return this.addMessage(text, 'warning', duration);
    }

    info(text, duration) {
        return this.addMessage(text, 'info', duration);
    }
}

/**
 * Local storage utility.
 * Fixes: Duplicated localStorage handling code.
 */
export const storage = {
    get(key, defaultValue = null) {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : defaultValue;
        } catch (error) {
            console.error('Error reading from localStorage:', error);
            return defaultValue;
        }
    },

    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
            return true;
        } catch (error) {
            console.error('Error writing to localStorage:', error);
            return false;
        }
    },

    remove(key) {
        try {
            localStorage.removeItem(key);
            return true;
        } catch (error) {
            console.error('Error removing from localStorage:', error);
            return false;
        }
    },

    clear() {
        try {
            localStorage.clear();
            return true;
        } catch (error) {
            console.error('Error clearing localStorage:', error);
            return false;
        }
    }
};

/**
 * URL parameter utilities.
 * Fixes: Duplicated URL handling code.
 */
export const urlUtils = {
    getParams() {
        return new URLSearchParams(window.location.search);
    },

    getParam(name, defaultValue = null) {
        const params = this.getParams();
        return params.get(name) || defaultValue;
    },

    setParam(name, value) {
        const params = this.getParams();
        params.set(name, value);
        window.history.replaceState({}, '', `${window.location.pathname}?${params}`);
    },

    removeParam(name) {
        const params = this.getParams();
        params.delete(name);
        window.history.replaceState({}, '', `${window.location.pathname}?${params}`);
    }
};