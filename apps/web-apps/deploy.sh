#!/bin/bash

echo "Deploying Homepage Dashboard..."

# Apply namespace first
kubectl apply -f homepage-deployment.yaml

# Apply config
kubectl apply -f homepage-config.yaml

# Apply ingress
kubectl apply -f homepage-ingress.yaml

echo "Waiting for homepage to be ready..."
kubectl wait --for=condition=available --timeout=300s deployment/homepage -n web-apps

echo ""
echo "Homepage deployment complete!"
echo ""
echo "Access your homepage at:"
echo "- http://homelab.local"  
echo "- http://home.homelab.local"
echo ""
echo "Make sure to add these entries to your DNS or /etc/hosts file"