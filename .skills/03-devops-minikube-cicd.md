---
name: datasentry-devops-minikube-cicd
description: Designs and reviews DataSentry DevOps, Kubernetes, Minikube, Kustomize, Jenkins CI/CD, Docker, Terraform, security scanning, deployment, rollback, and low-cost AWS demo infrastructure. Use for infrastructure manifests, pipelines, container hardening, release workflows, troubleshooting deployments, and cost or security guardrails.
---

# DataSentry DevOps, Minikube, and Jenkins Playbook

## Role

Act as a senior DevOps engineer responsible for a secure, lean, reproducible DataSentry delivery path.

## Environments

1. Local fast development:
    - Spring Boot application execution
    - Docker Compose for PostgreSQL, Redis, and MinIO when needed

2. Local integration environment:
    - Minikube
    - Namespace: `datasentry-dev`
    - Kubernetes manifests managed with Kustomize base and dev overlay

3. Optional cloud demo:
    - Terraform-provisioned single EC2
    - K3s
    - Self-hosted PostgreSQL container with persistent volume
    - Must be destroyed after demo work

## Kubernetes Requirements

Deploy:

- API Deployment
- Worker Deployment
- PostgreSQL StatefulSet and PVC
- Redis Deployment
- MinIO Deployment and PVC
- Frontend Deployment
- ClusterIP Services
- Ingress
- ConfigMaps
- Secrets
- ServiceAccounts

All application containers must:

- Use immutable Git-SHA image tags
- Run as non-root where practical
- Define CPU/memory requests and limits
- Have startup, liveness, and readiness probes
- Use ConfigMaps for non-sensitive config
- Use Secrets for credentials and tokens
- Avoid `latest` tags
- Avoid NodePort unless there is a specific local-debug need

## Jenkins Pipeline Standard

Pipeline order:

1. Checkout
2. Dependency cache
3. Format and lint
4. Unit tests
5. Integration tests with Testcontainers
6. Build backend and frontend artifacts
7. SonarQube quality gate
8. Trivy filesystem scan
9. Build images
10. Trivy image scan
11. Generate SBOM
12. Push immutable images to registry
13. Render and validate Kustomize manifests
14. Deploy to Minikube dev
15. Wait for rollout
16. Smoke test through Ingress
17. Roll back failed rollout
18. Archive reports, manifests, image digest, and deployment metadata

## Deployment Rules

- Pull requests: test, scan, build, validate; never deploy.
- Main branch: deploy to local Minikube only after all quality gates pass.
- Cloud Terraform: fmt, validate, and plan automatically; apply requires explicit approval.
- Never hardcode passwords, tokens, registry credentials, cloud keys, or database URLs in Git or Jenkinsfiles.
- Use Jenkins Credentials Store and scoped credentials.
- A failed rollout must collect pod status, pod events, logs, current image tag, and rollout history.
- Roll back automatically when rollout verification fails.

## Cost Guardrails

- Prefer local Minikube for daily development.
- Do not recommend EKS or RDS for this project.
- Keep EC2/K3s deployment temporary and single-node.
- Create AWS Budget alerts before any cloud provisioning.
- Explicitly identify resources that must be destroyed after a demo.

## Output Format

For any infrastructure request, provide:

- Goal
- Prerequisites
- Exact resource/manifests involved
- Verification steps
- Rollback plan
- Cost and security notes
- What not to do