# Setup Guide for Signal
On this repository, you will find guides about Signal Setup that might help people who want to run their own signal server.

## Disclaimer
This repository is not an official Signal Guide and there is no relation to the team behind Signal. It's just a guide written outside my work-time to help people. 

These guides are provided as-is, please refer to Issue section of this readme file for more information regarding this guide.

Please do not contact me through my personal line or social media.

## Requirements
* SSL Certificate of your server's domain (For secure communication)
* Google Recaptcha (For anti-spam in authentication)
* Firebase (For push notification)
* Twilio (For SMS OTP & Voice Call)
* AWS S3 & Cloudfront (For Attachments & CDN. Can be subtituted with MinIO & LocalStack)
* AWS SQS (For CDS queue)
* AWS DynamoDB (For nosql database)
* Micrometer (For monitoring)
* Fixer (For payment)

## Content
What's proven works
* [Signal Server v2.92](./signal-server-2.92/)
* [Signal Server v3.21](./signal-server-3.21/)
* [Signal Server v4.xx](./signal-server-4.xx/)
* [Signal Server v5.xx](./signal-server-5.xx/)
* [Signal Android](./signal-android/)
* [Signal IOS](./signal-ios/)
* [Signal Desktop](./signal-desktop/)
* [Signal Dependency on Docker (Postgres & Redis)](./signal-docker/)
* [Voice/Video Call  (CoTurn)](./coturn-server/)
* [Port Forwarding (Nginx)](./signal-server-2.92/example-nginx.conf)
* [Signal Server with AWS S3 CDN & SQS](./signal-server-aws-setup/)
* [Signal Server v2.92 with MinIO CDN](./signal-minio/)
* [Signal Server v3.21 with MinIO CDN (Improvement & Cleaner)](./signal-server-3.21/MINIO.md)
* [Signal Server Autostart Script](./signal-server-autostart/)
* [Signal Server With Self Signed Certificate for Localhost](./signal-server-self-signed-certificate/)
* [Signal Server Without Twilio](./signal-server-no-twilio/)
* [Signal Server OTP With smsgateway.me](./signal-server-no-twilio/twillo%20replacement%20with%20smsgateway.me)

What's not proven work
* [Contact Discovery Service (CDS)](./signal-server-2.92/example-cds.yml), you still can use your signal server without CDS. If you want to implement CDS, please refer to [ the guide written by @konglomerat-id here](https://github.com/secure-sign/securesign-setup-guide)

## Updating
If you have do some modification to your code and want to get update from the official git repo, you can follow [UPDATING](./UPDATING.md)

## FAQ
Q: Will I be able to do ... with Signal?

A: My suggestion is first you need to setup normal signal server and check if it will fulfill your need. 

Q: My chat only goes one way or can't send chat even when the server already set properly.

A: Probably caused by keystore haven't contain your SSL certificate / certificate did not match the url / you have not setup the Unidentified Delivery properly in the client. Will be explained in each guide of the server & clients.

Q: How to setup Turn Server? I can't do voice/video call.

A: Probably related to your port, either it is not allowed by the firewall or it is not properly set. See my example config on [Coturn Server](./coturn-server/example-turnserver.conf)

Q: Can I subtitute AWS to MinIO?

A: I tried it here [signal-minio](./signal-minio/) using modification created by <a href="https://community.signalusers.org/t/amazon-s3-component-replacement-for-text-secure-server-local-installation/5375/18">kondal789rao
</a>. Someone also did a pull request here: <a href="https://github.com/signalapp/signal-server-2.92/pull/76">Use MinIO instead of the AWS</a>. Also you can try to look into <a href="https://github.com/localstack/localstack">Localstack</a> for local development.

Q: Can I disable / change Twilio? (by getting the generated OTP on the server)

A: Yes, it is possible, you need to change TwilioSmsSender Class, see example on [Signal Server Without Twilio](./signal-server-no-twilio/).

Q: Can I use Signal in localhost / internal IP / self-signed certificate (by trusting all certificate)

A: Yes, it is possible but it is not secure, see example on [Signal Server Self Signed Certificate](./signal-server-self-signed-certificate/).

Q: Can I remove Google Mobile Service (GMS) from Signal?

A: Yes! A Signal user named "tx-hw" did it on their <a href="https://github.com/tw-hx/Signal-Android/tree/4.60.5.0-FOSS">Github Fork of Signal-Android</a>. You can read more about it on their <a href="https://community.signalusers.org/t/ive-removed-gms-from-the-signal-website-build-its-now-completely-open-source/14382">Signal Community Post</a>

## Contributor
This guide has been written by the help of the developers from community.

* [konglomerat-id](https://github.com/on-premise-signal/signal-setup-guide) wrote the configuration and steps to setup signal server 2.55 
* [LiteSpeedDev](https://github.com/LiteSpeedDev/SignalApp-Setup) wrote nginx configuration and turn/stun server 
* [kondal789rao](https://community.signalusers.org/t/amazon-s3-component-replacement-for-text-secure-server-local-installation/5375/18) modified AWS to MinIO.
* [madeindra](https://github.com/madeindra/setup-guide) compiled the guide, improve the steps, and wrote more guide on uncovered topics
* [sinholic](https://github.com/sinholic) wrote iOS guide
* [amargarido](https://github.com/amargarido) helped to improve the readme
* [jacob-pro ](https://github.com/jacob-pro) added v4.xx guide & helped improving v5.xx
* [tronbiniyam](https://github.com/tronbiniyam) added sms otp subtitute to use to replace Twilio

You are welcome to contribute on this guide, please fork the repository and create a Pull Request.

## Issue
If you have any questions please create a discussion instead of issue.

You are recommended to <a href="https://github.com/madeindra/setup-guide/discussions/new">open a discussion thread here</a> if you face a difficulties to let the communities help you too and contributing to the communities in the process.
