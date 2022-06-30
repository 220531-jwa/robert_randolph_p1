/*
 * === Initialize ===
 */

window.onload = initializePage;
function initializePage() {
    // Getting query params
    let query = window.location.search;
    const params = new URLSearchParams(query);

    // Checking if id was given
    if (!params.has('id')) {
        notFound();
        return;
    }

    // Global
    id = params.get('id');
    managerView = params.get('managerView') === 'true';

    // Validating id
    if (id < 0) {
        notFound();
        return;
    }

    // Checking if viewing already existing request
    if (id == 0) {
        // new request
        updateNewRequest();
    }
    else {
        // existing request
        updateExistingRequest();
    }
}

/*
 * === Updates ===
 */

/**
 * Updates the request page to show as a new request form
 * Calls for meta data from the server to populate selection fields
 *  - If for some reason this fails shows a 404 page.
 * Updates the button listener for - submit
 * Sets the title
 * Hides all elements other then input fields
 */
async function updateNewRequest() {
    // Getting meta information
    let url = "http://localhost:8080/meta";
    // let metaData = await fetchGetRequest(url);

    // // Checking if successful
    // if (metaData === null) {
    //     // Failed to fetch
    //     notFound();
    // }

    // === UPDATEING LISTENERS ===

    // Setting button listener
    document.getElementById('btnSubmit').addEventListener('click', submit);

    // === MODIFYING ELEMENTS ===

    // Setting title
    document.getElementById('title').innerHTML = 'New Request:';

    // Getting elements to hide away from user
    let updateElements = document.getElementsByClassName('update');
    let readOnlyElements = document.getElementsByClassName('readOnly');

    // Hiding
    for (elem of updateElements) {
        elem.hidden = true;
    }
    for (elem of readOnlyElements) {
        elem.hidden = true;
    }

    // Filling Event Types
    let eventTypeElement = document.getElementById("inputEventType");
    for (ev of meta.events) {
        let optionElement = document.createElement('option');
        optionElement.value = ev;
        optionElement.innerHTML = ev;
        eventTypeElement.append(optionElement);
    }

    // Filling Grade Formats
    let gradeFormatElement = document.getElementById("inputGradeFormat");
    for (ev of meta.gradeFormats) {
        let optionElement = document.createElement('option');
        optionElement.value = ev;
        optionElement.innerHTML = ev;
        gradeFormatElement.append(optionElement);
    }
}

/**
 * Updates the request page to show an existing request form
 * Updates button listener for - save
 * Disables all input fields and only allows the user to update a select few
 *  - This depends on the update, manager, and employee tags.
 * If the status of the request is finished, then everything is readonly (disabled)
 * Updates all fields with the given request data.
 */
async function updateExistingRequest() {
    // Getting request data
    let data = await getRequest();
    let requestData = data[0];
    let request = requestData.request;
    let meta = requestData.meta;

    // === UPDATEING LISTENERS ===

    // Button (submit/save) - Changed to save instead of submit
    document.getElementById('btnSubmit').innerHTML = 'Save';
    document.getElementById('btnSubmit').addEventListener('click', save);

    // === UPDATING FLAGS ===

    // For manager -> If reimAmount is changed, a reason must be provided.
    previousReimAmount = request.reimAmount.toFixed(2);

    // === MODIFYING ELEMENTS ===

    // Getting elements to disable for user
    let inputElements = document.getElementsByClassName('input');
    let updateElements = document.getElementsByClassName('update');
    let updateManagerElements = document.getElementsByClassName('update manager');
    let updateEmployeeElements = document.getElementsByClassName('update employee');
    let readOnlyManagerElements = document.getElementsByClassName('readOnly manager');

    // Input elements are only avaliable when creating a new request
    // Readonly - disabled
    for (elem of inputElements) {
        elem.disabled = true;
    }

    // Checking if remaining elements can be edited
    // Cannot change a finished request
    let finished = false;
    if (meta.statuses[1].includes(request.status)) {
         // Status is finished
         for (elem of updateElements) {
            elem.disabled = true;
         }
         document.getElementById('btnSubmit').hidden = true;    // Nothing to save/submit
         finished = true;
     }

    /*
    Updating based on current view
    - Updates title
    - Name is hidden if employee
    - Everything is disabled | Other than certain elements:
        - Reim Amount is disabled for employee
        - Grade is disabled for manager
        - Reason is disabled for employee
    */
    if (managerView) {
        // In Manager View
        document.getElementById('title').innerHTML = 'Manage Request:';

        // Disabling elements that manager can't edit
        for (elem of updateEmployeeElements) {
            elem.disabled = true;
        }
    }
    else {
        // In Employee View
        document.getElementById('title').innerHTML = 'Your Request:';

        // Disabling elements that employee can't edit
        for (elem of readOnlyManagerElements) {
            elem.hidden = true;
        }
        for (elem of updateManagerElements) {
            elem.disabled = true;
        }
    }

    // === POPULATING FORM ===

    // Employee Information
    if (managerView) {
        // Manager View - Manger needs to know who's request it is (employee knows who they are)
        document.getElementById('firstName').value = meta.firstName;
        document.getElementById('lastName').value = meta.lastName;
    }
    document.getElementById('reimFunds').value = `$${meta.reimFunds.toFixed(2)}`;

    // Event Details
    if (managerView) {
        // Status - Manager View - Adding all statuses
        let statusElement = document.getElementById('inputStatus');
        for (stats of meta.statuses) {
            for (stat of stats) {
                let optionElement = document.createElement('option');
                optionElement.value = stat;
                if (request.status === stat) {
                    // Selecting current status
                    optionElement.selected = true;
                }
                optionElement.innerHTML = stat;
                statusElement.append(optionElement);
            }
        }
    }
    else {
        // Status - Employee View - Adding current status
        let statusElement = document.getElementById('inputStatus');
        let optionElement = document.createElement('option');
        optionElement.value = request.status;
        optionElement.innerHTML = request.status;
        statusElement.append(optionElement);
        // Adding Canceled status if status isn't finished
        if (!finished) {
            optionElement = document.createElement('option');
            optionElement.value = 'CANCELLED'
            optionElement.innerHTML = 'CANCELLED'
            statusElement.append(optionElement);
        }
    }
    {
        // Event Type
        let eventTypeElement = document.getElementById('inputEventType');
        let optionElement = document.createElement('option');
        optionElement.value = request.eventType;
        optionElement.innerHTML = request.eventType;
        eventTypeElement.append(optionElement);
    }
    document.getElementById('inputEventType').value = request.eventType;
    document.getElementById('inputCost').value = request.cost.toFixed(2);
    document.getElementById('inputReimAmount').value = request.reimAmount.toFixed(2);
    {
        // Grade Format
        let gradeFormatElement = document.getElementById('inputGradeFormat');
        let optionElement = document.createElement('option');
        optionElement.value = request.gradeFormat;
        optionElement.innerHTML = request.gradeFormat;
        gradeFormatElement.append(optionElement);
    }
    document.getElementById('inputGrade').value = request.grade;
    document.getElementById('inputCutoff').value = request.cutoff;
    document.getElementById('inputDescription').value = request.eventDescription;

    // Event Location & Time
    document.getElementById('inputLocation').value = request.eventLocation;
    {
        console.log(1);
        const pad = (num) => String(num).padStart(2, '0');
        let startts = new Date(request.startDate);
        let startDate = `${startts.getFullYear()}-${pad(startts.getMonth())}-${pad(startts.getDate())}`;
        let startTime = `${pad(startts.getHours())}:${pad(startts.getMinutes())}:${pad(startts.getMinutes())}`;
        let submissionts = new Date(request.submissionDate);
        let submissionDate = `${submissionts.getFullYear()}-${pad(submissionts.getMonth())}-${pad(submissionts.getDate())}`;
        let submissionTime = `${pad(submissionts.getHours())}:${pad(submissionts.getMinutes())}:${pad(submissionts.getMinutes())}`;
        document.getElementById('inputStartDate').value = startDate;
        document.getElementById('inputStartTime').value = startTime;
        document.getElementById('submissionDate').value = submissionDate;
        document.getElementById('submissionTime').value = submissionTime;
    }
    
    // For Manager Review
    document.getElementById('urgentFlag').checked = request.urgent;
    document.getElementById('exceedsFundsFlag').checked = request.exceedsFunds;
    document.getElementById('inputJustification').value = request.justification;
    document.getElementById('inputReason').value = request.reason;
}

/*
 * === Fetch ===
 */

/**
 * Fetch POST call for new request.
 * Input is validated before hand to minimize server calls.
 * Final validation is still done by the server.
 * @return The data of the post request
 */
function createRequest() {
    // Init
    const userData = getSessionUserData();
    const url = `http://localhost:8080/request/${userData.username}`;

    // Creating body with form details
    const formBody = {
        eventType: document.getElementById('inputEventType').value,
        cost: document.getElementById('inputCost').value,
        gradeFormat: document.getElementById('inputGradeFormat').value,
        cutoff: document.getElementById('inputCutoff').value,
        eventDescription: document.getElementById('inputDescription').value,
        eventLocation: document.getElementById('inputLocation').value,
        startDate: `${document.getElementById('inputStartDate').value} ${document.getElementById('inputStarTime').value}`,
        justification: document.getElementById('inputJustification').value
    }

    return fetchPostRequest(url, formBody);
}

/**
 * Calls a fetch request to get the data of a specific employee request.
 * @returns The data of the request
 */
function getRequest() {
    // Init
    const userData = getSessionUserData();
    const url = `http://localhost:8080/request/${userData.username}?rid=${id}`;

    return fetchGetRequest(url);
}

/**
 * Fetch PUT call for updating a request.
 * Input is validated before hand to minimize server calls.
 * Final validating is still done by the server.
 * @returns The data of the request
 */
function updateRequest() {
    // Init
    const userData = getSessionUserData();
    const url = `http://localhost:8080/request/${userData.username}/${id}`;

    const formBody = {

    }

    return fetchPutRequest(url, formBody);
}

/*
 * === Event Listeners ===
 */

/**
 * Redirects the user to the home page.
 * Doesn't save/submit the request.
 */
function back() {
    location.href = "../html/homePage.html";
}

/**
 * Submits the request to the server to create.
 */
async function submit() {
    // Checking if required fields are filled out
    validateNewRequestInputs();
    // let data = await fetchPostRequest();

    // // Checking if submission was valid
    // if (data === null) {
    //     // Failed
    //     // Show errors
    // }
    // else {
    //     // Success - Moving back to homepage
    //     back();
    // }
}

/**
 * Saves the request to the server to update.
 */
function save() {
    validateExistingRequestInputs();
}

/*
 * === Utility ===
 */

/**
 * Checks whether the inputs for a new request are valid.
 * @returns true if the inputs were valid, and false otherwise.
 */
function validateNewRequestInputs() {
    // Init
    let success = true;

    // Getting input fields
    let inputElements = document.getElementsByClassName('input');

    // Checking if required inputs have been filled
    for (elem of inputElements) {
        if (elem.value === "") {
            // Required input field wasn't filled in
            success = false;
            document.getElementById(`${elem.id}Error`).innerHTML = "This field is required";
        }
        else {
            document.getElementById(`${elem.id}Error`).innerHTML = "";
        }
    }

    // Checking if cost is valid
    let costElement = document.getElementById('inputCost');
    if (costElement.value !== "" && costElement.value < costElement.min || costElement.value > costElement.max) {
        success = false;
        document.getElementById(`${costElement.id}Error`).innerHTML = `Invalid Amount. Must be between $${costElement.min} and $${costElement.max}`;
    }

    return success;
}

/**
 * Checks whether the inputs for an existing request are valid.
 * @returns true if the inputs were valid and false otherwise.
 */
function validateExistingRequestInputs() {
    // Init
    let success = true;

    // Checking if manager
    if (managerView) {
        // Manager - Checking if reim amount was changed
        let reimAmountElement = document.getElementById('inputReimAmount');
        if (reimAmountElement.value != previousReimAmount) {
            // Reim amount was changed
            // Checking if amount is within range.
            if (reimAmountElement.value !== "" && reimAmountElement.value < reimAmountElement.min || reimAmountElement.value > reimAmountElement.max) {
                // Invalid range
                success = false;
                document.getElementById(`${reimAmountElement.id}Error`).innerHTML = `Invalid Amount. Must be between $${reimAmountElement.min} and $${reimAmountElement.max}`;
            }
            else {
                document.getElementById(`${reimAmountElement.id}Error`).innerHTML = '';
            }
            // Checking if reason was provided for change
            let reasonElement = document.getElementById('inputReason');
            if (reasonElement.value === "") {
                // Reason wasn't provided
                success = false;
                document.getElementById(`${reasonElement.id}Error`).innerHTML = 'This field is required when reimbursement amount is changed';
            }
            else {
                document.getElementById(`${reasonElement.id}Error`).innerHTML = '';
            }
        }
    }
    else {
        // Employee - Checking if grade input was valid (based on grade format)
        success = validateGrade();
    }

    return success;
}

/**
 * Determiens whether the grade input is valid.
 * Depends on grade format
 * @returns True if the grade input is valid, and false othewise.
 */
function validateGrade() {
    // Getting grade related elements
    let gradeFormatElement = document.getElementById('inputGradeFormat');
    let gradeElement = document.getElementById('inputGrade');

    // Checking type
    let accept;
    if (gradeFormatElement.value === 'LETTER') {
        // Letter
        accept = ['A', 'B', 'C', 'D', 'F', ''];
    }
    else {
        // Pass Fail
        accept = ['P', 'F', ''];
    }

    if (!accept.includes(gradeElement.value)) {
        // Not a valid letter grade
        document.getElementById(`${gradeElement.id}Error`).innerHTML = `Invalid Grade. Grade must be one of the following: ${accept}`;
    }
    else {
        document.getElementById(`${gradeElement.id}Error`).innerHTML = '';
        return true;
    }

    return true;
}

/**
 * Updates the request page for when a request wasn't found or was invalid.
 */
function notFound() {
    document.getElementById('title').innerHTML = "404 : Request Not Found";
    document.getElementById('form').innerHTML = '';
    document.getElementById('btnSubmit').hidden = true;
}