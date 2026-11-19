package br.com.natanielptech.seguranca;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void leitorPodeConsultarMasNaoEnviar() throws Exception {
        mockMvc.perform(comoUsuario(get("/api/observacao"), "leitor", "leitor123"))
                .andExpect(status().isOk());

        mockMvc.perform(comoUsuario(post("/api/observacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\"Olá\"}"),
                        "leitor", "leitor123"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPodeConsultarEEnviar() throws Exception {
        mockMvc.perform(comoUsuario(get("/api/observacao"), "admin", "admin123"))
                .andExpect(status().isOk());

        mockMvc.perform(comoUsuario(post("/api/observacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\"Olá\"}"),
                        "admin", "admin123"))
                .andExpect(status().isCreated());
    }

    private MockHttpServletRequestBuilder comoUsuario(
            MockHttpServletRequestBuilder request, String usuario, String senha) {
        String credenciais = Base64.getEncoder().encodeToString(
                (usuario + ":" + senha).getBytes(StandardCharsets.UTF_8));
        return request.header("Authorization", "Basic " + credenciais);
    }
}
