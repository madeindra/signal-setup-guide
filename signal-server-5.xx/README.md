# Signal Server
Written using Signal Server v5.51 in Ubuntu 18.04 x64

## Requirement
- Git
- JDK 11
- Mavn

```
sudo apt update
sudo apt install git default-jre maven -y
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

2. Install (There would be some error here, the build will fail)
```
mvn install
```

3. Compile (There would be some error, but all service should be success on build)
```
mvn -e -B package
```

4. Create config.yml, [see example](./config.yml)

Steps to run the server will be updated
