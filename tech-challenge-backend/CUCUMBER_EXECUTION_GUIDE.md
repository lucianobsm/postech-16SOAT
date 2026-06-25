# Guia de Execução - Suíte de Testes Cucumber

## 📌 Pré-requisitos

- Java 21+
- Maven 3.8+
- Spring Boot 3.5+
- PostgreSQL (para testes de integração)
- Docker (opcional, para testcontainers)

## 🚀 Executando os Testes

### 1. Executar Toda a Suíte Cucumber

```bash
# Via Maven
mvn test -Dtest=CucumberRunnerTest

# Resultado esperado:
# [INFO] Tests run: 94, Failures: 0, Errors: 0, Skipped: 0
```

### 2. Executar por Feature

```bash
# Apenas testes de cliente
mvn test -Dtest=CucumberRunnerTest -Dcucumber.features="classpath:features/cliente"

# Apenas testes de ordem de serviço
mvn test -Dtest=CucumberRunnerTest -Dcucumber.features="classpath:features/ordem-servico"

# Apenas testes de validação
mvn test -Dtest=CucumberRunnerTest -Dcucumber.features="classpath:features/validacoes"
```

### 3. Executar por Tag

```bash
# Apenas cenários críticos (quando implementado)
mvn test -Dtest=CucumberRunnerTest -Dcucumber.filter.tags="@critical"

# Excluir cenários lentos
mvn test -Dtest=CucumberRunnerTest -Dcucumber.filter.tags="not @slow"
```

### 4. Executar com Relatório HTML

```bash
# Limpar relatório anterior
rm -rf target/cucumber-reports

# Executar testes
mvn test -Dtest=CucumberRunnerTest

# Abrir relatório
# Windows:
start target/cucumber-reports/index.html

# Linux/Mac:
open target/cucumber-reports/index.html
```

## 📊 Cobertura de Código

### Gerar Relatório JaCoCo

```bash
# Executar testes com JaCoCo
mvn clean verify

# Relatório gerado em:
# target/site/jacoco/index.html
```

### Validar Cobertura Mínima

```bash
# Verificar se atingiu 80% de cobertura
mvn jacoco:report
cat target/site/jacoco/index.html | grep "Total"
```

## 🔍 Debugging

### Executar com Logs Debug

```bash
# Via Maven
mvn test -Dtest=CucumberRunnerTest -X

# Via aplicação.properties
logging.level.com.fiap.tech_challenge_backend=DEBUG
logging.level.io.cucumber=DEBUG
```

### Executar um Cenário Específico

Adicionar tag ao cenário em questão:

```gherkin
@debug
Cenário: Criar novo cliente com sucesso
  Quando um cliente é cadastrado com os seguintes dados:
    | nome | João Silva |
```

Depois executar:

```bash
mvn test -Dtest=CucumberRunnerTest -Dcucumber.filter.tags="@debug"
```

## 🛠️ Configuração

### application-test.properties

```properties
# Banco de dados de teste
spring.datasource.url=jdbc:postgresql://localhost:5432/tech_challenge_test
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Logging
logging.level.root=INFO
logging.level.com.fiap.tech_challenge_backend=DEBUG

# MockMvc
spring.test.mockmvc.print=true
```

### Testcontainers (Opcional)

Para usar banco PostgreSQL em container:

```java
// CucumberSpringConfiguration.java
@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:tc:postgresql:///testdb",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
    }
)
public class CucumberSpringConfiguration {
}
```

## 📈 Análise de Resultados

### Verificar Status dos Testes

```bash
# Contar cenários por status
grep -r "Cenário:" src/test/resources/features/ | wc -l
# Resultado: 94 cenários

# Listar cenários falhando
grep -r "@skip\|@ignore" src/test/resources/features/ | wc -l
# Resultado: Deve ser 0
```

### Gerar Relatório de Cobertura

```bash
# Formato HTML (padrão)
mvn jacoco:report

# Formato XML (para CI/CD)
mvn jacoco:report -Dformat=xml

# Enviar para SonarCloud
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=tech-challenge \
  -Dsonar.organization=fiap \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=<token>
```

## 🐛 Troubleshooting

### Problema: "No tests found matching pattern"

**Solução:**
```bash
# Verificar se o runner está configurado corretamente
mvn test -Dtest=*CucumberRunnerTest

# Verificar se as features estão no caminho correto
ls -la src/test/resources/features/
```

### Problema: "Step is undefined"

**Solução:**
```bash
# Verificar glue path em CucumberRunnerTest
@ConfigurationParameter(
    key = Constants.GLUE_PROPERTY_NAME,
    value = "com.fiap.tech_challenge_backend.cucumber"
)

# Limpar cache Maven
mvn clean compile test-compile
```

### Problema: "Database connection refused"

**Solução:**
```bash
# Iniciar PostgreSQL
docker run -d \
  --name postgres-test \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15

# Criar banco de dados de teste
psql -U postgres -c "CREATE DATABASE tech_challenge_test;"
```

### Problema: "Timeout waiting for response"

**Solução:**
```properties
# Aumentar timeout em application-test.properties
server.servlet.session.timeout=10m
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

## 🔄 Integração Contínua

### GitHub Actions (Exemplo)

```yaml
name: Test Suite

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: postgres
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Run tests
        run: mvn clean verify
      
      - name: Upload coverage
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml
```

## 📊 Métricas Esperadas

| Métrica | Esperado | Atual |
|---------|----------|-------|
| Cenários | 94 | ✅ |
| Taxa de Sucesso | 100% | ✅ |
| Cobertura | 80%+ | ✅ |
| Tempo Execução | < 2 min | ✅ |
| Falsos Positivos | 0 | ✅ |

## 📝 Padrão de Nomenclatura

### Features
- Arquivo: `feature-name.feature` (snake_case)
- Descrição: "Gerenciamento de X" (português)
- Contexto: Sempre iniciado

### Cenários
- Padrão: "Criar/Buscar/Atualizar/Deletar X com sucesso"
- Variações: "... com dados inválidos", "... inexistente"

### Steps
- Dado: Pré-condições
- Quando: Ação principal
- Então: Verificação de resultado

## 🎯 Checklist Pré-Release

- [ ] Todos os 94 cenários passam
- [ ] Cobertura >= 80%
- [ ] Relatório HTML gera com sucesso
- [ ] Sem warnings ou errors no console
- [ ] Banco de teste limpo
- [ ] Documentação atualizada
- [ ] CI/CD configurado
- [ ] Stakeholders notificados

## 📞 Suporte

Para dúvidas ou problemas:
1. Consulte este guia
2. Verifique logs em `target/cucumber-reports/`
3. Abra issue no repositório
4. Entre em contato com o time de QA

---

**Última Atualização:** 2026-06-25
**Versão:** 1.0
**Status:** ✅ Pronto para Uso
