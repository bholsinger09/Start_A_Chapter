/**
 * Frontend Error Handler - implements try-catch-finally first approach
 * Transforms API errors and handles error boundaries in Vue.js components
 */

import {
  UserRegistrationError,
  UserAuthenticationError,
  DataFetchError,
  InputValidationError
} from './ErrorTypes.js';

/**
 * Global Error Handler Class
 * Implements error boundaries and consistent error processing
 */
export class ErrorHandler {
  
  /**
   * Handles API errors by transforming them into domain-specific exceptions
   * Implements try-catch-finally first approach with proper cleanup
   */
  static async handleApiOperation(operation, cleanup = null) {
    let operationStarted = false;
    
    try {
      // Write try-catch-finally first, then implement operation
      operationStarted = true;
      
      // Execute the actual operation
      const result = await operation();
      return result;
      
    } catch (error) {
      // Transform API errors into domain exceptions
      throw ErrorHandler.transformApiError(error);
    } finally {
      // Always execute cleanup in finally block
      if (cleanup && operationStarted) {
        try {
          await cleanup();
        } catch (cleanupError) {
          console.warn('Cleanup failed:', cleanupError);
        }
      }
    }
  }

  /**
   * Transforms API errors into domain-specific exceptions
   * Named from caller's perspective rather than technical details
   */
  static transformApiError(error) {
    // Handle network errors
    if (!error.response) {
      return DataFetchError.networkError();
    }

    const status = error.response?.status;
    const data = error.response?.data;
    const errorType = data?.errorType;
    const errorCode = data?.errorCode;

    // Transform based on error type from backend
    switch (errorType) {
      case 'REGISTRATION_FAILED':
        return ErrorHandler.transformRegistrationError(errorCode, data);
      
      case 'AUTHENTICATION_FAILED':
        return ErrorHandler.transformAuthenticationError(errorCode, data);
      
      case 'VALIDATION_FAILED':
        return ErrorHandler.transformValidationError(data);
      
      case 'CHAPTER_OPERATION_FAILED':
        return ErrorHandler.transformChapterError(errorCode, data);
      
      default:
        return ErrorHandler.transformGenericError(status, data);
    }
  }

  /**
   * Transforms registration errors from backend into frontend domain exceptions
   */
  static transformRegistrationError(errorCode, data) {
    switch (errorCode) {
      case 'DUPLICATE_EMAIL':
        // Extract email from error message if available
        const emailMatch = data.message?.match(/email '([^']+)'/);
        const email = emailMatch ? emailMatch[1] : 'this email';
        return UserRegistrationError.duplicateEmail(email);
      
      case 'INVALID_EMAIL_FORMAT':
        const invalidEmailMatch = data.message?.match(/Email address '([^']+)'/);
        const invalidEmail = invalidEmailMatch ? invalidEmailMatch[1] : 'invalid email';
        return UserRegistrationError.invalidEmailFormat(invalidEmail);
      
      case 'WEAK_PASSWORD':
        return UserRegistrationError.weakPassword();
      
      case 'MISSING_REQUIRED_FIELD':
        const fieldMatch = data.message?.match(/^(.+) is required/);
        const fieldName = fieldMatch ? fieldMatch[1] : 'Required field';
        return UserRegistrationError.missingRequiredField(fieldName);
      
      case 'CHAPTER_NOT_FOUND':
        return UserRegistrationError.chapterNotFound('selected chapter');
      
      default:
        return new UserRegistrationError(
          data.message || 'Registration failed',
          errorCode || 'UNKNOWN_ERROR'
        );
    }
  }

  /**
   * Transforms authentication errors with security considerations
   */
  static transformAuthenticationError(errorCode, data) {
    switch (errorCode) {
      case 'AUTH_FAILED':
        return UserAuthenticationError.invalidCredentials();
      
      case 'MISSING_CREDENTIALS':
        return UserAuthenticationError.missingCredentials();
      
      case 'ACCOUNT_DISABLED':
        return UserAuthenticationError.accountDisabled();
      
      case 'TOO_MANY_ATTEMPTS':
        return UserAuthenticationError.tooManyAttempts();
      
      default:
        return UserAuthenticationError.invalidCredentials(); // Security: don't expose specific reasons
    }
  }

  /**
   * Transforms validation errors with field-specific information
   */
  static transformValidationError(data) {
    const fieldName = data.field || 'unknown field';
    const invalidValue = data.invalidValue;
    const message = data.message || 'Validation failed';
    
    return new InputValidationError(message, fieldName, invalidValue);
  }

  /**
   * Transforms chapter operation errors
   */
  static transformChapterError(errorCode, data) {
    const message = data.message || 'Chapter operation failed';
    return new DataFetchError(message, 'CHAPTER_OPERATION', errorCode);
  }

  /**
   * Transforms generic errors with appropriate user messaging
   */
  static transformGenericError(status, data) {
    const message = data?.message || 'An unexpected error occurred';
    
    switch (status) {
      case 400:
        return new InputValidationError(message, 'unknown', null);
      case 401:
        return UserAuthenticationError.invalidCredentials();
      case 403:
        return UserAuthenticationError.accountDisabled();
      case 404:
        return new DataFetchError('Requested resource not found', 'NOT_FOUND', 404);
      case 409:
        return new UserRegistrationError(message, 'CONFLICT');
      case 429:
        return UserAuthenticationError.tooManyAttempts();
      case 500:
      default:
        return new Error('System temporarily unavailable. Please try again later.');
    }
  }

  /**
   * Logs errors for monitoring while preserving user privacy
   */
  static logError(error, context = '') {
    const timestamp = new Date().toISOString();
    const logEntry = {
      timestamp,
      context,
      errorName: error.name,
      errorMessage: error.message,
      reason: error.reason || 'UNKNOWN',
      fieldName: error.fieldName,
      // Don't log sensitive data like passwords or full form data
      userAgent: navigator.userAgent,
      url: window.location.href
    };
    
    // In production, send to monitoring service
    console.error('Error logged:', logEntry);
  }

  /**
   * Creates user-friendly error display object
   */
  static createErrorDisplay(error) {
    return {
      message: error.userFriendly ? error.message : 'An unexpected error occurred',
      type: error.name || 'Error',
      field: error.fieldName || null,
      canRetry: ErrorHandler.isRetryable(error),
      timestamp: new Date().toISOString()
    };
  }

  /**
   * Determines if an error is retryable
   */
  static isRetryable(error) {
    const retryableErrors = [
      'DataFetchError',
      'NetworkError'
    ];
    
    const nonRetryableReasons = [
      'DUPLICATE_EMAIL',
      'INVALID_EMAIL_FORMAT', 
      'WEAK_PASSWORD',
      'MISSING_REQUIRED_FIELD'
    ];
    
    if (nonRetryableReasons.includes(error.reason)) {
      return false;
    }
    
    return retryableErrors.includes(error.name) || 
           error.name === 'Error'; // Generic system errors are retryable
  }
}

/**
 * Vue.js composable for error handling in components
 * Implements try-catch-finally first pattern
 */
export function useErrorHandler() {
  const handleAsyncOperation = async (operation, options = {}) => {
    const { 
      loadingRef = null, 
      errorRef = null, 
      onSuccess = null,
      onError = null,
      cleanup = null
    } = options;
    
    // Set loading state
    if (loadingRef) {
      loadingRef.value = true;
    }
    
    try {
      // Clear previous errors
      if (errorRef) {
        errorRef.value = '';
      }
      
      // Execute operation with error handling
      const result = await ErrorHandler.handleApiOperation(operation, cleanup);
      
      // Handle success
      if (onSuccess) {
        onSuccess(result);
      }
      
      return result;
      
    } catch (error) {
      // Log error for monitoring
      ErrorHandler.logError(error, 'Vue Component Operation');
      
      // Update error display
      if (errorRef) {
        const errorDisplay = ErrorHandler.createErrorDisplay(error);
        errorRef.value = errorDisplay.message;
      }
      
      // Custom error handling
      if (onError) {
        onError(error);
      } else {
        // Re-throw if no custom handler
        throw error;
      }
      
    } finally {
      // Always clear loading state
      if (loadingRef) {
        loadingRef.value = false;
      }
    }
  };

  return { handleAsyncOperation, ErrorHandler };
}