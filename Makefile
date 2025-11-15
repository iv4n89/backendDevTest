.PHONY: help start stop test test-k6 clean

# Variables
COMPOSE=docker-compose
BACKEND_SERVICE=backend
K6_SERVICE=k6

help: ## Shows this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  %-15s %s\n", $$1, $$2}'

start: ## Starts the server (build + up)
	$(COMPOSE) build $(BACKEND_SERVICE)
	$(COMPOSE) up -d influxdb grafana simulado $(BACKEND_SERVICE)
	@echo "✓ Server started at http://localhost:5000"

stop: ## Stops all services
	$(COMPOSE) down

test: ## Runs application tests
	cd backend && ./mvnw test

test-k6: ## Runs load tests with k6
	$(MAKE) start
	@echo "Waiting for services to be healthy..."
	@$(COMPOSE) exec $(BACKEND_SERVICE) sh -c 'timeout 90 sh -c "until wget -q --spider http://localhost:5000/actuator/health; do sleep 2; done"' || (echo "Backend failed to start" && exit 1)
	@echo "✓ Backend is healthy, running k6 tests..."
	$(COMPOSE) run --rm $(K6_SERVICE) run /scripts/test.js
	$(COMPOSE) down

clean: ## Cleans everything (containers, volumes and images)
	$(COMPOSE) down -v
	@docker rmi backenddevtest-backend 2>/dev/null || true