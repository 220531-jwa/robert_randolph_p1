/*
 * === Initialize ===
 */

initializePage();
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

function updateNewRequest() {
    // Setting title
    document.getElementById('title').innerHTML = 'New Request:';

    // Setting button listener
    document.getElementById('btnSubmit').addEventListener('click', submit);

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
}

async function updateExistingRequest() {
    // Getting elements to disable from user
    let inputElements = document.getElementsByClassName('input');
    let updateManagerElements = document.getElementsByClassName('update manager');
    let updateEmployeeElements = document.getElementsByClassName('update employee');
    let readOnlyManagerElements = document.getElementsByClassName('readOnly manager');

    // ReadOnly
    for (elem of inputElements) {
        elem.disabled = true;
    }

    // Button
    document.getElementById('btnSubmit').innerHTML = 'Save';
    document.getElementById('btnSubmit').addEventListener('click', save);

    // Checking current view
    console.log(1);
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

    // Populating request form details
    let data = await getRequest();
}

/*
 * === Fetch ===
 */

function createRequest() {

}

async function getRequest() {
    // Init
    const userData = getSessionUserData();
    let url = `http://localhost:8080/request/${userData.username}?rid=${id}`;

    return fetchGetRequest(url);
}

function updateRequest() {

}

/*
 * === Event Listeners ===
 */

function back() {

}

function submit() {

}

function save() {

}

/*
 * === Utility ===
 */

function notFound() {
    document.getElementById('title').innerHTML = "404 : Request Not Found";
    document.getElementById('form').innerHTML = '';
    document.getElementById('btnSubmit').hidden = true;
}