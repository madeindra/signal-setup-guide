# Postgres & Redis on Docker

This is signal dependencies setup on Docker to make development easier.

| container_name          | image                                 | port | info                                              |
|-------------------------|---------------------------------------|------|---------------------------------------------------|
| signal_account_database | postgres:11                           | 5431 |                                                   |
| signal_keys_database    | postgres:11                           | 5432 |                                                   |
| signal_message_database | postgres:11                           | 5433 |                                                   |
| signal_abuse_database   | postgres:11                           | 5434 |                                                   |
| redis_main              | redis:5                               | 6379 |                                                   |
| redis_replication       | redis:5                               | 6380 |                                                   |
| signal_adminer          | adminer:latest                        | 8000 | optional use for database management with web GUI |
| redis_commander         | rediscommander/redis-commander:latest | 8001 | optional use for cache management with web GUI    |

## Starting up

```
docker-compose up -d
```

## For macOS users
If yor server is macOs you are limited to specific folders to mount docker volumes, so for example
the volume on "./redis_master:/data" may not be mounted and produce an error since the relative path "./" is not likely to be included in the inclusion list.
In order set up the volumes properly refer to:
https://docs.docker.com/docker-for-mac/osxfs/#namespaces
