// ============================================
// API CONFIGURATION
// ============================================

const API_BASE_URL = 'http://localhost:8080/api';

const API_ENDPOINTS = {
    // Authentication
    AUTH: {
        LOGIN: `${API_BASE_URL}/auth/login`,
        VERIFY: `${API_BASE_URL}/auth/verify`,
        ME: `${API_BASE_URL}/auth/me`,
        LOGOUT: `${API_BASE_URL}/auth/logout`
    },
    
    // Student Management
    STUDENT: {
        SIGNUP: `${API_BASE_URL}/auth/signup`,
        PROFILE: `${API_BASE_URL}/student/profile`,
        UPDATE: `${API_BASE_URL}/student/profile`
    },
    
    // Admin - Student Management
    ADMIN_STUDENT: {
        CREATE: `${API_BASE_URL}/admin/students`,
        GET_ALL: `${API_BASE_URL}/admin/students`,
        GET_ONE: (id) => `${API_BASE_URL}/admin/students/${id}`,
        UPDATE: (id) => `${API_BASE_URL}/admin/students/${id}`,
        DELETE: (id) => `${API_BASE_URL}/admin/students/${id}`,
        APPROVE: `${API_BASE_URL}/admin/students/approve`
    },
    
    // Maintenance
    MAINTENANCE: {
        CREATE: `${API_BASE_URL}/maintenance`,
        GET_ALL: `${API_BASE_URL}/maintenance`,
        GET_MY: `${API_BASE_URL}/maintenance/my-queries`,
        GET_ONE: (id) => `${API_BASE_URL}/maintenance/${id}`,
        UPDATE_STATUS: (id) => `${API_BASE_URL}/maintenance/${id}/status`,
        ASSIGN: (id) => `${API_BASE_URL}/maintenance/${id}/assign`,
        RESOLVE: (id) => `${API_BASE_URL}/maintenance/${id}/resolve`
    },
    
    // Sleepover
SLEEPOVER: {
    CREATE: `${API_BASE_URL}/sleepover-passes`,
    GET_ALL: `${API_BASE_URL}/sleepover-passes`,
    GET_MY: `${API_BASE_URL}/sleepover-passes/my`,
    GET_ONE: (id) => `${API_BASE_URL}/sleepover-passes/${id}`,
    APPROVE: (id) => `${API_BASE_URL}/sleepover-passes/${id}/approve`,
    REJECT: (id) => `${API_BASE_URL}/sleepover-passes/${id}/reject`
},
    
    // Rooms
    ROOM: {
        GET_ALL: `${API_BASE_URL}/rooms`,
        GET_ONE: (id) => `${API_BASE_URL}/rooms/${id}`,
        GET_AVAILABLE: `${API_BASE_URL}/rooms/available`
    }
};

// Helper function to make authenticated API calls
async function apiCall(url, options = {}) {
    const user = firebase.auth().currentUser;
    
    if (!user) {
        throw new Error('Not authenticated. Please log in.');
    }
    
    try {
        const token = await user.getIdToken();
        
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        };
        
        const mergedOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...(options.headers || {})
            }
        };
        
        const response = await fetch(url, mergedOptions);
        
        if (!response.ok) {
            if (response.status === 401) {
                await logout();
                throw new Error('Session expired. Please log in again.');
            }
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.error || errorData.message || `API call failed: ${response.statusText}`);
        }
        
        return response;
    } catch (error) {
        console.error('API call error:', error);
        throw error;
    }
}