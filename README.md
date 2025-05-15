# AnimTracker

Aplicação web para gestão e rastreamento de projetos de animação, cenas e clientes.
Full-stack, segura, moderna e robusta.
Desenvolvida para demonstrar domínio prático de backend, frontend, arquitetura, segurança e integração.

## Sumário

* Visão geral do projeto
* Funcionalidades
* Stack e principais tecnologias
* Arquitetura e boas práticas
* Segurança e diferenciais técnicos
* Instalação e execução
* Estrutura de pacotes
* API REST
* Contribuição

---

## Visão Geral

AnimTrackerV2 centraliza o controle de projetos de animação, gestão de clientes, acompanhamento de cenas e desempenho da equipe. Ideal para estúdios, freelancers e equipes que querem organização real e dados acessíveis.

---

## Funcionalidades

* **Gestão de Usuários**

    * Registro/login com hash de senha (BCrypt)
    * Login social Google OAuth2
    * Edição de perfil
    * Logs de acesso (IP, MAC, User-Agent)
* **Dashboard**

    * Métricas, filtros e progresso visual em tempo real
* **Gestão de Clientes**

    * CRUD completo, busca, filtragem, categorização
* **Gestão de Projetos**

    * CRUD completo, associação com clientes, status, deadlines, specs técnicas
* **Gestão de Cenas**

    * CRUD completo, associação com projetos, status, frames, duração, observações
* **API REST**

    * Endpoints para integração de terceiros
* **Interface Responsiva**

    * Bootstrap 5, componentes reutilizáveis (header, footer, cards, sidebar)
* **Documentação automática**

    * Swagger/OpenAPI

---

## Stack & Tecnologias

* **Java 21**
* **Spring Boot 3.4.4**

    * Spring Data JPA, Spring Data REST, Spring Security, Web, WebFlux
* **MySQL**
* **Maven**
* **Thymeleaf** (templates server-side)
* **Bootstrap 5.3** (UI responsiva)
* **Lombok** (boilerplate reduction)
* **Swagger (SpringDoc OpenAPI)**
* **HTML5/CSS3/JS** (customizações front-end)
* **DTOs e ModelMapper** (padronização de dados)

---

## Arquitetura & Boas Práticas

* **MVC real** (controllers web, controllers API, services, repositories, models)
* **DTO Pattern** (separação entre domínio e API)
* **Injeção de Dependências (IoC, DI)**
* **Componentização front-end**
* **Camada de serviços isolando lógica de negócio**
* **Pacotes organizados por contexto:**

    * `/apis` (REST)
    * `/controllers` (web)
    * `/services` (business)
    * `/models` (JPA)
    * `/dtos`
    * `/repositories`
    * `/configs` (segurança, encoder, OAuth, etc.)
    * `/utils`
* **Código limpo, documentação in-code e testes básicos**

---

## Segurança & Diferenciais Técnicos

* **Spring Security completo**

    * Autenticação local + OAuth2 (Google)
    * BCryptPasswordEncoder
    * CSRF ativado
    * Controle granular de permissões
* **Criptografia customizada**

    * `AttributeEncryptor` para dados sensíveis direto no banco
* **Logs de segurança**

    * Registro de login (IP, MAC, user-agent)
* **Configurações externalizadas**

    * Segredos em application.properties (pronto para externalizar)
* **Swagger documentando todas as APIs**
* **Tratamento de erros customizado**

    * Páginas de erro, exceptions tratadas

---

## Instalação e Execução

**Pré-requisitos:**

* JDK 21+
* Maven 3.6+
* MySQL

**Passos:**

1. Clone o repositório
   `git clone https://github.com/lucasvm1/AnimTrackerP.git`
2. Crie o banco MySQL (ex: `animtracker`)
3. Configure `src/main/resources/application.properties`:

   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/animtracker
   spring.datasource.username=USUARIO
   spring.datasource.password=SENHA

   spring.security.oauth2.client.registration.google.client-id=...
   spring.security.oauth2.client.registration.google.client-secret=...

   app.encryption.secret=...
   app.encryption.salt=...
   ```
4. Rode a aplicação
   `mvn spring-boot:run`
5. Acesse
   `http://localhost:8080`
   Documentação API: `/swagger-ui.html`

---

## Estrutura de Pacotes (Exemplo)

```
com.lucasvm.animtrackerv2
│
├── apis            // REST Controllers
├── configs         // Configurações (segurança, beans, etc)
├── controllers     // Web MVC Controllers
├── dtos            // Data Transfer Objects
├── models          // Entidades JPA
├── repositories    // Spring Data JPA
├── services        // Lógica de negócio
├── utils           // Utilitários e helpers
└── resources
    ├── application.properties
    ├── static     // CSS, JS, imagens
    └── templates  // HTML/Thymeleaf
```

---

## Endpoints Principais (API)

* `/api/usuarios`
* `/api/clientes`
* `/api/projetos`
* `/api/cenas`

