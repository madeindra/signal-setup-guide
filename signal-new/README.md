# Signal New Code
This guide is written by using Signal Server v3.21 & Signal Android v4.53.6

## Requirement
* Same requirement as old version
* GCP is currenlty disabled

## Steps for Signal Server

1. Clone signal server

```
git clone https://github.com/signalapp/Signal-Server/
```

2. Create your config using new `config.yml`

3. Update `zkgroup` dependency in `service/pom.xml` from `0.6.0` to `0.7.0`

```
...

<dependency>
    <groupId>org.signal</groupId>
    <artifactId>zkgroup-java</artifactId>
    <version>0.7.0</version>
</dependency>

...
```

4. Add this `import` statement and fix the `final PrivateKey key` variable in `service/src/main/java/org/whispersystems/textsecuregcm/gcp/CanonicalRequestSigner.java`

```
...

import java.security.KeyPair;

...

final PrivateKey key = ((KeyPair) pemReader.readObject()).getPrivate();

...
```

5. (Optional, for attachment bucket) Change `objectName` in `service/src/main/java/org/whispersystems/textsecuregcm/controllers/AttachmentControllerV2.java` and change the variable.

```
...

String   objectName   =    "attachments/" + String.valueOf(attachmentId);

...
```

6. (Optional, if you follow the previous step) Update the test file in `service/src/test/java/org/whispersystems/textsecuregcm/tests/controllers/AttachmentControllerTest.java` 

```
...

assertThat(descriptor.getKey()).isEqualTo("attachments/" + descriptor.getAttachmentIdString());

...
```

7. Compile the server code

```
mvn clean install -DskipTests
```

8. Generate `zkparams` value for the config.

```
java -jar service/target/TextSecureServer-3.21.jar zkparams
```

**Note**: The public key generated in this step may not suffice the length needed in the android, in that case, please add '=' to the end of the public key.

9. Generate `UnidentifiedDelivery` value for the config.

```
java -jar service/target/TextSecureServer-3.21.jar certificate -ca
```

Use the private key from previous command here. You can use any ID for the `key_ID`.

```
java -jar service/target/TextSecureServer-3.21.jar certificate --key priv_key_from_step_above --id key_ID
```

10. Setup Nginx & coTurn, it has the same steps as the old code.

11. Run PostgreSQL & Redis, using the `docker-compose` is recommended.

12. Run database migration

```
java -jar service/target/TextSecureServer-3.21.jar abusedb migrate service/config/config.yml

java -jar service/target/TextSecureServer-3.21.jar accountdb migrate service/config/config.yml

java -jar service/target/TextSecureServer-3.21.jar messagedb migrate service/config/config.yml
```

13. Run the server

```
java -jar service/target/TextSecureServer-3.21.jar server service/config/config.yml
```

## Steps for Signal Android

1. Clone signal android

```
git clone https://github.com/signalapp/Signal-Android/
```

2. Import your SSL certificate to `app/src/main/res/raw/whisper.store`. The password is "whisper" without quotation mark.

3. Remove `distributionSha256Sum` line from `gradle/wrapper/gradle-wrapper.properties`

4. Modify the server url in `app/build.gradle` under `defaultConfig`

```
...

buildConfigField "String", "SIGNAL_URL", "\"https://domain.com\""
buildConfigField "String", "STORAGE_URL", "\"https://domain.com\""
buildConfigField "String", "SIGNAL_CDN_URL", "\"https://xxx.cloudfront.net\""
buildConfigField "String", "SIGNAL_CDN2_URL", "\"https://xxx.cloudfront.net\""
buildConfigField "String", "SIGNAL_CONTACT_DISCOVERY_URL", "\"https://domain.com\""
buildConfigField "String", "SIGNAL_SERVICE_STATUS_URL", "\"https://domain.com\""
buildConfigField "String", "SIGNAL_KEY_BACKUP_URL", "\"https://domain.com\""

...

buildConfigField "String", "UNIDENTIFIED_SENDER_TRUST_ROOT", "\"change-to-unidentified-delivery-public-key\""
buildConfigField "String", "ZKGROUP_SERVER_PUBLIC_PARAMS", "\"change-to-zkparams-public-key\""
        
...
```

5. Select `Sync Project` when prompted after saving the build.gradle change. **Note**: Please ignore when prompted to update the dependency.

6. (Optional, for attachment bucket) Change `ATTACHMENT_UPLOAD_PATH`  in `libsignal/service/src/main/java/org/whispersystems/signalservice/internal/push/PushServiceSocket.java`

```
...

private static final String ATTACHMENT_UPLOAD_PATH   =   "";

...
```

7. Setup Firebase, it has the same steps as the old code.

8. Build the project.
