#!/usr/bin/env bash
echo "Configuración básica de HAProxy"

# Eliminando todas las aplicaciones Java.
killall java

# Creando una copia del archivo actual.
if [ ! -e "/etc/haproxy/haproxy.cfg.original" ]; then
  echo "Creando archivo de backup"
  sudo cp /etc/haproxy/haproxy.cfg /etc/haproxy/haproxy.cfg.original
fi

# Copiando el archivo de configuración de HAProxy.
sudo cp ~/balanceadores-java-haproxy/configuracion-haproxy/ubuntu/haproxy.cfg.simple /etc/haproxy/haproxy.cfg

# Reiniciando el servicio de HAProxy
sudo service haproxy stop && sudo service haproxy start

# Instalando docker
if ! command -v docker &> /dev/null; then
    echo "Installing Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    echo "Docker installed successfully. Please log out and log back in to apply user permissions."
    exit
fi
