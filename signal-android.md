# Signal Android
This guide is written by using Signal-Android branch Master version 4.53.6

## Requirement
Android Studio 3.5
JRE 1.8.0 
JVM OpenJDK 64
SDK 28

## How to
1. Convert your server ssl cert to pkcs#12 
`openssl pkcs12 -export -out keystore.pkcs12 -in fullchain.pem -inkey privkey.pem`

2. Use 'Keystore Explorer’ (MacOS software, you can try using another software), edit `whisper.store` files, insert your pk12 certificate there.

3. Open the project in Android Studio.

4. Update URL with own server in app/build.gradle (be sure to use https and don't include trailing slash)

3. update UNIDENTIFIED_SENDER_TRUST_ROOT (value is generated when creating UnidentifiedDelivery of server’s config.yml)

4. Comment out `distributionSha256Sum` on `gradle/wrapper/gradle-wrapper.properties`

5. Download `google-service.json` from Firebase, put it inside `app/`

6. Update `app/src/main/res/values/firebase_messaging.xml` according to value from `google-service.json`

7. Update `ATTACHMENT_DOWNLOAD_PATH` and `ATTACHMENT_UPLOAD_PATH` in `libsignal/service/src/main/java/org/whispersystems/signalservice/internal/push/PushServiceSocket.java` by deleting ‘attachments/‘ so attachment will be uploaded in root ( / ). 

## FAQ
Q: Why did I need to change the Attachment Path?

A: For now, I have no idea why you can't upload to a path except root, I've tried modifying every permission in AWS but to no avail. 
