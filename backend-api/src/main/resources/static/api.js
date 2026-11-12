// Shared helpers for every ToolPool page.
// Load this before a page's own script:  <script src="js/api.js"></script>
//
// - ToolPool.get/post/put/patch/del call the REST API with fetch() and return the JSON body.
// - Errors are thrown as ApiError with a readable message taken from the API's { "error": "..." }.
// - The logged-in user (id + name) is kept in localStorage so other pages know who is acting.

// An error from the API (or from not reaching it), with the HTTP status and a readable message.
class ApiError extends Error {
    constructor(status, message) {
        super(message);
        this.status = status;
    }
}

const ToolPool = {
    // Sends a request to /api{path}. Resolves with the parsed JSON (or null for 204 No Content).
    // Rejects with ApiError, e.g. status 409 and message "An account with that email already exists."
    async request(method, path, body) {
        const options = { method, headers: {} };
        if (body !== undefined) {
            options.headers["Content-Type"] = "application/json";
            options.body = JSON.stringify(body);
        }

        let response;
        try {
            response = await fetch("/api" + path, options);
        } catch (networkError) {
            // fetch() only throws when the server can't be reached at all.
            throw new ApiError(0, "Can't reach the ToolPool server. Make sure the app is running.");
        }

        // 204 No Content (e.g. DELETE) has no body to parse.
        const text = await response.text();
        let data = null;
        if (text) {
            try {
                data = JSON.parse(text);
            } catch (parseError) {
                data = null;
            }
        }

        if (!response.ok) {
            const message = (data && data.error) || ToolPool.defaultMessage(response.status);
            throw new ApiError(response.status, message);
        }
        return data;
    },

    get(path) { return ToolPool.request("GET", path); },
    post(path, body) { return ToolPool.request("POST", path, body); },
    put(path, body) { return ToolPool.request("PUT", path, body); },
    patch(path, body) { return ToolPool.request("PATCH", path, body); },
    del(path) { return ToolPool.request("DELETE", path); },

    // Fallback text when an error response has no { "error": "..." } body.
    defaultMessage(status) {
        switch (status) {
            case 400: return "Some of the information entered isn't valid.";
            case 401: return "Please log in to continue.";
            case 403: return "You're not allowed to do that.";
            case 404: return "We couldn't find what you were looking for.";
            case 409: return "That conflicts with existing data.";
            default: return "Something went wrong on the server. Please try again.";
        }
    },

    // ----- Logged-in user (no real sessions yet: this only remembers who signed in) -----

    getCurrentUser() {
        try {
            return JSON.parse(localStorage.getItem("toolpoolUser"));
        } catch (storageError) {
            return null;
        }
    },

    // Saves just what other pages need: the id (ownerId / renterId / reviewerId) and a display name.
    setCurrentUser(user) {
        localStorage.setItem("toolpoolUser", JSON.stringify({ id: user.id, name: user.name }));
    },

    clearCurrentUser() {
        localStorage.removeItem("toolpoolUser");
    },

    // ----- Page helpers -----

    // Shows a message in an element. type is "error", "success", or "info".
    showMessage(element, text, type = "info") {
        element.textContent = text;
        element.className = "message " + type;
        element.hidden = false;
    },

    hideMessage(element) {
        element.textContent = "";
        element.hidden = true;
    },

    // Makes API text safe to put inside innerHTML (stops "<script>" in a name from running).
    escapeHtml(value) {
        return String(value ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");
    },

    // Picks a picture for a tool from the images we have, based on its name or category.
    imageFor(tool) {
        const text = ((tool.name || "") + " " + (tool.category || "")).toLowerCase();
        if (text.includes("drill")) return "images/powerdrill.jpg";
        if (text.includes("wash")) return "images/powerwasher.jpg";
        if (text.includes("saw")) return "images/powersaw.jpg";
        return "images/testtool_.jpg";
    },

    // Formats a number as dollars, e.g. 25 -> "$25.00".
    formatMoney(amount) {
        return "$" + Number(amount ?? 0).toFixed(2);
    },

    // Replaces the nav's "Login / Sign Up" link with "Hi, Name · Log Out" when someone is logged in.
    updateNav() {
        const authLink = document.getElementById("auth-link");
        const user = ToolPool.getCurrentUser();
        if (!authLink || !user) {
            return;
        }
        authLink.textContent = "Log Out (" + user.name + ")";
        authLink.href = "index.html";
        authLink.addEventListener("click", () => ToolPool.clearCurrentUser());
    }
};

document.addEventListener("DOMContentLoaded", ToolPool.updateNav);
