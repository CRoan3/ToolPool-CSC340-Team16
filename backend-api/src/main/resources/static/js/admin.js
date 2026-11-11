// Admin dashboard (issues #61 and #62).
//   Story 6: see reported users      -> GET /api/reports, PUT /api/reports/{id}
//   Story 1: remove suspicious users -> PUT /api/users/{id}/status, DELETE /api/users/{id}
// Stats come from GET /api/users, GET /api/tools, and GET /api/reports. No hardcoded data.

const adminMessage = document.getElementById("admin-message");
const reportFilter = document.getElementById("report-filter");
const reportedUsersBody = document.getElementById("reported-users-body");
const allUsersBody = document.getElementById("all-users-body");

// Latest data from the API, kept so the report filter can redraw without refetching.
let users = [];
let reports = [];

// ----- Loading -----

// Fetches everything the page shows, then draws it. Called on load and after every action,
// so the page always matches what's saved in the database.
async function loadDashboard() {
    try {
        // The three requests are independent, so run them at the same time.
        const [userList, toolList, reportList] = await Promise.all([
            ToolPool.get("/users"),
            ToolPool.get("/tools"),   // only available (listed) tools
            ToolPool.get("/reports")  // all reports, newest first
        ]);
        users = userList;
        reports = reportList;

        document.getElementById("stat-users").textContent = users.length;
        document.getElementById("stat-listings").textContent = toolList.length;
        document.getElementById("stat-banned").textContent =
            users.filter(user => user.accountStatus === "BANNED").length;
        document.getElementById("stat-reports").textContent =
            reports.filter(report => report.status === "OPEN").length;

        renderReportedUsers();
        renderAllUsers();
    } catch (error) {
        ToolPool.showMessage(adminMessage, "Couldn't load the dashboard: " + error.message, "error");
        reportedUsersBody.innerHTML = '<tr><td colspan="5">Reports unavailable.</td></tr>';
        allUsersBody.innerHTML = '<tr><td colspan="6">Users unavailable.</td></tr>';
    }
}

// ----- Drawing the tables -----

// One row per reported user, combining all of their reports that match the filter.
function renderReportedUsers() {
    const filter = reportFilter.value; // "OPEN", "RESOLVED", or "" for all
    const shown = reports.filter(report => !filter || report.status === filter);

    if (shown.length === 0) {
        const label = filter ? filter.toLowerCase() + " " : "";
        reportedUsersBody.innerHTML = `<tr><td colspan="5">No ${label}reports.</td></tr>`;
        return;
    }

    // Group the reports by the user they're about.
    const byUser = new Map();
    for (const report of shown) {
        if (!byUser.has(report.reportedUserId)) {
            byUser.set(report.reportedUserId, []);
        }
        byUser.get(report.reportedUserId).push(report);
    }

    reportedUsersBody.innerHTML = [...byUser.entries()].map(([userId, userReports]) => {
        const user = users.find(u => u.id === userId);
        const name = userReports[0].reportedUserName;
        const hasOpen = userReports.some(report => report.status === "OPEN");

        const reasons = userReports.map(report =>
            `<div>"${ToolPool.escapeHtml(report.reason)}"
                <small>— ${ToolPool.escapeHtml(report.reporterName)}, ${formatDate(report.reportDate)}
                (${report.status.toLowerCase()})</small></div>`
        ).join("");

        return `
            <tr>
                <td>${ToolPool.escapeHtml(name)}</td>
                <td>${userReports.length}</td>
                <td>${reasons}</td>
                <td>${statusLabel(user)}</td>
                <td>
                    ${banButton(user)}
                    ${hasOpen ? `<button class="view-button" data-action="resolve" data-user-id="${userId}">Resolve</button>` : ""}
                    <button class="remove-button" data-action="remove" data-user-id="${userId}">Remove</button>
                </td>
            </tr>
        `;
    }).join("");
}

function renderAllUsers() {
    if (users.length === 0) {
        allUsersBody.innerHTML = '<tr><td colspan="6">No users yet.</td></tr>';
        return;
    }

    allUsersBody.innerHTML = users.map(user => `
        <tr>
            <td>${user.id}</td>
            <td>${ToolPool.escapeHtml(user.name)}</td>
            <td>${ToolPool.escapeHtml(user.email)}</td>
            <td>${ToolPool.escapeHtml(user.location || "—")}</td>
            <td>${statusLabel(user)}</td>
            <td>
                ${banButton(user)}
                <button class="remove-button" data-action="remove" data-user-id="${user.id}">Remove</button>
            </td>
        </tr>
    `).join("");
}

// "Active" / "Banned", or "Removed" if the user no longer exists.
function statusLabel(user) {
    if (!user) {
        return "Removed";
    }
    return user.accountStatus === "BANNED" ? "<strong>Banned</strong>" : "Active";
}

// Ban for active users, Unban for banned ones, nothing for removed users.
function banButton(user) {
    if (!user) {
        return "";
    }
    const banned = user.accountStatus === "BANNED";
    return `<button class="view-button" data-action="${banned ? "unban" : "ban"}" data-user-id="${user.id}">
                ${banned ? "Unban" : "Ban"}
            </button>`;
}

function formatDate(isoDateTime) {
    return new Date(isoDateTime).toLocaleDateString();
}

// ----- Actions -----

// Story 1: PUT /api/users/{id}/status with ACTIVE or BANNED.
async function setAccountStatus(userId, accountStatus) {
    const user = await ToolPool.put(`/users/${userId}/status`, { accountStatus });
    const verb = accountStatus === "BANNED" ? "banned. They can no longer log in." : "unbanned.";
    ToolPool.showMessage(adminMessage, `${user.name} has been ${verb}`, "success");
}

// Story 6: mark every open report about this user RESOLVED (PUT /api/reports/{id}).
async function resolveReports(userId) {
    const open = reports.filter(report => report.reportedUserId === userId && report.status === "OPEN");
    await Promise.all(open.map(report => ToolPool.put(`/reports/${report.id}`, { status: "RESOLVED" })));
    const name = open.length > 0 ? open[0].reportedUserName : "this user";
    ToolPool.showMessage(adminMessage,
        `Resolved ${open.length} report${open.length === 1 ? "" : "s"} about ${name}.`, "success");
}

// Story 1: DELETE /api/users/{id}. The API refuses (409) if the user still has data.
async function removeUser(userId) {
    const user = users.find(u => u.id === userId);
    const name = user ? user.name : "user " + userId;
    if (!confirm(`Permanently remove ${name}? This can't be undone.`)) {
        return;
    }
    try {
        await ToolPool.del(`/users/${userId}`); // 204 No Content
        ToolPool.showMessage(adminMessage, `${name} has been removed.`, "success");
    } catch (error) {
        if (error.status === 409) {
            // e.g. "User 3 has reports. Ban the user instead."
            ToolPool.showMessage(adminMessage, `Can't remove ${name}: ${error.message}`, "error");
            return;
        }
        if (error.status === 404) {
            ToolPool.showMessage(adminMessage, `${name} was already removed.`, "info");
            return;
        }
        throw error;
    }
}

// One click handler for every button in both tables (event delegation), so buttons keep
// working after the tables are redrawn.
async function handleTableClick(event) {
    const button = event.target.closest("button[data-action]");
    if (!button) {
        return;
    }
    const userId = Number(button.dataset.userId);
    button.disabled = true;

    try {
        switch (button.dataset.action) {
            case "ban": await setAccountStatus(userId, "BANNED"); break;
            case "unban": await setAccountStatus(userId, "ACTIVE"); break;
            case "resolve": await resolveReports(userId); break;
            case "remove": await removeUser(userId); break;
        }
    } catch (error) {
        // e.g. 404 "User 7 was not found." if someone removed them in another tab
        ToolPool.showMessage(adminMessage, error.message, "error");
    } finally {
        // Reload from the API so the tables and stats show what's really saved.
        await loadDashboard();
    }
}

reportedUsersBody.addEventListener("click", handleTableClick);
allUsersBody.addEventListener("click", handleTableClick);
reportFilter.addEventListener("change", renderReportedUsers);

loadDashboard();
