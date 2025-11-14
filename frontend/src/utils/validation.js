/**
 * Frontend validation utilities.
 * Fixes: Duplicated Code smell across Vue components.
 * Single Responsibility: Provide reusable validation functions.
 */

/**
 * Check if a value is null, undefined, or empty string.
 */
export function isEmpty(value) {
  return value === null || value === undefined || value === '';
}

/**
 * Check if a value is not empty.
 */
export function isNotEmpty(value) {
  return !isEmpty(value);
}

/**
 * Validate email format.
 */
export function isValidEmail(email) {
  if (isEmpty(email)) return false;
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

/**
 * Validate required field.
 */
export function isRequired(value, fieldName = 'Field') {
  if (isEmpty(value)) {
    return `${fieldName} is required`;
  }
  return null;
}

/**
 * Validate email field.
 */
export function validateEmail(email, fieldName = 'Email') {
  const required = isRequired(email, fieldName);
  if (required) return required;
  
  if (!isValidEmail(email)) {
    return `${fieldName} format is invalid`;
  }
  return null;
}

/**
 * Validate password strength.
 */
export function validatePassword(password, fieldName = 'Password') {
  const required = isRequired(password, fieldName);
  if (required) return required;
  
  if (password.length < 6) {
    return `${fieldName} must be at least 6 characters`;
  }
  return null;
}

/**
 * Validate name fields.
 */
export function validateName(name, fieldName = 'Name') {
  const required = isRequired(name, fieldName);
  if (required) return required;
  
  if (name.length > 50) {
    return `${fieldName} must be less than 50 characters`;
  }
  
  const nameRegex = /^[a-zA-Z\s'-]+$/;
  if (!nameRegex.test(name)) {
    return `${fieldName} contains invalid characters`;
  }
  return null;
}

/**
 * Validate multiple fields and return all errors.
 * Fixes: Long Parameter Lists by accepting validation rules object.
 */
export function validateFields(data, rules) {
  const errors = {};
  
  Object.keys(rules).forEach(field => {
    const value = data[field];
    const fieldRules = rules[field];
    
    for (const rule of fieldRules) {
      const error = rule(value, field);
      if (error) {
        errors[field] = error;
        break; // Stop at first error for each field
      }
    }
  });
  
  return {
    isValid: Object.keys(errors).length === 0,
    errors
  };
}

/**
 * Create validation rules for common scenarios.
 * Fixes: Duplicated validation setup code.
 */
export const ValidationRules = {
  required: (fieldName) => (value) => isRequired(value, fieldName),
  email: (fieldName) => (value) => validateEmail(value, fieldName),
  password: (fieldName) => (value) => validatePassword(value, fieldName),
  name: (fieldName) => (value) => validateName(value, fieldName),
  
  // Registration validation rules
  registration: {
    firstName: [ValidationRules.name('First Name')],
    lastName: [ValidationRules.name('Last Name')],
    email: [ValidationRules.email('Email')],
    password: [ValidationRules.password('Password')]
  },
  
  // Login validation rules  
  login: {
    email: [ValidationRules.email('Email')],
    password: [ValidationRules.required('Password')]
  }
};