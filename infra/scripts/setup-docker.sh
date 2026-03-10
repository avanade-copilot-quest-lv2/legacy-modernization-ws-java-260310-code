#!/bin/bash
# setup-docker.sh — Install Docker Engine + Docker Compose on Ubuntu 22.04
# Run as root or with sudo
set -euo pipefail

# Skip if Docker is already installed
if command -v docker &> /dev/null; then
  echo "Docker is already installed: $(docker --version)"
  # Ensure the service is running
  systemctl enable docker
  systemctl start docker
  exit 0
fi

echo "=== Installing Docker Engine ==="

# Prerequisites
apt-get update
apt-get install -y ca-certificates curl gnupg

# Docker GPG key
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

# Docker apt repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker
apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Enable and start Docker
systemctl enable docker
systemctl start docker

# Add azureuser to docker group (no sudo needed for docker commands)
usermod -aG docker azureuser

echo "=== Docker installed successfully ==="
docker --version
docker compose version
