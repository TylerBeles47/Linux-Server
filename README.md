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

## 🌐 Networking Architecture

### Cilium CNI with L2 LoadBalancer
This homelab uses **Cilium** as the Container Network Interface (CNI) with L2 announcement policies for LoadBalancer services, providing direct Layer 2 network integration.

**Why Cilium over traditional solutions:**
- **Enhanced Security**: eBPF-based network policies with Layer 7 filtering
- **Performance**: Kernel-bypass networking with eBPF programs
- **Observability**: Built-in network monitoring and troubleshooting
- **L2 Announcements**: Native LoadBalancer support without external dependencies

### LoadBalancer Service Architecture
Services are exposed using **LoadBalancer** type with dedicated IP addresses via Cilium's L2 announcement policy:

```yaml
# Cilium L2 Announcement Policy
interfaces: [^eth[0-9]+$, ^enp[0-9]+s[0-9]+$, ^enx[a-f0-9]+$]
loadBalancerIPs: true
```

**Benefits over NodePort/Ingress:**
- **Direct Access**: Services accessible via dedicated IPs without port conflicts
- **Simplified Routing**: No need for Ingress controllers or reverse proxies  
- **Network Integration**: Services appear as first-class network citizens
- **Protocol Support**: Full TCP/UDP support (not just HTTP/HTTPS)

### Service IP Allocation
| Service | IP Address | Ports | Purpose |
|---------|------------|-------|---------|
| Pi-hole | 192.168.1.210 | 53 (DNS), 80 (Web) | Network-wide DNS filtering |
| Cilium Ingress | 192.168.1.211 | 80, 443 | HTTP/HTTPS ingress (future use) |
| Grafana | 192.168.1.212 | 3000 | Metrics visualization |
| Prometheus | 192.168.1.213 | 9090 | Metrics collection |
| Homer Dashboard | 192.168.1.225 | 80 | Centralized service dashboard |

### DNS Configuration
- **Primary DNS**: Pi-hole (192.168.1.210) for ad-blocking and local resolution
- **Network Integration**: Direct LoadBalancer IPs eliminate DNS complexity
- **Failover**: Kubernetes CoreDNS for cluster-internal resolution

## 📊 Monitoring & Management

### Cluster Monitoring
![K9s Terminal Interface](screenshots/k9s.png)

*Real-time cluster monitoring and management with k9s*

### Metrics Stack
- **Metrics**: Prometheus (http://192.168.1.213:9090)
- **Visualization**: Grafana (http://192.168.1.212:3000)
- **Cluster Management**: k9s terminal interface

## 🚀 Services

### Infrastructure Services
- **Pi-hole**: DNS ad-blocking (http://192.168.1.210)
- **Homer Dashboard**: Centralized service access (http://192.168.1.225)

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
| Homer Dashboard | http://192.168.1.225 | Main service dashboard |
| Pi-hole | http://192.168.1.210 | DNS management & ad-blocking |
| Prometheus | http://192.168.1.213:9090 | Metrics collection |
| Grafana | http://192.168.1.212:3000 | Metrics visualization |
| Cilium Ingress | http://192.168.1.211 | HTTP/HTTPS ingress (available) |

## 🚧 Work in Progress

### Media Server
- **Status**: Planning/Development
- **Purpose**: Media streaming and management
- **Planned Features**: Movie/TV show streaming, media library organization

This homelab is actively being developed with more services and automation planned.