// Run when page loads
initializePage();

/*
 * === Updates ===
 */

function initializePage() {
    // Updating employee information
    updateEmployeeInformation();

    // update employee requests table
    updateRequestInformation();
}

async function updateEmployeeInformation() {
    // Getting up to date employee information
    let userData = await getEmployeeData();
    console.log("got user data: ");
    console.log(userData);

    // Updating page with user information
    document.getElementById("welcome").innerHTML = `Welcome: ${userData.firstName} ${userData.lastName}`;
    document.getElementById("reimFunds").innerHTML = `$${userData.reimFunds}`;
    document.getElementById("funds").innerHTML = `$${userData.funds}`;

    // Checking if employee is manager -> unlocks manage requests nav button
    if (userData.type === 'MANAGER') {
        document.getElementById("btnManage").hidden = false;
    }
}

async function updateRequestInformation() {
    // Getting up to date request information
    let requestData = await getReimbursementRequests();
    console.log("got request data: ");
    console.log(requestData);
}

/*
 * === Fetch calls ===
 */

async function getEmployeeData() {
    // Init
    let url = "http://localhost:8080/employee?username=";

    // Getting userdata
    const userData = getSessionUserData();

    // Updating url
    url += userData.username;

    // Sending request for employee information
    let response = await fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Token': userData.password
        }
    });

    // Processing response
    if (response.status === 200) {
        // Getting up-to-date user information
        let data = await response.json();
        return data;
    }
    else if (response.status === 401) {
        logout();
    }
}

async function getReimbursementRequests() {
    // Init
    console.log(3);
    let url = "http://localhost:8080/request";

    // Getting userdata
    const userData = getSessionUserData();

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
        let data = await response.json();
        return data;
    }
    else if (response.status === 401) {
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
    let data = getReimbursementRequests();
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

    const userDataJson = JSON.parse(userData);
    return userDataJson;
}

/**
 * Removes active session data and logouts the user.
 * Redirects the user to the login page.
 */
function logout() {
    sessionStorage.clear();
    // location.href = "../html/loginPage.html";
}