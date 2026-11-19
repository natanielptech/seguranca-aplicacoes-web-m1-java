package br.com.natanielptech.seguranca;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
class AutenticacaoAutorizacaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exigeAutenticacao() throws Exception {
        mockMvc.perform(get("/api/observacao"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(comoUsuario(get("/api/observacao"), "leitor", "senha-incorreta"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void disponibilizaTokenCsrfParaCliente() throws Exception {
        mockMvc.perform(get("/csrf"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("XSRF-TOKEN"))
                .andExpect(jsonPath("$.headerName").value("X-XSRF-TOKEN"));
    }

    @Test
    void leitorPodeConsultarMasNaoEnviar() throws Exception {
        mockMvc.perform(comoUsuario(get("/api/observacao"), "leitor", "leitor123"))
                .andExpect(status().isOk());

        mockMvc.perform(comoUsuario(post("/api/observacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\"Olá\"}"),
                        "leitor", "leitor123", true))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPodeConsultarEEnviar() throws Exception {
        mockMvc.perform(comoUsuario(get("/api/observacao"), "admin", "admin123"))
                .andExpect(status().isOk());

        mockMvc.perform(comoUsuario(post("/api/observacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\"Olá\"}"),
                        "admin", "admin123", true))
                .andExpect(status().isCreated());

        mockMvc.perform(comoUsuario(post("/api/observacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\"Olá\"}"),
                        "admin", "admin123"))
                .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder comoUsuario(
            MockHttpServletRequestBuilder request, String usuario, String senha) {
        return comoUsuario(request, usuario, senha, false);
    }

    private MockHttpServletRequestBuilder comoUsuario(
            MockHttpServletRequestBuilder request, String usuario, String senha, boolean incluirCsrf) {
        String credenciais = Base64.getEncoder().encodeToString(
                (usuario + ":" + senha).getBytes(StandardCharsets.UTF_8));
        request.header("Authorization", "Basic " + credenciais);
        if (incluirCsrf) {
            request.cookie(new Cookie("XSRF-TOKEN", "test-csrf-token"));
            request.header("X-XSRF-TOKEN", "test-csrf-token");
        }
        return request;
    }
}
