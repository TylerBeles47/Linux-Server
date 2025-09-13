# Homelab Kubernetes Cluster

A Kubernetes homelab setup running across multiple nodes with monitoring and network services.

## 🏗️ Infrastructure

**Controller Node**: ThinkPad laptop
**Worker Nodes**: 
- Raspberry Pi (running Pi-hole)
- EliteDesk (planned for n8n automation)

## 🌐 Networking
- **CNI**: Cilium
- **Load Balancer**: MetalLB
- **DNS**: Pi-hole on Raspberry Pi

## 📊 Monitoring Stack
- **Metrics**: Prometheus
- **Visualization**: Grafana

## 🔐 Applications

### Password Manager
- **Language**: Java 11
- **Database**: PostgreSQL
- **Security**: AES-256 encryption with PBKDF2 key derivation
- **Features**: Multi-user support, encrypted password storage
- **Location**: `/apps/passwordmanager/`

### n8n Automation
- **Platform**: Workflow automation
- **Node**: EliteDesk
- **Purpose**: Process automation and integrations

## 🚧 Work in Progress

### Media Server
- **Status**: Planning/Development
- **Purpose**: Media streaming and management
- **Planned Features**: Movie/TV show streaming, media library organization

This homelab is actively being developed with more services and automation planned.