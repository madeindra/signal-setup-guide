# Signal-Guide
On this repository, you will find guides about Signal Setup that might help people who want to run their own signal server.

## Requirements
* SSL Certificate for your servers's domain 
* Google Recaptcha
* Firebase
* Twilio
* Amazon Web Service (AWS)

## Content
What's proven works
* Signal Server
* Signal Android
* Signal Desktop
* Signal Dependency on Docker
* Coturn Server
* Nginx Server

What's not proven work
* Contact Discovery Service (CDS), you still can use your signal server without CDS.

At the moment, I don't have Apple Developer license, so I won't be able to help anything regarding Signal iOS.

## FAQ
Q: Will I be able to do ... with Signal?

A: My suggestion is first you need to setup normal signal server and check if it will fulfill your need. 

Q: Can I subtitute AWS to MinIO?

A: Someone did that in a pull request here: <a href="https://github.com/signalapp/Signal-Server/pull/76">Use MinIO instead of the AWS</a>

Q: Can I disable / change Twilio? (by getting the generated OTP on the server)

A: Yes, it is possible, you need to change TwilioSmsSender Class, see example on <a href="https://github.com/indrawp/Signal-Guide/blob/master/selfsignedcertificate-PushServiceSocket.java">nosmsotp-TwilioSmsSender.java</a>

Q: Can I use Signal in localhost / internal IP / self-signed certificate (by trusting all certificate)

A: Yes, it is possible but it is not secure, see example on <a href="https://github.com/indrawp/Signal-Guide/blob/master/selfsignedcertificate-PushServiceSocket.java">selfsignedcertificate-PushServiceSocket.java</a>

## Cotributing
You are welcome to contribute on this guide. If you have any questions please write an issues and I will try to help. If you have any suggestion you can
<a href = "mailto:indrawp@protonmail.com">reach me via email</a>.

## Donating
You can donate to me if you think this guide has helped you, your donation will greatly appreciated.

<a href ="https://www.paypal.me/indrawp" target="_blank"><img src="https://raw.githubusercontent.com/stefan-niedermann/paypal-donate-button/master/paypal-donate-button.png" height="75"></a>
