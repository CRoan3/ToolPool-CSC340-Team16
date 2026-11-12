// Listings page: shows every available tool from GET /api/tools.
// Unlisted tools are not returned by the API, so they disappear from this page.

const grid = document.getElementById("listing-grid");
const messageBox = document.getElementById("message");

async function loadListings() {
    try {
        const tools = await ToolPool.get("/tools");

        if (!tools.length) {
            grid.innerHTML = "<p>No tools are listed right now. Check back soon!</p>";
            return;
        }

        grid.innerHTML = tools.map(tool => `
            <div class="listing-card">
                <div class="listing-image">
                    <img src="${ToolPool.imageFor(tool)}" alt="${ToolPool.escapeHtml(tool.name)}">
                </div>
                <div class="listing-info">
                    <h3>${ToolPool.escapeHtml(tool.name)}</h3>
                    <p class="category">${ToolPool.escapeHtml(tool.category || "Uncategorized")}</p>
                    <p class="listing-description">${ToolPool.escapeHtml(tool.description)}</p>
                    <p class="availability">
                        <strong>Daily price:</strong> ${ToolPool.formatMoney(tool.dailyPrice)}
                    </p>
                    <div class="listing-bottom">
                        <a href="tool-details.html?id=${tool.id}" class="listing-button">View Listing</a>
                    </div>
                </div>
            </div>
        `).join("");
    } catch (error) {
        grid.innerHTML = "";
        ToolPool.showMessage(messageBox, error.message, "error");
    }
}

loadListings();
