/*
 * === Init ===
 */

/**
 * Initializes the page when the page is first loaded.
 *  - Including various elements
 * Calls for employee and request updates
 */
 initializePage();
function initializePage() {
    // Init - global vars
    managerView = false;

    // Adding event listener to filter
    // - Filter will update the table after a NEW item was selected.
    let filter = document.getElementById('filter');
    filter.addEventListener('change', () => {
        updateRequestInformation();
    });

    // Updating employee information
    updateEmployeeInformation();

    // update employee requests table
    updateRequestInformation();
}

/*
 * === Updates ===
 */

/**
 * Updates the employee information on the page.
 * Updates the nav buttons depending on employee/manager view
 * Calls for a fetch to get user employee data.
 */
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

/**
 * Updates the request information on the page.
 * Updates the table columns depending on employee/manager view
 * Calls for a fetch to get user request data.
 */
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

/**
 * Fetches the users employee data to populate the page with.
 * This is view independent.
 * Needs the active session token for user authorization.
 *  - if not authorized, user is redirected to login page
 * @returns The employee data if successful, and null otherwise.
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

/**
 * Fetches the users request data to populate the table with.
 * If in manager view, fetches all requests.
 * If in employee view, fetches only the users requests.
 * Is able to filter based on status.
 * Needs the active session token for user authorization.
 *  - if not authorized, user is redirected to login page
 * @returns The request data if successful, and null otherwise.
 */
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

/**
 * A listener for the 'Your Requests' button.
 * Updates the view to employee, and updates elements accordingly
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

/**
 * A listener for the 'Manage Requests' button.
 * Updates the view to manager, and updates elements accordingly
 */
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

/**
 * Redirects the user to a new page to create a new request.
 */
function newRequest() {
    // move to new html page, a form to enter request information
}

/**
 * Redirects the user to a new page to see all the details of the selected request.
 * @param {The id of the request to find} item 
 */
function seeRequest(item) {
    // Move to new html page, to see specific request details
    console.log('Clicked request item: ' + item);
}

/*
 * === Utility ===
 */

/**
 * Creates a request row for the data table in order to populate it.
 * Row created depends if user is in manager view or not.
 * Creates an event listener as a link on the event type.
 *  - This allows the user to go to a new page to see all the details of the request.
 * @param {The data to turn into a request row} requestData 
 * @returns A <tr> element with request information as values.
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

/**
 * Gets the user data from the current session.
 * @returns The user data for the currently active session
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