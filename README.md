# TP5 do Projeto de Bloco — Aplicação Web Java com Javalin e Pipelines CI/CD

[![Workflow CI/CD](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/gradle-ci.yml)
[![CodeQL Analysis](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/codeQL.yml/badge.svg)](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/codeQL.yml)
[![DAST Scan](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/dastScan.yml/badge.svg)](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/dastScan.yml)
[![Pipeline Java](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/deployTests.yml/badge.svg)](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/deployTests.yml)

---

## Visão Geral

Repositório desenvolvido para o **TP5_PB** que implementa uma aplicação web completa em **Java 21** utilizando **Javalin 6** como framework leve. O projeto gerencia uma biblioteca digital com CRUD completo, incluindo interface Bootstrap, testes automatizados abrangentes (>90% cobertura Jacoco) e pipelines GitHub Actions para CI/CD com aprovação manual em produção.

---

## Stack Tecnológico

| Categoria | Tecnologias                                            |
|-----------|--------------------------------------------------------|
| **Linguagem** | Java 21                                                |
| **Framework Web** | Javalin 6 (API REST + HTML rendering)                  |
| **Build Tool** | Gradle (Groovy)                                        |
| **Testes Unitários** | JUnit 5 + jqwik (Property-Based Testing)               |
| **Testes E2E** | Selenium WebDriver + WebDriverManager                  |
| **Cobertura** | Jacoco                                                 |
| **Interface** | Bootstrap 5 + HTML templates customizados              |
| **CI/CD** | GitHub Actions (4 workflows: CI, CodeQL, DAST, Deploy) |
| **Logging** | SLF4J                                                  |

---

## Como Colocar em Execução Localmente

### Pré-requisitos

Certifique-se de ter instalado:

- [Java 21+](https://adoptium.net/) — Linguagem e runtime
- [Gradle 8.14+](https://gradle.org/install/) — Ou utilize o wrapper incluso (`./gradlew`)
- [Git](https://git-scm.com/) — Para clonar o repositório
- Chrome/Chromium — Necessário para testes Selenium (o WebDriverManager baixa automaticamente)

### Início Rápido

```bash
# Clonar o repositório
git clone https://github.com/gustalgebaile/TP5_PB2
cd TP5_PB2

# Build completo com testes e cobertura
./gradlew clean build test jacocoTestReport

# Iniciar a aplicação
./gradlew run

# A aplicação estará disponível em:
# http://localhost:7000/biblioteca
```

### Comandos Úteis

```bash
# Apenas compilar (sem testes)
./gradlew build -x test

# Rodar apenas testes unitários
./gradlew test

# Executar testes com property-based (jqwik)
./gradlew test --tests "*jqwik*"

# Gerar relatório de cobertura
./gradlew jacocoTestReport

# Limpar build anterior
./gradlew clean

# Ver dependências do projeto
./gradlew dependencies
```

---

## Endpoints da API

A aplicação expõe os seguintes endpoints para gerenciar a biblioteca:

| HTTP | Rota | Funcionalidade |
|------|------|---|
| `GET` | `/biblioteca` | Listar todos os livros cadastrados |
| `GET` | `/biblioteca/new` | Exibir formulário para novo livro |
| `POST` | `/biblioteca` | Salvar novo livro no banco |
| `GET` | `/biblioteca/edit/:id` | Exibir formulário de edição |
| `POST` | `/biblioteca/edit/:id` | Atualizar dados do livro |
| `POST` | `/biblioteca/delete/:id` | Remover livro do banco |

**Nota:** Todos os endpoints retornam HTML renderizado. Para integração com frontends SPA, considere adicionar endpoints JSON.

---

## Relatórios de Testes e Cobertura

Após executar `./gradlew test jacocoTestReport`, os relatórios estarão disponíveis em:

```
build/reports/
├── jacoco/test/html/index.html      # Cobertura de código (meta: >90%)
├── tests/test/index.html             # Resultados JUnit 5
└── jqwik/                             # Property-based test reports
```

**Abra `build/reports/jacoco/test/html/index.html` no navegador** para visualizar a cobertura linha-por-linha.

---

## Fluxo de Deployment com GitHub Actions

O repositório inclui **4 workflows automáticos**:

### 1- **Workflow CI/CD** (`gradle-ci.yml`)
- Compila o código
- Executa testes (JUnit + jqwik + Selenium)
- Gera cobertura Jacoco
- Publica artefatos de build

### 2️- **Análise Estática** (`codeQL.yml`)
- CodeQL para detecção de vulnerabilidades
- Executa em cada push e PR

### 3️- **Testes de Segurança Dinâmicos** (`dastScan.yml`)
- Simula ataques contra a aplicação rodando
- Valida proteções contra OWASP Top 10

### 4️- **Deploy Multi-ambiente** (`deployTests.yml`)
- Dev: Deploy automático após CI passar
- Staging: Aprovação manual obrigatória
- Production: Gate de aprovação manual com requisição de revisor

---

## Funcionalidades Implementadas

 **CRUD Completo**
- Criar, ler, atualizar e deletar livros
- Suporte a categorias (Fantasia, Romance, Terror, Épico, etc.)

 **Interface Responsiva**
- Bootstrap 5 para layout moderno
- Formulários validados frontend + backend
- Tabela com ações inline (editar/deletar)

 **Testes Abrangentes**
- JUnit 5 com testes unitários
- jqwik para property-based testing (geração de dados randômicos)
- Selenium WebDriver para E2E (headless Chrome)
- Cobertura ≥90% via Jacoco

 **Segurança**
- CodeQL para análise estática
- DAST para testes dinâmicos
- Validações de entrada em todas as camadas

 **DevOps**
- 4 workflows GitHub Actions orchestrados
- Deploy automático em dev
- Aprovações manuais para staging/prod
- Relatórios de testes e cobertura

---

##  Estrutura do Projeto

```
TP5-PB2/
├── src/
│   ├── main/java/com/biblioteca/
│   │   ├── app/               # Classe Main (BibliotecaWebApplication
│   │   ├── controller/        # Controladores Javalin
│   │   ├── model/             # Modelos (Book, etc)
│   │   │   └── enums          # Enums de User/Admin
│   │   ├── service/           # Lógica de negócio
│   │   ├── repository/        # Acesso a dados
│   │   ├── view/              # Renderização HTML
│   │   └── exception/         # Exceções customizadas
│   └── test/java/com/biblioteca/
│       ├── BibliotecaControllerTest.java    # Testes jqwik
│       ├── BookViewTest.java                # Testes Selenium
│       ├── UserViewTest.java                # Testes de Segurança
│       ├── BookRepositoryTest.java          
│       ├── BookRepositoryGapsTest.java      
│       ├── BookServiceTest.java             
│       ├── ExceptionTest.java               
│       ├── UserControllerGaps.java          
│       ├── UserRepositoryTest.java          
│       └── UserServiceTest.java             
├── build.gradle          # Configuração Gradle (Groovy)
├── .github/workflows/         # Workflows CI/CD
│   ├── gradle-ci.yml
│   ├── codeQL.yml
│   ├── dastScan.yml
│   └── deployTests.yml
├── README.md
└── gradlew / gradlew.bat      # Gradle Wrapper
```

---

## Troubleshooting

### Erro: "ChromeDriver not found"
**Solução:** WebDriverManager baixa automaticamente. Reinicie a JVM ou rode `./gradlew test` novamente.

### Erro: "Port 7000 already in use"
**Solução:** Altere a porta no `BibliotecaController.java`:
```java
app.start(8080);
```

### Testes Selenium falhando no CI
**Verificar:** GitHub Actions runner possui Chrome instalado. Se não, adicione step:
```yaml
- name: Install Chrome
  run: apt-get update && apt-get install -y chromium-browser
```

### Cobertura Jacoco abaixo de 90%
**Ação:** Adicione testes para métodos não cobertos:
```bash
./gradlew jacocoTestReport
# Abra build/reports/jacoco/test/html/index.html e identifique linhas vermelhas
```

---

## Recursos Adicionais

- [Documentação Javalin](https://javalin.io)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [jqwik Property-Based Testing](https://jqwik.net)
- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [GitHub Actions Best Practices](https://docs.github.com/en/actions/guides)

---