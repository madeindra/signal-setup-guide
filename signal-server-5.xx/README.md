# Signal Server
Written using Signal Server v5.51 in Ubuntu 18.04 x64

This guide is a WIP!

## Background

To run Signal server 5.xx out of the box / without substantial source modifications
it requires it to be run in an AWS EC2 instance.

The EC2 instance must be given an associated IAM role.

### Amazon AppConfig

Signal v5 uses a [DynamicConfigurationManager](https://github.com/signalapp/Signal-Server/blob/10cd60738ac086407f01d68700717b7e544739b0/service/src/main/java/org/whispersystems/textsecuregcm/storage/DynamicConfigurationManager.java#L45) 
to fetch config changes from an AWS AppConfig. 

You must setup and deploy an [AppConfig environment](https://docs.aws.amazon.com/appconfig/latest/userguide/what-is-appconfig.html) 
with the same parameters as the config file:
```
appConfig:
  application: ...
  environment:
  configuration:
```
The EC2 IAM role must have a policy granting access to the AppConfig.

### Amazon DynamoDb

Signal v5 uses a number of DynamoDb tables. You must create these and put them in the config
file, e.g.:

```
messageDynamoDb:
  region: us-east-1
  tableName: ...
```

Once again the EC2 IAM role must have a policy granting access to the tables.

## Install

- Git
- Maven

```
sudo apt update
sudo apt install git maven -y
```

## Dependencies
Note that in v5.xx, there are a lot of new dependecies that didn't exist in older version.

- [coTurn Server](https://github.com/coturn/coturn)
- [Redis](https://redis.io/)
- [PostgreSQL](https://www.postgresql.org/)
- [AWS DynamoDB](https://aws.amazon.com/dynamodb/)
- [AWS SQS](https://aws.amazon.com/sqs/)
- [AWS S3 Bucket](https://aws.amazon.com/s3/)
- [AWS Cloudfront CDN](https://aws.amazon.com/cloudfront/)
- [Google Cloud Platform](https://cloud.google.com/gcp)
- [Twilio SMS & Voice](https://www.twilio.com/)
- [Firebase](https://firebase.google.com/)
- [Recaptcha v3](https://www.google.com/recaptcha/admin/create)
- [Apple Push Notification](https://developer.apple.com/documentation/usernotifications/setting_up_a_remote_notification_server/establishing_a_certificate-based_connection_to_apns)
- [Micrometer](http://micrometer.io/)
- [Fixer](https://fixer.io/)

## Steps
1. Clone 
```
git clone https://github.com/signalapp/Signal-Server
cd Signal-Server
```

2. Fetch dependencies
```
mvn dependency:go-offline
```

3. Compile
```
mvn -e -B package
```

4. Create config.yml, [see example](./config.yml)

Steps to run the server will be updated

## Running with docker
**This docker containers only support some dependency that can be run locally.**

1. Copy `docker-compose.yml`

2. Start the compose
```
docker-compose start -D
```

3. Run the server with your config
