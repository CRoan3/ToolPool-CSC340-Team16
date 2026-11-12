
document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const toolId = params.get("id");

    const bookingForm = document.getElementById("booking-form");
    const bookingMessage = document.getElementById("booking-message");

    const user = ToolPool.getCurrentUser();

    let currentTool = null;

    // Check whether a tool ID was provided.
    if (!toolId) {
        document.getElementById("tool-name").textContent =
            "No tool was selected.";
        bookingForm.hidden = true;
        return;
    }

    // Load the selected tool and its owner.
    try {
        currentTool = await ToolPool.get("/tools/" + toolId);

        document.title = "ToolPool - " + currentTool.name;

        document.getElementById("tool-name").textContent =
            currentTool.name;

        document.getElementById("tool-image").src =
            ToolPool.imageFor(currentTool);

        document.getElementById("tool-category").textContent =
            currentTool.category || "Uncategorized";

        document.getElementById("tool-description").textContent =
            currentTool.description;

        document.getElementById("tool-price").textContent =
            ToolPool.formatMoney(currentTool.dailyPrice);

        document.getElementById("tool-deposit").textContent =
            ToolPool.formatMoney(currentTool.depositAmount);

        document.getElementById("tool-availability").textContent =
            currentTool.available ? "Available" : "Unavailable";

        // Load the owner's information.
        const owner = await ToolPool.get("/users/" + currentTool.ownerId);

        document.getElementById("owner-name").textContent =
            owner.name || "Not provided";

        document.getElementById("owner-location").textContent =
            owner.location || "Not provided";

        document.getElementById("owner-phone").textContent =
            owner.phoneNumber || "Not provided";

        // Prevent booking unavailable tools.
        if (!currentTool.available) {
            bookingForm.hidden = true;
            ToolPool.showMessage(
                bookingMessage,
                "This tool is not available for rental.",
                "error"
            );
        }

        // Prevent the owner from booking their own tool.
        if (user && Number(user.id) === Number(currentTool.ownerId)) {
            bookingForm.hidden = true;
            ToolPool.showMessage(
                bookingMessage,
                "You cannot book your own tool.",
                "info"
            );
        }

        if (!user) {
            bookingForm.hidden = true;
            ToolPool.showMessage(
                bookingMessage,
                "Please log in before booking this tool.",
                "info"
            );
        }

    } catch (error) {
        document.getElementById("tool-name").textContent =
            "Unable to load tool.";

        bookingForm.hidden = true;

        ToolPool.showMessage(
            bookingMessage,
            error.message,
            "error"
        );

        return;
    }

    // Submit a booking.
    bookingForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const startDate = document.getElementById("start-date").value;
        const endDate = document.getElementById("end-date").value;

        ToolPool.hideMessage(bookingMessage);

        if (!startDate || !endDate) {
            ToolPool.showMessage(
                bookingMessage,
                "Please select both dates.",
                "error"
            );
            return;
        }

        if (endDate <= startDate) {
            ToolPool.showMessage(
                bookingMessage,
                "End date must be after start date.",
                "error"
            );
            return;
        }

        try {
            const booking = await ToolPool.post("/rentals", {
                toolId: currentTool.id,
                renterId: user.id,
                startDate: startDate,
                endDate: endDate
            });

            ToolPool.showMessage(
                bookingMessage,
                "Booking successful! Rental ID: " +
                booking.id +
                " | Status: " +
                booking.status +
                " | Total Price: " +
                ToolPool.formatMoney(booking.totalPrice),
                "success"
            );

            bookingForm.reset();

        } catch (error) {
            ToolPool.showMessage(
                bookingMessage,
                error.message,
                "error"
            );
        }
    });
});
