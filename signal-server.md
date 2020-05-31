# Signal Server
This guide is written by using Signal v2.92.

## Requirement
* JDK 11
* SSL Certificate for your domain
* Google Recaptcha
* Firebase Cloud Messaging (It used to be GCM)
* Twilio
* AWS

1. Create your own `config.yml`, put it inside `signal-server/service/config/`

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
