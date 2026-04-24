# Spec 01 — Signup (Tenant + User)

## Objetivo

Cadastrar uma nova empresa (tenant) e seu primeiro usuário administrador na mesma transação.

## Endpoint

`POST /auth/register`

**Público** — não exige autenticação.

## Request Body

```json
{
  "companyName": "Locadora Silva",
  "userName": "João Silva",
  "email": "joao@locadorasilva.com",
  "password": "senha123"
}
```

## Response (201 Created)

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userName": "João Silva",
  "email": "joao@locadorasilva.com"
}
```

## Regras de Validação

- `companyName` não pode ser vazio
- `userName` não pode ser vazio
- `email` deve ter formato válido
- `email` não pode já estar cadastrado no sistema
- `password` mínimo 8 caracteres

## Comportamento

1. Validar campos de entrada
2. Verificar se email já existe → se sim, retornar 409 Conflict
3. Criar Tenant (name = companyName, active = true)
4. Criar User (name = userName, email, password com BCrypt, role = ADMIN, tenant_id = tenant criado)
5. Tudo na mesma transação — se qualquer parte falhar, nada é salvo
6. Gerar JWT com tenant_id e user_id nos claims
7. Retornar 201 com token e dados do usuário

## Testes Esperados

- Signup com dados válidos → 201 + token retornado
- Signup com email duplicado → 409 Conflict
- Signup com email inválido → 400 Bad Request
- Signup com campos vazios → 400 Bad Request
- Signup com senha curta → 400 Bad Request
- Verificar que Tenant e User foram salvos no banco
- Verificar que password foi salvo com hash (não plain text)

## O que NÃO fazer

- Não enviar email de confirmação
- Não validar força de senha complexa (maiúscula, número, símbolo)
- Não criar dados de exemplo no signup
- Não implementar login com Google/redes sociais
- Não implementar billing/planos
- Não criar endpoints além do POST /auth/register nessa spec
