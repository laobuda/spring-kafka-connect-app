# Technical Application Analysis (Source & Config Only)

![Information Flow Diagram](/home/laobuda/.gemini/antigravity/brain/124b81b7-a324-4844-9a95-f1ddc65e01bc/information_flow_diagram_1766843653939.png)

This analysis is derived strictly from the Java source code, `application.properties`, and `docker-compose.yml`, ignoring the `README.md`.

## System Architecture

The application acts as a management layer for a Confluent-based data ecosystem. It coordinates data movement between MySQL and PostgreSQL via Kafka.

```mermaid
graph TD
    User([User / Browser]) <--> UI[Thymeleaf Web UI :9001]
    UI <--> Controller[ShellCommandController]
    
    subgraph "Spring Boot Application"
        Controller --> P[Producer Service]
        Controller --> C[Consumer Service]
        Controller --> Con[Connectors Service]
        Controller --> T[Topics Service]
        P & C & Con & T --> CS[CommandService]
    end
    
    subgraph "Infrastructure (Docker Compose)"
        KC[Kafka Connect :8083]
        RP[REST Proxy :8082]
        K[Kafka Broker :9092]
        MySQL[(MySQL :3306)]
        PSQL[(PostgreSQL :5432)]
        ZK[Zookeeper :2181]
    end

    P -- "POST /connectors (JDBC Source)" --> KC
    C -- "POST /connectors (JDBC Sink)" --> KC
    Con -- "DELETE /connectors/{name}" --> KC
    T -- "GET /topics" --> RP
    CS -- "Runtime.exec()" --> ZK_CLI["zookeeper-shell"]
    CS -- "Runtime.exec()" --> K_CLI["kafka-topics --list"]

    KC -- "Extract" --> MySQL
    KC -- "Load" --> PSQL
    KC <--> K
    RP <--> K
```

## Key Components

### 1. Management Services
- **Producer & Consumer Services**: Programmatically generate JSON configurations for Kafka Connect. They use the `JdbcSourceConnector` and `JdbcSinkConnector` classes specified in `application.properties`.
- **Connectors Service**: Provides bulk operations like "Delete All" and "Reset Topics" for all active Kafka Connectors.
- **Topics Service**: Interfaces with the `rest-proxy` container to list non-internal Kafka topics.

### 2. Infrastructure Integration
- **Kafka Connect (`connect` container)**: The central engine for data movement. It is loaded with custom JDBC drivers provided via the `./db-jars` volume.
- **Databases**: 
    - **MySQL**: The data source, initialized with scripts from `./scripts/db`.
    - **PostgreSQL**: The data sink.
- **Direct CLI Execution**: The `CommandService` uses `ProcessBuilder` to run local binaries (e.g., `/usr/bin/kafka-topics`) for cluster diagnostics, as defined in the properties file.

## Data Pipeline Flow
1. **Source**: Data is extracted from **MySQL** by a JDBC Source Connector.
2. **Transit**: Data is produced into **Kafka Topics**.
3. **Sink**: A JDBC Sink Connector consumes data from Kafka and inserts it into **PostgreSQL**.
4. **Management**: The Spring Boot app monitors and configures this entire lifecycle.
