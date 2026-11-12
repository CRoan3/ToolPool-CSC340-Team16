// ===== Tool Pool: tool-details.js =====
// Covers user story 2 (book a tool) and user story 3 (edit / unlist a tool).
//
// !! If your JSON field names differ, change them ONLY in the FIELD map below.
const FIELD = {
  name: "name",
  description: "description",
  price: "dailyPrice",
  location: "location",
  ownerId: "ownerId",
  listed: "listed",          // boolean. If you use a status string, edit isListed()
  ownerName: "name"          // field on the user object
};

// Logged-in user: read from localStorage (set by your login page).
// Fallback so the page still works in a demo: ?userId=1 in the URL.
function getCurrentUserId() {
  const fromUrl = new URLSearchParams(window.location.search).get("userId");
  if (fromUrl) localStorage.setItem("userId", fromUrl);
  const id = localStorage.getItem("userId");
  return id ? Number(id) : null;
}

const toolId = new URLSearchParams(window.location.search).get("id");
const currentUserId = getCurrentUserId();
let currentTool = null;

const $ = (id) => document.getElementById(id);

function show(el, text, type) {
  el.textContent = text;
  el.className = "message " + type;
}

function isListed(tool) {
  return tool[FIELD.listed] === true;
}

// Turn any error response into a readable sentence.
async function readError(response) {
  let detail = "";
  try {
    const body = await response.json();
    detail = body.message || body.error || body.detail || "";
  } catch (e) { /* body was not JSON */ }

  if (response.status === 404) return detail || "Not found: that tool or user does not exist.";
  if (response.status === 409) return detail || "This tool is unlisted and cannot be booked.";
  if (response.status === 400) return detail || "Invalid request. Check the dates (end must be after start) and that you are not booking your own tool.";
  return detail || "Something went wrong (status " + response.status + ").";
}

// ---------- Load tool + owner ----------
async function loadPage() {
  if (!toolId) {
    show($("pageMessage"), "No tool selected. Open this page from the listings.", "error");
    return;
  }
  try {
    const toolRes = await fetch("/api/tools/" + encodeURIComponent(toolId));
    if (!toolRes.ok) {
      show($("pageMessage"), await readError(toolRes), "error");
      return;
    }
    currentTool = await toolRes.json();
    renderTool(currentTool);

    // Owner details
    const ownerRes = await fetch("/api/users/" + currentTool[FIELD.ownerId]);
    $("ownerName").textContent = ownerRes.ok
      ? (await ownerRes.json())[FIELD.ownerName]
      : "Unknown owner";

    // Decide which section to show
    const isOwner = currentUserId !== null && currentUserId === currentTool[FIELD.ownerId];
    if (isOwner) {
      fillEditForm(currentTool);
      $("ownerCard").classList.remove("hidden");
    } else {
      $("bookingCard").classList.remove("hidden");
    }
  } catch (err) {
    show($("pageMessage"), "Cannot reach the server. Is the application running?", "error");
  }
}

function renderTool(tool) {
  $("toolName").textContent = tool[FIELD.name];
  $("toolDescription").textContent = tool[FIELD.description] || "";
  $("toolPrice").textContent = tool[FIELD.price];
  $("toolLocation").textContent = tool[FIELD.location] || "";
  const listed = isListed(tool);
  $("toolStatus").textContent = listed ? "Listed" : "Unlisted";
  $("toolStatus").className = "badge " + (listed ? "listed" : "unlisted");
  $("toolCard").classList.remove("hidden");
  $("unlistBtn").disabled = !listed;
}

function fillEditForm(tool) {
  $("editName").value = tool[FIELD.name] || "";
  $("editDescription").value = tool[FIELD.description] || "";
  $("editPrice").value = tool[FIELD.price] ?? "";
  $("editLocation").value = tool[FIELD.location] || "";
}

// ---------- User story 2: book the tool ----------
$("bookingForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const msg = $("bookingMessage");

  if (currentUserId === null) {
    show(msg, "Please log in before booking.", "error");
    return;
  }
  const startDate = $("startDate").value;
  const endDate = $("endDate").value;
  if (endDate < startDate) {
    show(msg, "The end date must be on or after the start date.", "error");
    return;
  }

  try {
    const res = await fetch("/api/rentals", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        toolId: Number(toolId),
        renterId: currentUserId,
        startDate: startDate,
        endDate: endDate
      })
    });
    if (res.status === 201) {
      const rental = await res.json();
      show(msg, "Booking confirmed! Total price: $" + rental.totalPrice, "success");
    } else {
      show(msg, await readError(res), "error");
    }
  } catch (err) {
    show(msg, "Cannot reach the server. Is the application running?", "error");
  }
});

// ---------- User story 3: edit listing (PUT) ----------
$("editForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const msg = $("ownerMessage");

  // Send the whole tool back with the edited fields changed.
  const updated = { ...currentTool };
  updated[FIELD.name] = $("editName").value.trim();
  updated[FIELD.description] = $("editDescription").value.trim();
  updated[FIELD.price] = Number($("editPrice").value);
  updated[FIELD.location] = $("editLocation").value.trim();

  try {
    const res = await fetch("/api/tools/" + encodeURIComponent(toolId), {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(updated)
    });
    if (res.ok) {
      currentTool = await res.json();
      renderTool(currentTool);
      show(msg, "Listing updated.", "success");
    } else {
      show(msg, await readError(res), "error");
    }
  } catch (err) {
    show(msg, "Cannot reach the server. Is the application running?", "error");
  }
});

// ---------- User story 3: unlist (PATCH) ----------
$("unlistBtn").addEventListener("click", async () => {
  const msg = $("ownerMessage");
  if (!confirm("Unlist this tool? Renters will no longer see it.")) return;

  try {
    const res = await fetch("/api/tools/" + encodeURIComponent(toolId) + "/unlist", {
      method: "PATCH"
    });
    if (res.ok) {
      // Reload from the database so the page shows the saved state.
      const fresh = await fetch("/api/tools/" + encodeURIComponent(toolId));
      if (fresh.ok) {
        currentTool = await fresh.json();
        renderTool(currentTool);
      }
      show(msg, "Tool unlisted. It no longer appears in the listings.", "success");
    } else {
      show(msg, await readError(res), "error");
    }
  } catch (err) {
    show(msg, "Cannot reach the server. Is the application running?", "error");
  }
});

loadPage();
