// ============================================
// FIREBASE CONFIGURATION
// ============================================

const firebaseConfig = {
    apiKey: "AIzaSyBcl0Iqy5whmS3uzhhSIYHLy_b8rl2lF4c",
    authDomain: "res-alira.firebaseapp.com",
    projectId: "res-alira",
    storageBucket: "res-alira.firebasestorage.app",
    messagingSenderId: "379961709205",
    appId: "1:379961709205:web:9854f5d24047be63d7019c",
    measurementId: "G-FCKD5P1SNV"
};

// Initialize Firebase (only if not already initialized)
if (!firebase.apps.length) {
    firebase.initializeApp(firebaseConfig);
} else {
    firebase.app(); // Use existing app
}

console.log('Firebase initialized successfully');