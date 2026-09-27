# Mini Autorizador

API REST de autorização de transações de benefícios (Vale Refeição / Vale Alimentação), construída como parte do teste técnico da VR Benefícios.

## Stack

- Java 21
- Spring Boot 4.1.1 (Spring Web MVC, Spring Data MongoDB, Spring Security, Bean Validation)
- MongoDB 4.2
- Maven
- Lombok
- JUnit, Mockito, AssertJ, PITest (teste de mutação)

## Arquitetura — Clean Architecture

As dependências apontam sempre para dentro:

```
presentation  →  application  →  domain
infrastructure ─────────────────┘  (implementa as portas do domínio)
```

- **domain**: `Cartao` (POJO puro, sem framework), a porta `CartaoRepository` e as exceções de negócio.
- **application**: casos de uso (`CriarCartaoUseCase`, `ObterSaldoUseCase`, `AutorizarTransacaoUseCase`), o comando `AutorizarTransacaoCommand` e as regras de autorização (`RegraAutorizacao` + implementações).
- **presentation**: controllers REST, DTOs (com Bean Validation) e o `GlobalExceptionHandler`.
- **infrastructure**: documento MongoDB, repositório Spring Data, adaptador `CartaoRepositoryImpl`, mapper e configuração (segurança + beans).

## Como executar

1. Suba o MongoDB:
   ```bash
   docker compose up -d
   ```
2. Rode a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
   (Windows: `.\mvnw.cmd spring-boot:run`)

A aplicação sobe em `http://localhost:8080`.

## Autenticação

Todos os endpoints usam HTTP Basic: usuário `username`, senha `password`.

## Endpoints

### Criar cartão — `POST /cartoes`
Request: `{ "numeroCartao": "6549873025634501", "senha": "1234" }`
- `201` + `{ "senha": "1234", "numeroCartao": "6549873025634501" }`
- `422` (cartão já existe) + mesmo corpo
- `400` campos ausentes/em branco ou JSON malformado
- `401` sem autenticação ou credenciais inválidas

### Consultar saldo — `GET /cartoes/{numeroCartao}`
- `200` + saldo (ex.: `495.15`)
- `404` sem corpo (cartão não existe)
- `401` sem autenticação

### Autorizar transação — `POST /transacoes`
Request: `{ "numeroCartao": "6549873025634501", "senhaCartao": "1234", "valor": 10.00 }`
- `201` + `OK`
- `422` + `SALDO_INSUFICIENTE` | `SENHA_INVALIDA` | `CARTAO_INEXISTENTE`
- `400` campos ausentes, `valor` zero/negativo ou com mais de 2 casas decimais, ou JSON malformado
- `401` sem autenticação

## Testes

```bash
./mvnw test
```

Teste de mutação (PITest):

```bash
./mvnw test-compile org.pitest:pitest-maven:mutationCoverage
```

Relatório HTML em `target/pit-reports/`.

## Decisões de projeto e boas práticas

- **Clean Architecture** com inversão de dependência via porta `CartaoRepository`, mantendo o domínio livre de framework.
- **Strategy pattern** para as regras de autorização: cada regra é um `@Component` que implementa `RegraAutorizacao`; o caso de uso recebe a `List<RegraAutorizacao>` ordenada por `@Order` e as executa com `forEach`, antes do débito. Adicionar uma nova regra não altera o caso de uso.
- **Desafio "sem if"**: nenhum `if` no código de produção. Verificações de presença usam `Optional` (`findById(...).orElseThrow(...)`, `ifPresent`), condicionais viram `Optional.filter(...).orElseThrow(...)`, e o mapeamento de erros HTTP usa despacho polimórfico via `@ExceptionHandler` sobre a hierarquia `TransacaoNaoAutorizadaException`.
- **Validação de entrada** com Bean Validation (`@NotBlank`, `@NotNull`, `@Positive`): impede, por exemplo, que um `valor` negativo credite o cartão.
- **Senhas** armazenadas como hash BCrypt.
- **Teste de mutação** com PITest para garantir que os testes de fato exercitam a lógica (não apenas cobrem linhas).

## Concorrência

Duas transações simultâneas no mesmo cartão (inclusive em instâncias diferentes da aplicação) não podem aprovar mais do que o saldo. O débito é um **único update condicional atômico** no MongoDB:

```
updateFirst({ _id: <cartão>, saldo: { $gte: <valor> } }, { $inc: { saldo: -<valor> } })
```

Operações em um único documento são atômicas no MongoDB: entre duas transações disputando o último saldo, só uma casa o filtro; a outra não altera nada e recebe `422 SALDO_INSUFICIENTE`. Não há trava na JVM, então a garantia vale para várias instâncias, e não depende de replica set nem de transações multi-documento (funciona no MongoDB 4.2 standalone do `compose.yaml`).

O saldo é gravado como `Decimal128` (`spring.data.mongodb.representation.big-decimal=decimal128`) para que a comparação `$gte` seja numérica e a escala (`490.00`) seja preservada.

### Verificação manual

Com o MongoDB e a aplicação no ar (cartão novo começa com 500,00; duas transações de 400,00 ao mesmo tempo):

```bash
curl -u username:password -H 'Content-Type: application/json' \
  -d '{"numeroCartao":"1111","senha":"1234"}' localhost:8080/cartoes
for i in 1 2; do
  curl -s -u username:password -H 'Content-Type: application/json' \
    -d '{"numeroCartao":"1111","senhaCartao":"1234","valor":400.00}' \
    localhost:8080/transacoes &
done; wait
curl -u username:password localhost:8080/cartoes/1111
```

Esperado: exatamente um `OK` e um `SALDO_INSUFICIENTE`; saldo final `100.00` (nunca `-300.00`).

## Suposições

- MongoDB foi o banco escolhido.
- As credenciais Basic Auth são fixas: `username` / `password`, conforme o contrato.
- A resposta de criação retorna a senha em texto plano recebida no request (não o hash).
- Transações não são persistidas — apenas o saldo do cartão é atualizado.
- Ordem das regras de autorização: existência do cartão → senha correta → saldo suficiente.
- Requisições inválidas (campos ausentes, `valor` ≤ 0) retornam `400`, pois o contrato não define esse caso.
- A garantia de concorrência não é coberta por teste automatizado contra MongoDB real (a suíte roda sem Docker); ela é verificada pelo roteiro manual acima.
