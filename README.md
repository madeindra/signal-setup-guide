# Setup Guide for Signal
On this repository, you will find guides about Signal Setup that might help people who want to run their own signal server.

## Requirements
* SSL Certificate of your server's domain (For secure communication, can be bypassed but it will be less secure)
* Google Recaptcha (For anti-spam in authentication)
* Firebase (For push notification, if not used, the notification will not work correctly)
* Twilio (For SMS OTP, can be by passsed by priting the OTP to another means to user)
* Amazon Web Service (For Profile Picture / Avatar, Attachments, and CDS Queue. Can be subtituted with MinIO & LocalStack)

## Content
What's proven works
* [Signal Server](./signal-server/)
* [Signal Android](./signal-android/)
* [Signal Desktop](./signal-desktop/)
* [Postgres & Redis for Signal Dependency on Docker](./signal-docker/)
* [Turn Server with CoTurn](./coturn-server/)
* [Nginx for Port Forwarding](./signal-server/example-nginx.conf)
* [Signal with AWS S3 CDN & SQS](./signal-server-aws-setup/)
* [Signal with MinIO CDN](./signal-minio/)

What's not proven work
* [Contact Discovery Service (CDS)](./signal-server/example-cds.yml), you still can use your signal server without CDS.
* [Signal IOS](./signal-ios/), still on-going.

## FAQ
Q: Will I be able to do ... with Signal?

A: My suggestion is first you need to setup normal signal server and check if it will fulfill your need. 

Q: My chat only goes one way or can't send chat even when the server already set properly.

A: Probably caused by keystore haven't contain your SSL certificate / certificate did not match the url / you have not setup the Unidentified Delivery properly in the client. Will be explained in each guide of the server & clients.

Q: How to setup Turn Server? I can't do voice/video call.

A: Probably related to your port, either it is not allowed by the firewall or it is not properly set. See my example config on [Coturn Server](./coturn-server/example-turnserver.conf)

Q: Can I subtitute AWS to MinIO?

A: I tried it here [signal-minio](./signal-minio/) using modification created by <a href="https://community.signalusers.org/t/amazon-s3-component-replacement-for-text-secure-server-local-installation/5375/18">kondal789rao
</a>. Someone also did a pull request here: <a href="https://github.com/signalapp/Signal-Server/pull/76">Use MinIO instead of the AWS</a>. Also you can try to look into <a href="https://github.com/localstack/localstack">Localstack</a> for local development.

Q: Can I disable / change Twilio? (by getting the generated OTP on the server)

A: Yes, it is possible, you need to change TwilioSmsSender Class, see example on [Signal Server Without Twilio](./signal-server-no-twilio/).

Q: Can I use Signal in localhost / internal IP / self-signed certificate (by trusting all certificate)

A: Yes, it is possible but it is not secure, see example on [Signal Server Self Signed Certificate](./signal-server-self-signed-certificate/).

Q: Can I remove Google Mobile Service (GMS) from Signal?

A: Yes! A Signal user named "tx-hw" did it on their <a href="https://github.com/tw-hx/Signal-Android/tree/4.60.5.0-FOSS">Github Fork of Signal-Android</a>. You can read more about it on their <a href="https://community.signalusers.org/t/ive-removed-gms-from-the-signal-website-build-its-now-completely-open-source/14382">Signal Community Post</a>

## To-Dos

* CDS Server: Find server with Intel SGX to try to setup custom CDS Server.
* Signal iOS: Still can't afford Apple Dev License, find someone with license.
* Load Test: I want to see the performance of the custom server under stress, will update later.
* **Lately there's a lot of news about leaking in Intel SGX, I think Signal will stop using this method in near future, not to mention that it's not required if you have no concern about contact's privacy**

## Cotributing
You are welcome to contribute on this guide. If you have any questions please write an issues and I will try to help.

You are recommended to <a href="https://github.com/madeindra/setup-guide/issues/new/choose">open an issue here</a> if you face a difficulties to let the communities help you too and contributing to the communities in the process.

## Donating
You can donate to me if you think this guide has helped you, your donation will greatly appreciated.

Bitcoin (BTC):

<img src="https://raw.githubusercontent.com/madeindra/setup-guide/master/.resources/btc-address.png" alt="16xY3wXVNmRqBzbyQpE3VpYawbjuXkyDe2" width="150">

<a href = "bitcoin:16xY3wXVNmRqBzbyQpE3VpYawbjuXkyDe2">16xY3wXVNmRqBzbyQpE3VpYawbjuXkyDe2</a>
