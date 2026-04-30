# OmniRent API
Backend da plataforma OmniRent, um marketplace de aluguel de equipamentos variados

A aplicação permite que os usuários anunciem equipamentos para que outros usuários possam alugar

## Objetivo
O projeto foi desenvolvido com o objetivo de compor um portfólio em backend baseado em arquiteturas de sistemas reais.

## Tecnologias
* **Backend**: Java, Spring Framework (Web, Security, JWT)
* **Segurança**: JWT (Auth0 Java JWT)
* **Banco**: MySQL 8
* **Infra**: Docker, Docker Compose, AWS (EC2, EBS, CloudWatch, EventBridge)
* **CI/CD**: GitHub Actions
* **Testes**: JUnit 5, Mockito, Testcontainers, AssertJ
* **Observabilidade**: SLF4J / Logback

### Execução
```bash
docker compose up
```

## Funcionalidades
### Equipamentos
Usuários podem anunciar equipamentos para aluguel

### Aluguéis
Locação de equipamentos por diversos períodos definidos

### Interação
Troca de informações entre usuários envolvidos na locação por meio de chats.
