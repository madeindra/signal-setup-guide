# Signal Server
This guide is written by using Signal v2.92.

## Requirement
* JDK 11
* SSL Certificate for your domain

1. Create your own `config.yml`

2. Generate value for unidentifiedDelivery

3.	Build the server (I suggest you keep the DskipTests if you do a modification)
mvn clean install -DskipTests

4.	Run postgres, redis, coturn (I suggest you use docker-compose)

5.	Migrate databases
java -jar service/targetTextSecure-2.92.jar abusedb migrate service/config/config.yml
java -jar service/targetTextSecure-2.92.jar accountdb migrate service/config/config.yml
java -jar service/targetTextSecure-2.92.jar keysdb migrate service/config/config.yml
java -jar service/targetTextSecure-2.92.jar messagedb migrate service/config/config.yml

6.	Run the server (config.yml is from step 1)
java -jar service/targetTextSecure-<Version> server config.yml

## FAQ
Q: How do I disable AccountCrawler Error?

A: Disable accountDatabaseCrawler logging by commenting `environment.lifecycle().manage(accountDatabaseCrawler);` it is located in `service/src/main/java/org/whispersystems/textsecuregcm/WhisperServerService.java`.
