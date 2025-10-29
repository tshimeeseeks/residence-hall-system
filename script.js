function goBack() {
  window.history.back();
}

// Startup animation logic
window.addEventListener("load", () => {
  const overlay = document.querySelector(".startup-overlay");

  // Delay fade-out to allow animation to play
  setTimeout(() => {
    overlay.classList.add("hidden");
  }, 1800);
});

// 🔹 Function to show logout confirmation and redirect
function logoutConfirm() {
  const confirmLogout = confirm("Are you sure you want to log out?");
  if (confirmLogout) {
    window.location.href = "index.html";
  }
}
// ADMIN SLEEPVER REQUESTS PAGE JS
function openVerification(studentName) {
  // You can redirect to a verification page and pass student name as a query parameter
  window.location.href = `verify_request.html?name=${encodeURIComponent(
    studentName
  )}`;
}

function goHome() {
  window.location.href = "dashboard.html";
}

function showSuccess(message) {
    alert('Success: ' + message);
    // Or use a better notification system
}

function showError(message) {
    alert('Error: ' + message);
    // Or use a better notification system
}

function goBack() {
    window.history.back();
}

// JavaScript to count words
const textarea = document.getElementById("message");
const wordCountDiv = document.getElementById("wordCount");

textarea.addEventListener("input", () => {
  const words = textarea.value
    .trim()
    .split(/\s+/)
    .filter((word) => word.length > 0);
  wordCountDiv.textContent = `${words.length} / 200 words`;
});

// JS function to "call" or copy number
function callNumber(number) {
  // If on mobile, use tel: link to call
  window.location.href = `tel:${number}`;

  // Optional: alert user the number was selected
  // alert(`You selected: ${number}`);
}
const imageInput = document.getElementById("support-image");
const previewDiv = document.getElementById("image-preview");

imageInput.addEventListener("change", () => {
  const file = imageInput.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = function (e) {
      previewDiv.innerHTML = `<img src="${e.target.result}" alt="Support Picture Preview">`;
    };
    reader.readAsDataURL(file);
  }
});

function simulateSubmit(button) {
  // Add loading state
  button.classList.add("loading");
  button.disabled = true;

  // Simulate a short delay (e.g., server response)
  setTimeout(() => {
    button.classList.remove("loading");
    button.classList.add("success");
    button.textContent = "Request Sent";

    // Add tick icon
    const tick = document.createElement("span");
    tick.textContent = " ✔";

    // Optional: Reset button after 3 seconds
    setTimeout(() => {
      button.classList.remove("success");
      button.textContent = "Send Request";
      button.disabled = false;
    }, 3000);
  }, 2000);
}

// script.js

// Smooth page fade-in on load
window.addEventListener("DOMContentLoaded", () => {
  document.body.classList.add("fade-in");
});

// Smooth page fade-out on navigation
function navigateTo(url) {
  document.body.classList.remove("fade-in");
  document.body.style.opacity = "0";
  setTimeout(() => {
    window.location.href = url;
  }, 600); // match the CSS transition time
}

// Go back to previous page with fade effect
function goBack() {
  document.body.classList.remove("fade-in");
  document.body.style.opacity = "0";
  setTimeout(() => {
    window.history.back();
  }, 600);
}

// Go home to dashboard with fade effect
function goHome() {
  navigateTo("dashboard.html");
}

// Example of data (this can be updated by admin in future)
const sleepoverRequest = {
  status: "pending", // can be "pending", "accepted", or "declined"
  requestDate: "2025-10-15",
};

const statusBox = document.getElementById("statusMessage");
const dateBox = document.getElementById("requestDate");

function updateStatusUI(data) {
  // Capitalize first letter of status
  statusBox.textContent =
    data.status.charAt(0).toUpperCase() + data.status.slice(1);

  // Remove old status classes
  statusBox.classList.remove("pending", "accepted", "declined");

  // Add new class depending on status
  statusBox.classList.add(data.status);

  // Format and display date
  const formattedDate = new Date(data.requestDate).toLocaleDateString();
  dateBox.textContent = "Date requested: " + formattedDate;
}

// Load status when page loads
updateStatusUI(sleepoverRequest);

// Example data (can be updated later or fetched from localStorage)
const queryData = {
  question: "What time does the residence gate close?",
  response: "", // if empty => pending
  status: "pending", // "pending" | "answered" | "declined"
  date: "2025-10-16",
};

const userQuestionEl = document.getElementById("userQuestion");
const adminResponseEl = document.getElementById("adminResponse");
const responseBoxEl = document.getElementById("responseBox");
const dateEl = document.getElementById("queryDate");

function updateQueryUI(data) {
  userQuestionEl.textContent = data.question;

  // Remove any previous status classes
  responseBoxEl.classList.remove("pending", "answered", "declined");

  if (data.status === "pending") {
    adminResponseEl.textContent = "Pending...";
    responseBoxEl.classList.add("pending");
  } else if (data.status === "answered") {
    adminResponseEl.textContent = data.response;
    responseBoxEl.classList.add("answered");
  } else if (data.status === "declined") {
    adminResponseEl.textContent = "Your query was declined.";
    responseBoxEl.classList.add("declined");
  }

  const formattedDate = new Date(data.date).toLocaleDateString();
  dateEl.textContent = "Date asked: " + formattedDate;
}

updateQueryUI(queryData);

// ADMIN VERIFY REQUEST PAGE JS
// Go Back
function goBack() {
  window.history.back();
}

// Go to correct verify page based on request type
function goToVerify(name, type) {
  let page = "";
  if (type === "sleepover") page = "sleepover_verify.html";
  if (type === "maintenance") page = "maintenance_verify.html";
  if (type === "query") page = "query_verify.html";

  window.location.href = `${page}?name=${encodeURIComponent(name)}`;
}

// Display student name on verify page
window.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(window.location.search);
  const name = params.get("name");
  if (name) {
    const nameEl = document.getElementById("studentName");
    if (nameEl) nameEl.textContent = name;
  }

  const dateEl = document.getElementById("requestDate");
  if (dateEl) {
    dateEl.textContent = new Date().toLocaleDateString();
  }
});

// Approve or Deny (example only)
document.addEventListener("click", (e) => {
  if (e.target.closest(".approve-btn")) {
    alert("✅ Request Approved");
    window.location.href = "admin_dashboard.html";
  }
  if (e.target.closest(".deny-btn")) {
    alert("❌ Request Denied");
    window.location.href = "admin_dashboard.html";
  }
});

// OVERRIDING A FEW ISSUES!!!
// dashboard.js
document.addEventListener("DOMContentLoaded", () => {
  const buttons = document.querySelectorAll(".option-card, .btn");

  buttons.forEach((button) => {
    button.addEventListener("click", (e) => {
      console.log(`${button.textContent.trim()} clicked`);
      // Add your navigation or functionality here
    });
  });
});

// STUDENT CSS
// Navigate back
function goBack() {
  window.history.back();
}

// Open student detail page with name in query string
function goToStudentDetail(studentName) {
  window.location.href = `student_detail.html?name=${encodeURIComponent(
    studentName
  )}`;
}

// When on student_detail.html — load details dynamically
document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(window.location.search);
  const studentName = params.get("name");

  if (studentName) {
    document.getElementById("studentName").textContent = studentName;

    // Simulate data fetching (this can later come from a backend)
    const studentData = {
      "John Doe": {
        number: "S101",
        room: "A12",
        email: "john.doe@rhsalira.com",
      },
      "Jane Smith": {
        number: "S102",
        room: "B05",
        email: "jane.smith@rhsalira.com",
      },
      "Alex Brown": {
        number: "S103",
        room: "C03",
        email: "alex.brown@rhsalira.com",
      },
      "Sarah Lee": {
        number: "S104",
        room: "D01",
        email: "sarah.lee@rhsalira.com",
      },
    };

    const details = studentData[studentName] || {
      number: "Unknown",
      room: "Unknown",
      email: "Unknown",
    };

    document.getElementById("studentNumber").textContent = details.number;
    document.getElementById("roomNumber").textContent = details.room;
    document.getElementById("studentEmail").textContent = details.email;
  }
});

// Show confirmation box
function confirmDeleteStudent() {
  document.getElementById("confirmBox").classList.remove("hidden");
}

// Hide confirmation box
function closeConfirm() {
  document.getElementById("confirmBox").classList.add("hidden");
}

// Delete student (simulate)
function deleteStudent() {
  alert("Student successfully removed from system.");
  window.location.href = "students.html"; // Redirect back to list
}
