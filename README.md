# Laboratório de Segurança de Aplicações Web — Módulo 1 (Java)

Exemplo didático para observar como o navegador troca requisições HTTP com
uma API. A aplicação usa **Java 27 e Spring Boot**, e funciona localmente sem
Vercel, banco de dados ou serviços externos. As mensagens recebidas não são
armazenadas.

## Executar

Tenha Java 27 ou superior e Maven instalados. Na raiz do projeto:

```bash
mvn spring-boot:run
```

Abra <http://localhost:8080>. Para executar os testes:

```bash
mvn test
```

## Exemplos HTTP

A página apresenta quatro ações e permite conferir método, URL, status e JSON
na área de resposta. Abra o DevTools do navegador (F12), selecione **Network**
e execute cada exemplo.

### GET válido

`GET /api/observacao?tema=http` retorna `200 OK`:

```json
{
  "tema": "http",
  "explicacao": "HTTP é o protocolo usado para trocar requisições e respostas na web."
}
```

Os temas aceitos são `http`, `api` e `seguranca`. Sem o parâmetro `tema`, a
API consulta `http`.

### POST válido

`POST /api/observacao` recebe JSON:

```json
{
  "mensagem": "Olá, mundo!"
}
```

A resposta `201 Created` devolve a mensagem e avisa que ela não foi armazenada.
São aceitas mensagens de 1 a 200 caracteres.

### Erros

- Tema desconhecido ou mensagem ausente/inválida: `400 Bad Request`.
- Método diferente de GET ou POST: `405 Method Not Allowed`, com
  `Allow: GET, POST`.
- Todas as respostas da API incluem `Cache-Control: no-store`.

## Limites de segurança

Use somente dados fictícios. Este laboratório não possui autenticação,
autorização, sessões, cookies de sessão ou persistência. A aplicação local usa
HTTP; HTTPS protege o transporte, mas não substitui controles de segurança no
servidor.
