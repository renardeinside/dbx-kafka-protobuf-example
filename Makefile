ifneq (,$(wildcard ./.env))
    include .env
    export
endif

build:
	sbt clean assembly

dev-deploy: build
	dbx deploy --files-only --no-package --no-rebuild --deployment-file=conf/deployment.yaml


create-scope:
	databricks --profile=$(DATABRICKS_CONFIG_PROFILE) secrets create-scope --scope=dbx

add-secrets:
	databricks --profile=$(DATABRICKS_CONFIG_PROFILE) \
		secrets write \
		--scope=dbx --key=kafkaBootstrapServers \
		--string-value=$(KAFKA_BOOTSTRAP_SERVERS_TO_SECRETS)

launch-generator: dev-deploy
	dbx launch \
		--job=dbx-kafka-protobuf-example-generator \
		--as-run-submit --trace

launch-processor: dev-deploy
	dbx launch \
		--job=dbx-kafka-protobuf-example-processor \
		--as-run-submit --trace