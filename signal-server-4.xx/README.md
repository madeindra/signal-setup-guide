# Signal Server
Written using Signal Server v4.97 in Ubuntu 20.04 x64

This guide is a WIP! It will allow the server to launch and register devices, nothing
more has been tested yet.

## Dependencies

- https://docs.docker.com/engine/install/ubuntu/
- `sudo apt install -y git maven`

Download the server and build:
```
git clone https://github.com/signalapp/Signal-Server.git
cd Signal-Server
git checkout v4.97
mvn -DskipTests package
```

## Databases:

```
docker run -d --restart unless-stopped --name accountdb -e "POSTGRES_PASSWORD=postgres" -e "POSTGRES_DB=accountdb" -p 5432:5432 postgres:11
docker run -d --restart unless-stopped --name abusedb -e "POSTGRES_PASSWORD=postgres" -e "POSTGRES_DB=abusedb" -p 5433:5432 postgres:11
docker run -d --restart unless-stopped --name messagedb -e "POSTGRES_PASSWORD=postgres" -e "POSTGRES_DB=messagedb" -p 5434:5432 postgres:11
```
(Alternatively use https://github.com/mrts/docker-postgresql-multiple-databases to run all in one container/port)

You must run the database migrations to initialise them:

```
cd Signal-Server/service/target
java -jar TextSecureServer-4.97.jar accountdb migrate config.yml
java -jar TextSecureServer-4.97.jar abusedb migrate config.yml
java -jar TextSecureServer-4.97.jar messagedb migrate config.yml
```

## Redis

```
docker run -d --restart unless-stopped --name redis -e "IP=0.0.0.0" -p 7000-7005:7000-7005 grokzen/redis-cluster:latest
```

## Proxy

Signal server expects the HTTP requests to arrive with the `X-Forwarded-For` header - i.e. via a proxy

You can use Nginx with the [example config](./nginx.conf), 
(note the nginx config should be changed to add your certificates):

```
docker run -d --restart unless-stopped --name nginx --net="host" -v nginx.conf:/etc/nginx/nginx.conf:ro nginx
```

## Signal Configuration

An example config is [provided](./config.yml), however many fields must be initialised
with valid keys and certificates for it to start - even if you are not using those particular
services. Instructions to do so are included in the config file.

## Launch

```
java -jar Signal-Server/service/target/TextSecureServer-4.97.jar server config.yml
```

(See [signal-server-autostart](../signal-server-autostart/) to install as a service)
