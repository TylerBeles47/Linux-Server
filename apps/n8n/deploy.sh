#!/bin/bash

echo "Deploying n8n to Kubernetes..."

# Apply namespace
kubectl apply -f namespace.yaml

# Apply storage
kubectl apply -f n8n-storage.yaml

# Apply deployment
kubectl apply -f n8n-deployment.yaml

# Apply service
kubectl apply -f n8n-service.yaml

echo "n8n deployment complete!"
echo "Check status with: kubectl get pods -n n8n"
echo "Access n8n at: kubectl port-forward -n n8n svc/n8n 5678:5678"