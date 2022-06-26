// Run when page loads
initializePage();

/*
 * === Initialize ===
 */

function initializePage() {
    // Getting user data
    const userData = getEmployeeData();

    // Updating page with user information
    document.getElementById("welcome").innerHTML = `Welcome: ${userData.firstName}`;
    document.getElementById("reimFunds").innerHTML = `$${userData.reimFunds}`;
    document.getElementById("funds").innerHTML = `$${userData.funds}`;

    // Populating table
    yourRequests();
}

/*
 * === Fetch calls ===
 */

async function getEmployeeData() {
    // Init
    let url = "http://localhost:8080/employee";

    // Getting userdata
    const userData = getSessionUserData();

    // Sending request for employee information
    let response = await fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Token': userData.password
        }
    })

    // Processing response
    if (response.status === 200) {
        // Getting up-to-date user information
        let data = await response.json;
        sessionStorage.userData = JSON.stringify(data);
        return data;
    }
    else {
        logout();
    }
}

async function getReimbursementRequests() {
    // Init
    let url = "http://localhost:8080/request";

    // Getting userdata
    const userData = getSessionUserData();

    // Updating url if manager
    if (userData.type === 'MANAGER') {
        url += `/all`
    }

    // Adding filters
    // url += ?status=notApproved (or something)

    // Sending request
    let response = await fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Token': userData.password
        }
    });

    // Processing response
    if (response.status === 200) {
        let data = await response.json;
        return data;
    }
    else {
        logout();
    }
}

/*
 * === Event Listeners ===
 */

function yourRequests() {
    // Updating nav
    document.getElementById('btnManage').classList.remove('btn-primary');
    document.getElementById('btnRequests').classList.add('btn-primary');

    // Updating table
    document.getElementById('tableLabel').innerHTML = "Your Requests:";
    document.getElementById('btnNewRequest').hidden = false;
    // ...
}

function manageRequests() {
    // Updating nav
    document.getElementById('btnRequests').classList.remove('btn-primary');
    document.getElementById('btnManage').classList.add('btn-primary');

    // Updating table
    document.getElementById('tableLabel').innerHTML = "Employee Requests:";
    document.getElementById('btnNewRequest').hidden = true;
    // ...
}

function newRequest() {
    // move to new html page, a form to enter request information
}

function seeRequest(item) {
    // Move to new html page, to see specific request details
}

/*
 * === Utility ===
 */

function getSessionUserData() {
    const userData = sessionStorage.userData;

    // Checking if in active session
    if (userData === undefined) {
        // Not in active session
        logout();
    }

    return JSON.parse(userData);
}

/**
 * Removes active session data and logouts the user.
 * Redirects the user to the login page.
 */
function logout() {
    sessionStorage.clear();
    window.location = "../html/homePage.html";
}