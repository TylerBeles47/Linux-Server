#!/bin/bash

echo "Deploying Jellyfin media server..."

kubectl apply -f namespace.yaml
kubectl apply -f jellyfin-storage.yaml
kubectl apply -f jellyfin-deployment.yaml
kubectl apply -f jellyfin-service.yaml
kubectl apply -f jellyfin-ingress.yaml

echo "Jellyfin deployment completed!"
echo "Access Jellyfin at: http://jellyfin.homelab.local"
echo "Check status with: kubectl get pods -n jellyfin"