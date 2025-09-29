document.addEventListener("DOMContentLoaded", () => {
    const forms = document.querySelectorAll("form");
    forms.forEach(form => {
        form.addEventListener("submit", async (e) => {
            e.preventDefault();

            const message = form.querySelector(".message");
            const formData = new FormData(form);
            const params = new URLSearchParams(formData);

            try {
                const response = await fetch(form.action, {
                    method: form.method || "POST",
                    body: params
                });

                if (response.ok) {
                    message.textContent = "✅ Success!";
                    message.style.color = "#a5d6a7";
                    form.reset();
                } else {
                    message.textContent = "❌ Something went wrong.";
                    message.style.color = "#e57373";
                }
            } catch (err) {
                message.textContent = "⚠️ Error connecting to server.";
                message.style.color = "#e57373";
            }
        });
    });
});
