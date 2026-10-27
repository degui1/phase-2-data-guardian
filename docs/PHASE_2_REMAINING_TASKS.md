# Relatório de pendências — Tech Challenge Fase 2

Projeto auditado: C:\projects\Java\fiap\phase-2-data-guardian  
Data da auditoria: 26/10/2026

## Resumo

O projeto possui entidades de domínio/JPA, DTOs, repositórios, adapters, mappers, erros customizados e parte do módulo de cardápio. Ainda não está pronto para entrega: faltam funcionalidades obrigatórias, banco reproduzível, Docker, testes, documentação, collection, repositório público confirmado e vídeo.

## Status dos entregáveis obrigatórios

| Entregável | Status | Principal pendência |
|---|---|---|
| Funcionalidades | Parcial | User type, associação e restaurante retornam 501; menu update/delete incompletos |
| Qualidade | Parcial | Corrigir inconsistências, duplicação e validar build |
| Documentação | Ausente | Criar README completo |
| Collection | Ausente | Criar e testar Postman/Insomnia/Bruno |
| Docker Compose | Ausente | Criar Dockerfile, Compose e healthcheck |
| Repositório aberto | Não confirmado | Publicar e testar clone limpo |
| Clean Architecture | Parcial | Só MenuItem possui use case/service |
| Testes 80% | Ausente | Apenas contextLoads; falta JaCoCo |
| Vídeo | Ausente | Gravar aproximadamente cinco minutos |

## Funcionalidades

### Tipos de usuário

- [ ] Criar UserTypeUseCase, UserTypeService e bean de configuração.
- [ ] Implementar POST, GET paginado, GET por ID, PUT e DELETE.
- [ ] Impedir nomes duplicados ignorando maiúsculas/minúsculas.
- [ ] Retornar 409 ao excluir tipo associado a usuários.

### Associação usuário/tipo

- [ ] Criar caso de uso e serviço.
- [ ] Implementar PUT /users/{userId}/user-type.
- [ ] Implementar GET /users/{userId}/user-type.
- [ ] Validar existência de usuário e tipo.
- [ ] Confirmar se o CRUD da Fase 1 precisa permanecer nesta entrega.

### Restaurantes

- [ ] Criar RestaurantUseCase, RestaurantService e bean.
- [ ] Implementar CRUD completo e listagem paginada.
- [ ] Validar existência e tipo do proprietário.
- [ ] Validar horários inválidos e períodos sobrepostos.
- [ ] Definir exclusão de restaurante com itens associados.

### Itens do cardápio

- [x] Criar item.
- [x] Listar por restaurante com paginação.
- [x] Consultar por ID, ainda sem validar o restaurante da URL.
- [ ] Corrigir update: userId não está declarado e menuItemId não é atribuído.
- [ ] Implementar delete; ainda retorna 501.
- [ ] Buscar por menuItemId e restaurantId juntos em consulta/update/delete.
- [ ] Receber X-User-Id em update/delete enquanto não houver autenticação.
- [ ] Extrair toResponse ou mapper HTTP.
- [ ] Remover findAll global se não for usado.

### Contrato HTTP

- [ ] Aplicar /api/v1 se essa continuar sendo a decisão.
- [ ] Padronizar 201 criação, 200 leitura/update e 204 delete.
- [ ] Manter IDs dos recursos nas respostas.
- [ ] Padronizar 400, 403, 404 e 409.
- [ ] Escolher ProblemDetail ou ApiErrorResponse; hoje ambos coexistem.
- [ ] Documentar page, size, sort e página inicial zero.

## Banco de dados

- [ ] Configurar conexão por variáveis de ambiente.
- [ ] Criar configurações local, teste e Docker.
- [ ] Definir estratégia de schema do Hibernate.
- [ ] Preferencialmente adicionar Flyway e migrations.
- [ ] Criar FKs, índices e constraints.
- [ ] Criar dados iniciais de Restaurant Owner e Customer, se necessário.
- [ ] Confirmar preço em centavos com Long.
- [ ] Testar horários, orphanRemoval e timestamps.
- [ ] Não versionar credenciais.

Migrations não são exigidas literalmente, mas tornam o banco reproduzível e melhoram a qualidade.

## Segurança

- [ ] Decidir se autenticação faz parte desta fase.
- [ ] Se não fizer, simplificar a configuração incompleta e documentar X-User-Id.
- [ ] Se fizer, obter usuário do contexto de segurança, não de header manipulável.
- [ ] Completar authorizeHttpRequests e testar autorização.

Autenticação não aparece como requisito explícito. Ela não deve bloquear os CRUDs obrigatórios.

## Docker

- [ ] Criar Dockerfile multi-stage e .dockerignore.
- [ ] Criar docker-compose.yml com aplicação e MySQL.
- [ ] Usar variáveis de ambiente para credenciais.
- [ ] Adicionar volume e healthcheck.
- [ ] Aguardar o banco saudável.
- [ ] Validar docker compose up --build em ambiente limpo.
- [ ] Documentar subida, logs e encerramento.

## Testes e cobertura

- [ ] Adicionar JaCoCo e regra mínima de 80%.
- [ ] Criar profile/banco de testes.
- [ ] Considerar Testcontainers com MySQL.
- [ ] Testar todos os services e regras de negócio.
- [ ] Testar todos os mappers nas duas direções.
- [ ] Testar controllers com MockMvc e validações.
- [ ] Testar paginação e ordenação.
- [ ] Testar repositórios JPA e relacionamentos.
- [ ] Criar integrações controller → use case → adapter → banco.
- [ ] Cobrir 201, 200, 204, 400, 403, 404 e 409.
- [ ] Manter contextLoads, sem tratá-lo como cobertura suficiente.

## Collection Postman ou similar

- [ ] Versionar collection e environment.
- [ ] Usar variáveis baseUrl, userId, userTypeId, restaurantId e menuItemId.
- [ ] Cobrir todos os CRUDs, associação, paginação e ordenação.
- [ ] Salvar IDs automaticamente.
- [ ] Validar status e campos.
- [ ] Incluir cenários negativos.
- [ ] Executar tudo contra o Docker Compose.

## Documentação

- [ ] Criar README próprio; HELP.md é apenas do Spring Initializr.
- [ ] Explicar problema, escopo, tecnologias e versões.
- [ ] Explicar camadas e estrutura.
- [ ] Adicionar diagramas de arquitetura e dados.
- [ ] Documentar endpoints, headers, bodies, respostas e status.
- [ ] Documentar paginação, variáveis, execução local/Docker e testes.
- [ ] Informar collection, integrantes e links.
- [ ] Registrar decisões: Long, centavos e autenticação.

Swagger/OpenAPI não é obrigatório. README e collection podem cumprir a documentação da API.

## Repositório

- [ ] Publicar/confirmar GitHub ou GitLab público.
- [ ] Confirmar clone em outra pasta.
- [ ] Ignorar .idea, target, segredos e arquivos locais.
- [ ] Organizar commits.
- [ ] Versionar README, Docker, collection, migrations e testes.
- [ ] Executar tudo a partir de clone limpo.

## Vídeo de aproximadamente cinco minutos

1. 0:00–0:30 — problema, integrantes e objetivo.
2. 0:30–1:10 — arquitetura e camadas.
3. 1:10–1:40 — Docker Compose.
4. 1:40–3:50 — CRUDs de tipos, associação, restaurante e cardápio.
5. 3:50–4:20 — validação e erro 403/404.
6. 4:20–4:45 — testes e cobertura ≥ 80%.
7. 4:45–5:00 — README, collection e repositório.

- [ ] Ensaiar, garantir legibilidade e não mostrar credenciais.
- [ ] Adicionar link do vídeo à entrega.

## Problemas encontrados no código

1. UserTypeController, UserController e RestaurantController retornam 501.
2. MenuItemController.delete retorna 501.
3. MenuItemController.update usa userId sem declará-lo.
4. Update não atribui menuItemId; o service busca ID nulo.
5. FindById de menu ignora restaurantId.
6. Há findAll global de menu aparentemente sem uso.
7. Só existem MenuItemUseCase e MenuItemService.
8. application.properties só possui o nome da aplicação.
9. Segurança está incompleta.
10. Existe apenas contextLoads.
11. Não há Docker, collection, migrations ou README próprio.
12. ProblemDetail e ApiErrorResponse coexistem.

## Ordem recomendada

1. Fazer compilar/executar: corrigir menu, banco e segurança provisória.
2. Concluir funcionalidades: tipos, associação, restaurantes e cardápio.
3. Tornar reproduzível: migrations, Docker e dados de demonstração.
4. Garantir qualidade: testes unitários/integrados e JaCoCo ≥ 80%.
5. Produzir entrega: README, collection, repositório e vídeo.

## Critério de pronto

- [ ] Nenhum endpoint obrigatório retorna 501.
- [ ] Docker Compose inicia aplicação e banco do zero.
- [ ] Collection executa o fluxo completo.
- [ ] Testes passam e JaCoCo comprova pelo menos 80%.
- [ ] README permite execução sem ajuda.
- [ ] Repositório está público e sem segredos.
- [ ] Vídeo demonstra funcionalidades, arquitetura, Docker e testes.
