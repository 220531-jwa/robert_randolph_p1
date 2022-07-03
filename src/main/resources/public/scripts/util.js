/*
 * === FETCH CALLS ===
 */

/**
 * Fetches a get request at a given url.
 * If the user isn't in an active session, redirects them to the login page.
 * @param {The url to send the request to} url
 * @returns The response status with the body data as JSON if successful, and null otherwise.
 */
async function fetchGetRequest(reqUrl) {
    // Getting userdata
    const userData = getSessionUserData();

    // Sending request
    let response = await fetch(reqUrl, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Token': userData.password
        }
    });

    // Processing response
    let data = null;
    if (response.ok) {
        data = await response.json();
    }
    else if (response.status === 401) {
        // Not in active session
        notInActiveSession();
    }

    return [response.status, data];
}

/**
 * Fetches a post request at a given url.
 * If the user isn't in an active session, redirects them to the login page.
 * @param {The url to send the request to} reqUrl 
 * @param {The body of the request} reqBody 
 * @returns The response status with the body data as JSON if successful, and null otherwise.
 */
async function fetchPostRequest(reqUrl, reqBody) {
    // Getting userdata
    const userData = getSessionUserData();

    // Sending request
    let response = await fetch(reqUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Token': userData.password
        },
        body: reqBody
    });

    // Processing response
    let data = null;
    if (response.ok) {
        data = await response.json();
    }
    else if (response.status === 401) {
        // Not in active session
        notInActiveSession();
    }

    return [response.status, data];
}

/**
 * Fetches a put request at a given url.
 * If the user isn't in an active sesion, redirects them to the login page.
 * @param {The url to send the request to} reqUrl 
 * @param {The body of the request} reqBody 
 * @returns The response status with the body data as JSON if successful, and null otherwise
 */
async function fetchPatchRequest(reqUrl, reqBody) {
    // Getting userdata
    const userData = getSessionUserData();

    // Sending request
    let response = await fetch(reqUrl, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            'Token': userData.password
        },
        body: reqBody
    });

    // Processing response
    let data = null;
    if (response.ok) {
        data = await response.json();
    }
    else if (response.status === 401) {
        // Not in active session
        notInActiveSession();
    }
    
    return [response.status, data];
}

/*
 * === ACTIVE SESSION ===
 */

/**
 * Gets the user data from the current session.
 * If no user data is found, then the user isn't in an active session.
 * @returns The user data for the currently active session
 */
function getSessionUserData() {
    const userData = sessionStorage.userData;

    // Checking if in active session
    if (userData === undefined) {
        // Not in active session
        notInActiveSession();
    }

    const userDataJson = JSON.parse(userData);
    return userDataJson;
}

/**
 * Is called when the user isn't in an active session.
 * Clears any left over session data, and redirects user to login page.
 */
function notInActiveSession() {
    // Clearing storage
    sessionStorage.clear();

    // Moving to login page
    location.href = "../html/loginPage.html";
}

/**
 * Tells the server that user is logging out.
 * Calls notInActiveSession.
 */
function logout() {
    // Init
    const url = "http://localhost:8080/logout";

    // Logging out - Don't care about response
    fetchPostRequest(url, null);

    // Moving to inacitve session
    notInActiveSession();
}

/*
 * === UTILITY ===
 */

/**
 * Converts a timestamp into a YYYY-MM-DD format
 * @param {The timestamp to convert} timestamp 
 */
function getDateFromTimestamp(timestamp) {
    // Init
    const pad = (num) => String(num).padStart(2, '0');
    let dateStamp = new Date(timestamp);

    // Converting to date
    let date = `${dateStamp.getFullYear()}-${pad(dateStamp.getMonth())}-${pad(dateStamp.getDate())}`;

    return date;
}

/**
 * Converts a timestamp into a HH:MM:SS format
 * @param {The timestamp to convert} timestamp 
 */
function getTimeFromTimestamp(timestamp) {
    // Init
    const pad = (num) => String(num).padStart(2, '0');
    let dateStamp = new Date(timestamp);

    // Converting to date
    let time = `${pad(dateStamp.getHours())}:${pad(dateStamp.getMinutes())}-${pad(dateStamp.getSeconds())}`;

    return time;
}

/**
 * Converts a timestamp into a YYYY-MM-DD HH:MM:SS format
 * @param {The timestamp to convert} timestamp 
 * @returns 
 */
function getDateTimeFromTimestamp(timestamp) {
    return `${getDateFromTimestamp(timestamp)} ${getTimeFromTimestamp(timestamp)}`
}