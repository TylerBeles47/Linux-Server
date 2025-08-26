#!/bin/bash

# Prometheus deployment script for homelab
echo "Deploying Prometheus monitoring stack..."

# Create the monitoring namespace
kubectl apply -f namespace.yaml

# Create persistent storage
kubectl apply -f prometheus-storage.yaml

# Deploy RBAC configuration
kubectl apply -f prometheus-rbac.yaml

# Deploy configuration
kubectl apply -f prometheus-config.yaml

# Deploy core components
kubectl apply -f prometheus-deployment.yaml
kubectl apply -f prometheus-service.yaml
kubectl apply -f prometheus-ingress.yaml

# Deploy node-exporter for node metrics
kubectl apply -f node-exporter-daemonset.yaml

# Deploy kube-state-metrics for cluster metrics
kubectl apply -f kube-state-metrics.yaml

echo "Waiting for deployments to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/prometheus -n monitoring
kubectl wait --for=condition=available --timeout=300s deployment/kube-state-metrics -n monitoring

echo "Getting service information..."
kubectl get services -n monitoring

echo ""
echo "Prometheus deployment completed!"
echo "Access Prometheus at:"
echo "- LoadBalancer IP (check 'kubectl get svc prometheus-loadbalancer -n monitoring')"
echo "- Or via ingress at http://prometheus.homelab.local (if ingress controller is configured)"
echo ""
echo "To check status: kubectl get pods -n monitoring"