/**
 * API Configuration for RHS Frontend
 * This file contains all API endpoint configurations and utility functions
 * for communicating with the backend server.
 */

// Base URL for the backend API
const API_BASE_URL = 'http://localhost:8080/api';

// API Endpoints
const API_ENDPOINTS = {
    // Authentication endpoints
    auth: {
        studentSignup: `${API_BASE_URL}/auth/signup`,
        studentLogin: `${API_BASE_URL}/auth/login`,
        adminLogin: `${API_BASE_URL}/auth/admin/login`,
        adminSignup: `${API_BASE_URL}/auth/admin/signup`,
        logout: `${API_BASE_URL}/auth/logout`
    },
    
    // Student endpoints
    students: {
        getAll: `${API_BASE_URL}/admin/students`,
        getById: (id) => `${API_BASE_URL}/admin/students/${id}`,
        create: `${API_BASE_URL}/admin/students`,
        update: (id) => `${API_BASE_URL}/admin/students/${id}`,
        delete: (id) => `${API_BASE_URL}/admin/students/${id}`,
        approve: `${API_BASE_URL}/auth/approve`,
        getPending: `${API_BASE_URL}/admin/students/pending`
    },
    
    // Maintenance endpoints
    maintenance: {
        create: `${API_BASE_URL}/maintenance`,
        getAll: `${API_BASE_URL}/maintenance`,
        getById: (id) => `${API_BASE_URL}/maintenance/${id}`,
        getByStudent: (studentId) => `${API_BASE_URL}/maintenance/student/${studentId}`,
        update: (id) => `${API_BASE_URL}/maintenance/${id}`,
        delete: (id) => `${API_BASE_URL}/maintenance/${id}`,
        uploadImage: `${API_BASE_URL}/maintenance/upload`
    },
    
    // Sleepover pass endpoints
    sleepover: {
        create: `${API_BASE_URL}/sleepover`,
        getAll: `${API_BASE_URL}/sleepover`,
        getById: (id) => `${API_BASE_URL}/sleepover/${id}`,
        getByStudent: (studentId) => `${API_BASE_URL}/sleepover/student/${studentId}`,
        update: (id) => `${API_BASE_URL}/sleepover/${id}`,
        approve: (id) => `${API_BASE_URL}/sleepover/${id}/approve`,
        reject: (id) => `${API_BASE_URL}/sleepover/${id}/reject`,
        delete: (id) => `${API_BASE_URL}/sleepover/${id}`
    },
    
    // Emergency contacts endpoints
    emergency: {
        getAll: `${API_BASE_URL}/emergency-contacts`,
        create: `${API_BASE_URL}/emergency-contacts`,
        update: (id) => `${API_BASE_URL}/emergency-contacts/${id}`,
        delete: (id) => `${API_BASE_URL}/emergency-contacts/${id}`
    },
    
    // Reports endpoints
    reports: {
        generate: `${API_BASE_URL}/reports/generate`,
        download: (type) => `${API_BASE_URL}/reports/download/${type}`
    }
};

// HTTP Methods helper
const HTTP_METHODS = {
    GET: 'GET',
    POST: 'POST',
    PUT: 'PUT',
    DELETE: 'DELETE',
    PATCH: 'PATCH'
};

/**
 * Get authorization token from localStorage
 */
function getAuthToken() {
    return localStorage.getItem('authToken');
}

/**
 * Get current user data from localStorage
 */
function getCurrentUser() {
    const userJson = localStorage.getItem('currentUser');
    return userJson ? JSON.parse(userJson) : null;
}

/**
 * Save user data to localStorage
 */
function saveCurrentUser(userData) {
    localStorage.setItem('currentUser', JSON.stringify(userData));
    if (userData.token) {
        localStorage.setItem('authToken', userData.token);
    }
}

/**
 * Clear user session
 */
function clearSession() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
}

/**
 * Check if user is authenticated
 */
function isAuthenticated() {
    return !!getAuthToken();
}

/**
 * Generic API call function
 * @param {string} url - The API endpoint URL
 * @param {string} method - HTTP method (GET, POST, PUT, DELETE)
 * @param {object} data - Request body data (optional)
 * @param {boolean} requiresAuth - Whether authentication token is required
 * @returns {Promise} - Promise resolving to response data
 */
async function apiCall(url, method = 'GET', data = null, requiresAuth = true) {
    const headers = {
        'Content-Type': 'application/json'
    };
    
    // Add authorization header if required
    if (requiresAuth) {
        const token = getAuthToken();
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
    }
    
    const config = {
        method: method,
        headers: headers,
        mode: 'cors',
        credentials: 'include'
    };
    
    // Add body for POST, PUT, PATCH requests
    if (data && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
        config.body = JSON.stringify(data);
    }
    
    try {
        const response = await fetch(url, config);
        
        // Handle authentication errors
        if (response.status === 401) {
            clearSession();
            window.location.href = 'index.html';
            throw new Error('Unauthorized - Please login again');
        }
        
        // Parse response
        const responseData = await response.json();
        
        if (!response.ok) {
            throw new Error(responseData.message || 'API request failed');
        }
        
        return responseData;
        
    } catch (error) {
        console.error('API Call Error:', error);
        throw error;
    }
}

/**
 * Upload file (for images, documents, etc.)
 * @param {string} url - The API endpoint URL
 * @param {FormData} formData - Form data containing the file
 * @returns {Promise} - Promise resolving to response data
 */
async function uploadFile(url, formData) {
    const token = getAuthToken();
    const headers = {};
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: headers,
            body: formData,
            mode: 'cors',
            credentials: 'include'
        });
        
        if (response.status === 401) {
            clearSession();
            window.location.href = 'index.html';
            throw new Error('Unauthorized - Please login again');
        }
        
        const responseData = await response.json();
        
        if (!response.ok) {
            throw new Error(responseData.message || 'File upload failed');
        }
        
        return responseData;
        
    } catch (error) {
        console.error('File Upload Error:', error);
        throw error;
    }
}

/**
 * Display error message to user
 */
function showError(message) {
    alert('Error: ' + message);
    // You can replace this with a better UI notification system
}

/**
 * Display success message to user
 */
function showSuccess(message) {
    alert('Success: ' + message);
    // You can replace this with a better UI notification system
}

/**
 * Check if backend server is running
 */
async function checkServerHealth() {
    try {
        const response = await fetch(`${API_BASE_URL}/health`, {
            method: 'GET',
            mode: 'cors'
        });
        return response.ok;
    } catch (error) {
        console.error('Server health check failed:', error);
        return false;
    }
}

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        API_ENDPOINTS,
        HTTP_METHODS,
        apiCall,
        uploadFile,
        getAuthToken,
        getCurrentUser,
        saveCurrentUser,
        clearSession,
        isAuthenticated,
        showError,
        showSuccess,
        checkServerHealth
    };
}
