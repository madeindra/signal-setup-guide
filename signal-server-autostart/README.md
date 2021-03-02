# Signal Systemd Service

This guide is for those who want to autostart their signal server in Linux environment by using systemd.

1. Create a file named `signal.service` on `/etc/systemd/system/`

2. Fill the file with the content from example file [signal-example.service](signal-example.service)

3. In the example file, `java` is installed in `usr/bin/`, make sure you change it according to your path to java. (Try running `which java` in terminal to know the path).

4. Change the `/path/to/signal` in the example file to where you store the server & change the jar file name, I wrote `x.xx` as the version, you migh need to change it.

5. Now reload the systemd with this command

```
sudo systemctl daemon-reload
```

6. Then you can enable autostart with this command
```
sudo systemctl start signal
```
