# Data Guardian — Tech Challenge Fase 2

API REST desenvolvida para o Tech Challenge da pós-graduação em Java da FIAP. Permite cadastrar e gerenciar usuários, tipos de usuário, restaurantes e itens de cardápio, com autenticação JWT e autorização por proprietário.

**Estado atual:** CRUDs e consulta do usuário autenticado implementados. O cadastro retorna um token JWT. O login foi implementado pelo usuário e validado com testes unitários, HTTP e integração com banco H2, BCrypt e JWT reais. O profile de testes usa H2 isolado. Docker e migrations permanecem pendentes.

## Tecnologias

| Tecnologia | Versão / uso |
| --- | --- |
| Java | Java 21 para compilação, testes e execução |
| Spring Boot | 4.1.1 |
| Spring Web MVC | Controllers REST e validação |
| Spring Data JPA / Hibernate | Persistência relacional |
| Spring Security / OAuth2 Resource Server | Validação de JWT e controle de acesso |
| MySQL | Banco da aplicação |
| springdoc-openapi / Swagger UI | 3.2.0 |
| Maven Wrapper | Compilação, execução e testes |
| JUnit / Mockito / MockMvc | Testes de regras, HTTP e segurança |

As versões de Spring, Hibernate e das dependências de testes são gerenciadas pelo parent do Spring Boot no pom.xml.

## Organização e arquitetura

~~~text
src/main/java/com/api_application/phase_2_data_guardian/
├── domain/
│   ├── entity/          Entidades sem anotações JPA
│   ├── exception/       Erros de negócio
│   └── services/        Implementações dos casos de uso
├── application/
│   ├── model/          Contratos de autenticação
│   ├── repository/     Ports de persistência
│   ├── security/       Contratos de token e senha
│   └── usecase/        Interfaces dos casos de uso
└── infra/
    ├── configuration/  Beans, segurança, JWT e OpenAPI
    ├── database/       Entidades JPA, repositories, adapters e mappers
    ├── http/           Controllers, DTOs e tratamento de erros
    └── security/       Adapters de hash de senha e emissão de token
postman/                Collection, environment e instruções
docs/                   Relatório de pendências
~~~

~~~mermaid
flowchart LR
    Client[Cliente HTTP] --> Security[Spring Security]
    Security --> Controller[Controller e DTOs]
    Controller --> UseCase[Interface do caso de uso]
    UseCase --> Service[Service de domínio]
    Service --> Port[Repository port]
    Port --> Adapter[Persistence adapter e mapper]
    Adapter --> JPA[Spring Data JPA]
    JPA --> DB[(MySQL)]
~~~

Controllers dependem dos casos de uso. Services utilizam ports da aplicação, implementados pelos adapters da infraestrutura. Mappers convertem entidades de domínio e entidades JPA. A paginação usa Page e Pageable.

Relacionamentos principais: UserType 1:N User, User 1:N Restaurant e Restaurant 1:N MenuItem. Os horários são armazenados em períodos associados ao restaurante.

## Requisitos

- JDK 21, com JAVA_HOME apontando para o JDK 21.
- MySQL em execução e usuário com acesso ao banco configurado.
- Acesso ao Maven Central na primeira compilação.
- Postman atualizado para importar a collection e executar seus scripts.

Não é necessário instalar Maven separadamente: utilize o wrapper versionado.

## Configuração

As propriedades de src/main/resources/application.properties leem as variáveis abaixo.

| Variável | Padrão de desenvolvimento | Finalidade |
| --- | --- | --- |
| DB_HOST | localhost | Host do MySQL |
| DB_PORT | 3306 | Porta do MySQL |
| DB_NAME | phase_2_data_guardian | Nome do banco |
| DB_USERNAME | root | Usuário do banco |
| DB_PASSWORD | root | Senha do banco |
| JPA_DDL_AUTO | update | Estratégia de criação/atualização do schema |
| JPA_SHOW_SQL | false | Exibição de SQL |
| JPA_FORMAT_SQL | false | Formatação de SQL |
| JWT_SECRET | Chave de desenvolvimento no arquivo de propriedades | Base64 de pelo menos 32 bytes |
| JWT_ISSUER | phase-2-data-guardian | Emissor esperado do JWT |
| JWT_EXPIRATION_SECONDS | 3600 | Duração do token em segundos |
| API_DOCS_ENABLED | true | Habilita Swagger UI e OpenAPI |
| SERVER_PORT | 8080 | Porta HTTP, propriedade padrão do Spring Boot |

O arquivo .env.example é uma referência. **O Spring Boot não carrega .env automaticamente neste projeto.** Configure as variáveis no terminal ou na configuração de execução da IDE.

Exemplo no PowerShell 7, a partir da raiz do projeto:

~~~powershell
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "phase_2_data_guardian"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "sua-senha-local"
$env:JWT_SECRET = [Convert]::ToBase64String(
    [System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32)
)
$env:JWT_ISSUER = "phase-2-data-guardian"
$env:JWT_EXPIRATION_SECONDS = "3600"
$env:API_DOCS_ENABLED = "true"
.\mvnw.cmd spring-boot:run
~~~

O exemplo de geração de chave usa PowerShell 7 com .NET moderno. Em outros ambientes, gere 32 bytes aleatórios com uma ferramenta criptográfica e codifique em Base64. Configure a mesma chave em todas as instâncias; mudar a chave invalida os tokens existentes.

No Linux/macOS:

~~~bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=phase_2_data_guardian
export DB_USERNAME=root
export DB_PASSWORD='sua-senha-local'
export JWT_SECRET="$(openssl rand -base64 32)"
./mvnw spring-boot:run
~~~

A conexão usa createDatabaseIfNotExist=true. O usuário precisa de permissão para criar o banco, ou o banco deve existir previamente. Com JPA_DDL_AUTO=update, o Hibernate cria/atualiza as tabelas ao iniciar. Não há migrations neste momento.

## Preparar os tipos iniciais

O cadastro exige que CUSTOMER exista. A criação de restaurantes exige RESTAURANT_OWNER. Como o catálogo exige um proprietário autenticado para mutações, os dois tipos precisam ser inseridos antes do primeiro cadastro.

1. Inicie a aplicação com MySQL acessível para criar as tabelas.
2. Execute [postman/setup-user-types.sql](postman/setup-user-types.sql) no banco definido em DB_NAME.
3. Inicie o fluxo de cadastro no Swagger ou no Postman.

Exemplo pelo cliente MySQL, a partir da raiz do projeto:

~~~bash
mysql --host=localhost --port=3306 --user=root --password --database=phase_2_data_guardian --execute="source postman/setup-user-types.sql"
~~~

O script inclui apenas os tipos ausentes e não pressupõe valores fixos para os IDs. É um preparo manual de desenvolvimento, não uma migration. Sem esse preparo, o registro falha porque o tipo padrão não está configurado.

## Executar e compilar

| Operação | Windows PowerShell | Linux/macOS |
| --- | --- | --- |
| Executar | .\mvnw.cmd spring-boot:run | ./mvnw spring-boot:run |
| Compilar | .\mvnw.cmd compile | ./mvnw compile |
| Testar | .\mvnw.cmd test | ./mvnw test |
| Gerar JAR sem executar testes | .\mvnw.cmd package -DskipTests | ./mvnw package -DskipTests |

Execute o JAR gerado com as mesmas variáveis de ambiente:

~~~bash
java -jar target/phase-2-data-guardian-0.0.1-SNAPSHOT.jar
~~~

A aplicação responde por padrão em http://localhost:8080. Não há prefixo /api/v1.

## Swagger / OpenAPI

Com a aplicação iniciada:

- Interface: [Swagger UI](http://localhost:8080/swagger-ui.html)
- Especificação JSON: [OpenAPI](http://localhost:8080/v3/api-docs)
- Especificação YAML: [OpenAPI YAML](http://localhost:8080/v3/api-docs.yaml)

A documentação é pública; as rotas de negócio mantêm suas regras de autenticação. Para desabilitar a documentação, defina API_DOCS_ENABLED=false.

1. Execute POST /auth/register com um email ainda não cadastrado.
2. Copie o campo accessToken.
3. Clique em **Authorize**, cole apenas o token, sem o prefixo Bearer, e confirme.
4. Consulte GET /user-types, identifique o ID de RESTAURANT_OWNER e altere o próprio tipo com PUT /users/{userId}/user-type.
5. Crie restaurantes e itens do cardápio.

A especificação descreve os bodies, a paginação, os status e os erros ProblemDetail. O login está documentado como funcional e inclui a resposta 401 para credenciais inválidas.

## Autenticação

O cadastro cria um usuário CUSTOMER e retorna:

~~~json
{
  "accessToken": "<token-retornado-pela-api>",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "name": "Maria Silva",
    "email": "maria@example.com",
    "userType": { "id": 1, "name": "CUSTOMER" }
  }
}
~~~

IDs acima são ilustrativos. Use os valores retornados pelo seu banco.

Nas chamadas protegidas:

~~~http
Authorization: Bearer <accessToken>
~~~

O JWT usa o ID do usuário no subject. O ID autenticado não é recebido por X-User-Id. O token precisa ter assinatura, emissor, validade e subject numérico positivo válidos. As regras de tipo consultam o banco, portanto a alteração do próprio tipo é reconhecida mesmo com o token de cadastro.

**Login implementado e testado.** Normaliza o email, compara a senha com o hash
armazenado e consulta o tipo atual para gerar o JWT e a resposta. Cadastro, login,
consulta autenticada, credenciais inválidas e atualização do tipo foram validados
em testes de integração com H2, BCrypt e JWT reais.

## Endpoints

Exceto registro, login e documentação, todas as rotas exigem Bearer JWT.

| Método | Rota | Sucesso | Regra principal |
| --- | --- | --- | --- |
| POST | /auth/register | 201 | Cria CUSTOMER e emite JWT |
| POST | /auth/login | 200 | Credenciais válidas emitem JWT; email ou senha inválidos retornam 401 |
| GET | /auth/me | 200 | Usuário e tipo atuais |
| GET | /user-types | 200 | Lista paginada |
| GET | /user-types/{userTypeId} | 200 | Consulta por ID |
| POST | /user-types | 201 | Somente RESTAURANT_OWNER |
| PUT | /user-types/{userTypeId} | 200 | Somente RESTAURANT_OWNER; tipo em uso não pode ser renomeado |
| DELETE | /user-types/{userTypeId} | 204 | Somente RESTAURANT_OWNER; tipo em uso retorna 409 |
| GET | /users | 200 | Resumo paginado com ID, nome e tipo, para usuários autenticados |
| GET | /users/{userId} | 200 | Dados completos somente do próprio usuário, sem senha/hash |
| PUT | /users/{userId} | 200 | Atualiza apenas nome e email da própria conta |
| DELETE | /users/{userId} | 204 | Exclui a própria conta se não houver restaurantes vinculados |
| GET | /users/{userId}/user-type | 200 | Consulta do tipo associado |
| PUT | /users/{userId}/user-type | 200 | Apenas o próprio usuário |
| GET | /restaurants | 200 | Lista paginada |
| GET | /restaurants/{restaurantId} | 200 | Consulta por ID |
| POST | /restaurants | 201 | Usuário autenticado deve ser RESTAURANT_OWNER |
| PUT | /restaurants/{restaurantId} | 200 | Apenas o proprietário |
| DELETE | /restaurants/{restaurantId} | 204 | Apenas o proprietário e sem itens associados |
| GET | /restaurants/{restaurantId}/menu-items | 200 | Cardápio paginado |
| GET | /restaurants/{restaurantId}/menu-items/{menuItemId} | 200 | Item deve pertencer ao restaurante da URL |
| POST | /restaurants/{restaurantId}/menu-items | 201 | Apenas o proprietário |
| PUT | /restaurants/{restaurantId}/menu-items/{menuItemId} | 200 | Apenas o proprietário e com vínculo correto |
| DELETE | /restaurants/{restaurantId}/menu-items/{menuItemId} | 204 | Apenas o proprietário e com vínculo correto |

Criações retornam Location; exclusões bem-sucedidas não possuem body.

O CRUD de usuários reutiliza POST /auth/register para criação. Não há um segundo
endpoint de cadastro em POST /users. A listagem de usuários expõe apenas ID, nome
e tipo; email e datas ficam na consulta do próprio perfil. A ordenação do diretório
aceita somente id e name.

### Exemplos de bodies

Cadastro — senha de 8 a 64 caracteres, com maiúscula, minúscula, número, caractere especial e sem espaços:

~~~json
{
  "name": "Maria Silva",
  "email": "maria@example.com",
  "password": "Guardian@123"
}
~~~

Atualizar o próprio usuário com PUT /users/{userId}:

~~~json
{
  "name": "Maria Silva atualizada",
  "email": "maria.atualizada@example.com"
}
~~~

Este endpoint preserva senha, tipo, ID e data de criação. O tipo continua sendo
alterado pela rota específica de associação. Email já usado por outra conta
retorna 409; tentativa de consultar, alterar ou excluir conta de outra pessoa
retorna 403. Excluir conta com restaurantes também retorna 409.

Criar/atualizar tipo:

~~~json
{ "name": "DELIVERY_PARTNER" }
~~~

Alterar o próprio tipo:

~~~json
{ "userTypeId": 2 }
~~~

Criar/atualizar restaurante — ownerId é obtido do JWT e não é enviado no body:

~~~json
{
  "name": "Restaurante Guardian",
  "address": {
    "street": "Rua das Flores",
    "number": "100",
    "complement": "Loja 1",
    "neighborhood": "Centro",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01001-000"
  },
  "cuisineType": "Brasileira",
  "openingHours": {
    "monday": [
      { "opensAt": "08:00:00", "closesAt": "12:00:00" },
      { "opensAt": "12:00:00", "closesAt": "18:00:00" }
    ],
    "tuesday": [],
    "wednesday": [],
    "thursday": [],
    "friday": [],
    "saturday": [],
    "sunday": []
  }
}
~~~

Criar/atualizar item — 2590 representa R$ 25,90:

~~~json
{
  "name": "Prato do dia",
  "description": "Arroz, feijão e legumes",
  "priceInCents": 2590,
  "dineInOnly": false,
  "imagePath": "/images/prato-do-dia.jpg"
}
~~~

imagePath armazena uma referência à imagem. Não existe endpoint de upload neste projeto.

### Paginação

~~~http
GET /restaurants?page=0&size=10&sort=name,asc
GET /restaurants/1/menu-items?page=0&size=10&sort=priceInCents,desc
~~~

- A primeira página é 0.
- O tamanho padrão dos controllers é 10.
- A ordenação padrão é name,asc.
- As respostas são páginas com content e metadados de paginação.

## Regras de negócio

- IDs usam Long; preços usam Long em centavos.
- Usuários alteram apenas o próprio tipo entre CUSTOMER e RESTAURANT_OWNER. Não há papel ADMIN.
- Tipos personalizados podem existir no catálogo, mas não podem ser autoatribuídos.
- Quem possui restaurantes não pode mudar para CUSTOMER; a API retorna 409.
- Somente RESTAURANT_OWNER gerencia o catálogo de tipos.
- Tipo associado a usuários não pode ser excluído nem renomeado para outro nome.
- O proprietário do restaurante precisa existir e ter tipo RESTAURANT_OWNER.
- Horário de abertura deve anteceder o fechamento no mesmo dia. Períodos sobrepostos são rejeitados; períodos adjacentes são aceitos.
- Todos os dias devem estar presentes no body de horários. Lista vazia representa dia fechado. Intervalos que cruzem meia-noite precisam ser divididos entre os dias.
- Restaurante com itens não pode ser excluído. Remova os itens primeiro.
- Consultar, atualizar ou excluir item pela URL de outro restaurante retorna 404.
- Apenas o proprietário pode criar, atualizar ou excluir itens do próprio cardápio.

## Respostas de erro

O formato é application/problem+json, baseado em ProblemDetail.

| Status | Situação |
| --- | --- |
| 400 | Body, parâmetros, preço ou horários inválidos |
| 401 | Token ausente, inválido ou expirado |
| 403 | Usuário sem autorização para a operação |
| 404 | Recurso não encontrado, inclusive item fora do restaurante informado |
| 409 | Email/nome duplicado, tipo em uso, restaurante com itens ou conflito de integridade |

~~~json
{
  "type": "about:blank",
  "title": "Resource conflict",
  "status": 409,
  "detail": "User type is associated with users",
  "instance": "/user-types/1",
  "code": "RESOURCE_CONFLICT"
}
~~~

Erros de validação podem incluir errors, com mensagens por campo. O campo code aparece nos handlers específicos; erros gerais tratados pelo Spring podem não incluí-lo.

## Postman

Os arquivos estão separados em [postman/](postman/README.md):

- data-guardian.postman_collection.json
- local.postman_environment.json
- setup-user-types.sql

Importe a collection e o environment, selecione **Data Guardian — Local** e execute as pastas 00 a 07 em ordem. O fluxo cadastra dois usuários, captura tokens e IDs, testa os CRUDs e cenários negativos e remove itens, restaurantes, tipo temporário e as duas contas criadas. A exclusão de conta ocorre somente após remover seus restaurantes.

O login faz parte da preparação, captura o JWT e possui cenários negativos de email inexistente e senha incorreta. Os detalhes de execução e as variáveis estão no README da pasta.

## Testes e cobertura

Os testes estão organizados por camada e recurso em src/test/java/com/api_application/phase_2_data_guardian:

| Pasta | Responsabilidade |
| --- | --- |
| services | Regras e casos de uso de Auth, User, UserType, Restaurant e MenuItem |
| controllers | Contratos HTTP, validação, status e respostas sem senha/hash |
| mappers | Conversão entre domínio e persistência |
| integration | Fluxos reais com H2; registro/login/JWT; persistência; Swagger |
| security | BCrypt, emissão/validação JWT e cadeia de segurança |
| support | Preparação compartilhada dos testes de persistência |

### Executar toda a suíte

~~~powershell
.\mvnw.cmd clean verify
~~~

No Linux/macOS, use ./mvnw clean verify. É necessário JAVA_HOME válido e acesso ao
Maven Central na primeira execução. O profile test usa H2 em memória em modo MySQL
e os testes que precisam dele o ativam explicitamente; não usam o MySQL local.

O comando verify executa os testes, gera o JAR e verifica o mínimo de **80% de
cobertura de linhas** sobre o conjunto completo das classes de produção, sem
exclusões configuradas. O comando .\mvnw.cmd test executa a suíte e gera os
relatórios, mas o limite mínimo é verificado na fase verify.

- Resultado registrado: **174 testes, zero falhas, zero erros e zero ignorados**.
- Cobertura de linhas: **92,45%** (1053 de 1139).
- Cobertura de decisões: **90,00%** (144 de 160).
- Cobertura de instruções: **94,21%** (4363 de 4631).
- Relatório visual: target/site/jacoco/index.html.
- Relatórios por teste: target/surefire-reports/.
- Relatórios de cobertura para ferramentas: target/site/jacoco/jacoco.xml e jacoco.csv.

O Maven configura explicitamente o agente do Mockito junto ao JaCoCo. Não é mais
necessário informar o caminho do Mockito em -DargLine para executar a suíte.
Ao executar testes diretamente pela IDE, a configuração de agentes é a da IDE.

Os testes escritos pelo usuário em AuthServiceTest foram preservados. Foi
corrigido o mock de busca por email no cenário duplicado e adicionados casos de
normalização, tipo padrão ausente, falha ao salvar, login válido, senha incorreta,
email inexistente, tipo associado e tipo atualizado. A integração usa os services,
adapters, banco H2, BCrypt, emissão/validação JWT e filtros reais, sem simular os
casos de uso nesse fluxo.

Swagger também é exercitado pela suíte. A collection agora possui **53 requisições**,
com login ativo e captura de token; schema Postman v2.1, scripts e bodies foram
validados. A collection não foi executada contra MySQL/Compose nesta rodada.

H2 não comprova equivalência completa ao MySQL real; essa validação permanece pendente.

## Pendências de entrega

O checklist detalhado está em [docs/PHASE_2_REMAINING_TASKS.html](docs/PHASE_2_REMAINING_TASKS.html).

- Executar os fluxos e a collection também contra a aplicação com MySQL real/Compose.
- Validar os cenários de persistência também contra MySQL real/Compose; H2 e contextLoads já estão configurados e aprovados.
- Ampliar a cobertura conforme novos requisitos; a suíte atual e o limite JaCoCo de 80% já estão aprovados.
- Adicionar migrations e dados iniciais reproduzíveis.
- Criar e validar Dockerfile e Docker Compose.
- Executar a collection contra a aplicação com banco e, depois, contra o Compose.
- Confirmar repositório público e execução a partir de clone limpo.
- Informar integrantes e links finais da entrega; preparar o vídeo.

Docker/Compose e migrations permanecem pendentes. A comprovação de cobertura está disponível nos relatórios JaCoCo gerados por verify.

## Referências

- [Documentação do Spring Boot](https://docs.spring.io/spring-boot/)
- [Documentação e compatibilidade do springdoc-openapi](https://springdoc.org/)
- [Formato de collection Postman v2.1](https://schema.getpostman.com/json/collection/v2.1.0/docs/index.html)