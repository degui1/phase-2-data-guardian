# Postman — Data Guardian

Esta pasta reúne os arquivos para importar e exercitar a API.

| Arquivo | Uso |
| --- | --- |
| data-guardian.postman_collection.json | Collection v2.1 com 53 requisições, incluindo login ativo e cenários negativos |
| local.postman_environment.json | Environment local, sem tokens preenchidos |
| setup-user-types.sql | Preparo manual dos tipos CUSTOMER e RESTAURANT_OWNER, sem IDs fixos |

## Preparação

1. Configure as variáveis da API conforme o [README principal](../README.md).
2. Inicie MySQL e a API. O Hibernate deve criar as tabelas.
3. Execute setup-user-types.sql no banco selecionado pela aplicação. O script inclui somente os tipos ausentes.
4. Importe a collection e o environment no Postman.
5. Selecione **Data Guardian — Local** e ajuste baseUrl se necessário.
6. Abra o Collection Runner e execute as pastas 00 a 07 na ordem apresentada, com uma iteração.

Mantenha API_DOCS_ENABLED=true para a pasta 00. O fluxo foi desenhado para um banco de desenvolvimento. Ele cadastra dois usuários por execução, cria recursos de teste e executa mutações. A limpeza também exclui os dois usuários por suas próprias sessões, depois de remover os restaurantes. Se o fluxo for interrompido, recursos e contas podem permanecer no banco.

## Ordem do fluxo

| Pasta | Ações |
| --- | --- |
| 00 — Documentação | OpenAPI e configuração pública do Swagger UI |
| 01 — Cadastro e preparação | Registra proprietário inicialmente CUSTOMER; descobre os IDs dos tipos; altera o próprio tipo para RESTAURANT_OWNER; faz login e captura o novo JWT; registra um segundo CUSTOMER |
| 02 — Tipos de usuário | Cria, lista, consulta e atualiza tipo temporário sem usuários associados |
| 03 — Restaurantes | Cria dois restaurantes, lista, consulta e atualiza |
| 04 — Cardápio | Cria, lista, consulta e atualiza um item |
| 05 — Usuários | Lista resumos, consulta/atualiza o próprio perfil e verifica bloqueios de acesso/exclusão |
| 06 — Cenários negativos | Valida 400, 401, 403, 404 e 409, incluindo URL de outro restaurante |
| 07 — Limpeza dos recursos | Remove item, restaurantes, tipo temporário e as duas contas de teste |

A autenticação padrão da collection usa accessToken. Cadastro e documentação não enviam autenticação; requisições negativas usam token do cliente, token inválido ou nenhum token conforme o cenário.

A primeira requisição de cadastro gera emails únicos, com timestamp, e limpa os IDs/tokens de uma execução anterior. Por isso, execute o fluxo desde o início. Se interromper antes da limpeza, os recursos podem permanecer; use os IDs capturados para removê-los antes de iniciar outra execução.

## Variáveis

| Variável | Origem / uso |
| --- | --- |
| baseUrl | URL da API, padrão http://localhost:8080, sem barra final |
| page, size | Paginação, padrões 0 e 10 |
| password | Senha de exemplo usada nos dois cadastros: Guardian@123 |
| runId, ownerEmail, customerEmail | Gerados no primeiro cadastro |
| accessToken, userId | Capturados do cadastro do proprietário |
| customerAccessToken, customerUserId | Capturados do cadastro do cliente |
| customerTypeId, ownerTypeId | Descobertos por nome na listagem de tipos |
| userTypeId | ID do tipo temporário criado pelo fluxo |
| restaurantId, otherRestaurantId | IDs dos dois restaurantes criados |
| menuItemId | ID do item criado no restaurante principal |
| invalidToken | Texto propositalmente inválido para testar 401 |

A descoberta dos tipos iniciais consulta a primeira página com até 100 tipos ordenados por nome. Em um catálogo maior, ajuste essa requisição ou as variáveis antes das operações seguintes.

IDs em bodies são interpolados como números JSON, não como strings. Os IDs em branco só serão preenchidos após os passos de preparação. Executar requisição isolada antes disso pode produzir body inválido.

Tokens e senha estão marcados como valores sensíveis no environment. Antes de exportar ou compartilhar um environment usado, limpe os tokens e quaisquer credenciais reais.

## Asserções

As requisições executáveis verificam o status esperado. Cenários negativos também verificam application/problem+json e o campo status. Alguns passos verificam o tipo atual, a estrutura da página e a especificação OpenAPI.

O diretório GET /users retorna somente ID, nome e tipo e aceita sort por id ou name. Os endpoints de perfil completo, atualização e exclusão são restritos ao próprio usuário. A criação é feita por POST /auth/register.

O fluxo cobre:

- 201 no cadastro e nas criações;
- 200 em consultas e atualizações;
- 204 na limpeza;
- 400 para horários sobrepostos e preço negativo;
- 401 para token ausente ou inválido;
- 403 para operações sem permissão;
- 404 para GET, PUT e DELETE de item pela URL de outro restaurante;
- 409 para tipo associado, restaurante com itens e proprietário que tenta virar CUSTOMER.

A collection não gera JWT artificialmente nem acessa o segredo JWT: utiliza tokens retornados pelo cadastro.

## Login

O login está ativo na pasta 01, depois da criação e mudança de tipo da conta do
proprietário. Verifica 200, Bearer JWT e o tipo RESTAURANT_OWNER, e atualiza accessToken.

A pasta 06 inclui senha incorreta e email inexistente, ambos com 401 genérico e
sem accessToken na resposta. A variável enablePendingLogin e a pasta 90 foram removidas.
As contas são excluídas na limpeza; repetir login com elas após a pasta 07 não é
um cenário válido de sucesso. Execute o fluxo inteiro desde a preparação.

## Limites da validação

Os arquivos JSON e scripts podem ser validados sem banco. A execução completa exige MySQL, API ativa e os tipos iniciais. A criação da collection não comprova que o fluxo passou contra banco real ou Docker Compose; essa validação permanece pendente.