# Postgres & Redis on Docker

This is signal dependencies setup on Docker to make development easier.

| container_name          | image                                 | port     | info                                              |
|-------------------------|---------------------------------------|----------|---------------------------------------------------|
| signal_database         | postgres:11                           |   5432   |                                                   |
| redis_main              | redis:5                               |   6379   |                                                   |
| redis_replication       | redis:5                               |   6380   |                                                   |
| nginx                   | nginx:1.20                            |  80,443  | optional, if you already have ssl cert            |
| coturn                  | coturn/coturn:4.5                     |   3478   | optional, easier setup                            |
| signal_adminer          | adminer:latest                        |   8000   | optional, database management with web GUI        |
| redis_commander         | rediscommander/redis-commander:latest |   8001   | optional, cache management with web GUI           |

## Starting up

```
docker-compose up -d
```

## Postgres Multiple Database
If you encounter `bad permission` error during running it up, please modify the permission of the script 

```
sudo chmod +x create-multiple-postgresql-databases.sh
```

## Nginx
If you already have SSL Certicicate & Private key, please put it inside `nginx` directory and rename it to `certificate.crt` and `private.key`, then modify `default.conf` to use your own domain name in it.

If you prefer non docker containerized nginx, please refer to Signal-Server guide.

## CoTurn
If you want easier setup, you can use coturn in container, be sure to update `coturn.conf` inside `coturn` directory to change domain name and secret used for STUN/TURN.

If you prefer non docker containerized nginx, please refer to Coturn-Server guide.

## For macOS users
If yor server is macOs you are limited to specific folders to mount docker volumes, so for example
the volume on "./redis_master:/data" may not be mounted and produce an error since the relative path "./" is not likely to be included in the inclusion list.
In order set up the volumes properly refer to:
https://docs.docker.com/docker-for-mac/osxfs/#namespaces

## Known Issue
Docker have issue with `firewalld` with `iptables`, I suggest you use `ufw` instead to prevent such issue.
