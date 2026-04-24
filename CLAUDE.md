# CLAUDE.md — ToolRent

## O que é

SaaS multi-tenant para empresas que alugam ferramentas e maquinários de construção.

## Stack

- Java 21
- Spring Boot 3
- Maven
- PostgreSQL + Flyway (migrations)
- Spring Security + JWT (autenticação com tenant_id no claim)
- Docker + Docker Compose
- Testcontainers (testes de integração)
- Lombok
- GitHub Actions (CI/CD)

## Estrutura de pastas

Package by feature. Cada feature tem 4 subpastas:

```
src/main/java/com/toolrent/
├── config/                  # Configurações globais (Security, JWT, Hibernate Filter)
├── common/                  # Classes compartilhadas (exceções base, BaseEntity)
├── tenant/
│   ├── domain/              # Entidades de domínio, regras de negócio puras (sem Spring)
│   ├── service/             # Lógica de aplicação (@Service)
│   ├── web/                 # Controller + DTOs
│   └── persistence/         # JPA Entity + Repository
├── user/
│   ├── domain/
│   ├── service/
│   ├── web/
│   └── persistence/
├── equipment/
│   ├── domain/
│   ├── service/
│   ├── web/
│   └── persistence/
├── customer/
│   ├── domain/
│   ├── service/
│   ├── web/
│   └── persistence/
└── rental/
    ├── domain/
    ├── service/
    ├── web/
    └── persistence/
```

## Idioma

- Código em inglês: classes, métodos, variáveis, endpoints, mensagens de erro da API
- Português permitido em: comentários, specs, commits, documentação interna

## Convenções

- Manter código simples e direto. Sem overengineering.
- Regras de negócio ficam em domain/ (classes Java puras, sem Spring).
- Service orquestra, domain decide.
- Soft delete em todas as entidades de negócio (campo `active`).
- Dois IDs por entidade: `id` (UUID, interno) e `code` (sequencial por tenant, visível pro usuário).
- tenant_id em todas as entidades de negócio.
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
