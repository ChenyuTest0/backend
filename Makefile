#!make
# cSpell:ignore SYSNAME itadeploy PPROFILE
all: image push rmi push-archive

AWS_REGION := ap-northeast-1

SYSNAME := $(shell grep -E "^sysname=" gradle.properties | sed 's/^sysname=\(.*\)/\1/g')
IMAGE_NAME := $(SYSNAME)
IMAGE_NAME_SPECIFICATION := $(SYSNAME)-specification
SPECIFICATION_NAME := $(shell grep -E "^sysname=" gradle.properties | sed 's/^sysname=\(.*\)/\1/g')-specification

TMP_VERSION := $(shell grep -E "^sysVersion=" gradle.properties | sed 's/^sysVersion=\(.*\)/\1/g')
ECR_SNAPSHOT := $(shell grep -E "^awsEcrSnapshot=" gradle.properties | sed 's/^awsEcrSnapshot=\(.*\)/\1/g')
ECR_VERSION := $(shell grep -E "^awsEcrVersion=" gradle.properties | sed 's/^awsEcrVersion=\(.*\)/\1/g')
S3_SNAPSHOT := $(shell grep -E "^awsS3Snapshot=" gradle.properties | sed 's/^awsS3Snapshot=\(.*\)/\1/g')
S3_VERSION := $(shell grep -E "^awsS3Version=" gradle.properties | sed 's/^awsS3Version=\(.*\)/\1/g')

DOCKER_TAG_VERSION := $(if $(shell echo $(TMP_VERSION) | grep -E ".*SNAPSHOT"),snapshot,$(TMP_VERSION))

DOCKER_REGISTRY_URL := $(if $(shell echo $(TMP_VERSION) | grep -E ".*SNAPSHOT"),$(ECR_SNAPSHOT),$(ECR_VERSION))
S3_URL := $(if $(shell echo $(TMP_VERSION) | grep -E ".*SNAPSHOT"),$(S3_SNAPSHOT),$(S3_VERSION))

DOCKER_TAG := $(DOCKER_REGISTRY_URL)/$(IMAGE_NAME)
DOCKER_TAG_SPECIFICATION := $(DOCKER_REGISTRY_URL)/$(IMAGE_NAME_SPECIFICATION)

BASE_IMAGE_TAG := $(SYSNAME):$(DOCKER_TAG_VERSION)
BASE_IMAGE_SPECIFICATION_TAG := $(SPECIFICATION_NAME):$(DOCKER_TAG_VERSION)

.PHONY: test
test:
	@echo $(BASE_IMAGE_TAG)
	@echo $(BASE_IMAGE_SPECIFICATION_TAG)
	@echo $(DOCKER_TAG):$(DOCKER_TAG_VERSION)
	@echo $(DOCKER_TAG_SPECIFICATION):$(DOCKER_TAG_VERSION)
	@echo "s3://$(S3_URL)/$(SYSNAME)/$(DOCKER_TAG_VERSION)"

# pipelineなら ./gradlew clean の部分はビルド済みとなるので不要になる
# OpenAPIは環境別にビルドする必要があり、ビルド後にDockerImage作成用に作る
.PHONY: build-local
build-local:
	@./gradlew clean generateSwaggerUI spotlessApply build bootjar asciidoc
	@./gradlew generateSwaggerUI generateReDoc -PPROFILE=e2e
	@./gradlew prepareDockerBuildSpecification

.PHONY: build
build:
	@./gradlew generateSwaggerUI generateReDoc -PPROFILE=e2e
	@./gradlew prepareDockerBuildSpecification

.PHONY: image
image:
	@docker pull $(shell grep -E "^FROM" build/docker/api/Dockerfile | sed 's/^FROM \(.*\)/\1/g')
	@docker build build/docker/api \
		-t $(BASE_IMAGE_TAG) \
		--pull
	@docker pull $(shell grep -E "^FROM" build/docker/specification/Dockerfile | sed 's/^FROM \(.*\)/\1/g')
	@docker build build/docker/specification \
		-t $(BASE_IMAGE_SPECIFICATION_TAG) \
		--pull

.PHONY: push
push:
	@aws ecr get-login-password --region $(AWS_REGION) | docker login --username AWS --password-stdin https://$(DOCKER_REGISTRY_URL)
	docker tag $(BASE_IMAGE_TAG) $(DOCKER_TAG):$(DOCKER_TAG_VERSION)
	docker push $(DOCKER_TAG):$(DOCKER_TAG_VERSION)
	docker rmi $(DOCKER_TAG):$(DOCKER_TAG_VERSION)
	docker tag $(BASE_IMAGE_SPECIFICATION_TAG) $(DOCKER_TAG_SPECIFICATION):$(DOCKER_TAG_VERSION)
	docker push $(DOCKER_TAG_SPECIFICATION):$(DOCKER_TAG_VERSION)
	docker rmi $(DOCKER_TAG_SPECIFICATION):$(DOCKER_TAG_VERSION)

.PHONY: rmi
rmi:
	docker rmi $(BASE_IMAGE_TAG)
	docker rmi $(BASE_IMAGE_SPECIFICATION_TAG)

.PHONY: push-archive
push-archive:
	@aws s3 cp build/env-archive "s3://$(S3_URL)/$(SYSNAME)/$(DOCKER_TAG_VERSION)" --recursive

.PHONY: run
run:
	@docker run -it --rm $(BASE_IMAGE_TAG)

.PHONY: run-bash
run-bash:
	@docker run -it --rm --entrypoint="bash" $(BASE_IMAGE_TAG)
