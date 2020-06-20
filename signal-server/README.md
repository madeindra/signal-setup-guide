# Signal Server
This guide is written by using Signal v2.92.

## Requirement
* JDK 11
* SSL Certificate for your domain
* Google Recaptcha
* Firebase Cloud Messaging (It used to be GCM)
* Twilio
* AWS

1. Create your own `config.yml`, put it inside `signal-server/service/config/`. You can take a look at [the example here](./example-signal.yml).

2.	Build the server (I suggest you keep the DskipTests if you do a modification)
```
mvn clean install -DskipTests
```

3. Generate value for UnidentifiedDelivery

You will get key pair using this command (keep the keypair, you will need it for Android and for the next step)
```
java -jar service/target/TextSecureServer-2.92.jar certificate -ca
```

Use the Private key to generate certificate (key id can be random, i use 1234)
```
java -jar service/target/TextSecureServer-2.92.jar certificate --key <priv_key_from_step_above> --id <the_key_ID>
```

4.	Run postgres, redis, coturn (I suggest you use docker-compose)

5.	Migrate databases
```
java -jar service/target/TextSecureServer-2.92.jar abusedb migrate service/config/config.yml
java -jar service/target/TextSecureServer-2.92.jar accountdb migrate service/config/config.yml
java -jar service/target/TextSecureServer-2.92.jar keysdb migrate service/config/config.yml
java -jar service/target/TextSecureServer-2.92.jar messagedb migrate service/config/config.yml
```

6.	Run the server (config.yml is from step 1)
```
java -jar service/target/TextSecureServer-2.92.jar server service/config/config.yml
```

7. To run the server in the background (run continously), use nohup
```
nohup java -jar service/target/TextSecureServer-2.92.jar server service/config/config.yml > /dev/null &
```

## Configuring Nginx & Generating SSL Certificate with Let's Encrypt

If you already has your SSL Certificate, you can use [the example nginx config](./example-nginx.conf) on the `Step 4` and skip the `Step 6 - 9`.

1. Install nginx on your system
```
sudo apt install nginx     
```

2. Install Certbot for Let's Encrypt
```
sudo add-apt-repository ppa:certbot/certbot
sudo apt-get install python-certbot-nginx
```

3. Allow Nginx to be accessed from outside using Firewall. The `Nginx Full` argument will create a rule that allow port 80 and port 443, you can change it to `Nginx HTTP` to allow only port 80 or change it to `Nginx HTTPS` to allow only port 443
```
sudo ufw allow 'Nginx Full'
```

4. Create your server configuration in `/etc/nginx/sites-available/domain.com`. 
```
server {
  listen 80;
  listen [::]:80;

  server_name domain.com, www.domain.com;
}
```

5. Create a symbolic link from your configuration to sites-enabled.
```
sudo ln -s /etc/nginx/sites-available/domain.com /etc/nginx/sites-enabled/domain.com
```

6. Reload your nginx to apply the new configuration
```
sudo nginx -s reload

```

7. Run certbot to generate SSL Certificate
```
certbot --nginx -d domain.com -d www.domain.com
``` 

8. When asked `Please choose whether or not to redirect HTTP traffic to HTTPS, removing HTTP access.` You are recommended to choose `2: Redirect`. After the process is done your certificate will be located in
```
etc
└── letsencrypt
    └── live 
        └── domain.com
            └── *.pem
```

9. Update your nginx config to suits your need, you can take a look at [the example here](./example-nginx.conf).

10.  Check if your configuration is correct
```
sudo nginx -t
```

11. If there's no error, you can reload your nginx to apply the new configuration
```
sudo nginx -s reload
```

If you are having a hard time configuring NginX manually, you can try generating configuration file by using <a href="https://nginxconfig.io/">nginxconfig.io</a>.

## FAQ
Q: How do I get Recapthca?

A: You register for <a href="https://www.google.com/recaptcha/intro/v3.html">Google Recaptcha v3</a>, put your server's domain there.

Q: How do I get GCM?

A: Setup <a href="https://firebase.google.com/">Firebase Cloud Messaging</a>, you will get the key from there.

Q: What AWS service do i need?

A: CDN Cloudfront, S3 Bucket, SQS FIFO type, and IAM for the key.

Q: How do I disable AccountCrawler Error?

A: Disable accountDatabaseCrawler logging by commenting `environment.lifecycle().manage(accountDatabaseCrawler);` it is located in `service/src/main/java/org/whispersystems/textsecuregcm/WhisperServerService.java`. Rebuild the server then rerun it after you did the modification.

```
...

apnSender.setApnFallbackManager(apnFallbackManager);
environment.lifecycle().manage(apnFallbackManager);
environment.lifecycle().manage(pubSubManager);
environment.lifecycle().manage(pushSender);
environment.lifecycle().manage(messagesCache);
// environment.lifecycle().manage(accountDatabaseCrawler);

...
```

Q: I got an error from updating profile name / avatar.

A: Some error presumebly caused by constant change in client or server, starting fresh by reseting database & storage usually stopped this. Some people reported that modifying nginx configuration by adding `$uri` to the end of `proxy_pass`, but unfortunately I can't reproduce the desired result.
