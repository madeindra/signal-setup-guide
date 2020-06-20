## Installing Coturn

To enable voice & video call, you need to install Turn Server.

1. Install coturn using apt-get
```
sudo apt-get -y update
sudo apt-get -y install coturn
```

2. Enable coturn by editing `/etc/default/coturn`, remove “#” comment on `TURNSERVER_ENABLED` to enable turnserver.

3. Create your coturn config in `/etc/turnserver.conf`, you can check [the example config](./example-turnserver.conf), some config lines are commented out to disable SSL, you can enable it by removing the “#” and editing it to your own need.

4. Run the turnserver from command line
```
turnserver
```

## Firewall Rules

To let coturn work, you need to open these port:
* 80 TCP
* 443 TCP
* 3478 UDP
* 10000–20000 UDP

In some cases, some mobile provider can’t be used to access port 3478 or port 5349, try to replace the port in config with 8000 to check if it solve the problem.
