// Running init
initializePage();

function initializePage() {
    const user = sessionStorage.user;
    const userJson = JSON.parse(user);
    document.getElementById("test").innerHTML = `<h1>Welcome ${userJson.username}</h1>`;
}