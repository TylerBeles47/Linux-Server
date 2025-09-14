# Homelab Kubernetes Cluster

A complete K3s homelab setup with centralized dashboard, monitoring, automation, and network services.

## 📋 Dashboard

![Homelab Dashboard](screenshots/homelab-dashboard.png)

*Centralized Homer dashboard providing easy access to all homelab services*

## 🏗️ Infrastructure

**Controller Node**: ThinkPad laptop
**Worker Nodes**: 
- Raspberry Pi (running Pi-hole)
- EliteDesk (planned for n8n automation)

## 🌐 Networking
- **CNI**: Cilium
- **Load Balancer**: MetalLB
- **DNS**: Pi-hole on Raspberry Pi

## 📊 Monitoring & Management

### Cluster Monitoring
![K9s Terminal Interface](screenshots/k9s-monitoring.png)

*Real-time cluster monitoring and management with k9s*

### Metrics Stack
- **Metrics**: Prometheus (http://192.168.1.202:9090)
- **Visualization**: Grafana (http://192.168.1.201:3000)
- **Cluster Management**: k9s terminal interface

## 🚀 Services

### Infrastructure Services
- **Pi-hole**: DNS ad-blocking (http://192.168.1.200)
- **Homepage Dashboard**: Centralized service access (http://homelab.local)

### Automation & Workflows
- **n8n**: Workflow automation platform (http://n8n.homelab.local)

### Applications
- **Password Manager**: 
  - **Language**: Java 11
  - **Database**: PostgreSQL  
  - **Security**: AES-256 encryption with PBKDF2 key derivation
  - **Features**: Multi-user support, encrypted password storage

## 🔧 Access Points

| Service | URL | Description |
|---------|-----|-------------|
| Homepage | http://homelab.local | Main dashboard |
| Pi-hole | http://192.168.1.200 | DNS management |
| n8n | http://n8n.homelab.local | Workflow automation |
| Prometheus | http://192.168.1.202:9090 | Metrics collection |
| Grafana | http://192.168.1.201:3000 | Metrics visualization |

## 🚧 Work in Progress

### Media Server
- **Status**: Planning/Development
- **Purpose**: Media streaming and management
- **Planned Features**: Movie/TV show streaming, media library organization

This homelab is actively being developed with more services and automation planned.