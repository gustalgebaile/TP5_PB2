# TP5 do Projeto de Bloco — Aplicação Web Java com Javalin e Pipelines CI/CD

[![Workflow CI/CD](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/gradle-ci.yml)
[![CodeQL Analysis](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/codeQL.yml/badge.svg)](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/codeQL.yml)
[![DAST Scan](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/dastScan.yml/badge.svg)](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/dastScan.yml)
[![Pipeline Java](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/deployTests.yml/badge.svg)](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/deployTests.yml)
[![Alertas de Falha](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/alert.yml/badge.svg)](https://github.com/gustalgebaile/TP5_PB2/actions/workflows/alert.yml)

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
| **CI/CD** | GitHub Actions (5 workflows: CI, CodeQL, DAST, Deploy, Alertas) |
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

O repositório inclui **5 workflows automáticos**:

### 1️- **Workflow CI/CD** (`gradle-ci.yml`)
Executa em cada push/PR para `master` ou `main`:
- Compila o código com Gradle
- Executa testes (JUnit + jqwik + Selenium)
- Gera cobertura Jacoco
- **Otimização:** Build paralelo com flag `--parallel` (reduz tempo ~30%)
- Publica artefatos de build e relatórios

### 2- **Análise Estática** (`codeQL.yml`)
- CodeQL para detecção de vulnerabilidades
- Executa em cada push, PR e semanalmente
- Identifica padrões de código inseguro

### 3️- **Testes de Segurança Dinâmicos** (`dastScan.yml`)
- OWASP ZAP para testes dinâmicos
- Simula ataques contra a aplicação rodando
- Valida proteções contra OWASP Top 10

### 4️- **Pipeline de Deploy e Testes Pós-Deploy** (`deployTests.yml`)
Gerencia o ciclo de vida de entrega com validação contínua:

#### **Build**
Compila e gera o artefato executável .jar:
- Checkout do código
-  Setup Java 21
-  Cache Gradle para otimizar dependências
-  Execução de testes completos
-  Geração do Shadow JAR (artefato executável)
-  Armazenamento de artefatos para próximas etapas

#### **Deploy Dev**
Realiza o deploy automático no ambiente de desenvolvimento com validação completa:
-  Download do artefato JAR
-  Instalação de dependências (Chrome para Selenium)
-  **Inicialização automática da aplicação** para testes
-  **Health check via curl** para validar disponibilidade
-  **Execução de testes Selenium pós-deploy** (BookViewTest + UserViewTest)
- Testa todos os endpoints da API
- Valida funcionamento da interface web
- Detecta regressões imediatamente
-  Upload automático de relatórios de teste
-  Parada controlada da aplicação
-  Execução automática - sem aprovação necessária

#### **Deploy Prod**
Possui uma proteção de ambiente (environment: production), exigindo aprovação manual antes de implantar a versão em produção:
-  Aprovação manual obrigatória no GitHub
-  Download do artefato JAR
-  Deploy em ambiente de produção
-  **Smoke tests críticos com Selenium**
- Testa funcionalidades essenciais apenas
- Valida que a aplicação está operacional
- Reduz tempo de execução
-  Upload de relatórios de smoke tests
-  Fluxo corporativo seguro com rastreamento completo
-  Monitoramento e métricas para auditoria

**Fluxo Completo:**
```
Build (Automático)
  ↓
Deploy Dev (Automático) → Testes Selenium Completos → Upload Reports
  ↓
Deploy Prod (Aprovação Manual) → Smoke Tests → Upload Reports
```

### 5️- **Sistema de Monitoramento e Alertas** (`alert.yml`)
Fornece visibilidade operacional contínua com detecção automática de falhas em tempo real:

#### **Detecção de Falhas**
Monitora a conclusão de todos os workflows e identifica problemas:
-  Ativação automática ao término de qualquer pipeline
-  Análise do status de conclusão (success/failure)
-  Captura de contexto completo da execução
-  Rastreamento de branch, commit e timestamp

#### **Criação Automática de Issues**
Gera tickets de alerta quando pipelines falham:
-  Criação automática de issue no GitHub Issues
-  Título descritivo com emoji de alerta (🚨)
-  Corpo detalhado incluindo:
- Nome do workflow que falhou
- ID da execução para rastreamento
- Branch e commit SHA
- Timestamp da falha
- Link direto para detalhes do workflow
-  Labels automáticas para priorização:
- `bug` - Indica bug de pipeline
- `ci-failure` - Marca como falha de CI/CD
- `urgent` - Prioridade alta para ação imediata
-  Notificação visual com alertas para equipe

#### **Registro de Métricas**
Documenta todas as execuções com dados para análise:
-  Tabela estruturada em Job Summary
-  Registro de métricas incluindo:
- Nome do workflow executado
- Status de conclusão (success/failure)
- Branch de origem
- Número da tentativa de execução
- Timestamp UTC da execução
-  Armazenamento histórico para auditoria
-  Facilita análise de tendências de falha

**Fluxo Automático:**
```
Workflow Concluído
  ↓
Análise de Status
  ├─ Failure? → Criar Issue + Labels + Detalhes
  └─ Sempre → Registrar Métricas em Summary
```

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
- **Testes Pós-Deploy** que validam a aplicação após deployment

**Segurança**
- CodeQL para análise estática
- DAST para testes dinâmicos
- Validações de entrada em todas as camadas
- Testes de segurança em staging/prod

**DevOps Avançado**
- 5 workflows GitHub Actions orchestrados
- Deploy automático em dev
- Aprovações manuais para prod
- Sistema de alertas automáticos
- Testes pós-deploy com Selenium
- Monitoramento em tempo real
- Relatórios de testes e cobertura

---

## Estrutura do Projeto

```
TP5-PB2/
├── src/
│   ├── main/java/com/biblioteca/
│   │   ├── app/               # Classe Main (BibliotecaWebApplication)
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
│   ├── gradle-ci.yml          # Build paralelo + testes
│   ├── codeQL.yml             # Análise estática
│   ├── dastScan.yml           # Testes dinâmicos OWASP
│   ├── deployTests.yml        # Deploy + Testes Pós-Deploy
│   └── alert.yml              # Monitoramento e alertas
├── README.md
├── MUDANCAS.md                # Documentação das melhorias implementadas
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

### Pipeline Deploy falhando
**Verificar:**
1. Artefato foi gerado corretamente no step de Build
2. Chrome está instalado no runner
3. Aplicação inicia sem erros (`java -jar`)
4. Testes Selenium conseguem acessar `http://localhost:7000`

---