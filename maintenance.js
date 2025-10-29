/**
 * Maintenance Module
 * Handles maintenance query creation, viewing, and management
 */

// ===== CREATE MAINTENANCE REQUEST =====
async function handleMaintenanceSubmit(event) {
    event.preventDefault();
    
    const roomNumber = document.getElementById('room').value;
    const message = document.getElementById('message').value;
    const imageFile = document.getElementById('support-image').files[0];
    
    // Validate required fields
    if (!roomNumber || !message) {
        showError('Please fill in all required fields');
        return;
    }
    
    try {
        // Show loading state
        const submitBtn = event.target.querySelector('.submit-btn');
        const originalText = submitBtn.textContent;
        submitBtn.disabled = true;
        submitBtn.textContent = 'Submitting...';
        
        // First, upload image if provided
        let photoUrl = null;
        if (imageFile) {
            const formData = new FormData();
            formData.append('file', imageFile);
            
            const uploadResponse = await fetch(API_ENDPOINTS.FILES.UPLOAD, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${await firebase.auth().currentUser.getIdToken()}`
                },
                body: formData
            });
            
            if (!uploadResponse.ok) {
                throw new Error('Failed to upload image');
            }
            
            const uploadData = await uploadResponse.json();
            photoUrl = uploadData.url || uploadData.filePath;
        }
        
        // Create maintenance request with CORRECT field names
        const requestData = {
            queryTitle: "Maintenance Request - Room " + roomNumber,
            queryDescription: message,
            category: "GENERAL",
            photoUrls: photoUrl ? [photoUrl] : [],
            priority: "MEDIUM"
        };
        
        const token = await firebase.auth().currentUser.getIdToken();
        const response = await fetch(API_ENDPOINTS.MAINTENANCE.CREATE, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(requestData)
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to submit maintenance request');
        }
        
        const result = await response.json();
        console.log('Maintenance request created:', result);
        
        showSuccess('Maintenance request submitted successfully!');
        
        // Reset form
        event.target.reset();
        document.getElementById('image-preview').innerHTML = '';
        
        setTimeout(() => {
            window.location.href = 'maintenancestatus.html';
        }, 1500);
        
    } catch (error) {
        console.error('Error submitting maintenance request:', error);
        showError(error.message || 'Failed to submit maintenance request');
        
        // Re-enable submit button
        const submitBtn = event.target.querySelector('.submit-btn');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Send';
        }
    }
}

// ===== LOAD MAINTENANCE REQUESTS (FOR STUDENT) =====
async function loadStudentMaintenanceRequests() {
    try {
        const token = await firebase.auth().currentUser.getIdToken();
        
        const response = await fetch(API_ENDPOINTS.MAINTENANCE.GET_MY, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to load maintenance requests');
        }
        
        const requests = await response.json();
        console.log('Loaded maintenance requests:', requests);
        
        displayMaintenanceRequests(requests);
        
    } catch (error) {
        console.error('Failed to load maintenance requests:', error);
        showError('Failed to load maintenance requests: ' + error.message);
    }
}

// ===== LOAD ALL MAINTENANCE REQUESTS (FOR ADMIN) =====
async function loadAllMaintenanceRequests() {
    try {
        const token = await firebase.auth().currentUser.getIdToken();
        
        const response = await fetch(API_ENDPOINTS.MAINTENANCE.GET_ALL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to load maintenance requests');
        }
        
        const requests = await response.json();
        console.log('Loaded admin maintenance requests:', requests);
        
        displayAdminMaintenanceRequests(requests);
        
    } catch (error) {
        console.error('Failed to load maintenance requests:', error);
        showError('Failed to load maintenance requests: ' + error.message);
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
                <h3>${request.queryTitle || 'Maintenance Issue'}</h3>
                <span class="status-badge ${request.status.toLowerCase()}">${request.status}</span>
            </div>
            <div class="request-body">
                <p><strong>Room:</strong> ${request.roomId}</p>
                <p><strong>Priority:</strong> ${request.priority}</p>
                <p><strong>Description:</strong> ${request.queryDescription || 'No description provided'}</p>
                <p><strong>Date:</strong> ${new Date(request.createdAt).toLocaleDateString()}</p>
                ${request.assignedToName ? `<p><strong>Assigned to:</strong> ${request.assignedToName}</p>` : ''}
                ${request.resolutionNotes ? `<p><strong>Resolution:</strong> ${request.resolutionNotes}</p>` : ''}
                ${request.photoUrls && request.photoUrls.length > 0 ? `
                    <div class="request-images">
                        ${request.photoUrls.map(photo => `<img src="${photo}" alt="Issue photo" class="request-image">`).join('')}
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
                <h4>${request.queryTitle || 'Maintenance Request'}</h4>
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
        const token = await firebase.auth().currentUser.getIdToken();
        
        // Determine which endpoint to use based on the status
        let endpoint;
        let updateData;
        
        if (newStatus === 'RESOLVED') {
            endpoint = API_ENDPOINTS.MAINTENANCE.RESOLVE(requestId);
            updateData = { resolutionNotes: resolutionNotes };
        } else {
            endpoint = API_ENDPOINTS.MAINTENANCE.UPDATE_STATUS(requestId);
            updateData = { status: newStatus, resolutionNotes: resolutionNotes };
        }
        
        const response = await fetch(endpoint, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(updateData)
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to update maintenance request');
        }
        
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
        const token = await firebase.auth().currentUser.getIdToken();
        
        const response = await fetch(API_ENDPOINTS.MAINTENANCE.GET_ONE(requestId), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Failed to load maintenance request details');
        }
        
        const request = await response.json();
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
        'issue-type': request.queryTitle || request.category,
        'priority-level': request.priority,
        'description-text': request.queryDescription,
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
    if (request.photoUrls && request.photoUrls.length > 0) {
        const imageContainer = document.getElementById('request-images');
        if (imageContainer) {
            imageContainer.innerHTML = request.photoUrls.map(photo => 
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