#!/bin/bash
set -e

echo "🔴 Deleting Pi-hole Deployment..."
kubectl delete deploy pihole --ignore-not-found

echo "🔴 Deleting Pi-hole Service..."
kubectl delete svc pihole-service --ignore-not-found

echo "🔴 Deleting Pi-hole Pods..."
kubectl delete pod -l app=pihole --ignore-not-found

echo "🔴 Deleting Pi-hole ConfigMaps..."
kubectl delete configmap -l app=pihole --ignore-not-found

echo "🔴 Deleting Pi-hole Secrets..."
kubectl delete secret -l app=pihole --ignore-not-found

echo "🔴 Deleting Pi-hole Persistent Volume Claims (if any)..."
kubectl delete pvc -l app=pihole --ignore-not-found
kubectl delete pv -l app=pihole --ignore-not-found

echo "✅ Checking cleanup..."
kubectl get all -A | grep pihole || echo "Pi-hole fully deleted ✅"
