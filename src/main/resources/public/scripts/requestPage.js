/*
 * === Initialize ===
 */

window.onload = initializePage;
function initializePage() {
    // Getting query params
    let query = window.location.search;
    const params = new URLSearchParams(query);

    // Checking if new page
    if (params.has('id')) {
        // Getting params
        // Global
        username = params.get('username')
        requestId = params.get('id');
        managerView = params.get('managerView') === 'true';

        // Validating params
        if (!params.has('username') || !params.has('managerView') || requestId < 0) {
            // Invalild
            notFound();
            return;
        }

        // Valid
        updateExistingRequest();
    }
    else {
        // Getting new page
        updateNewRequest();
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
    metaData = await getMetaData(); // Global for input validation

    // Checking if successful
    if (metaData === null) {
        // Failed to fetch
        notFound();
        return;
    }

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
    for (ev of metaData.events) {
        let optionElement = document.createElement('option');
        optionElement.value = ev;
        optionElement.innerHTML = ev;
        eventTypeElement.append(optionElement);
    }

    // Filling Grade Formats
    let gradeFormatElement = document.getElementById("inputGradeFormat");
    for (gradeFormat of metaData.gradeFormats) {
        let optionElement = document.createElement('option');
        optionElement.value = gradeFormat.first;
        optionElement.innerHTML = gradeFormat.first;
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
    let requestData = await getRequest();
    console.log('request data');
    console.log(requestData);

    // Checking if sucessful
    if (requestData === null) {
        notFound();
        return;
    }
    
    let request = requestData.request;
    metaData = requestData.meta;    // Global for input verification

    // === UPDATEING LISTENERS ===

    // Button (submit/save) - Changed to save instead of submit
    document.getElementById('btnSubmit').innerHTML = 'Save';
    document.getElementById('btnSubmit').addEventListener('click', save);

    // === UPDATING FLAGS ===

    // For manager -> If reimAmount is changed, a reason must be provided.
    previousReimAmount = request.reimAmount.toFixed(2); // Global for input verification

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
    if (metaData.statuses[1].includes(request.status)) {
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
        document.getElementById('firstName').value = requestData.firstName;
        document.getElementById('lastName').value = requestData.lastName;
    }
    document.getElementById('reimFunds').value = `$${requestData.reimFunds.toFixed(2)}`;

    // Event Details
    if (managerView) {
        // Status - Manager View - Adding all statuses
        let statusElement = document.getElementById('inputStatus');
        for (stats of metaData.statuses) {
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
        optionElement.selected = true;
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
        optionElement.selected = true;
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
        startDate: Date.parse(`${document.getElementById('inputStartDate').value} ${document.getElementById('inputStartTime').value}`),
        justification: document.getElementById('inputJustification').value
    }
    const formBodyJson = JSON.stringify(formBody);
    console.log('form body');
    console.log(formBody);

    return fetchPostRequest(url, formBodyJson);
}

/**
 * Calls a fetch request to get the data of a specific employee request.
 * @returns The data of the request
 */
function getRequest() {
    // Init
    const url = `http://localhost:8080/request/${username}/${requestId}`;

    return fetchGetRequest(url);
}

/**
 * Fetcg GET call for meta data.
 * @returns The data of the get request
 */
function getMetaData() {
    // Init
    let url = "http://localhost:8080/meta";

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
    const url = `http://localhost:8080/request/${username}/${requestId}`;

    const formBody = {
        status: document.getElementById('inputStatus').value,
        reimAmount: document.getElementById('inputReimAmount').value,
        grade: document.getElementById('inputGrade').value,
        reason: document.getElementById('inputReason').value
    }
    const formBodyJson = JSON.stringify(formBody);
    console.log('form body');
    console.log(formBody);

    return fetchPatchRequest(url, formBodyJson);
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
    let valid = validateNewRequestInputs();

    // Checking if valid
    if (!valid) {
        // Not valid
        return;
    }

    // Getting post response data
    let data = await createRequest();
    console.log(data);

    // Checking if submission was successful
    if (data === null) {
        // Failed
        document.getElementById('error').innerHTML = "Failed to submit. Try again later."
    }
    else {
        // Success - Moving back to homepage
        back();
    }
}

/**
 * Saves the request to the server to update.
 */
async function save() {
    // Checking if changes were made and they're valid
    let valid = validateExistingRequestInputs();

    // Checking if valid
    if (!valid) {
        // Not valid
        return;
    }

    // Getting put response data
    let data = await updateRequest();

    // Checking if save was successful
    if (data === null) {
        // Failed
        document.getElementById('error').innerHTML = "Failed to save. Try again later."
    }
    else {
        // Success - Moving back to homepage
        back();
    }
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

    // Checking if cutoff is valid
    let cutoffElement = document.getElementById('inputCutoff');
    let result = validateGrade(cutoffElement.value);
    if (cutoffElement.value !== "" && !result[0]) {
        success = false;
        document.getElementById(`${cutoffElement.id}Error`).innerHTML = `Invalid Cutoff. Must be one of [${result[1]}]`;
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
        // Manager
        // Checking if reim amount was changed
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
        // Employee
        // Checking if grade is valid
        let gradeElement = document.getElementById('inputGrade');
        let result = validateGrade(gradeElement.value);
        if (gradeElement.value !== "" && !result[0]) {
            success = false;
            document.getElementById(`${gradeElement.id}Error`).innerHTML = `Invalid Cutoff. Must be one of [${result[1]}]`;
        }
        else {
            document.getElementById(`${gradeElement.id}Error`).innerHTML = '';
        }
    }

    return success;
}

/**
 * Determiens whether the given grade is valid.
 * Depends on grade format
 * @param {The grade to check} grade 
 * @returns True if the grade input is valid, and false othewise. Also sends back the list of acceptable grades.
 */
function validateGrade(grade) {
    // Init
    let valid = false;
    let acceptable = null;

    // Getting format
    let format = document.getElementById('inputGradeFormat').value;

    // Finding format to check against
    for (gradeFormat of metaData.gradeFormats) {
        // Checking if format matches
        if (gradeFormat.first === format) {
            // Found format
            acceptable = gradeFormat.second
            // Checking if format accetable grades includes given grade
            if (acceptable.includes(grade)) {
                valid = true;
            }
            break;
        }
    }

    return [valid, acceptable];
}

/**
 * Updates the request page for when a request wasn't found or was invalid.
 */
function notFound() {
    document.getElementById('title').innerHTML = "404 : Request Not Found";
    document.getElementById('form').innerHTML = '';
    document.getElementById('btnSubmit').hidden = true;
}