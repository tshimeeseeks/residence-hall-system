/**
 * Maintenance Module
 * Handles maintenance query creation, viewing, and management
 */

// ===== CREATE MAINTENANCE REQUEST =====
async function handleMaintenanceSubmit(event) {
    event.preventDefault();
    
    const issueType = document.getElementById('issue-type').value;
    const description = document.getElementById('description').value;
    const priority = document.getElementById('priority').value;
    const imageFile = document.getElementById('support-image').files[0];
    
    try {
        // First, upload image if provided
        let photoUrl = null;
        if (imageFile) {
            const formData = new FormData();
            formData.append('file', imageFile);
            
            const uploadResponse = await uploadFile(
                API_ENDPOINTS.maintenance.uploadImage,
                formData
            );
            photoUrl = uploadResponse.url || uploadResponse.filePath;
        }
        
        // Get current user details
        const user = getCurrentUser();
        
        // Create maintenance request
        const requestData = {
            studentId: user.userId,
            roomId: user.roomId || document.getElementById('room-id').value,
            issueType: issueType,
            description: description,
            priority: priority,
            photos: photoUrl ? [photoUrl] : [],
            status: 'PENDING'
        };
        
        const response = await apiCall(
            API_ENDPOINTS.maintenance.create,
            HTTP_METHODS.POST,
            requestData
        );
        
        showSuccess('Maintenance request submitted successfully!');
        setTimeout(() => {
            window.location.href = 'maintanance.html';
        }, 1500);
        
    } catch (error) {
        showError(error.message || 'Failed to submit maintenance request');
    }
}

// ===== LOAD MAINTENANCE REQUESTS (FOR STUDENT) =====
async function loadStudentMaintenanceRequests() {
    try {
        const user = getCurrentUser();
        const response = await apiCall(
            API_ENDPOINTS.maintenance.getByStudent(user.userId),
            HTTP_METHODS.GET
        );
        
        displayMaintenanceRequests(response);
        
    } catch (error) {
        console.error('Failed to load maintenance requests:', error);
        showError('Failed to load maintenance requests');
    }
}

// ===== LOAD ALL MAINTENANCE REQUESTS (FOR ADMIN) =====
async function loadAllMaintenanceRequests() {
    try {
        const response = await apiCall(
            API_ENDPOINTS.maintenance.getAll,
            HTTP_METHODS.GET
        );
        
        displayAdminMaintenanceRequests(response);
        
    } catch (error) {
        console.error('Failed to load maintenance requests:', error);
        showError('Failed to load maintenance requests');
    }
}

// ===== DISPLAY MAINTENANCE REQUESTS (STUDENT VIEW) =====
function displayMaintenanceRequests(requests) {
    const container = document.getElementById('maintenance-requests-container');
    
    if (!container) return;
    
    if (!requests || requests.length === 0) {
        container.innerHTML = '<p class="no-data">No maintenance requests found</p>';
        return;
    }
    
    container.innerHTML = requests.map(request => `
        <div class="request-card ${request.status.toLowerCase()}">
            <div class="request-header">
                <h3>${request.issueType || 'Maintenance Issue'}</h3>
                <span class="status-badge ${request.status.toLowerCase()}">${request.status}</span>
            </div>
            <div class="request-body">
                <p><strong>Room:</strong> ${request.roomId}</p>
                <p><strong>Priority:</strong> ${request.priority}</p>
                <p><strong>Description:</strong> ${request.description || 'No description provided'}</p>
                <p><strong>Date:</strong> ${new Date(request.createdAt).toLocaleDateString()}</p>
                ${request.assignedToName ? `<p><strong>Assigned to:</strong> ${request.assignedToName}</p>` : ''}
                ${request.resolutionNotes ? `<p><strong>Resolution:</strong> ${request.resolutionNotes}</p>` : ''}
                ${request.photos && request.photos.length > 0 ? `
                    <div class="request-images">
                        ${request.photos.map(photo => `<img src="${photo}" alt="Issue photo" class="request-image">`).join('')}
                    </div>
                ` : ''}
            </div>
        </div>
    `).join('');
}

// ===== DISPLAY MAINTENANCE REQUESTS (ADMIN VIEW) =====
function displayAdminMaintenanceRequests(requests) {
    const container = document.getElementById('admin-maintenance-container');
    
    if (!container) return;
    
    if (!requests || requests.length === 0) {
        container.innerHTML = '<p class="no-data">No maintenance requests found</p>';
        return;
    }
    
    container.innerHTML = requests.map(request => `
        <div class="admin-request-card" onclick="viewMaintenanceDetail('${request.id}')">
            <div class="request-info">
                <h4>${request.issueType || 'Maintenance Request'}</h4>
                <p class="student-info">Student: ${request.studentName || request.studentEmail}</p>
                <p class="room-info">Room: ${request.roomId}</p>
                <p class="priority-info priority-${request.priority.toLowerCase()}">${request.priority} Priority</p>
            </div>
            <div class="request-status">
                <span class="status-badge ${request.status.toLowerCase()}">${request.status}</span>
                <p class="date-info">${new Date(request.createdAt).toLocaleDateString()}</p>
            </div>
        </div>
    `).join('');
}

// ===== UPDATE MAINTENANCE REQUEST STATUS =====
async function updateMaintenanceStatus(requestId, newStatus, resolutionNotes = '') {
    try {
        const updateData = {
            status: newStatus,
            resolutionNotes: resolutionNotes
        };
        
        const response = await apiCall(
            API_ENDPOINTS.maintenance.update(requestId),
            HTTP_METHODS.PUT,
            updateData
        );
        
        showSuccess('Maintenance request updated successfully!');
        loadAllMaintenanceRequests(); // Reload the list
        
    } catch (error) {
        showError(error.message || 'Failed to update maintenance request');
    }
}

// ===== VIEW MAINTENANCE DETAIL =====
function viewMaintenanceDetail(requestId) {
    window.location.href = `maintenance_verify.html?id=${requestId}`;
}

// ===== LOAD SINGLE MAINTENANCE REQUEST DETAIL =====
async function loadMaintenanceDetail() {
    const urlParams = new URLSearchParams(window.location.search);
    const requestId = urlParams.get('id');
    
    if (!requestId) {
        showError('Invalid request ID');
        return;
    }
    
    try {
        const request = await apiCall(
            API_ENDPOINTS.maintenance.getById(requestId),
            HTTP_METHODS.GET
        );
        
        displayMaintenanceDetail(request);
        
    } catch (error) {
        showError('Failed to load maintenance request details');
    }
}

// ===== DISPLAY MAINTENANCE DETAIL =====
function displayMaintenanceDetail(request) {
    // Populate detail elements
    const elements = {
        'student-name': request.studentName || request.studentEmail,
        'room-number': request.roomId,
        'issue-type': request.issueType,
        'priority-level': request.priority,
        'description-text': request.description,
        'request-date': new Date(request.createdAt).toLocaleDateString(),
        'current-status': request.status
    };
    
    for (const [id, value] of Object.entries(elements)) {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = value;
        }
    }
    
    // Display images if available
    if (request.photos && request.photos.length > 0) {
        const imageContainer = document.getElementById('request-images');
        if (imageContainer) {
            imageContainer.innerHTML = request.photos.map(photo => 
                `<img src="${photo}" alt="Issue photo" class="detail-image">`
            ).join('');
        }
    }
    
    // Store request ID for update functions
    window.currentRequestId = request.id;
}

// ===== APPROVE/COMPLETE MAINTENANCE REQUEST =====
async function approveMaintenanceRequest() {
    const resolutionNotes = document.getElementById('resolution-notes')?.value || 'Request approved and completed';
    
    if (!window.currentRequestId) {
        showError('No request ID found');
        return;
    }
    
    await updateMaintenanceStatus(window.currentRequestId, 'RESOLVED', resolutionNotes);
    
    setTimeout(() => {
        window.location.href = 'admaintanance.html';
    }, 1500);
}

// ===== REJECT MAINTENANCE REQUEST =====
async function rejectMaintenanceRequest() {
    const rejectionReason = prompt('Please provide a reason for rejection:');
    
    if (!rejectionReason) {
        return; // User cancelled
    }
    
    if (!window.currentRequestId) {
        showError('No request ID found');
        return;
    }
    
    await updateMaintenanceStatus(window.currentRequestId, 'REJECTED', rejectionReason);
    
    setTimeout(() => {
        window.location.href = 'admaintanance.html';
    }, 1500);
}

// ===== AUTO-INITIALIZE =====
document.addEventListener('DOMContentLoaded', () => {
    // Attach maintenance form submission
    const maintenanceForm = document.getElementById('maintenance-form');
    if (maintenanceForm) {
        maintenanceForm.addEventListener('submit', handleMaintenanceSubmit);
    }
    
    // Load maintenance requests based on page
    if (document.getElementById('maintenance-requests-container')) {
        loadStudentMaintenanceRequests();
    }
    
    if (document.getElementById('admin-maintenance-container')) {
        loadAllMaintenanceRequests();
    }
    
    // Load detail if on detail page
    if (window.location.pathname.includes('maintenance_verify.html')) {
        loadMaintenanceDetail();
    }
    
    // Attach approve/reject buttons
    const approveBtn = document.getElementById('approve-maintenance-btn');
    if (approveBtn) {
        approveBtn.addEventListener('click', approveMaintenanceRequest);
    }
    
    const rejectBtn = document.getElementById('reject-maintenance-btn');
    if (rejectBtn) {
        rejectBtn.addEventListener('click', rejectMaintenanceRequest);
    }
});
