// adminLogin.js - Fixed version that calls your Java backend

// Import Firebase (make sure firebase is initialized in your HTML)
// <script src="https://www.gstatic.com/firebasejs/9.x.x/firebase-app.js"></script>
// <script src="https://www.gstatic.com/firebasejs/9.x.x/firebase-auth.js"></script>

const API_BASE_URL = 'http://localhost:8080/api';

/**
 * Admin Login Function
 * Step 1: Authenticate with Firebase
 * Step 2: Get user data from MongoDB via backend API
 * Step 3: Check if user is admin
 */
function adminLogin(email, password) {
    // Show loading state
    const loginButton = document.getElementById('loginButton');
    const originalText = loginButton.textContent;
    loginButton.disabled = true;
    loginButton.textContent = 'Logging in...';

    // Step 1: Firebase Authentication
    firebase.auth().signInWithEmailAndPassword(email, password)
        .then((userCredential) => {
            const user = userCredential.user;
            console.log('Firebase authentication successful');
            
            // Step 2: Get Firebase token
            return user.getIdToken();
        })
        .then((token) => {
            console.log('Got Firebase token, calling backend...');
            
            // Step 3: Call backend API to get user data from MongoDB
            return fetch(`${API_BASE_URL}/users/profile`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to get user profile');
            }
            return response.json();
        })
        .then(userData => {
            console.log('Got user data from backend:', userData);
            
            // Step 4: Check if user is admin
            if (userData.userType === 'ADMIN' && userData.accountStatus === 'APPROVED') {
                // Store user data for use in admin dashboard
                localStorage.setItem('userData', JSON.stringify(userData));
                localStorage.setItem('userRole', userData.userType);
                
                // Redirect to admin dashboard
                window.location.href = 'admin-dashboard.html';
            } else {
                // Not an admin
                firebase.auth().signOut();
                alert('Access denied. Admin privileges required.');
                
                // Reset button
                loginButton.disabled = false;
                loginButton.textContent = originalText;
            }
        })
        .catch((error) => {
            console.error('Login error:', error);
            
            // Show user-friendly error message
            let errorMessage = 'Login failed. Please try again.';
            
            if (error.code === 'auth/invalid-email') {
                errorMessage = 'Invalid email address.';
            } else if (error.code === 'auth/user-not-found') {
                errorMessage = 'No user found with this email.';
            } else if (error.code === 'auth/wrong-password') {
                errorMessage = 'Incorrect password.';
            } else if (error.message.includes('network')) {
                errorMessage = 'Network error. Please check your connection.';
            }
            
            alert(errorMessage);
            
            // Reset button
            loginButton.disabled = false;
            loginButton.textContent = originalText;
        });
}

/**
 * Example: How to use in your HTML
 */
/*
<!DOCTYPE html>
<html>
<head>
    <title>Admin Login</title>
</head>
<body>
    <div class="login-container">
        <h2>Admin Login</h2>
        <form id="loginForm" onsubmit="handleLogin(event)">
            <input type="email" id="email" placeholder="Email" required />
            <input type="password" id="password" placeholder="Password" required />
            <button type="submit" id="loginButton">Login</button>
        </form>
    </div>

    <!-- Firebase SDK -->
    <script src="https://www.gstatic.com/firebasejs/9.x.x/firebase-app.js"></script>
    <script src="https://www.gstatic.com/firebasejs/9.x.x/firebase-auth.js"></script>
    <script src="firebase-config.js"></script>
    <script src="adminLogin.js"></script>
    
    <script>
        function handleLogin(event) {
            event.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            adminLogin(email, password);
        }
    </script>
</body>
</html>
*/

// Helper function to check if user is logged in
function checkAdminAuth() {
    const userData = localStorage.getItem('userData');
    const userRole = localStorage.getItem('userRole');
    
    if (!userData || userRole !== 'ADMIN') {
        // Not logged in or not admin, redirect to login
        window.location.href = 'admin-login.html';
        return false;
    }
    
    return true;
}

// Helper function to get current user data
function getCurrentUser() {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : null;
}

// Helper function to logout
function adminLogout() {
    firebase.auth().signOut().then(() => {
        localStorage.removeItem('userData');
        localStorage.removeItem('userRole');
        window.location.href = 'admin-login.html';
    });
}

// Export functions if using modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        adminLogin,
        checkAdminAuth,
        getCurrentUser,
        adminLogout
    };
}