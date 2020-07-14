# Postgres & Redis on Docker

This is signal dependencies setup on Docker to make development easier.

## Starting up

```
docker-compose up -d
```

## For macOS users
If yor server is macOs you are limited to specific folders to mount docker volumes, so for example
the volume on "./redis_master:/data" may not be mounted and produce an error since the relative path "./" is not likely to be included in the inclusion list.
In order set up the volumes properly refer to:
https://docs.docker.com/docker-for-mac/osxfs/#namespaces
