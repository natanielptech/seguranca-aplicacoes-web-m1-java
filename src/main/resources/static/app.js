const tema = document.querySelector("#tema");
const mensagem = document.querySelector("#mensagem");
const usuario = document.querySelector("#usuario");
const senha = document.querySelector("#senha");
const senhasDemo = { leitor: "leitor123", admin: "admin123" };
const placeholder = document.querySelector("#response-placeholder");
const resultado = document.querySelector("#response-result");
const methodOutput = document.querySelector("#response-method");
const urlOutput = document.querySelector("#response-url");
const statusOutput = document.querySelector("#response-status");
const bodyOutput = document.querySelector("#response-body");
const buttons = [...document.querySelectorAll("button")];

usuario.addEventListener("change", () => {
    senha.value = senhasDemo[usuario.value];
});

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
        const authorization = `Basic ${btoa(`${usuario.value}:${senha.value}`)}`;
        const response = await fetch(url, {
            ...options,
            headers: { ...options.headers, Authorization: authorization },
        });
        const responseBody = await response.text();
        let data;
        try {
            data = responseBody ? JSON.parse(responseBody) : { status: response.statusText };
        } catch {
            data = { resposta: responseBody };
        }
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
