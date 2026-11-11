// Login page (issue #59): POST /api/users/login, then remember the user via js/api.js.

const loginForm = document.getElementById("login-form");
const loginMessage = document.getElementById("login-message");
const loginButton = document.getElementById("login-button");

// Already logged in? Say so instead of silently showing the form.
const alreadyLoggedIn = ToolPool.getCurrentUser();
if (alreadyLoggedIn) {
    ToolPool.showMessage(loginMessage,
        "You're logged in as " + alreadyLoggedIn.name + ". Log in below to switch accounts.", "info");
}

loginForm.addEventListener("submit", async (event) => {
    // Stop the browser's normal form submit (which would reload the page).
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    // Quick check in the browser; the API checks again and returns 400 if blank.
    if (!email || !password) {
        ToolPool.showMessage(loginMessage, "Please enter your email and password.", "error");
        return;
    }

    loginButton.disabled = true;
    loginButton.textContent = "Logging in...";

    try {
        // 200 -> the user (no password). 401 wrong details, 403 banned: both throw ApiError.
        const user = await ToolPool.post("/users/login", { email, password });
        ToolPool.setCurrentUser(user);
        ToolPool.showMessage(loginMessage, "Welcome back, " + user.name + "! Taking you home...", "success");
        setTimeout(() => { window.location.href = "index.html"; }, 800);
    } catch (error) {
        // Shows the API's own message, e.g. "Invalid email or password." or
        // "This account has been banned."
        ToolPool.showMessage(loginMessage, error.message, "error");
        loginButton.disabled = false;
        loginButton.textContent = "Login";
    }
});
