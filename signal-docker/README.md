# Postgres & Redis on Docker

I add the PostgreSQL to the docker-compose. The Redis on docker-compose is created by [xavierchow](https://github.com/xavierchow/docker-redis-sentinel).

## How to start

```
./start.sh
```

If you see the console output as follows, you have set up the sentinel!

```
docker-compose ps
     Name                   Command               State                 Ports
--------------------------------------------------------------------------------------------
redis_master     docker-entrypoint.sh redis ...   Up      0.0.0.0:6379->6379/tcp
redis_sentinel   sentinel-entrypoint.sh           Up      0.0.0.0:26379->26379/tcp, 6379/tcp
redis_slave      docker-entrypoint.sh redis ...   Up      6379/tcp, 0.0.0.0:6380->6380/tcp

```

## For macOS users
If yor server is macOs you are limited to specific folders to mount docker volumes, so for example
the volume on "./redis_master:/data" may not be mounted and produce an error since the relative path "./" is not likely to be included in the inclusion list.
In order set up the volumes properly refer to:
https://docs.docker.com/docker-for-mac/osxfs/#namespaces
