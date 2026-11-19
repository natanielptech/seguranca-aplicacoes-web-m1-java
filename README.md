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
e execute cada exemplo. Ela também permite alternar entre dois perfis para
observar autenticação e autorização.

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

## Exemplo de autenticação e autorização

A API usa HTTP Basic e dois usuários em memória:

| Usuário | Senha | Perfil | Permissões em `/api/observacao` |
| --- | --- | --- | --- |
| `leitor` | `leitor123` | `LEITOR` | GET |
| `admin` | `admin123` | `ADMIN` | GET e POST |

Sem credenciais válidas, a API responde `401 Unauthorized`. Um usuário
autenticado sem permissão para a operação recebe `403 Forbidden`. Selecione o
usuário na página e confira a requisição no DevTools: o navegador envia as
credenciais no header `Authorization`.

## Limites de segurança

Use somente dados fictícios. Os usuários e senhas são públicos, ficam apenas
em memória e servem exclusivamente para demonstração local; não os reutilize
nem use esta configuração em produção. A autenticação usa HTTP Basic sem
sessão, e a aplicação local usa HTTP. Em produção, use HTTPS e credenciais
gerenciadas com segurança; HTTPS protege o transporte, mas não substitui
controles de segurança no servidor.
