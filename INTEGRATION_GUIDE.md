# Frontend-Backend Integration Guide

## Overview
This guide explains how to connect the RHS frontend (HTML/CSS/JavaScript) with the backend (Spring Boot REST API) to create a fully functional application.

---

## 🏗️ Architecture Overview

```
┌─────────────────┐         HTTP/REST API          ┌─────────────────┐
│                 │  ←────────────────────────────→ │                 │
│    Frontend     │      (JSON over HTTPS)          │     Backend     │
│  (HTML/JS/CSS)  │                                 │  (Spring Boot)  │
│                 │                                 │                 │
└─────────────────┘                                 └─────────────────┘
  - HTML Pages                                        - Controllers
  - JavaScript                                        - Services  
  - API calls                                         - MongoDB
```

---

## 📁 Project Structure

```
residence-hall-system/
├── frontend/                    # Frontend files (HTML, CSS, JS)
│   ├── *.html                  # All HTML pages
│   ├── styles.css              # Main stylesheet
│   ├── script.js               # Original scripts
│   ├── api-config.js           # NEW: API configuration
│   ├── auth.js                 # NEW: Authentication module
│   ├── maintenance.js          # NEW: Maintenance module
│   └── img/                    # Images
│
└── rhs-backend/                # Backend (Spring Boot)
    ├── src/
    │   └── main/
    │       └── java/
    │           └── com/rhs/backend/
    │               ├── controller/      # REST Controllers
    │               ├── service/         # Business logic
    │               ├── model/           # Data models
    │               ├── repository/      # Database access
    │               └── Config/
    │                   └── CorsConfig.java  # NEW: CORS config
    └── pom.xml
```

---

## 🔧 Setup Instructions

### Step 1: Backend Setup

1. **Start MongoDB** (if not already running):
   ```bash
   # On Windows:
   mongod
   
   # On Linux/Mac:
   sudo systemctl start mongod
   ```

2. **Configure application.properties**:
   ```properties
   # rhs-backend/src/main/resources/application.properties
   
   server.port=8080
   
   # MongoDB Configuration
   spring.data.mongodb.uri=mongodb://localhost:27017/rhs_database
   spring.data.mongodb.database=rhs_database
   
   # File upload settings
   spring.servlet.multipart.max-file-size=5MB
   spring.servlet.multipart.max-request-size=10MB
   file.upload-dir=uploads/
   ```

3. **Build and run the backend**:
   ```bash
   cd rhs-backend
   mvn clean install -DskipTests
   mvn spring-boot:run
   ```

4. **Verify backend is running**:
   - Open browser to http://localhost:8080
   - You should see the backend is accessible

### Step 2: Frontend Setup

1. **Include the new JavaScript files in your HTML pages**:

   Add these `<script>` tags to the `<head>` section of EVERY HTML page (before other scripts):

   ```html
   <!-- API Configuration (must be first) -->
   <script src="api-config.js"></script>
   
   <!-- Authentication Module -->
   <script src="auth.js"></script>
   
   <!-- Maintenance Module (for maintenance pages) -->
   <script src="maintenance.js"></script>
   
   <!-- Original scripts -->
   <script src="script.js"></script>
   ```

2. **Update HTML forms with proper IDs**:

   **Example: login.html**
   ```html
   <form id="student-login-form">
       <input type="email" id="email" name="email" required>
       <input type="password" id="password" name="password" required>
       <button type="submit">Login</button>
   </form>
   ```

   **Example: signup.html**
   ```html
   <form id="student-signup-form">
       <input type="email" id="email" required>
       <input type="password" id="password" required>
       <input type="text" id="firstName" required>
       <input type="text" id="lastName" required>
       <input type="tel" id="phoneNumber" required>
       <input type="text" id="studentNumber" required>
       <input type="text" id="roomId" required>
       <input type="text" id="building" required>
       <input type="number" id="floor" required>
       <input type="text" id="course" required>
       <input type="number" id="yearOfStudy" required>
       <button type="submit">Sign Up</button>
   </form>
   ```

3. **Protect authenticated pages**:

   Add this at the top of pages that require authentication (e.g., dashboard.html):
   ```html
   <script>
       // Protect this page - redirect to login if not authenticated
       requireAuth();
   </script>
   ```

   For admin pages (e.g., addashboard.html):
   ```html
   <script>
       // Protect this page - redirect to admin login if not admin
       requireAdminAuth();
   </script>
   ```

4. **Serve the frontend**:
   
   **Option A: Using VS Code Live Server**
   - Install "Live Server" extension in VS Code
   - Right-click on `index.html`
   - Select "Open with Live Server"
   - Frontend will open at http://localhost:5500

   **Option B: Using Python**
   ```bash
   # In the frontend directory
   python -m http.server 3000
   ```
   - Frontend will be at http://localhost:3000

---

## 🔌 API Endpoints Reference

### Authentication
- **POST** `/api/auth/signup` - Student signup
- **POST** `/api/auth/login` - Student login
- **POST** `/api/auth/admin/login` - Admin login
- **POST** `/api/auth/admin/signup` - Admin signup
- **POST** `/api/auth/approve` - Approve student account

### Students
- **GET** `/api/admin/students` - Get all students
- **GET** `/api/admin/students/{id}` - Get student by ID
- **POST** `/api/admin/students` - Create student
- **PUT** `/api/admin/students/{id}` - Update student
- **DELETE** `/api/admin/students/{id}` - Delete student
- **GET** `/api/admin/students/pending` - Get pending students

### Maintenance
- **POST** `/api/maintenance` - Create maintenance request
- **GET** `/api/maintenance` - Get all maintenance requests
- **GET** `/api/maintenance/{id}` - Get maintenance request by ID
- **GET** `/api/maintenance/student/{studentId}` - Get requests by student
- **PUT** `/api/maintenance/{id}` - Update maintenance request
- **DELETE** `/api/maintenance/{id}` - Delete maintenance request
- **POST** `/api/maintenance/upload` - Upload maintenance image

### Sleepover Passes
- **POST** `/api/sleepover` - Create sleepover pass request
- **GET** `/api/sleepover` - Get all sleepover requests
- **GET** `/api/sleepover/{id}` - Get request by ID
- **GET** `/api/sleepover/student/{studentId}` - Get requests by student
- **PUT** `/api/sleepover/{id}` - Update sleepover request
- **PUT** `/api/sleepover/{id}/approve` - Approve request
- **PUT** `/api/sleepover/{id}/reject` - Reject request

---

## 🔐 Authentication Flow

1. **User logs in** → Frontend sends credentials to `/api/auth/login`
2. **Backend validates** → Returns JWT token and user data
3. **Frontend stores** → Saves token in `localStorage`
4. **Subsequent requests** → Token sent in `Authorization` header
5. **Token expires/invalid** → User redirected to login page

---

## 📝 Example Usage

### Making an API Call

```javascript
// Create a maintenance request
const maintenanceData = {
    studentId: getCurrentUser().userId,
    roomId: 'A123',
    issueType: 'Plumbing',
    description: 'Leaking faucet',
    priority: 'HIGH'
};

const response = await apiCall(
    API_ENDPOINTS.maintenance.create,
    HTTP_METHODS.POST,
    maintenanceData
);
```

### Uploading a File

```javascript
// Upload maintenance image
const fileInput = document.getElementById('support-image');
const formData = new FormData();
formData.append('file', fileInput.files[0]);

const response = await uploadFile(
    API_ENDPOINTS.maintenance.uploadImage,
    formData
);
```

---

## ✅ Testing the Integration

### 1. Test Backend Health
```bash
curl http://localhost:8080/api/health
```

### 2. Test Student Signup
- Open frontend: http://localhost:5500/signup.html
- Fill in the form
- Click "Sign Up"
- Check browser console for errors
- Check MongoDB for new student record

### 3. Test Login
- Open http://localhost:5500/login.html
- Enter email and password
- Should redirect to dashboard.html

### 4. Test Maintenance Request
- Login as student
- Go to maintenance request page
- Submit a maintenance request
- Check MongoDB for new maintenance record

---

## 🐛 Troubleshooting

### CORS Errors
**Problem**: Browser shows "CORS policy" error

**Solution**:
- Ensure `CorsConfig.java` is in backend
- Check that frontend URL is in `allowedOrigins`
- Restart backend server

### 401 Unauthorized
**Problem**: API returns 401 even after login

**Solutions**:
- Check if token is saved: `localStorage.getItem('authToken')`
- Verify token format in Authorization header
- Check token expiration

### Network Errors
**Problem**: "Failed to fetch" or "Network error"

**Solutions**:
- Verify backend is running on port 8080
- Check MongoDB is running
- Verify API_BASE_URL in `api-config.js`

### File Upload Issues
**Problem**: Image upload fails

**Solutions**:
- Check file size (max 5MB)
- Ensure `uploads/` directory exists
- Check file type is image/*

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Fetch API Documentation](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [JWT Introduction](https://jwt.io/introduction)

---

## 🎯 Next Steps

1. ✅ Backend is running
2. ✅ Frontend can make API calls
3. ⏭️ Test all pages and features
4. ⏭️ Add error handling and validation
5. ⏭️ Implement sleepover pass functionality
6. ⏭️ Add admin dashboard features
7. ⏭️ Deploy to production server

---

## 📞 Support

If you encounter issues:
1. Check browser console for JavaScript errors
2. Check backend logs for server errors
3. Verify MongoDB is running and accessible
4. Review this guide's troubleshooting section

---

**Last Updated**: October 26, 2025  
**Version**: 1.0
