#!/bin/bash

# Grafana deployment script for homelab
echo "Deploying Grafana..."

# Create storage (commented out for now, using emptyDir)
# kubectl apply -f grafana-storage.yaml

# Deploy configuration
kubectl apply -f grafana-config.yaml

# Deploy Grafana
kubectl apply -f grafana-deployment.yaml
kubectl apply -f grafana-service.yaml

echo "Waiting for Grafana deployment to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/grafana -n monitoring

echo "Getting service information..."
kubectl get services -n monitoring | grep grafana

echo ""
echo "Grafana deployment completed!"
echo "Default credentials:"
echo "Username: admin"
echo "Password: homelab123"
echo ""
echo "Access Grafana at:"
echo "- LoadBalancer IP on port 3000 (check 'kubectl get svc grafana-loadbalancer -n monitoring')"
echo ""
echo "Prometheus data source is automatically configured!"
echo "To check status: kubectl get pods -n monitoring | grep grafana"