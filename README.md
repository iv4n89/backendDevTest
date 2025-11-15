# Similar Products Service API

API REST desarrollada con Spring Boot que proporciona recomendaciones de productos similares basándose en llamadas a APIs externas. Implementa patrones de arquitectura hexagonal, programación reactiva y resiliencia.

## 🚀 Inicio Rápido

### Prerequisitos

- Java 21
- Docker y Docker Compose
- Make (opcional, pero recomendado)

### Levantar el Proyecto

El proyecto incluye un **Makefile** que simplifica la ejecución de todas las tareas comunes:

```bash
# Ver todos los comandos disponibles
make help

# Iniciar la aplicación con Docker Compose
make start

# Detener todos los servicios
make stop

# Limpiar contenedores, volúmenes e imágenes
make clean
```

La aplicación estará disponible en: **http://localhost:5000**

### Ejecución Manual (sin Make)

Si prefieres ejecutar los comandos directamente:

```bash
# Construir y levantar servicios
docker-compose build backend
docker-compose up -d influxdb grafana simulado backend

# Detener servicios
docker-compose down
```

### Usando wrapper MVN

Para ejecutar con el wrapper de Maven:

```bash
cd backend
./mvnw spring-boot:run
```

## 🧪 Testing

### Tests Unitarios y de Integración

```bash
# Usando Make
make test

# Manualmente
cd backend && ./mvnw test
```

### Tests de Carga (K6)

El proyecto incluye tests de rendimiento con K6, que se pueden ejecutar fácilmente:

```bash
# Usando Make (recomendado)
make test-k6

# Esto hará:
# 1. Levantar todos los servicios necesarios
# 2. Esperar a que el backend esté healthy
# 3. Ejecutar los tests de carga
# 4. Detener los servicios
```

Los resultados de los tests se pueden visualizar en **Grafana**: http://localhost:3000

## 📚 Documentación API

### Swagger UI

La documentación interactiva de la API está disponible en:

**http://localhost:5000/swagger-ui.html**

### OpenAPI JSON

El esquema OpenAPI en formato JSON está en:

**http://localhost:5000/api-docs**

### Endpoint Principal

```
GET /product/{productId}/similar
```

Obtiene una lista de productos similares para un producto dado.

**Ejemplo:**
```bash
curl http://localhost:5000/product/1/similar
```

## 🛠️ Stack Tecnológico

### Core
- **Java 21** - Última versión LTS con soporte para Virtual Threads
- **Spring Boot 3.5.7** - Framework principal
- **Spring WebFlux** - Programación reactiva

### Resiliencia y Performance
- **Resilience4j** - Circuit Breaker, Retry, Time Limiter
- **Caffeine Cache** - Caché en memoria de alta performance
- **WebClient** - Cliente HTTP reactivo con connection pooling

### Documentación
- **SpringDoc OpenAPI 2.8.4** - Documentación automática de API

### Monitorización
- **Micrometer + Prometheus** - Métricas de la aplicación
- **Spring Boot Actuator** - Health checks y endpoints de gestión
- **Grafana** - Visualización de métricas

### Testing
- **JUnit 5** - Framework de testing
- **MockWebServer** - Mock de servidores HTTP
- **WireMock** - Simulación de APIs externas
- **REST Assured** - Testing de APIs REST
- **DataFaker** - Generación de datos de test
- **K6** - Tests de carga y rendimiento

### Utilidades
- **Lombok** - Reducción de código boilerplate
- **Jakarta Validation** - Validación de datos

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

El proyecto implementa **Clean Architecture** con una clara separación de responsabilidades:

```
backend/src/main/java/com/test/backend/
│
├── domain/                          # Capa de Dominio (núcleo)
│   ├── model/                       # Entidades de dominio
│   │   └── ProductDetail.java
│   ├── port/
│   │   ├── input/                   # Puertos de entrada (use cases)
│   │   │   └── GetSimilarProductsUseCase.java
│   │   └── output/                  # Puertos de salida (Web clients)
│   │       ├── ProductPort.java
│   │       └── SimilarIdsPort.java
│   └── exception/                   # Excepciones de dominio
│
├── application/                     # Capa de Aplicación
│   ├── usecases/                    # Implementación de casos de uso
│   │   └── GetSimilarProductsUseCaseImpl.java
│
└── infrastructure/                  # Capa de Infraestructura
    ├── rest/                        # Adaptadores REST
    │   └── controller/
    │       └── ProductController.java
    ├── client/                      # Adaptadores HTTP
    │   ├── ProductApiClientReactive.java
    │   └── SimilarIdsApiClientReactive.java
    ├── config/                      # Configuración de Spring
    ├── dto/                         # DTOs de entrada/salida
    ├── mapper/                      # Mappers entre capas
    └── exception/                   # Manejo global de excepciones
```

## 🎯 Decisiones Técnicas

### 1. Programación Reactiva con WebFlux

**Decisión:** Se optó por usar **Spring WebFlux** y **programación reactiva** para las llamadas a APIs externas.

**Alternativa evaluada:** Virtual Threads (Project Loom - Java 21)

**Justificación:**

Tras varias pruebas y ejecución de tests se vio una gran diferencia en el rendimiento entre el uso de programación reactiva vs virtual threads para las llamadas a APIs externas.

La media de rendimiento con virtual threads estaba en:

```bash
          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io
  execution: local
     script: scripts/test.js
     output: influxdb=http://influxdb:8086/k6 (http://influxdb:8086)
  scenarios: (100.00%) 5 scenarios, 200 max VUs, 1m30s max duration (incl. graceful stop):
           * normal: 200 looping VUs for 10s (exec: normal)
           * notFound: 200 looping VUs for 10s (exec: notFound, startTime: 10s)
           * error: 200 looping VUs for 10s (exec: error, startTime: 20s)
           * slow: 200 looping VUs for 10s (exec: slow, startTime: 30s, gracefulStop: 10s)
           * verySlow: 200 looping VUs for 10s (exec: verySlow, startTime: 50s, gracefulStop: 30s)
running (1m01.2s), 000/200 VUs, 12239 complete and 600 interrupted iterations
normal   ✓ [======================================] 200 VUs  10s
notFound ✓ [======================================] 200 VUs  10s
error    ✓ [======================================] 200 VUs  10s
slow     ✓ [======================================] 200 VUs  10s
verySlow ✓ [======================================] 200 VUs  10s
    data_received..............: 3.0 MB 49 kB/s
    data_sent..................: 1.4 MB 23 kB/s
    http_req_blocked...........: avg=66.58µs  min=1.41µs   med=4.21µs   max=30.99ms p(90)=7µs      p(95)=9.31µs  
    http_req_connecting........: avg=58.11µs  min=0s       med=0s       max=30.92ms p(90)=0s       p(95)=0s      
    http_req_duration..........: avg=314.29ms min=364.63µs med=2.13ms   max=3.27s   p(90)=990.49ms p(95)=3.13s   
    http_req_receiving.........: avg=131.25µs min=14.08µs  med=94.2µs   max=10.01ms p(90)=186.79µs p(95)=259.27µs
    http_req_sending...........: avg=18.72µs  min=5.31µs   med=13.6µs   max=15.08ms p(90)=26.52µs  p(95)=37.03µs 
    http_req_tls_handshaking...: avg=0s       min=0s       med=0s       max=0s      p(90)=0s       p(95)=0s      
    http_req_waiting...........: avg=314.14ms min=306.3µs  med=2.01ms   max=3.27s   p(90)=990.1ms  p(95)=3.13s   
    http_reqs..................: 12839  209.884252/s
    iteration_duration.........: avg=829.85ms min=500.43ms med=502.38ms max=3.77s   p(90)=1.54s    p(95)=3.63s   
    iterations.................: 12239  200.075813/s
    vus........................: 148    min=0   max=200
    vus_max....................: 200    min=200 max=200
```

Una vez pasamos a programación reactiva tanto en el caso de uso como en los API clients:

```bash
          /\      |‾‾| /‾‾/   /‾‾/   
     /\  /  \     |  |/  /   /  /    
    /  \/    \    |     (   /   ‾‾\  
   /          \   |  |\  \ |  (‾)  | 
  / __________ \  |__| \__\ \_____/ .io

  execution: local
     script: /scripts/test.js
     output: influxdb=http://influxdb:8086/k6 (http://influxdb:8086)

  scenarios: (100.00%) 5 scenarios, 200 max VUs, 1m30s max duration (incl. graceful stop):
           * normal: 200 looping VUs for 10s (exec: normal)
           * notFound: 200 looping VUs for 10s (exec: notFound, startTime: 10s)
           * error: 200 looping VUs for 10s (exec: error, startTime: 20s)
           * slow: 200 looping VUs for 10s (exec: slow, startTime: 30s, gracefulStop: 10s)
           * verySlow: 200 looping VUs for 10s (exec: verySlow, startTime: 50s, gracefulStop: 30s)


running (1m00.3s), 000/200 VUs, 16434 complete and 600 interrupted iterations
normal   ✓ [======================================] 200 VUs  10s
notFound ✓ [======================================] 200 VUs  10s
error    ✓ [======================================] 200 VUs  10s
slow     ✓ [======================================] 200 VUs  10s
verySlow ✓ [======================================] 200 VUs  10s

    data_received..............: 3.5 MB 59 kB/s
    data_sent..................: 1.8 MB 30 kB/s
    http_req_blocked...........: avg=32.66µs  min=1.9µs    med=5.09µs   max=19.98ms p(90)=8.17µs   p(95)=11.89µs 
    http_req_connecting........: avg=24.68µs  min=0s       med=0s       max=19.9ms  p(90)=0s       p(95)=0s      
    http_req_duration..........: avg=98.28ms  min=493.44µs med=1.7ms    max=4.21s   p(90)=5.2ms    p(95)=157.06ms
    http_req_receiving.........: avg=119.83µs min=15.42µs  med=56.21µs  max=6.1ms   p(90)=236.45µs p(95)=400.21µs
    http_req_sending...........: avg=27.52µs  min=7.55µs   med=18.05µs  max=9.66ms  p(90)=36.98µs  p(95)=51.94µs 
    http_req_tls_handshaking...: avg=0s       min=0s       med=0s       max=0s      p(90)=0s       p(95)=0s      
    http_req_waiting...........: avg=98.14ms  min=427.86µs med=1.59ms   max=4.21s   p(90)=4.9ms    p(95)=156.98ms
    http_reqs..................: 17034  282.608321/s
    iteration_duration.........: avg=602.22ms min=500.58ms med=501.99ms max=4.71s   p(90)=505.88ms p(95)=663.38ms
    iterations.................: 16434  272.653818/s
    vus........................: 200    min=0   max=200
    vus_max....................: 200    min=200 max=200
```

Como se puede observar, el rendimiento mejora significativamente con programación reactiva, especialmente en escenarios con alta latencia o carga.

Esto se debe a que la programación reactiva permite manejar múltiples solicitudes concurrentes de manera más eficiente, sin bloquear hilos, lo que es especialmente beneficioso cuando se realizan llamadas a servicios externos que pueden tener latencias variables.

### 2. Resiliencia con Resilience4j

Se implementaron múltiples patrones de resiliencia:

- **Circuit Breaker**: Previene cascadas de fallos
- **Retry**: Reintentos con backoff exponencial
- **Time Limiter**: Timeouts configurables

### 3. Connection Pooling

Se configuró un pool de conexiones optimizado:
- 500 conexiones máximas
- Reutilización de conexiones
- Timeouts configurables por tipo de operación

### 4. Caché con Caffeine

- **Caffeine** se eligió sobre otras opciones por su rendimiento superior
- Configuración: 1000 entradas, TTL de 10 minutos
- Estadísticas habilitadas para monitorización

## 📊 Monitorización

### Health Check

```bash
curl http://localhost:5000/actuator/health
```

### Métricas Prometheus

```bash
curl http://localhost:5000/actuator/prometheus
```

## 📝 Configuración

La configuración principal está en `backend/src/main/resources/application.yaml`:

- **Server**: Puerto 5000
- **Cache**: Caffeine con 1000 entradas, TTL 10min
- **WebClient**: Connection pool de 500 conexiones
- **Resilience4j**: Circuit breaker, retry y time limiter
- **APIs externas**: Base URL del servicio simulado
