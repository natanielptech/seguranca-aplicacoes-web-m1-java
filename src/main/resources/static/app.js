const tema = document.querySelector("#tema");
const mensagem = document.querySelector("#mensagem");
const placeholder = document.querySelector("#response-placeholder");
const resultado = document.querySelector("#response-result");
const methodOutput = document.querySelector("#response-method");
const urlOutput = document.querySelector("#response-url");
const statusOutput = document.querySelector("#response-status");
const bodyOutput = document.querySelector("#response-body");
const buttons = [...document.querySelectorAll("button")];

document.querySelector("#get-example").textContent =
    `GET /api/observacao?tema=${tema.value}\nAccept: application/json`;
tema.addEventListener("change", () => {
    document.querySelector("#get-example").textContent =
        `GET /api/observacao?tema=${tema.value}\nAccept: application/json`;
});

async function enviar(url, options) {
    placeholder.textContent = "Enviando requisição...";
    placeholder.hidden = false;
    resultado.hidden = true;
    buttons.forEach((button) => { button.disabled = true; });

    try {
        const response = await fetch(url, options);
        const data = await response.json();
        methodOutput.textContent = options.method;
        urlOutput.textContent = url;
        statusOutput.textContent = response.status;
        bodyOutput.textContent = JSON.stringify(data, null, 2);
        placeholder.hidden = true;
        resultado.hidden = false;
    } catch {
        placeholder.textContent = "Não foi possível acessar a API local.";
    } finally {
        buttons.forEach((button) => { button.disabled = false; });
    }
}

document.querySelector("#get-form").addEventListener("submit", (event) => {
    event.preventDefault();
    enviar(`/api/observacao?tema=${encodeURIComponent(tema.value)}`, {
        method: "GET",
        headers: { Accept: "application/json" },
    });
});

document.querySelector("#post-form").addEventListener("submit", (event) => {
    event.preventDefault();
    enviar("/api/observacao", {
        method: "POST",
        headers: { Accept: "application/json", "Content-Type": "application/json" },
        body: JSON.stringify({ mensagem: mensagem.value }),
    });
});

document.querySelector("#invalid-get").addEventListener("click", () => {
    enviar("/api/observacao?tema=desconhecido", {
        method: "GET",
        headers: { Accept: "application/json" },
    });
});

document.querySelector("#delete-request").addEventListener("click", () => {
    enviar("/api/observacao", {
        method: "DELETE",
        headers: { Accept: "application/json" },
    });
});
