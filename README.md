# OmniRent API

> Read in other languages: [English](README-eng.md)

## Descrição

Backend da plataforma OmniRent, um marketplace de aluguel de equipamentos variados.

A aplicação permite que os usuários anunciem equipamentos para que outros usuários possam buscar, reservar e realizar aluguéis por períodos definidos.

## Objetivo

O projeto foi desenvolvido com o objetivo de compor um portfólio em backend baseado em arquiteturas de sistemas em produção.

Além das funcionalidades principais da plataforma, o projeto explora autenticação e autorização, processamento assíncrono, mensageria, pagamentos, comunicação em tempo real, auditoria, cache, observabilidade e integrações com serviços externos.

## Tecnologias

* **Backend**: Java 21, Spring Boot, Spring Framework (Web, Security), JPA/Hibernate
* **Autenticação**: JWT, OAuth2 (Google, GitHub)
* **Banco de Dados**: PostgreSQL, Neon (produção), Query DTOs, consultas otimizadas
* **Armazenamento de Arquivos**: Cloudflare R2 (API compatível com Amazon S3)
* **Infraestrutura**: Docker, Docker Compose, AWS (EC2, EBS, CloudWatch, EventBridge)
* **CI/CD**: GitHub Actions
* **Testes**: JUnit 5, Mockito, Testcontainers, AssertJ
* **Mensageria**: RabbitMQ
* **Tempo Real**: WebSocket, STOMP
* **Observabilidade**: Logs estruturados, SLF4J, Logback, CloudWatch
* **Auditoria**: Registro de ações e alterações críticas do sistema
* **Pagamentos**: Stripe Checkout, Webhooks e reembolsos (Sandbox)
* **Segurança**: CSRF, controle de acesso por roles, rate limiting e invalidação de sessões
* **Localização**: Internacionalização de respostas da API e tratamento de fusos horários

## Arquitetura

A aplicação foi estruturada com separação entre responsabilidades de domínio, persistência, segurança, integrações externas e processamento assíncrono.

A camada HTTP é responsável pela exposição dos recursos da API, enquanto regras de negócio e transições de estado permanecem isoladas nos serviços da aplicação.

Eventos internos e mensageria são utilizados para desacoplar operações como notificações, auditoria e processamento de alterações importantes no sistema.

Integrações externas, como Stripe e Cloudflare R2, são mantidas separadas da lógica principal da aplicação.

Operações críticas também utilizam validação de estado para impedir transições inválidas e preservar a consistência das entidades.

## Execução

**1.** Configure suas variáveis de ambiente: [Exemplo](.env-example)

**2.** Execute:

```bash
docker compose up
```

## Funcionalidades

### Equipamentos

* Usuários podem anunciar equipamentos para aluguel
* Filtragem de equipamentos por condição, título, categoria e subcategoria
* Ordenação por data, maior preço e menor preço
* Gerenciamento de disponibilidade dos equipamentos
* Atualização de informações dos anúncios
* Upload de até 5 imagens por item
* Moderação de anúncios através da área administrativa
* Preservação de informações relevantes para aluguéis já realizados

### Aluguéis

* Solicitação de aluguel por período definido
* Validação de disponibilidade
* Controle do ciclo de vida do aluguel
* Histórico preservado das informações do equipamento alugado
* Controle de transições entre estados do aluguel
* Fluxos de preparação, envio, utilização e devolução
* Cancelamento e expiração de aluguéis conforme regras da plataforma

### Pagamentos

* Finalização de aluguel através de pagamento online
* Criação de sessões através do Stripe Checkout
* Atualização automática do status após confirmação do pagamento
* Processamento de Webhooks enviados pelo Stripe
* Cancelamento e reembolso do pagamento
* Renovação do aluguel mediante a novo pagamento
* Sincronização entre status de pagamento e estado do aluguel

### Autenticação

* Cadastro de usuários
* Login com email e senha
* Login utilizando conta Google e GitHub
* Autenticação baseada em JWT
* Armazenamento do token de acesso em cookie HttpOnly
* Controle de acesso baseado em roles
* Invalidação de sessões através de versionamento de tokens

### Administração

* Consulta e gerenciamento de usuários
* Banimento e reativação de contas
* Consulta de equipamentos
* Aprovação e rejeição de anúncios
* Bloqueio e desbloqueio de equipamentos
* Proteção dos endpoints administrativos baseada em roles

### Segurança

* JWT armazenado em cookie HttpOnly
* Proteção contra CSRF
* Controle de acesso baseado em autenticação e roles
* Invalidação de tokens através de versionamento associado ao usuário
* Invalidação global de sessões quando necessário
* Limite de requisições por IP e usuário autenticado
* Tratamento centralizado de falhas de autenticação e autorização

### Tempo Real

* Comunicação com o frontend através de WebSocket e STOMP
* Atualização do status de pagamentos em tempo real
* Notificação do cliente após processamento e confirmação de pagamentos
* Comunicação desacoplada das requisições tradicionais da API

### Processamento Assíncrono

* Processamento de eventos através do RabbitMQ
* Envio de notificações desacoplado do fluxo principal
* Processamento de operações que não precisam bloquear a requisição HTTP
* Integração com eventos internos gerados pelas operações de domínio

### Auditoria

* Registro de ações importantes realizadas na plataforma
* Armazenamento das alterações de estado relevantes
* Registro de informações anteriores e posteriores às operações quando necessário
* Auditoria de ações administrativas e operações críticas

### Cache

* Cache de informações acessadas frequentemente
* Redução de consultas repetidas ao banco de dados
* Cache de metadados utilizados durante validações de autenticação
* Invalidação de dados armazenados quando necessário para preservar consistência

### Sistema

* Registro de alterações e ações importantes
* Processamento de notificações e pagamentos
* Atualização de status de aluguéis e pagamentos atrasados
* Fluxo de entrega simulado com atualização de status automática após período definido
* Limite de requisições por IP e autenticação
* Tratamento centralizado de erros da API
* Internacionalização das mensagens retornadas ao cliente
* Tratamento consistente de datas e fusos horários

## Testes

* Testes unitários com JUnit 5
* Mock de dependências com Mockito
* Assertions utilizando AssertJ
* Testes de integração utilizando Testcontainers
* Execução de banco de dados real em containers durante testes de integração
* Testes de regras de negócio, persistência e fluxos entre camadas

## Infraestrutura

A aplicação é executada em containers Docker e pode ser iniciada localmente através do Docker Compose.

No ambiente de produção, os serviços da aplicação são executados em uma instância AWS EC2, enquanto o banco de dados PostgreSQL é hospedado no Neon.

O CloudWatch é utilizado para monitoramento da infraestrutura, enquanto o EventBridge participa da automação de operações relacionadas ao ambiente.

As imagens dos equipamentos são armazenadas no Cloudflare R2 através de uma API compatível com Amazon S3.

## Integrações

### Stripe

A integração com Stripe é utilizada para o processamento dos pagamentos dos aluguéis.

A confirmação do pagamento ocorre através de Webhooks, permitindo que o backend atualize o pagamento e o aluguel mesmo sem depender do retorno do navegador do usuário.

### Cloudflare R2

O Cloudflare R2 é utilizado para armazenamento das imagens dos equipamentos.

A integração utiliza uma API compatível com Amazon S3, permitindo que o gerenciamento dos arquivos permaneça desacoplado da aplicação.

### OAuth2

Google e GitHub podem ser utilizados como provedores externos de autenticação.

Após a autenticação, a aplicação integra o usuário ao mesmo fluxo de sessão utilizado pelas demais formas de login.
