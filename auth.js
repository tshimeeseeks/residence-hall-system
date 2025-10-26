/**
 * Authentication Module
 * Handles user login, signup, and session management
 */

// ===== STUDENT SIGNUP =====
async function handleStudentSignup(event) {
    event.preventDefault();
    
    const formData = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value,
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        phoneNumber: document.getElementById('phoneNumber').value,
        studentNumber: document.getElementById('studentNumber').value,
        roomId: document.getElementById('roomId').value,
        building: document.getElementById('building').value,
        floor: parseInt(document.getElementById('floor').value),
        course: document.getElementById('course').value,
        yearOfStudy: parseInt(document.getElementById('yearOfStudy').value)
    };
    
    try {
        const response = await apiCall(
            API_ENDPOINTS.auth.studentSignup,
            HTTP_METHODS.POST,
            formData,
            false // No auth required for signup
        );
        
        showSuccess('Signup successful! Please wait for admin approval.');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
        
    } catch (error) {
        showError(error.message || 'Signup failed. Please try again.');
    }
}

// ===== STUDENT LOGIN =====
async function handleStudentLogin(event) {
    event.preventDefault();
    
    const credentials = {
        email: document.getElementById('email').value,
        password: document.getElementById('password').value
    };
    
    try {
        const response = await apiCall(
            API_ENDPOINTS.auth.studentLogin,
            HTTP_METHODS.POST,
            credentials,
            false // No auth required for login
        );
        
        // Save user data and token
        saveCurrentUser(response);
        
        showSuccess('Login successful!');
        
        // Redirect based on account status
        if (response.accountStatus === 'PENDING') {
            showError('Your account is pending admin approval.');
            clearSession();
        } else if (response.accountStatus === 'APPROVED') {
            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 1000);
        } else {
            showError('Your account has been rejected.');
            clearSession();
        }
        
    } catch (error) {
        showError(error.message || 'Login failed. Please check your credentials.');
    }
}

// ===== ADMIN LOGIN =====
async function handleAdminLogin(event) {
    event.preventDefault();
    
    const credentials = {
        email: document.getElementById('admin-email').value,
        password: document.getElementById('admin-password').value
    };
    
    try {
        const response = await apiCall(
            API_ENDPOINTS.auth.adminLogin,
            HTTP_METHODS.POST,
            credentials,
            false // No auth required for login
        );
        
        // Save user data and token
        saveCurrentUser(response);
        
        showSuccess('Admin login successful!');
        setTimeout(() => {
            window.location.href = 'addashboard.html';
        }, 1000);
        
    } catch (error) {
        showError(error.message || 'Login failed. Please check your credentials.');
    }
}

// ===== ADMIN SIGNUP =====
async function handleAdminSignup(event) {
    event.preventDefault();
    
    const formData = {
        email: document.getElementById('admin-email').value,
        password: document.getElementById('admin-password').value,
        firstName: document.getElementById('admin-firstName').value,
        lastName: document.getElementById('admin-lastName').value,
        phoneNumber: document.getElementById('admin-phoneNumber').value,
        department: document.getElementById('admin-department').value,
        role: document.getElementById('admin-role').value || 'ADMIN'
    };
    
    try {
        const response = await apiCall(
            API_ENDPOINTS.auth.adminSignup,
            HTTP_METHODS.POST,
            formData,
            false // No auth required for signup
        );
        
        saveCurrentUser(response);
        
        showSuccess('Admin account created successfully!');
        setTimeout(() => {
            window.location.href = 'addashboard.html';
        }, 1500);
        
    } catch (error) {
        showError(error.message || 'Admin signup failed. Please try again.');
    }
}

// ===== LOGOUT =====
function handleLogout() {
    const confirmLogout = confirm('Are you sure you want to log out?');
    
    if (confirmLogout) {
        clearSession();
        window.location.href = 'index.html';
    }
}

// ===== ROUTE PROTECTION =====
/**
 * Protect pages that require authentication
 * Call this function at the top of protected pages
 */
function requireAuth() {
    if (!isAuthenticated()) {
        window.location.href = 'login.html';
    }
}

/**
 * Protect admin pages
 */
function requireAdminAuth() {
    if (!isAuthenticated()) {
        window.location.href = 'adlogin.html';
        return;
    }
    
    const user = getCurrentUser();
    if (user && user.userType !== 'ADMIN') {
        showError('Admin access required');
        window.location.href = 'login.html';
    }
}

/**
 * Display user info in navigation
 */
function displayUserInfo() {
    const user = getCurrentUser();
    if (user) {
        const userNameElement = document.getElementById('user-name');
        const userEmailElement = document.getElementById('user-email');
        
        if (userNameElement) {
            userNameElement.textContent = `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email;
        }
        
        if (userEmailElement) {
            userEmailElement.textContent = user.email;
        }
    }
}

// ===== AUTO-INITIALIZE =====
document.addEventListener('DOMContentLoaded', () => {
    // Attach event listeners for login forms
    const studentLoginForm = document.getElementById('student-login-form');
    if (studentLoginForm) {
        studentLoginForm.addEventListener('submit', handleStudentLogin);
    }
    
    const studentSignupForm = document.getElementById('student-signup-form');
    if (studentSignupForm) {
        studentSignupForm.addEventListener('submit', handleStudentSignup);
    }
    
    const adminLoginForm = document.getElementById('admin-login-form');
    if (adminLoginForm) {
        adminLoginForm.addEventListener('submit', handleAdminLogin);
    }
    
    const adminSignupForm = document.getElementById('admin-signup-form');
    if (adminSignupForm) {
        adminSignupForm.addEventListener('submit', handleAdminSignup);
    }
    
    // Attach logout button listener
    const logoutButtons = document.querySelectorAll('.logout-btn, #logout-btn');
    logoutButtons.forEach(button => {
        button.addEventListener('click', handleLogout);
    });
    
    // Display user info if on authenticated page
    displayUserInfo();
});
