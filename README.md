# Kanban Todo API

Uma API RESTful para gerenciar quadros Kanban com colunas e tarefas.

## 🚀 Tecnologias e Ferramentas

- **Linguagem:** Java 17
- **Framework Principal:** Spring Boot 3.5.0
- **Banco de Dados:** PostgreSQL
- **Migrations:** Flyway
- **Persistência:** Spring Data JPA / Hibernate
- **Documentação da API:** SpringDoc OpenAPI (Swagger UI)
- **Testes:** JUnit 5, Mockito
- **Utilitários:** Lombok

## 🏗️ Arquitetura e Estrutura

O projeto segue o padrão arquitetural **Package-by-Feature** (Pacotes por Funcionalidade). Cada entidade principal possui seu próprio pacote contendo Controller, Service, Repository, DTOs e as entidades de domínio:

- `com.example.todoapi.board`: Gerenciamento de Quadros.
- `com.example.todoapi.column`: Gerenciamento das Colunas do Kanban, contendo regras automáticas de reordenação.
- `com.example.todoapi.task`: Gerenciamento das Tarefas, permitindo reordenação e transição entre colunas.

## ⚙️ Pré-requisitos

- **Docker** e **Docker Compose** instalados na sua máquina.
- *(Opcional)* Java 17 e Maven se quiser rodar a aplicação nativamente fora do Docker.

## 🛠️ Configuração e Execução (Via Docker)

A maneira mais simples de rodar todo o ambiente (Banco de Dados + API) é utilizando o Docker Compose fornecido.

### 1. Subir a Aplicação e o Banco de Dados
Na raiz do projeto, execute:
```bash
docker compose up -d --build
```
O Docker irá:
1. Baixar a imagem do PostgreSQL e subir o banco de dados. O banco é exposto localmente na porta `5434` (evitando conflitos com serviços locais).
2. Fazer o build da aplicação Spring Boot via `Dockerfile`.
3. Subir a API na porta `8080` e rodar as migrações (Flyway) automaticamente ao iniciar.

### 2. Verificar os Logs
Se quiser acompanhar os logs da aplicação subindo:
```bash
docker compose logs -f api
```

*(Nota: Se preferir rodar apenas o banco via Docker e a API nativamente pela sua IDE, execute `docker compose up -d postgres` e certifique-se de atualizar seu `application.properties` para apontar para `localhost:5434` no banco `todoapi` com usuário/senha `postgres`).*

## 📚 Documentação da API (Swagger)

A API possui documentação automática gerada pelo OpenAPI.
Com a aplicação em execução, acesse através do navegador:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## 🧪 Testando a API

### Testes Automatizados
O projeto conta com uma cobertura completa de **Testes Unitários** utilizando JUnit e Mockito para a camada de Controladores e Serviços, garantindo a lógica de reordenação dinâmica e persistência de dados.

Para rodar os testes:
```bash
./mvnw test
```

## ✨ Regras de Negócio Importantes Implementadas

- **Posicionamento Automático:** Inserir uma coluna/tarefa numa posição específica (ex: Posição 0) empurra todas as entidades subsequentes para frente atomicamente.
- **Transição entre Colunas (Drag & Drop simulado):** Tarefas podem ser movidas dinamicamente entre colunas mantendo a consistência de posições tanto na coluna de origem quanto na de destino.
- **Deleção em Cascata:** A exclusão de um Quadro apaga automaticamente todas as suas Colunas e Tarefas.
- **Restrição de Posição Única:** Duas tarefas não podem ocupar a mesma posição dentro da mesma coluna.
