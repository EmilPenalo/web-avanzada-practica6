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