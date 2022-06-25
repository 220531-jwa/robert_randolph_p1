/**
 * Send a login request to the server
 * If successful will create a login session
 * Otherwise will notify the user there is
 */
async function signIn() {
    // Init
    const url = "http://localhost:8080/login";

    // Getting credentials
    const credentials = {
        username: document.getElementById("inputUsername").value,
        password: document.getElementById("inputPassword").value
    };
    const credentialsJson = JSON.stringify(credentials);

    // Sending request
    let response = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: credentialsJson
    });
    
    // Processing response
    if (response.status === 200) {
        let userData = await response.json();
        sessionStorage.userData = JSON.stringify(userData);
        window.location = "../html/homePage.html";
    }
    else {
        document.getElementById("error").innerHTML = "Invalid Username or Password";
    }
}