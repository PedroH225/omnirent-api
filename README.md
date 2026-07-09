# OmniRent API

> Read in other languages: [English](README-eng.md)

## Descrição
Backend da plataforma OmniRent, um marketplace de aluguel de equipamentos variados.

A aplicação permite que os usuários anunciem equipamentos para que outros usuários possam buscar, reservar e realizar aluguéis por períodos definidos.

## Objetivo
O projeto foi desenvolvido com o objetivo de compor um portfólio em backend baseado em arquiteturas de sistemas em produção.

## Tecnologias
* **Backend**: Java, Spring Framework (Web, Security), JPA/Hibernate
* **Autenticação**: JWT, OAuth2 (Google)
* **Banco de Dados**: MySQL 8, Query DTOs, consultas otimizadas
* **Infraestrutura**: Docker, Docker Compose, AWS (EC2, EBS, CloudWatch, EventBridge)
* **CI/CD**: GitHub Actions
* **Testes**: JUnit 5, Mockito, Testcontainers, AssertJ
* **Mensageria**: RabbitMQ
* **Observabilidade**: Logs estruturados, SLF4J, Logback
* **Auditoria**: Registro de ações e alterações críticas do sistema
* **Pagamentos**: Stripe(Sandbox)
* **Localização**: Internacionalização de respostas da API e tratamento de fusos horários

## Execução
**1.** Configure suas variáveis de ambiente: [Exemplo](.env-example)

**2.** Execute:
```bash
docker compose up
```

## Funcionalidades

### Equipamentos
- Usuários podem anunciar equipamentos para aluguel
- Organização por categorias e subcategorias
- Gerenciamento de disponibilidade dos equipamentos
- Atualização de informações dos anúncios

### Aluguéis
- Solicitação de aluguel por período definido
- Validação de disponibilidade
- Controle do ciclo de vida do aluguel
- Histórico preservado das informações do equipamento alugado

### Pagamentos
- Finalização de aluguel através de pagamento online
- Atualização automática do status após confirmação do pagamento
- Cancelamento e reembolso do pagamento
- Renovação do aluguel mediante a novo pagamento

### Autenticação
- Cadastro de usuários
- Login com email e senha
- Login utilizando conta Google

### Sistema
- Registro de alterações e ações importantes
- Processamento de notificações e pagamentos
- Atualização de status de aluguéis e pagamentos atrasados
- Fluxo de entrega simulado com atualização de status automática após período definido
