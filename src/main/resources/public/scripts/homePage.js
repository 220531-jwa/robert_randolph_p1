// Running init
initializePage();

function initializePage() {
    const user = sessionStorage.user;
    const userJson = JSON.parse(user);
    document.getElementById("output").innerHTML = `<h1>Welcome ${userJson.username}<br></h1>`;
}

async function getEmployee() {
    // Init
    const username = document.getElementById("inputUsername").value
    const url = "http://localhost:8080/employee?username=" + username;

    // Getting userdata
    const userData = sessionStorage.userData;
    const userDataJson = JSON.parse(userData);

    // Sending request for employee information
    let response = await fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Token": userDataJson.password
        }
    })

    // Processing response
    if (response.status === 200) {
        let data = await response.json;
        document.getElementById("output").innerHTML += "Got  data for: " + username + "<br>";
        document.getElementById("output").innerHTML += JSON.stringify(data);
        document.getElementById("output").innerHTML += "<br>";
    }
    else {
        document.getElementById("output").innerHTML += "Couldn't get user data for user: " + username + "<br>";
    }
}