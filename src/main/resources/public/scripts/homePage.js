// Run when page loads
var managerView = false;
initializePage();

/*
 * === Updates ===
 */

function initializePage() {
    // Adding event listener to filter
    let filter = document.getElementById('filter');
    filter.addEventListener('change', () => {
        updateRequestInformation();
    });

    // Updating employee information
    updateEmployeeInformation();

    // update employee requests table
    updateRequestInformation();
}

async function updateEmployeeInformation() {
    // Getting up to date employee information
    let userData = await getEmployeeData();

    // Checking if fetch was successful
    if (userData === null) {
        // Failed
        return;
    }

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
    // Clearing current table data
    let tableItems = document.getElementById("tableItems");
    tableItems.innerHTML = "";

    // Displaying/Hidding managerView cols
    let elements = document.getElementsByClassName('ManagerCol');
    for (elem of elements) {
        elem.hidden = !managerView;
    }

    // Getting up to date request information
    let requestData = await getReimbursementRequests();

    // Checking if fetch was succesful
    if (requestData === null) {
        // Failed
        return;
    }

    // Populating table
    for (req of requestData) {
        tableItems.append(createTableRow(req));
    }
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
    else {
        return null;
    }
}

async function getReimbursementRequests() {
    // Init
    let url = "http://localhost:8080/request";

    // Getting userdata
    const userData = getSessionUserData();

    // Updating url
    if (!managerView) {
        // Not a manager
        url += `/${userData.username}`;
    }
    let filter = document.getElementById('filter');
    let value = filter.options[filter.selectedIndex].value;
    url += `?statusFilter=${value}`;

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
    else {
        return null;
    }
}

/*
 * === Event Listeners ===
 */

function yourRequests() {
    // Updating flags
    managerView = false;
    
    // Updating nav
    document.getElementById('btnManage').classList.remove('btn-primary');
    document.getElementById('btnRequests').classList.add('btn-primary');

    // Updating table
    document.getElementById('tableLabel').innerHTML = "Your Requests:";
    document.getElementById('btnNewRequest').hidden = false;
    
    updateRequestInformation();
}

function manageRequests() {
    // Updating flags
    managerView = true;
    
    // Updating nav
    document.getElementById('btnRequests').classList.remove('btn-primary');
    document.getElementById('btnManage').classList.add('btn-primary');

    // Updating table
    document.getElementById('tableLabel').innerHTML = "Employee Requests:";
    document.getElementById('btnNewRequest').hidden = true;

    updateRequestInformation();
}

function newRequest() {
    // move to new html page, a form to enter request information
}

function seeRequest(item) {
    // Move to new html page, to see specific request details
    console.log('Clicked request item: ' + item);
}

/*
 * === Utility ===
 */

function createTableRow(requestData) {
    // Seperating data
    let firstName = requestData.firstName;
    let lastName = requestData.lastName;
    let request = requestData.request;
    let id = request.id;
    let values;

    if (managerView) {
        values = [`${firstName} ${lastName}`, request.eventType, `$${request.cost.toFixed(2)}`,
            `$${request.reimAmount.toFixed(2)}`, request.status, request.urgent, request.grade,
            request.passCutoff, request.justification, request.startDate];
    }
    else {
        values = [request.eventType, `$${request.cost.toFixed(2)}`,
            `$${request.reimAmount.toFixed(2)}`, request.status, request.grade,
            request.passCutoff, request.startDate];
    }

    // Creating row
    let tr = document.createElement('tr');
    tr.id = `request_${id}`

    // Adding link
    let i;
    if (managerView) {
        i = 0;
    }
    else {
        i = 1;
    }

    for (val of values) {
        let td = document.createElement('td');

        if (i == 1) {
            let a = document.createElement('a');
            a.setAttribute('href', '#');
            a.addEventListener('click', () => seeRequest(id));
            a.innerHTML = val;
            td.append(a);
        }
        else {
            td.innerHTML = val;
        }
        
        tr.append(td);
        i++;
    }

    return tr;
}

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