/**
 * Frontend Error Handling Utilities
 * Implements Clean Code error handling principles for Vue.js frontend
 */

/**
 * Context-specific exception classes named from caller's perspective
 * Follow Uncle Bob's principle: exceptions should be named in terms of the caller's needs
 */

export class UserRegistrationError extends Error {
  constructor(message, reason, fieldName = null) {
    super(message);
    this.name = 'UserRegistrationError';
    this.reason = reason;
    this.fieldName = fieldName;
    this.userFriendly = true;
  }

  static duplicateEmail(email) {
    return new UserRegistrationError(
      `An account with email '${email}' already exists`,
      'DUPLICATE_EMAIL',
      'email'
    );
  }

  static invalidEmailFormat(email) {
    return new UserRegistrationError(
      `Email address '${email}' is not in a valid format`,
      'INVALID_EMAIL_FORMAT',
      'email'
    );
  }

  static weakPassword() {
    return new UserRegistrationError(
      'Password must be at least 6 characters long',
      'WEAK_PASSWORD',
      'password'
    );
  }

  static missingRequiredField(fieldName) {
    return new UserRegistrationError(
      `${fieldName} is required`,
      'MISSING_REQUIRED_FIELD',
      fieldName.toLowerCase()
    );
  }

  static chapterNotFound(chapterName) {
    return new UserRegistrationError(
      `Chapter '${chapterName}' not found`,
      'CHAPTER_NOT_FOUND',
      'chapterName'
    );
  }
}

export class UserAuthenticationError extends Error {
  constructor(message, reason) {
    super(message);
    this.name = 'UserAuthenticationError';
    this.reason = reason;
    this.userFriendly = true;
  }

  static invalidCredentials() {
    return new UserAuthenticationError(
      'Invalid email or password',
      'INVALID_CREDENTIALS'
    );
  }

  static missingCredentials() {
    return new UserAuthenticationError(
      'Email and password are required',
      'MISSING_CREDENTIALS'
    );
  }

  static accountDisabled() {
    return new UserAuthenticationError(
      'Account is currently disabled',
      'ACCOUNT_DISABLED'
    );
  }

  static tooManyAttempts() {
    return new UserAuthenticationError(
      'Too many failed login attempts. Please try again later.',
      'TOO_MANY_ATTEMPTS'
    );
  }
}

export class DataFetchError extends Error {
  constructor(message, operation, statusCode = null) {
    super(message);
    this.name = 'DataFetchError';
    this.operation = operation;
    this.statusCode = statusCode;
    this.userFriendly = true;
  }

  static chaptersLoadFailed() {
    return new DataFetchError(
      'Failed to load chapters. Please refresh the page.',
      'LOAD_CHAPTERS'
    );
  }

  static memberSearchFailed() {
    return new DataFetchError(
      'Failed to search members. Please try again.',
      'SEARCH_MEMBERS'
    );
  }

  static networkError() {
    return new DataFetchError(
      'Network connection failed. Please check your internet connection.',
      'NETWORK_ERROR'
    );
  }
}

/**
 * Input Validation Error - for client-side validation failures
 */
export class InputValidationError extends Error {
  constructor(message, fieldName, invalidValue) {
    super(message);
    this.name = 'InputValidationError';
    this.fieldName = fieldName;
    this.invalidValue = invalidValue;
    this.userFriendly = true;
  }

  static invalidEmailFormat(email) {
    return new InputValidationError(
      'Please enter a valid email address',
      'email',
      email
    );
  }

  static invalidPhoneFormat(phone) {
    return new InputValidationError(
      'Please enter a valid phone number',
      'phoneNumber',
      phone
    );
  }

  static invalidLength(fieldName, value, minLength, maxLength = null) {
    let message = `${fieldName} must be `;
    if (maxLength) {
      message += `between ${minLength} and ${maxLength} characters`;
    } else {
      message += `at least ${minLength} characters`;
    }

    return new InputValidationError(message, fieldName, value);
  }
}