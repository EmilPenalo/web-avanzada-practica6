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
sudo cp practica6/haproxy.cfg.p6 /etc/haproxy/haproxy.cfg

# Reiniciando el servicio de HAProxy
sudo service haproxy stop && sudo service haproxy start

# Instalando docker
sudo apt install docker-compose

# Instalando certificados SSL
sudo apt-get update
sudo apt-get install software-properties-common
sudo add-apt-repository ppa:certbot/certbot
sudo apt-get update
sudo apt-get install certbot
