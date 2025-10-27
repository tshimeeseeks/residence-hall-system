// ============================================
// FIREBASE AUTHENTICATION HELPER FUNCTIONS
// ============================================

/**
 * Check if user is authenticated
 */
async function checkAuth() {
    return new Promise((resolve) => {
        if (typeof firebase === 'undefined' || !firebase.auth) {
            console.error('Firebase is not initialized');
            resolve({ authenticated: false });
            return;
        }

        firebase.auth().onAuthStateChanged(async (user) => {
            if (user) {
                try {
                    const token = await user.getIdToken();
                    const tokenResult = await user.getIdTokenResult();
                    
                    // Check if user has admin claim
                    const isAdmin = tokenResult.claims.admin === true;
                    
                    resolve({
                        authenticated: true,
                        user: user,
                        token: token,
                        isAdmin: isAdmin,
                        email: user.email,
                        uid: user.uid
                    });
                } catch (error) {
                    console.error('Error getting user token:', error);
                    resolve({ authenticated: false });
                }
            } else {
                resolve({ authenticated: false });
            }
        });
    });
}

/**
 * Protect pages that require authentication
 */
async function protectPage(requireAdmin = false) {
    const auth = await checkAuth();
    
    if (!auth.authenticated) {
        sessionStorage.setItem('redirectAfterLogin', window.location.href);
        window.location.href = 'login.html';
        return null;
    }
    
    // Check if admin access is required
    if (requireAdmin && !auth.isAdmin) {
        alert('Access denied. Admin privileges required.');
        window.location.href = 'dashboard.html';
        return null;
    }
    
    // Store user info
    sessionStorage.setItem('currentUser', JSON.stringify({
        email: auth.email,
        isAdmin: auth.isAdmin,
        uid: auth.uid
    }));
    
    return auth;
}

/**
 * Logout function
 */
async function logout() {
    try {
        if (firebase.auth().currentUser) {
            await firebase.auth().signOut();
        }
        
        localStorage.clear();
        sessionStorage.clear();
        
        window.location.href = 'index.html';
    } catch (error) {
        console.error('Logout error:', error);
        localStorage.clear();
        sessionStorage.clear();
        window.location.href = 'index.html';
    }
}

/**
 * Get current user from session
 */
function getCurrentUser() {
    const userStr = sessionStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
}

/**
 * Check if current user is admin
 */
function isAdmin() {
    const user = getCurrentUser();
    return user && user.isAdmin === true;
}

/**
 * Initialize auth listener
 */
function initAuthListener() {
    if (typeof firebase === 'undefined' || !firebase.auth) {
        console.error('Firebase is not initialized');
        return;
    }

    firebase.auth().onAuthStateChanged((user) => {
        if (!user) {
            sessionStorage.clear();
            
            const currentPage = window.location.pathname.split('/').pop();
            if (currentPage !== 'login.html' && 
                currentPage !== 'adlogin.html' && 
                currentPage !== 'index.html' &&
                currentPage !== 'signup.html') {
                window.location.href = 'index.html';
            }
        }
    });
}

// Auto-initialize
document.addEventListener('DOMContentLoaded', () => {
    initAuthListener();
});