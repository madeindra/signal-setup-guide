#!/bin/bash
set -ev

IP=`ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1'`
# echo $IP
pushd `dirname $0`

echo "EXTERNAL_HOST=$IP" > .env

# Start the services and wait for it.
docker-compose up -d --build
docker-compose ps

popd
