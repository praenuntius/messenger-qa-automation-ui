-include .env

ENV_FILE=.env
COMPOSE_FILE=$(or ${COMPOSE_FILES},-f docker-compose.yml)

export HOST_MACHINE_IP_ADDRESS = $(shell ip route | grep docker0 | cut -d " " -f 9 )

.DEFAULT_GOAL := help
help: ## Show this help
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z0-9_-]+:.*?## / {printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

create-network: ## Create fixed external network for Selenoid (run once, idempotent)
	docker network create qa-automation-ui_default || true

start: start-containers ## Start all containers for dev mode
stop: stop-containers ## Stop all started containers for dev mode
restart: stop start ## Restart all started containers for dev mode

build: # Build or rebuild services. make build nocache=1
	docker compose --env-file $(ENV_FILE) $(COMPOSE_FILE) --compatibility build --force-rm
build-without-cache: # Build or rebuild images
	docker compose --env-file $(ENV_FILE) $(COMPOSE_FILE) --compatibility build --force-rm --no-cache
up-containers:
	docker compose --env-file $(ENV_FILE) $(COMPOSE_FILE) --compatibility up $(RECREATE_MODE) --remove-orphans -d
down-containers:
	docker compose --env-file $(ENV_FILE) $(COMPOSE_FILE) --compatibility down
down-containers-and-remove:
	docker compose --env-file $(ENV_FILE) $(COMPOSE_FILE) --compatibility down --rmi all
start-containers:
	docker compose --env-file $(ENV_FILE) $(COMPOSE_FILE) --compatibility start
stop-containers:
	docker compose --env-file $(ENV_FILE) $(COMPOSE_FILE) --compatibility stop
restart-containers:
	docker compose --env-file $(ENV_FILE) $(COMPOSE_FILE) --compatibility restart

test-local: ## Run all tests locally with a local browser (remote=false)
	mvn test -Dremote=false