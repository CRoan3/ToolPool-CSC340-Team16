// Sign-up page (issue #60, US-1): POST /api/users, then log the new user in.
// Also fills the "Tools near you" cards from GET /api/tools (no hardcoded data).

const signupForm = document.getElementById("signup-form");
const signupMessage = document.getElementById("signup-message");
const signupButton = document.getElementById("signup-button");

// ----- "Tools near you" cards -----

async function loadToolPreview() {
    const toolList = document.getElementById("tool-list");
    const toolsMessage = document.getElementById("tools-message");

    try {
        // GET /api/tools returns only available tools.
        const tools = await ToolPool.get("/tools");

        if (tools.length === 0) {
            ToolPool.showMessage(toolsMessage, "No tools are listed yet. Be the first to post one!", "info");
            return;
        }

        // Show up to three tools. escapeHtml keeps user-entered text from becoming HTML.
        toolList.innerHTML = tools.slice(0, 3).map(tool => `
            <div class="tool-card">
                <h4>${ToolPool.escapeHtml(tool.name)}</h4>
                <p>${ToolPool.formatMoney(tool.dailyPrice)} / day</p>
                <p>${ToolPool.escapeHtml(tool.category || "Other")}</p>
            </div>
        `).join("");
    } catch (error) {
        ToolPool.showMessage(toolsMessage, "Couldn't load tools: " + error.message, "error");
    }
}

// ----- Create account -----

signupForm.addEventListener("submit", async (event) => {
    // Stop the browser's normal form submit (which would reload the page).
    event.preventDefault();

    const firstName = document.getElementById("first-name").value.trim();
    const lastName = document.getElementById("last-name").value.trim();
    const email = document.getElementById("email").value.trim();
    const phoneNumber = document.getElementById("phone").value.trim();
    const location = document.getElementById("location").value.trim();
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    // Checks the browser can do before calling the API. The API validates again (400).
    if (!firstName || !lastName || !email || !password) {
        ToolPool.showMessage(signupMessage, "Please fill in your name, email, and password.", "error");
        return;
    }
    if (password !== confirmPassword) {
        ToolPool.showMessage(signupMessage, "The passwords don't match.", "error");
        return;
    }

    // Matches UserRegistrationRequest. The API stores one "name", so first + last are joined.
    // Optional fields are sent as null when empty, so nothing blank gets saved.
    const body = {
        name: firstName + " " + lastName,
        email,
        password,
        phoneNumber: phoneNumber || null,
        location: location || null
    };

    signupButton.disabled = true;
    signupButton.textContent = "Creating account...";

    try {
        // 201 -> the new user (no password). 400 invalid fields, 409 email taken: both throw ApiError.
        const user = await ToolPool.post("/users", body);

        // Log the new user in right away, so other pages know who they are.
        ToolPool.setCurrentUser(user);
        signupForm.reset();
        ToolPool.showMessage(signupMessage,
            "Welcome to ToolPool, " + user.name + "! Your account is ready. Taking you home...", "success");
        setTimeout(() => { window.location.href = "index.html"; }, 1500);
    } catch (error) {
        // Shows the API's own message, e.g. "An account with that email already exists."
        ToolPool.showMessage(signupMessage, error.message, "error");
        signupButton.disabled = false;
        signupButton.textContent = "Create Account";
    }
});

loadToolPreview();
