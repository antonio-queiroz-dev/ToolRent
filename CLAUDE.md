# CLAUDE.md — ToolRent

## O que é

SaaS multi-tenant para empresas que alugam ferramentas e maquinários de construção.

## Stack

- Java 21
- Spring Boot 4.0.5
- Maven
- PostgreSQL + Flyway (migrations)
- Spring Security + JWT (autenticação com tenant_id no claim)
- Docker + Docker Compose
- Testcontainers (testes de integração)
- Lombok
- GitHub Actions (CI/CD)

## Gotchas do Spring Boot 4

- Flyway precisa do módulo `spring-boot-flyway` (não basta flyway-core)
- Testes web precisam de `spring-boot-starter-webmvc-test` (não vem transitivo do starter-test)
- Arquivos de teste devem terminar em `*Test.java` (Surefire ignora `*IT.java` sem Failsafe configurado)

## Estrutura de pastas

Package by feature. Cada feature é uma pasta com todas as classes na raiz — sem subpastas.

```
src/main/java/com/toolrent/
├── config/                  # Configurações globais (Security, JWT)
├── common/                  # Classes compartilhadas (exceções base)
├── tenant/                  # Tenant, TenantRepository, TenantService, TenantController
├── user/                    # User, Role, UserRepository, UserService, UserController
├── equipment/
├── customer/
└── rental/                  # Rental, RentalRepository, RentalService, RentalController, RentalPeriodValidator
```

## Entidades

- Usar UMA classe por entidade — a JPA Entity direto. Sem separação domain/persistence.
- Classes de regra de negócio pura (ex: RentalPeriodValidator) ficam direto na pasta da feature, junto com a entidade.
- Se já existem classes separadas (domain + persistence) de specs anteriores, unificar numa só (a JPA Entity).

## Idioma

- Código em inglês: classes, métodos, variáveis, endpoints, mensagens de erro da API
- Português permitido em: comentários, specs, commits, documentação interna

## Convenções

- Manter código simples e direto. Sem overengineering.
- Service orquestra; classes puras decidem (quando houver regra de negócio).
- Soft delete em todas as entidades de negócio (campo `active`).
- Dois IDs por entidade: `id` (UUID, interno) e `code` (sequencial por tenant, visível pro usuário).
- tenant_id em todas as entidades de negócio.
- createdAt e updatedAt em todas as entidades (colunas com default now() no banco).
- Um padrão definido = todos os CRUDs seguem o mesmo padrão.

## Regras — o que NÃO fazer

- NÃO fazer overengineering. Código complexo sem necessidade.
- NÃO adicionar dependência nova no pom.xml sem perguntar antes.
- NÃO mudar a modelagem de domínio (entidades, colunas) sem perguntar antes.
- NÃO criar endpoint que não está na spec.
- NÃO criar código que não pode ser explicado de forma simples.
- NÃO usar herança complexa quando composição resolve.
- NÃO misturar regra de negócio no controller. Controller recebe, valida entrada, chama service.
- NÃO ignorar testes. Toda feature precisa de teste.
- NÃO mudar padrão que já existe sem motivo. Se o primeiro CRUD segue um padrão, os outros seguem igual.
- NÃO separar entidade em domain + persistence. Usar a JPA Entity direto.
- NÃO criar subpastas dentro das features (sem persistence/, service/, web/, domain/). Tudo direto na pasta da feature.

## Comandos

```bash
# Subir banco de dados
docker compose up -d

# Rodar o projeto
./mvnw spring-boot:run

# Rodar testes
./mvnw test
```

## Fluxo de trabalho

1. Receber spec em markdown (escrita pelo dev)
2. Apresentar PLANO antes de codar — quais classes criar, quais decisões tomar
3. Esperar aprovação do plano
4. Implementar em etapas pequenas
5. Cada etapa precisa compilar e os testes precisam passar
