package br.com.natanielptech.seguranca;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

@SpringBootTest
@AutoConfigureMockMvc
class ObservacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getConsultaTemaPadraoETemasConhecidos() throws Exception {
        mockMvc.perform(comoAdmin(get("/api/observacao")))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.tema").value("http"));

        mockMvc.perform(comoAdmin(get("/api/observacao").param("tema", "seguranca")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tema").value("seguranca"));
    }

    @Test
    void getRejeitaTemaDesconhecido() throws Exception {
        mockMvc.perform(comoAdmin(get("/api/observacao").param("tema", "desconhecido")))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.erro").value(containsString("Tema inválido")));
    }

    @Test
    void postValidaMensagemEDevolveSemArmazenar() throws Exception {
        mockMvc.perform(comoAdmin(post("/api/observacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\" Olá \"}")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.mensagem").value("Olá"))
                .andExpect(jsonPath("$.aviso").value(containsString("não foi armazenada")));

        mockMvc.perform(comoAdmin(post("/api/observacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\"  \"}")))
                .andExpect(status().isBadRequest());

        mockMvc.perform(comoAdmin(post("/api/observacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\"" + "a".repeat(201) + "\"}")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void metodoNaoPermitidoRetorna405EAllow() throws Exception {
        mockMvc.perform(comoAdmin(delete("/api/observacao")))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Allow", "GET, POST"))
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.erro").value("Método não permitido."));
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder comoAdmin(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request) {
        String credentials = Base64.getEncoder().encodeToString(
                "admin:admin123".getBytes(StandardCharsets.UTF_8));
        return request
                .header("Authorization", "Basic " + credentials)
                .cookie(new Cookie("XSRF-TOKEN", "test-csrf-token"))
                .header("X-XSRF-TOKEN", "test-csrf-token");
    }
}
