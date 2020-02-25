# Redis sentinel via docker-compose

This is useful for local development and CI , you have 1 master, 1 slave and 1 sentinel with docker containers.

*Note*: The sentinel Dockerfile is heavily borrowed and inspired by [redis-cluster-with-sentinel](https://github.com/mustafaileri/redis-cluster-with-sentinel).


## How to start

```
> ./start.sh`
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

## How to verify

```
> redis-cli -p 26379
127.0.0.1:26379> sentinel master mysentinel
 1) "name"
 2) "mysentinel"
 3) "ip"
 4) "192.168.1.5"
 5) "port"
 6) "6379"
 7) "runid"
 8) ""
 ...
```

`192.168.1.5` is my IP but you should find your local en0 IP.


## License

[MIT](./LICENSE)
