# Spring Kafka Connect App

A Spring Boot web application that provides a user-friendly interface for managing Kafka Connect JDBC connectors. Instead of using multiple terminal windows, create and manage source/sink connectors through a clean web UI.

## Features

- **JDBC Source Connectors** - Extract data from MySQL tables into Kafka topics
- **JDBC Sink Connectors** - Load data from Kafka topics into PostgreSQL  
- **Connector Management** - View, delete, and reset all connectors
- **Topic Monitoring** - View available Kafka topics via REST Proxy
- **Cluster Diagnostics** - Execute shell commands against Kafka/Zookeeper

## Architecture

```
┌───────────────────────────────────────────────────────────────────────┐
│                         Spring Boot App (:9001)                       │
│  ┌──────────────────────────────────────────────────────────────────┐ │
│  │                    ShellCommandController                         │ │
│  │  • Producer   • Consumer   • Connectors   • Topics                │ │
│  └──────────────────────────────────────────────────────────────────┘ │
└───────────────────┬───────────────────────────────────────────────────┘
                    │
         ┌──────────┴──────────┐
         ▼                     ▼
┌─────────────────┐   ┌─────────────────┐
│ Kafka Connect   │   │   REST Proxy    │
│   (:8083)       │   │    (:8082)      │
└────────┬────────┘   └─────────────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌───────┐  ┌──────────┐
│ MySQL │  │PostgreSQL│
│(:3306)│  │ (:5432)  │
└───────┘  └──────────┘
```

## Technology Stack

| Component | Version |
|-----------|---------|
| Spring Boot | 4.0.1 |
| Java | 21 |
| Apache Kafka Connect | 3.6.0 |
| Confluent Platform | 8.0.0 |
| Thymeleaf | (managed by Spring Boot) |
| Lombok | (managed by Spring Boot) |
| MySQL | 8.0 |
| PostgreSQL | 15 |

## Prerequisites

- Java 21+
- Maven 3.8+
- Docker & Docker Compose

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd spring-kafka-connect-app
```

### 2. Start the Infrastructure

```bash
docker-compose up -d
```

This starts:
- Kafka Broker (KRaft mode)
- Schema Registry
- Kafka Connect (with JDBC connectors)
- ksqlDB Server & CLI
- REST Proxy
- MySQL (source database)
- PostgreSQL (sink database)
- Flink SQL Client & Job Manager

### 3. Verify Services

```bash
docker-compose ps
```

Wait until all services are healthy.

### 4. Run the Application

```bash
mvn spring-boot:run
```

### 5. Access the UI

Open your browser at: [http://localhost:9001](http://localhost:9001)

## Configuration

Key settings in `application.properties`:

```properties
# Server
server.port=9001

# Source Connector (MySQL)
connector.source.class=io.confluent.connect.jdbc.JdbcSourceConnector
connection.source.url=jdbc:mysql://mysql:3306/inventory?user=dbuser&password=dbpassword

# Sink Connector (PostgreSQL)
connector.sink.class=io.confluent.connect.jdbc.JdbcSinkConnector
connection.sink.url=jdbc:postgresql://postgres:5432/inventory?user=dbuser&password=dbpassword

# Kafka Connect REST API
kafka.connector.url=http://localhost:8083/connectors
```

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Home page |
| `/createProducer` | POST | Show source connector form |
| `/showProducer` | POST | Create/update source connector |
| `/createConsumer` | POST | Show sink connector form |
| `/showConsumer` | POST | Create/update sink connector |
| `/showTopics` | GET | List all Kafka topics |
| `/showAllConnectors` | GET | List all connectors |
| `/deleteAllConnectors` | POST | Delete all connectors |
| `/resetTopicsForConnectors` | POST | Reset topics for all connectors |

## Project Structure

```
src/main/java/jamsam/shellexample/demo/
├── DemoApplication.java          # Spring Boot entry point
├── config/                       # Configuration beans
│   ├── SinkConnectorConfig.java
│   ├── SourceConnectorConfig.java
│   └── WebClientConfig.java
├── controllers/
│   └── ShellCommandController.java
├── model/
│   ├── Command.java
│   ├── ConsumerConfig.java
│   └── ProducerConfig.java
├── services/
│   ├── CommandService.java       # Shell command execution
│   ├── ConnectorUtils.java       # Common connector operations
│   ├── Connectors.java           # Connector management
│   ├── Consumer.java             # JDBC Sink connector creation
│   ├── Producer.java             # JDBC Source connector creation
│   └── Topics.java               # Kafka topic operations
└── utils/
```

## Docker Commands

```bash
# Start all services
docker-compose up -d

# Check service status
docker-compose ps

# View Kafka Connect logs
docker logs -f connect

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## Building for Production

```bash
mvn clean package
```

The built JAR will be at `target/ROOT.jar`.

## Status

This is a proof-of-concept application demonstrating how to manage Kafka Connect JDBC connectors through a web UI. Not intended for production use without additional security and error handling.

## Contact

Created by Samman Jamal - laobuda@gmail.com
