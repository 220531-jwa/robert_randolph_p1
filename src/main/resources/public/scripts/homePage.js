// Running init
initializePage();

function initializePage() {
    const user = sessionStorage.user;
    const userJson = JSON.parse(user);
    document.getElementById("test").innerHTML = `<h1>Welcome ${userJson.username}<br></h1>`;
}

let i = -1;
async function getEmployee() {
    // Init
    users = ["user1", "user2", "user3", "wolf"];
    i = (i + 1) % 4;
    username = users[i];
    const url = "http://localhost:8080/employee?username=" + username;

    // Getting userdata
    const user = sessionStorage.user;
    const userJson = JSON.parse(user);

    // Sending request for employee information
    let response = await fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Token": userJson.password
        }
    })

    // Processing response
    if (response.status === 200) {
        let data = await response.json;
        document.getElementById("test").append("Got  data for: " + username + "<br>");
        document.getElementById("test").append(JSON.stringify(data) + "<br>");
    }
    else {
        document.getElementById("test").append("Couldn't get user data for user: " + username + "<br>");
    }
}