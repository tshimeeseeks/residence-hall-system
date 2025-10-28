# Quick Start Guide - Frontend Setup

## 📋 Step-by-Step Instructions

### 1. Pull Latest Changes
```bash
git checkout frontend
git pull origin frontend
```

### 2. Add Script Tags to Your HTML Files

**For EVERY HTML page**, add these lines in the `<head>` section (before closing `</head>`):

```html
<!-- Step 1: Add API Configuration -->
<script src="api-config.js"></script>

<!-- Step 2: Add Authentication Module -->
<script src="auth.js"></script>

<!-- Step 3: Add Maintenance Module (only for maintenance pages) -->
<script src="maintenance.js"></script>

<!-- Step 4: Your original script -->
<script src="script.js"></script>
```

### 3. Update Specific HTML Files

#### A. Update `login.html`

Add form ID and input IDs:
```html
<form id="student-login-form" onsubmit="return false;">
    <input type="email" id="email" name="email" placeholder="Email" required>
    <input type="password" id="password" name="password" placeholder="Password" required>
    <button type="submit">Login</button>
</form>
```

#### B. Update `signup.html`

Add form ID and all input IDs:
```html
<form id="student-signup-form" onsubmit="return false;">
    <input type="email" id="email" required>
    <input type="password" id="password" required>
    <input type="text" id="firstName" required>
    <input type="text" id="lastName" required>
    <input type="tel" id="phoneNumber" required>
    <input type="text" id="studentNumber" required>
    <input type="text" id="roomId" required>
    <input type="text" id="building">
    <input type="number" id="floor">
    <input type="text" id="course">
    <input type="number" id="yearOfStudy">
    <button type="submit">Sign Up</button>
</form>
```

#### C. Update `dashboard.html`

Add authentication protection at the top of the file (in a script tag):
```html
<script>
    // Redirect to login if not authenticated
    window.onload = function() {
        requireAuth();
        displayUserInfo();
    };
</script>
```

#### D. Update `maintanacerequest.html`

Add form ID and input IDs:
```html
<form id="maintenance-form" onsubmit="return false;">
    <select id="issue-type" required>
        <option value="">Select Issue Type</option>
        <option value="Plumbing">Plumbing</option>
        <option value="Electrical">Electrical</option>
        <option value="Internet">Internet</option>
        <option value="Furniture">Furniture</option>
        <option value="Other">Other</option>
    </select>
    
    <textarea id="description" placeholder="Describe the issue" required></textarea>
    
    <select id="priority" required>
        <option value="">Select Priority</option>
        <option value="LOW">Low</option>
        <option value="MEDIUM">Medium</option>
        <option value="HIGH">High</option>
    </select>
    
    <input type="file" id="support-image" accept="image/*">
    
    <button type="submit">Submit Request</button>
</form>
```

#### E. Update `maintanance.html`

Add a container for displaying requests:
```html
<div id="maintenance-requests-container">
    <!-- Requests will be loaded here automatically -->
</div>
```

#### F. Update `adlogin.html` (Admin Login)

```html
<form id="admin-login-form" onsubmit="return false;">
    <input type="email" id="admin-email" required>
    <input type="password" id="admin-password" required>
    <button type="submit">Admin Login</button>
</form>
```

#### G. Update `addashboard.html` (Admin Dashboard)

Add admin authentication protection:
```html
<script>
    window.onload = function() {
        requireAdminAuth();
        displayUserInfo();
    };
</script>
```

### 4. Test Your Frontend

1. Open VS Code
2. Right-click on `index.html`
3. Select "Open with Live Server"
4. Browser should open to http://localhost:5500

### 5. Verify Backend Connection

Open browser console (F12) and type:
```javascript
checkServerHealth().then(healthy => 
    console.log('Backend is', healthy ? 'running ✅' : 'not running ❌')
);
```

---

## 🎯 What Each File Does

### api-config.js
- Contains all API endpoint URLs
- Handles HTTP requests (GET, POST, PUT, DELETE)
- Manages authentication tokens
- Handles file uploads

### auth.js
- Student login/signup
- Admin login/signup
- Session management
- Page protection (requireAuth, requireAdminAuth)
- Logout functionality

### maintenance.js
- Create maintenance requests
- View maintenance requests
- Update request status
- Handle image uploads
- Admin approval/rejection

---

## 🔍 Testing Checklist

- [ ] All HTML files have script tags added
- [ ] Login form has correct IDs
- [ ] Signup form has correct IDs
- [ ] Maintenance form has correct IDs
- [ ] Frontend opens in browser
- [ ] No JavaScript errors in console (F12)
- [ ] Backend is running on port 8080

---

## 🚨 Common Mistakes to Avoid

1. ❌ **Don't** forget the `onsubmit="return false;"` on forms
2. ❌ **Don't** use wrong IDs on input fields
3. ❌ **Don't** forget to include api-config.js FIRST
4. ❌ **Don't** run frontend without backend running
5. ✅ **Do** check browser console for errors
6. ✅ **Do** verify backend is on port 8080
7. ✅ **Do** clear localStorage if login issues occur

---

## 📖 Need Help?

1. Check `INTEGRATION_GUIDE.md` for detailed instructions
2. Check `INTEGRATION_COMPLETE.md` for overview
3. Check browser console (F12) for errors
4. Verify backend logs for API errors

---

**You're almost done! Just add the script tags and IDs, and your app will work!** 🎉
