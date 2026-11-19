package br.com.natanielptech.seguranca;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/observacao")
public class ObservacaoController {

    private static final Map<String, String> TEMAS = Map.of(
            "http", "HTTP é o protocolo usado para trocar requisições e respostas na web.",
            "api", "Uma API permite que aplicações troquem informações por meio de regras definidas.",
            "seguranca", "Validação, autenticação e autorização devem ser verificadas no servidor."
    );

    @RequestMapping
    public ResponseEntity<Map<String, String>> observar(
            @RequestParam(required = false) String tema,
            @RequestBody(required = false) Map<String, Object> body,
            HttpServletRequest request) {
        String method = request.getMethod();

        if ("GET".equals(method)) {
            return consultarTema(tema);
        }
        if ("POST".equals(method)) {
            return receberMensagem(body);
        }

        return resposta(HttpStatus.METHOD_NOT_ALLOWED)
                .header("Allow", "GET, POST")
                .body(Map.of("erro", "Método não permitido."));
    }

    private ResponseEntity<Map<String, String>> consultarTema(String tema) {
        String temaSolicitado = tema == null ? "http" : tema;
        String explicacao = TEMAS.get(temaSolicitado);
        if (explicacao == null) {
            return resposta(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Tema inválido. Escolha http, api ou seguranca."));
        }

        return resposta(HttpStatus.OK).body(Map.of("tema", temaSolicitado, "explicacao", explicacao));
    }

    private ResponseEntity<Map<String, String>> receberMensagem(Map<String, Object> body) {
        Object valor = body == null ? null : body.get("mensagem");
        if (!(valor instanceof String mensagem) || mensagem.isBlank() || mensagem.length() > 200) {
            return resposta(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", "Informe uma mensagem de 1 a 200 caracteres."));
        }

        return resposta(HttpStatus.CREATED).body(Map.of(
                "mensagem", mensagem.trim(),
                "aviso", "Exemplo fictício: a mensagem não foi armazenada."
        ));
    }

    private ResponseEntity.BodyBuilder resposta(HttpStatus status) {
        return ResponseEntity.status(status).header("Cache-Control", "no-store");
    }
}
