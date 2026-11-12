const messageBox = document.getElementById("message");
const toolsContainer = document.getElementById("tools-container");

function showMessage(message, type) {
    messageBox.textContent = message;
    messageBox.className = "message " + type;
    messageBox.hidden = false;
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[character]);
}

function money(value) {
    return "$" + Number(value).toFixed(2);
}

async function loadMyTools() {
    const user = ToolPool.getCurrentUser();

    if (!user || !user.id) {
        toolsContainer.innerHTML =
            '<p>Please <a href="login.html">log in</a> to view your tools.</p>';
        return;
    }

    try {
        const tools = await ToolPool.get("/tools/owner/" + user.id);

        if (!tools.length) {
            toolsContainer.innerHTML = "<p>You have not created any tool listings yet.</p>";
            return;
        }

        toolsContainer.innerHTML = tools.map(tool => `
            <article class="listing-card my-tool-card">
                <div class="listing-info">
                    <h3>${escapeHtml(tool.name)}</h3>
                    <p class="category">${escapeHtml(tool.category || "Uncategorized")}</p>
                    <p class="listing-description">${escapeHtml(tool.description)}</p>
                    <p><strong>Daily price:</strong> ${money(tool.dailyPrice)}</p>
                    <p><strong>Deposit:</strong> ${money(tool.depositAmount)}</p>
                    <p><strong>Status:</strong>
                        ${tool.available ? "Listed / Available" : "Unlisted"}
                    </p>

                    <div class="my-tool-actions">
                        <button type="button" class="view-button"
                            data-action="edit" data-id="${tool.id}">
                            Edit
                        </button>
                        ${tool.available ? `
                            <button type="button" class="remove-button"
                                data-action="unlist" data-id="${tool.id}">
                                Unlist
                            </button>
                        ` : ""}
                    </div>

                    <form class="edit-tool-form" data-id="${tool.id}" hidden>
                        <label>Tool name</label>
                        <input name="name" maxlength="100" required
                            value="${escapeHtml(tool.name)}">

                        <label>Description</label>
                        <textarea name="description" required>${escapeHtml(tool.description)}</textarea>

                        <label>Category</label>
                        <input name="category" maxlength="100"
                            value="${escapeHtml(tool.category || "")}">

                        <label>Daily price ($)</label>
                        <input name="dailyPrice" type="number" min="0.01"
                            step="0.01" required value="${tool.dailyPrice}">

                        <label>Deposit ($)</label>
                        <input name="depositAmount" type="number" min="0"
                            step="0.01" value="${tool.depositAmount ?? 0}">

                        <button type="submit">Save Changes</button>
                        <button type="button" class="view-button"
                            data-action="cancel">Cancel</button>
                    </form>
                </div>
            </article>
        `).join("");
    } catch (error) {
        showMessage(error.message || "Could not load your tools.", "error");
        toolsContainer.innerHTML = "";
    }
}

toolsContainer.addEventListener("click", async event => {
    const button = event.target.closest("button[data-action]");
    if (!button) return;

    const action = button.dataset.action;
    const card = button.closest(".my-tool-card");

    if (action === "edit") {
        card.querySelector(".edit-tool-form").hidden = false;
        return;
    }

    if (action === "cancel") {
        card.querySelector(".edit-tool-form").hidden = true;
        return;
    }

    if (action === "unlist") {
        const toolId = button.dataset.id;

        if (!confirm("Are you sure you want to unlist this tool?")) return;

        try {
            await ToolPool.patch("/tools/" + toolId + "/unlist");
            showMessage("Tool successfully unlisted.", "success");
            await loadMyTools();
        } catch (error) {
            showMessage(error.message || "Could not unlist this tool.", "error");
        }
    }
});

toolsContainer.addEventListener("submit", async event => {
    const form = event.target.closest(".edit-tool-form");
    if (!form) return;

    event.preventDefault();

    const formData = new FormData(form);
    const toolId = form.dataset.id;

    const updatedTool = {
        name: formData.get("name").trim(),
        description: formData.get("description").trim(),
        category: formData.get("category").trim(),
        dailyPrice: Number(formData.get("dailyPrice")),
        depositAmount: Number(formData.get("depositAmount"))
    };

    if (!updatedTool.name || !updatedTool.description ||
        updatedTool.dailyPrice <= 0 || updatedTool.depositAmount < 0) {
        showMessage("Enter a name, description, positive daily price, and non-negative deposit.", "error");
        return;
    }

    try {
        await ToolPool.put("/tools/" + toolId, updatedTool);
        showMessage("Tool successfully updated.", "success");
        await loadMyTools();
    } catch (error) {
        showMessage(error.message || "Could not update this tool.", "error");
    }
});

loadMyTools();
