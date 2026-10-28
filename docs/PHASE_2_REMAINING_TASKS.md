# Relatório de pendências — Tech Challenge Fase 2

Projeto auditado: `C:\projects\Java\fiap\phase-2-data-guardian` Última atualização: 27/10/2026

## Resumo atual

Os CRUDs obrigatórios de tipos de usuário, associação de usuários, restaurantes e itens de cardápio já possuem controllers, casos de uso, services, repositories, adapters e mappers.

As listagens utilizam `Page` e `Pageable`. A aplicação compila com sucesso. A autenticação JWT está parcialmente implementada: emissão e validação do token foram estruturadas, a consulta completa do usuário autenticado foi concluída. O login foi implementado pelo usuário; sua validação fica sob responsabilidade dele.

O projeto ainda não está pronto para entrega. As principais pendências são: migrations e dados iniciais reproduzíveis, Docker Compose, testes automatizados com cobertura mínima de 80%, execução ponta a ponta da collection, repositório público confirmado e vídeo.

## O que falta — ordem de prioridade

README, Swagger e arquivos do Postman já foram criados. A compilação passou. Validação mais recente: 114 testes dos módulos de negócio passaram após o CRUD de usuários, incluindo contextLoads com H2 isolado. Os 57 testes acrescentados anteriormente foram reorganizados por camada/recurso; um caso misto foi dividido em dois, preservando todas as asserções. Os testes ligados a AuthUseCase, login e segurança não foram criados nem executados nesta rodada; ficam com o usuário, conforme combinado.

1. **Banco local:** configurar credenciais válidas para o MySQL e confirmar que a aplicação inicia. Executar postman/setup-user-types.sql no banco correto; o script existe, mas não foi executado nesta sessão.
2. **Validação MySQL:** o profile test com H2 em memória foi criado e contextLoads passou. Ainda é necessário validar os cenários contra MySQL real/Compose; H2 não comprova equivalência completa ao MySQL.
3. **Cobertura:** testes de negócio, controllers, mappers, repositories, FKs, relacionamentos e paginação foram ampliados e passaram com H2. Adicionar JaCoCo, medir a cobertura e completar os cenários faltantes até pelo menos 80%.
4. **Banco reproduzível:** criar migrations e dados iniciais versionados; validar FKs, índices, horários, orphanRemoval e timestamps. O SQL manual de preparo não substitui esse fluxo.
5. **Docker:** criar Dockerfile, .dockerignore e Compose com aplicação/MySQL, volume, variáveis e healthcheck; validar inicialização do zero e documentar os comandos.
6. **Postman ponta a ponta:** executar as 51 requisições, respeitando o login ignorado por padrão, contra a API com MySQL e depois contra Compose; conferir captura de tokens/IDs, cenários negativos e limpeza.
7. **Entrega e repositório:** completar integrantes e links finais do README, revisar arquivos/segredos, preparar commits somente quando solicitados, confirmar repositório público e validar um clone limpo.
8. **Vídeo:** preparar roteiro, gravar aproximadamente cinco minutos demonstrando arquitetura, execução, CRUDs, erros, testes e cobertura; incluir o link na entrega.

### Autenticação — validação reservada ao usuário

- Login implementado e corrigido pelo usuário. A leitura confirmou a busca do UserType associado e o preenchimento dos campos corretos. Os testes de AuthUseCase e componentes associados ficam com o usuário. Os rótulos antigos de login pendente no Swagger/Postman ainda precisam ser atualizados após essa validação.

### Decisão e melhoria separadas da entrega principal

- Refatoração opcional: extrair mappers HTTP nos demais controllers; o cardápio já utiliza MenuItemResponseMapper.

## Status dos entregáveis obrigatórios

| Entregável | Status | Principal pendência |
| --- | --- | --- |
| Funcionalidades | Quase concluído | Login implementado pelo usuário; validação de autenticação com o usuário |
| Qualidade do código | Parcial | Medir cobertura, completar cenários faltantes e validar no MySQL; mappers HTTP são melhoria opcional |
| Documentação | Parcial | README e Swagger disponíveis; informar integrantes e links finais |
| Collection | Criada; execução com banco pendente | 51 requisições; schema e scripts validados; falta executar contra API/MySQL |
| Docker Compose | Ausente | Criar Dockerfile e Compose para aplicação e MySQL |
| Repositório aberto | Não confirmado | Publicar e validar um clone limpo |
| Clean Architecture | Implementado | Revisar dependências e manter as fronteiras entre camadas |
| Testes e cobertura de 80% | Parcial | 114 testes de negócio passam com H2; faltam medir cobertura/JaCoCo e validar MySQL |
| Vídeo | Ausente | Gravar demonstração de aproximadamente cinco minutos |

## Funcionalidades

### Tipos de usuário

- [x] Criar UserTypeUseCase, UserTypeService e configuração do bean.

- [x] Implementar POST, GET paginado, GET por ID, PUT e DELETE.

- [x] Impedir nomes duplicados ignorando maiúsculas e minúsculas.

- [x] Retornar 409 ao excluir um tipo associado a usuários.

- [x] Definir quais tipos podem criar, atualizar ou excluir outros tipos.

### Associação usuário/tipo

- [x] Criar UserUseCase e UserService.

- [x] Implementar PUT /users/{userId}/user-type.

- [x] Implementar GET /users/{userId}/user-type.

- [x] Validar a existência do usuário e do tipo.

- [x] Restringir quem pode alterar o tipo de outro usuário.

- [x] Implementar CRUD completo de usuários: cadastro existente, listagem paginada, consulta, atualização e exclusão.

### Restaurantes

- [x] Criar RestaurantUseCase, RestaurantService e configuração do bean.

- [x] Implementar CRUD completo.

- [x] Implementar listagem com Page e Pageable.

- [x] Obter o proprietário pelo subject do JWT.

- [x] Impedir atualização e exclusão por usuários que não sejam proprietários.

- [x] Validar se o proprietário possui o tipo RESTAURANT_OWNER.

- [x] Validar horários inválidos e períodos sobrepostos.

- [x] Definir a exclusão de restaurantes que possuem itens associados.

### Itens do cardápio

- [x] Criar item.

- [x] Listar por restaurante com Page e Pageable.

- [x] Consultar item por ID.

- [x] Atualizar item utilizando o menuItemId da URL.

- [x] Excluir item.

- [x] Verificar se o usuário autenticado é proprietário do restaurante.

- [x] Buscar pelo menuItemId e restaurantId juntos.

- [x] Garantir que o item consultado pertence ao restaurantId da URL.

- [x] Extrair os mapeamentos de resposta do cardápio para infra/http/mapper/MenuItemResponseMapper.

- [x] Remover o findAll global caso ele não seja necessário.

### Contrato HTTP

- [x] Manter as rotas atuais sem prefixo /api/v1, conforme decisão documentada.

- [x] Retornar 201 e Location nas criações.

- [x] Retornar 200 nas consultas e atualizações.

- [x] Retornar 204 nas exclusões.

- [x] Manter IDs dos recursos nas respostas.

- [x] Validar e padronizar 400, 401, 403, 404 e 409.

- [x] Escolher entre ProblemDetail e ApiErrorResponse.

- [x] Documentar page, size, sort e que a primeira página é zero.

## Banco de dados

- [x] Configurar conexão por variáveis de ambiente.

- [x] Criar .env.example sem credenciais reais.

- [x] Manter .env fora do Git.

- [x] Armazenar preços em centavos utilizando Long.

- [x] Criar profile test com H2 em memória, isolado do MySQL de desenvolvimento.

- [ ] Criar configuração específica para Docker.

- [ ] Adicionar Flyway e migrations.

- [ ] Revisar FKs, índices e constraints.

- [ ] Criar migrations/dados iniciais automáticos de CUSTOMER e RESTAURANT_OWNER; o script SQL manual já existe.

- [ ] Ampliar testes de atualização de timestamps e repetir a validação dos relacionamentos no MySQL; horários, órfãos e timestamps de criação já foram validados em H2.

> O Hibernate está configurado com `spring.jpa.hibernate.ddl-auto=${JPA_DDL_AUTO:update}`. Isso ajuda no desenvolvimento, mas migrations são mais seguras e reproduzíveis para a entrega.

## Segurança e JWT

- [x] Adicionar Spring Security OAuth2 Resource Server.

- [x] Configurar JwtEncoder e JwtDecoder.

- [x] Gerar JWT com ID no subject.

- [x] Adicionar email, user_type_id e user_type como claims.

- [x] Obter o ID autenticado por Authentication.getName().

- [x] Remover a dependência do header X-User-Id.

- [ ] Validar o login implementado pelo usuário; testes de autenticação ficam com ele.

- [x] Implementar findAuthenticatedUser.

- [x] Testar token válido, expirado, ausente e inválido.

- [x] Criar regras de autorização por tipo de usuário.

- [x] Revisar se o SecurityFilter vazio ainda é necessário.

## Compilação e execução

- [x] Corrigir a permissão de escrita do Maven em .m2/repository.

- [x] Executar mvnw compile com sucesso.

- [x] Compilar os 99 arquivos Java atuais.

- [x] Executar contextLoads com H2 isolado (passou).

O erro de `.m2` foi resolvido. Ele era causado pela falta de permissão de escrita do processo no cache local do Maven, e não por um problema no código.

Última validação registrada em 27/10/2026: compilação Maven aprovada, 99 fontes principais e 114 testes dos módulos de negócio executados sem falhas ou erros. contextLoads passou com profile test e H2 isolado. A execução contra MySQL real e a medição de cobertura permanecem pendentes.

A seleção não executou AuthServiceTest, HttpSecurityTests e OpenApiIntegrationTests. A validação desses componentes permanece com o usuário. O resultado acima não comprova cobertura de 80% nem execução ponta a ponta da collection.

## Docker

- [ ] Criar Dockerfile multi-stage.

- [ ] Criar .dockerignore.

- [ ] Criar docker-compose.yml com aplicação e MySQL.

- [ ] Reutilizar as variáveis DB_HOST, DB_PORT, DB_NAME, DB_USERNAME e DB_PASSWORD.

- [ ] Adicionar volume persistente para o MySQL.

- [ ] Adicionar healthcheck do banco.

- [ ] Fazer a aplicação aguardar o banco ficar saudável.

- [ ] Validar docker compose up --build em ambiente limpo.

## Testes e cobertura

- [ ] Adicionar JaCoCo com regra mínima de 80%.

- [x] Criar application-test.properties com H2 em memória.

- [x] Configurar H2 em modo MySQL e dependência com escopo test.

- [x] Testar UserTypeService.

- [x] Testar UserService.

- [x] Testar RestaurantService.

- [x] Testar MenuItemService.

- [x] Preservar os três casos existentes em services/AuthServiceTest; revisão e testes adicionais de autenticação ficam com o usuário.

- [x] Testar os mappers de tipos, endereço, restaurante, item e horários nas duas direções; mapper de User fica separado dos testes gerados nesta rodada.

- [x] Testar controllers de restaurantes, tipos, associação de usuário e cardápio com MockMvc.

- [x] Testar paginação e ordenação de tipos, restaurantes e cardápio com persistência H2.

- [x] Testar repositories, relacionamentos, FKs e restrições de unicidade com H2.

- [x] Cobrir 201, 200, 204, 400, 401, 403, 404 e 409.

## Collection Postman ou similar

- [x] Criar collection e environment para versionamento (commit pendente).

- [x] Usar baseUrl, accessToken, userId, userTypeId, restaurantId e menuItemId.

- [ ] Atualizar o rótulo e habilitar o login na collection após os testes de autenticação do usuário; validar captura do token e cenários negativos.

- [x] Salvar o JWT automaticamente.

- [x] Cobrir todos os CRUDs e associação de tipo.

- [x] Incluir requisições de paginação e ordenação (execução ponta a ponta pendente).

- [x] Incluir cenários negativos.

- [ ] Executar a collection contra o Docker Compose.

## Documentação

- [x] Criar README próprio.

- [x] Explicar problema, escopo, tecnologias e versões.

- [x] Explicar Domain, Application e Infrastructure.

- [x] Documentar arquitetura com diagrama e relacionamentos principais em texto.

- [x] Documentar endpoints, bodies, respostas e status.

- [x] Documentar JWT e uso do header Authorization.

- [x] Documentar paginação, variáveis e execução local; registrar Docker como pendente.

- [x] Documentar execução dos testes; registrar JaCoCo/cobertura como pendentes.

- [ ] Informar integrantes, collection, vídeo e repositório.

- [x] Registrar decisões sobre Long, centavos e JWT.

> Swagger/OpenAPI não é obrigatório pelo enunciado. Um README completo e uma collection testada podem documentar adequadamente a API.

## Repositório

- [x] Ignorar .idea, target, .env e arquivos locais.

- [x] Histórico anterior de commits disponível; alterações desta sessão ainda não foram commitadas.

- [ ] Publicar ou confirmar GitHub/GitLab público.

- [ ] Confirmar clone em outra pasta.

- [ ] Versionar README, Docker, collection, migrations e testes.

- [ ] Executar tudo a partir de um clone limpo.

## Vídeo de aproximadamente cinco minutos

1. 0:00–0:30 — problema, integrantes e objetivo.
2. 0:30–1:10 — arquitetura e camadas.
3. 1:10–1:40 — Docker Compose.
4. 1:40–3:50 — autenticação e CRUDs.
5. 3:50–4:20 — validações e erros.
6. 4:20–4:45 — testes e cobertura mínima de 80%.
7. 4:45–5:00 — README, collection e repositório.

- [ ] Preparar roteiro.

- [ ] Ensaiar e garantir legibilidade.

- [ ] Não mostrar senhas, JWT_SECRET ou outras credenciais.

- [ ] Adicionar o link do vídeo à entrega.

## Regras implementadas e validadas em 27/10/2026

- Tipo associado a usuários não pode ser excluído nem renomeado para outro nome: HTTP 409. A FK permanece como proteção final e conflitos de integridade retornam 409 sem expor SQL.

- A consulta /auth/me busca usuário e tipo associados pelo ID. Ausência retorna 404. AuthService.login foi implementado pelo usuário; testes de autenticação ficam com ele.

- Somente RESTAURANT_OWNER existente pode criar restaurante. Alteração e exclusão continuam restritas ao proprietário.

- Horários devem abrir antes de fechar no mesmo dia. Intervalos adjacentes são permitidos; sobreposição é rejeitada com 400. Dias fechados usam lista vazia; períodos nulos são rejeitados. Períodos que cruzam meia-noite devem ser divididos entre os dias.

- Restaurante com itens não pode ser excluído: 409. Os itens devem ser removidos antes, sem exclusão em cascata do cardápio.

- GET, PUT e DELETE de item consultam restaurantId e menuItemId juntos. URL de outro restaurante retorna 404. Mutações continuam exigindo o proprietário.

- Não há ADMIN. Cada usuário só pode alterar o próprio tipo para CUSTOMER ou RESTAURANT_OWNER. Tipos personalizados não podem ser autoatribuídos. Quem possui restaurante não pode mudar para CUSTOMER: 409.

- Somente RESTAURANT_OWNER pode criar, atualizar e excluir o catálogo de tipos. A autorização consulta o estado atual no banco, sem depender de claims antigas do token. Consultas permanecem disponíveis a usuários autenticados.

- Os tipos CUSTOMER e RESTAURANT_OWNER precisam existir para registro e adesão ao perfil de proprietário; script SQL manual de preparo já existe; execução e migrations continuam pendentes.

- ProblemDetail é o formato único de erros, incluindo validação, JSON inválido e falhas da cadeia de segurança. ApiErrorResponse e FieldErrorResponse sem uso foram removidos.

- SecurityFilter vazio removido. OAuth2 Resource Server mantém a autenticação; token com subject ausente, inválido, não positivo ou fora de Long recebe 401.

- JWT_SECRET deve conter Base64 de pelo menos 32 bytes. Valor de desenvolvimento e .env.example foram corrigidos; não são credenciais de produção.

- Rotas existentes preservadas sem introduzir /api/v1. IDs Long, preços em centavos, paginação e separação domínio/JPA preservados.

## README, Swagger e Postman — 27/10/2026

README.md criado com requisitos, configuração, execução, arquitetura, endpoints, exemplos, regras, erros e limites atuais. Swagger UI disponível em /swagger-ui.html; OpenAPI em /v3/api-docs e /v3/api-docs.yaml, com JWT Bearer, paginação e erros. Dependência springdoc-openapi 3.2.0 compatível com Boot 4.1. API_DOCS_ENABLED permite desabilitar a documentação.

Pasta postman/ criada com collection de 51 requisições, environment local, instruções e script SQL manual para preparar os tipos iniciais. Collection validada contra o schema oficial Postman v2.1; 52 scripts, 22 bodies e cobertura das 24 operações conferidos. O contrato de login permanece identificado como pendente e ignorado por padrão. Não houve execução da collection contra o banco nem execução do script SQL.

OpenApiIntegrationTests adicionou três testes de integração web sem banco, validando documentação pública, recursos do Swagger e proteção das rotas. Essa validação inicial é histórica; a seleção atual de negócio tem 86 testes aprovados e contextLoads aprovado com H2. Docker, migrations, JaCoCo e vídeo continuam pendentes; o profile de testes já foi criado.

## Validação anterior dos módulos de negócio — antes do CRUD de usuários

Compilação limpa aprovada e 86 testes aprovados, sem falhas ou erros.

| Grupo | Casos aprovados |
| --- | --- |
| services: Restaurant, MenuItem, User e UserType | 38 |
| mappers: Address, OpeningHours, Restaurant, MenuItem e UserType | 12 |
| controllers: Restaurant, MenuItem, User e UserType | 22 |
| integration: persistência de Restaurant, MenuItem, User e UserType com H2 | 13 |
| Phase2DataGuardianApplicationTests.contextLoads | 1 |

Corrigida e coberta por teste de regressão a duplicação de horários ao atualizar restaurante mantendo os mesmos períodos. RestaurantPersistenceAdapter preserva os IDs dos períodos inalterados dentro de uma transação. Os métodos privados foram renomeados para ensureRestaurantOwnership e ensureEligibleOwner sem mudar as regras de negócio.

H2 tem escopo de testes e não altera a configuração MySQL da aplicação. Permanecem pendentes JaCoCo/cobertura, execução contra MySQL real e collection ponta a ponta. Os testes de autenticação foram apenas realocados, sem novos cenários nem execução nesta seleção.

## Organização dos testes por camada e recurso — 27/10/2026

Os arquivos de testes foram separados em services/, mappers/, controllers/, integration/, security/ e support/ dentro de src/test/java/com/api_application/phase_2_data_guardian. Cada recurso possui seu arquivo: RestaurantServiceTest, MenuItemServiceTest, UserTypeServiceTest e UserServiceTest, com separação equivalente para controllers, mappers e integração de persistência.

As classes genéricas de negócio foram substituídas pelos arquivos de cada recurso. A preparação compartilhada de JPA/H2 fica em support/PersistenceIntegrationSupport. Os casos existentes de autenticação foram somente realocados, mantendo as asserções. O smoke test da aplicação permanece na raiz do pacote de testes.

86 testes de negócio passaram após a reorganização. O teste combinado de paginação de restaurante e tipos foi dividido em dois, sem perder suas asserções. AuthServiceTest, HttpSecurityTests e OpenApiIntegrationTests permanecem fora dessa seleção. Os nomes de classes citados nas validações anteriores são históricos.

## Mapper HTTP do cardápio — 27/10/2026

MenuItemResponseMapper criado em infra/http/mapper. O MenuItemController usa o mapper nas respostas de criação, consulta por ID, atualização e listagem paginada com Page.map. Os nove campos de MenuItemResponse foram preservados.

Validação direcionada: 10 testes passaram (9 do MenuItemControllerTest e contextLoads), sem falhas ou erros. Três cenários HTTP novos conferem resposta completa, retorno do item salvo no PUT e preservação da paginação. A compilação passou. A seleção ampla anterior de 86 testes não foi repetida nesta refatoração; seu resultado permanece como histórico.

## CRUD completo de usuários — 27/10/2026

- Criação permanece em POST /auth/register; adicionados GET /users, GET /users/{userId}, PUT /users/{userId} e DELETE /users/{userId}.

- Listagem paginada somente com ID, nome e tipo, sem email, senha ou hash; ordenação por id/name.

- Perfil completo, alteração de nome/email e exclusão restritos ao próprio usuário. Não foi criado papel ADMIN.

- Email normalizado e duplicidade retorna 409. Atualização preserva senha, tipo, ID e data de criação.

- Exclusão de conta com restaurantes bloqueada com 409. FK preservada como proteção final.

- UserResponseMapper concentra as conversões de perfil e resumo. Swagger atualizado automaticamente pelos contratos do controller.

- Collection ampliada para 51 requisições, com cobertura das 24 operações e limpeza dos dois usuários de teste. JSON/schema, 52 scripts e 22 bodies validados; execução ponta a ponta ainda pendente.

- 25 testes de usuários acrescentados: 11 de serviço, 9 de controller e 5 de integração. Seleção completa de negócio: 114 testes aprovados, sem falhas ou erros. Compilação Maven aprovada.

- Login e código de autenticação não foram alterados. Testes de autenticação permanecem com o usuário; revogação de JWT não foi adicionada.

## Próxima etapa recomendada

Medir a cobertura com JaCoCo, validar no MySQL real, tornar a execução reproduzível e executar a collection ponta a ponta. O banco isolado de testes já está pronto. A validação dos componentes de autenticação fica com o usuário.

## Critério de pronto

- [x] Nenhum endpoint obrigatório retorna 501.

- [x] O projeto compila com sucesso.

- [ ] Login e autenticação funcionam com JWT.

- [ ] Docker Compose inicia aplicação e banco do zero.

- [ ] A collection executa o fluxo completo.

- [x] Compilação aprovada e 114 testes de negócio aprovados com H2, organizados por camada e recurso.

- [ ] Executar a suíte completa após a validação de autenticação pelo usuário.

- [ ] JaCoCo comprova pelo menos 80% de cobertura.

- [ ] O README permite executar o projeto sem ajuda externa.

- [ ] O repositório está público e sem segredos.

- [ ] O vídeo demonstra funcionalidades, arquitetura, Docker e testes.
