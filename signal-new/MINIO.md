# MinIO Implementation

To use MinIO instead of AWS in v3.21 you can implement this cleaner way rather than the old way

## Signal Server

`service/src/main/java/org/whispersystems/textsecuregcm/s3/PostPolicyGenerator.java`

```diff
                                        "    {\"bucket\": \"%s\"},\n" +
                                        "    {\"key\": \"%s\"},\n" +
                                        "    {\"acl\": \"private\"},\n" +
+                                       "    {\"success_action_status\": \"200\"},\n" +
                                        "    [\"starts-with\", \"$Content-Type\", \"\"],\n" +
                                        "    [\"content-length-range\", 1, " + maxSizeInBytes + "],\n" +
                                        "\n" +

```

`service/src/main/java/org/whispersystems/textsecuregcm/WhisperServerService.java`

```diff
import org.whispersystems.websocket.WebSocketResourceProviderFactory;
import org.whispersystems.websocket.setup.WebSocketEnvironment;

+ import com.amazonaws.ClientConfiguration;
+ import com.amazonaws.client.builder.AwsClientBuilder;
+ import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;

...
    environment.getObjectMapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
    environment.getObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

+    ClientConfiguration clientConfiguration = new ClientConfiguration();
+    clientConfiguration.setSignerOverride("AWSS3V4SignerType");
+
    JdbiFactory jdbiFactory = new JdbiFactory(DefaultNameStrategy.CHECK_EMPTY);
    Jdbi        accountJdbi = jdbiFactory.build(environment, config.getAccountsDatabaseConfiguration(), "accountdb");
    Jdbi        messageJdbi = jdbiFactory.build(environment, config.getMessageStoreConfiguration(), "messagedb" );
...

    environment.lifecycle().manage(accountDatabaseCrawler);
    environment.lifecycle().manage(remoteConfigsManager);

    AWSCredentials         credentials               = new BasicAWSCredentials(config.getCdnConfiguration().getAccessKey(), config.getCdnConfiguration().getAccessSecret());
    AWSCredentialsProvider credentialsProvider       = new AWSStaticCredentialsProvider(credentials);
-    AmazonS3               cdnS3Client               = AmazonS3Client.builder().withCredentials(credentialsProvider).withRegion(config.getCdnConfiguration().getRegion()).build();
+    AmazonS3               cdnS3Client               = AmazonS3ClientBuilder.standard().withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(config.getCdnConfiguration().getEndpoint(), config.getCdnConfiguration().getRegion())).withPathStyleAccessEnabled(true).withClientConfiguration(clientConfiguration).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    PostPolicyGenerator    profileCdnPolicyGenerator = new PostPolicyGenerator(config.getCdnConfiguration().getRegion(), config.getCdnConfiguration().getBucket(), config.getCdnConfiguration().getAccessKey());
    PolicySigner           profileCdnPolicySigner    = new PolicySigner(config.getCdnConfiguration().getAccessSecret(), config.getCdnConfiguration().getRegion());

    ServerSecretParams        zkSecretParams         = new ServerSecretParams(config.getZkConfig().getServerSecret());
    ServerZkProfileOperations zkProfileOperations    = new ServerZkProfileOperations(zkSecretParams);
    ServerZkAuthOperations    zkAuthOperations       = new ServerZkAuthOperations(zkSecretParams);
    boolean                   isZkEnabled            = config.getZkConfig().isEnabled();

-    AttachmentControllerV1 attachmentControllerV1    = new AttachmentControllerV1(rateLimiters, config.getAwsAttachmentsConfiguration().getAccessKey(), config.getAwsAttachmentsConfiguration().getAccessSecret(), config.getAwsAttachmentsConfiguration().getBucket());
+    AttachmentControllerV1 attachmentControllerV1    = new AttachmentControllerV1(rateLimiters, config.getAwsAttachmentsConfiguration().getEndpoint(), config.getAwsAttachmentsConfiguration().getAccessKey(), config.getAwsAttachmentsConfiguration().getAccessSecret(), config.getAwsAttachmentsConfiguration().getBucket());
    AttachmentControllerV2 attachmentControllerV2    = new AttachmentControllerV2(rateLimiters, config.getAwsAttachmentsConfiguration().getAccessKey(), config.getAwsAttachmentsConfiguration().getAccessSecret(), config.getAwsAttachmentsConfiguration().getRegion(), config.getAwsAttachmentsConfiguration().getBucket());
    AttachmentControllerV3 attachmentControllerV3    = new AttachmentControllerV3(rateLimiters, config.getGcpAttachmentsConfiguration().getDomain(), config.getGcpAttachmentsConfiguration().getEmail(), config.getGcpAttachmentsConfiguration().getMaxSizeInBytes(), config.getGcpAttachmentsConfiguration().getPathPrefix(), config.getGcpAttachmentsConfiguration().getRsaSigningKey());
    KeysController         keysController            = new KeysController(rateLimiters, keys, accountsManager, directoryQueue);
```

`service/src/main/java/org/whispersystems/textsecuregcm/configuration/AwsAttachmentsConfiguration.java`

```diff
  @JsonProperty
  private String region;

+  @NotEmpty
+  @JsonProperty
+  private String endpoint;

  public String getAccessKey() {
    return accessKey;
  }

  public String getAccessSecret() {
    return accessSecret;
  }

  public String getBucket() {
    return bucket;
  }

  public String getRegion() {
    return region;
  }
+
+  public String getEndpoint() {
+    return endpoint;
+  }
}
```

`service/src/main/java/org/whispersystems/textsecuregcm/configuration/CdnConfiguration.java`

```diff
  @JsonProperty
  private String region;

+  @NotEmpty
+  @JsonProperty
+  private String endpoint;

  public String getAccessKey() {
    return accessKey;
  }

  public String getAccessSecret() {
    return accessSecret;
  }

  public String getBucket() {
    return bucket;
  }

  public String getRegion() {
    return region;
  }

+  public String getEndpoint() {
+    return endpoint;
+  }
+
}
```

`service/src/main/java/org/whispersystems/textsecuregcm/s3/UrlSigner.java`

```diff
+ import java.io.IOException;
+ import java.security.InvalidKeyException;
+ import java.security.NoSuchAlgorithmException;
+
+ import org.xmlpull.v1.XmlPullParserException;
+
+ import io.minio.MinioClient;
+ import io.minio.errors.MinioException;

import java.net.URL;
import java.util.Date;
public class UrlSigner {

  private static final long   DURATION = 60 * 60 * 1000;

-  private final AWSCredentials credentials;
+  private final String endpoint;
+  private final String accessKey;
+  private final String accessSecret;
  private final String bucket;

-  public UrlSigner(String accessKey, String accessSecret, String bucket) {
-    this.credentials = new BasicAWSCredentials(accessKey, accessSecret);
+  public UrlSigner(String endpoint, String accessKey, String accessSecret, String bucket) {
+	this.endpoint = endpoint;
+	this.accessKey = accessKey;
+	this.accessSecret = accessSecret;
    this.bucket      = bucket;
  }

- public URL getPreSignedUrl(long attachmentId, HttpMethod method, boolean unaccelerated) {
-    AmazonS3                    client  = new AmazonS3Client(credentials);
-    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, String.valueOf(attachmentId), method);
-
-    request.setExpiration(new Date(System.currentTimeMillis() + DURATION));
-    request.setContentType("application/octet-stream");
-    if (unaccelerated) {
-      client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
-    } else {
-      client.setS3ClientOptions(S3ClientOptions.builder().setAccelerateModeEnabled(true).build());
-    }
-    return client.generatePresignedUrl(request);
-  }


+  public String getPreSignedUrl(long attachmentId, HttpMethod method) throws InvalidKeyException, NoSuchAlgorithmException, IOException, XmlPullParserException, MinioException {
+  		String request = geturl(bucket, String.valueOf(attachmentId), method);
+  		return request;
+  }

+  public String geturl(String bucketname, String attachmentId, HttpMethod method) throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException, MinioException {
+
+	    String url = null;
+
+		MinioClient minioClient = new MinioClient(endpoint, accessKey, accessSecret);
+	    try {
+	    	if(method==HttpMethod.PUT){
+	    		url = minioClient.presignedPutObject(bucketname, attachmentId, 60 * 60 * 24);
+	    	}
+	    	if(method==HttpMethod.GET){
+	    		url = minioClient.presignedGetObject(bucketname, attachmentId);
+	    	}
+	        System.out.println(url);
+	    } catch(MinioException e) {
+	      System.out.println("Error occurred: " + e);
+	    } catch (java.security.InvalidKeyException e) {
+			e.printStackTrace();
+		}
+
+	    return url;
+	}
```

`service/src/test/java/org/whispersystems/textsecuregcm/tests/util/UrlSignerTest.java`

```diff
package org.whispersystems.textsecuregcm.tests.util;

import com.amazonaws.HttpMethod;
import org.junit.Test;
import org.whispersystems.textsecuregcm.s3.UrlSigner;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlSignerTest {

  @Test
  public void testTransferAcceleration() {
-    UrlSigner signer = new UrlSigner("foo", "bar", "attachments-test");
-    URL url = signer.getPreSignedUrl(1234, HttpMethod.GET, false);
+    //UrlSigner signer = new UrlSigner("foo", "bar", "attachments-test");
+    //URL url = signer.getPreSignedUrl(1234, HttpMethod.GET, false);

-    assertThat(url).hasHost("attachments-test.s3-accelerate.amazonaws.com");
+    //assertThat(url).hasHost("attachments-test.s3-accelerate.amazonaws.com");
  }

  @Test
  public void testTransferUnaccelerated() {
-    UrlSigner signer = new UrlSigner("foo", "bar", "attachments-test");
-    URL url = signer.getPreSignedUrl(1234, HttpMethod.GET, true);
+    //UrlSigner signer = new UrlSigner("foo", "bar", "attachments-test");
+    //URL url = signer.getPreSignedUrl(1234, HttpMethod.GET, true);

-    assertThat(url).hasHost("s3.amazonaws.com");
+    //assertThat(url).hasHost("s3.amazonaws.com");
  }

}
```

`service/src/main/java/org/whispersystems/textsecuregcm/controllers/AttachmentControllerV1.java`

```diff

+ import org.xmlpull.v1.XmlPullParserException;
+ import java.security.InvalidKeyException;
+ import java.security.NoSuchAlgorithmException;
+ import io.minio.errors.MinioException;

import io.dropwizard.auth.Auth;

@Path("/v1/attachments")
public class AttachmentControllerV1 extends AttachmentControllerBase {

  @SuppressWarnings("unused")
  private final Logger logger = LoggerFactory.getLogger(AttachmentControllerV1.class);

  private static final String[] UNACCELERATED_REGIONS = {"+20", "+971", "+968", "+974"};

  private final RateLimiters rateLimiters;
  private final UrlSigner    urlSigner;

-  public AttachmentControllerV1(RateLimiters rateLimiters, String accessKey, String accessSecret, String bucket) {
+  public AttachmentControllerV1(RateLimiters rateLimiters, String endpoint, String accessKey, String accessSecret, String bucket) {
    this.rateLimiters = rateLimiters;
-    this.urlSigner    = new UrlSigner(accessKey, accessSecret, bucket);
+    this.urlSigner    = new UrlSigner(endpoint, accessKey, accessSecret, bucket);
  }

  @Timed
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public AttachmentDescriptorV1 allocateAttachment(@Auth Account account)
-      throws RateLimitExceededException
+      throws RateLimitExceededException, InvalidKeyException, NoSuchAlgorithmException, IOException, XmlPullParserException, MinioException
  {
    if (account.isRateLimited()) {
      rateLimiters.getAttachmentLimiter().validate(account.getNumber());
    }

    long attachmentId = generateAttachmentId();
-    URL  url          = urlSigner.getPreSignedUrl(attachmentId, HttpMethod.PUT, Stream.of(UNACCELERATED_REGIONS).anyMatch(region -> account.getNumber().startsWith(region)));
+    String  url          = urlSigner.getPreSignedUrl(attachmentId, HttpMethod.PUT);

-    return new AttachmentDescriptorV1(attachmentId, url.toExternalForm());
+    return new AttachmentDescriptorV1(attachmentId, url);

  }

  @Timed
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/{attachmentId}")
  public AttachmentUri redirectToAttachment(@Auth                      Account account,
                                            @PathParam("attachmentId") long    attachmentId)
-      throws IOException
+      throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, MinioException
  {
-    return new AttachmentUri(urlSigner.getPreSignedUrl(attachmentId, HttpMethod.GET, Stream.of(UNACCELERATED_REGIONS).anyMatch(region -> account.getNumber().startsWith(region))));
+    return new AttachmentUri(new URL(urlSigner.getPreSignedUrl(attachmentId, HttpMethod.GET)));
  }

}

```

`service/src/test/java/org/whispersystems/textsecuregcm/tests/controllers/AttachmentControllerTest.java`

```diff
              .addProvider(new PolymorphicAuthValueFactoryProvider.Binder<>(ImmutableSet.of(Account.class, DisabledPermittedAccount.class)))
              .setMapper(SystemMapper.getMapper())
              .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
-              .addResource(new AttachmentControllerV1(rateLimiters, "accessKey", "accessSecret", "attachment-bucket"))
+              .addResource(new AttachmentControllerV1(rateLimiters, "http://example.com:9000", "accessKey", "accessSecret", "attachment-bucket"))
              .addResource(new AttachmentControllerV2(rateLimiters, "accessKey", "accessSecret", "us-east-1", "attachmentv2-bucket"))
              .addResource(new AttachmentControllerV3(rateLimiters, "some-cdn.signal.org", "signal@example.com", 1000, "/attach-here", RSA_PRIVATE_KEY_PEM))
              .build();

...

-   assertThat(descriptor.getKey()).isEqualTo(descriptor.getAttachmentIdString());
+   assertThat(descriptor.getKey()).isEqualTo("attachments/" + descriptor.getAttachmentIdString());
    assertThat(descriptor.getAcl()).isEqualTo("private");
    assertThat(descriptor.getAlgorithm()).isEqualTo("AWS4-HMAC-SHA256");
    assertThat(descriptor.getAttachmentId()).isGreaterThan(0);
    assertThat(String.valueOf(descriptor.getAttachmentId())).isEqualTo(descriptor.getAttachmentIdString());
```

`service/config/signal.yml`

```diff
attachments: # S3 configuration
  accessKey:    Q3AM3UQ867SPQQA43P2F # change to your Minio Access Key
  accessSecret: zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG # change to your Minio Access Secret
  bucket:       bucket-name # change to your Minio bucket name
  region:       us-east-1 # change to your Minio region
+  endpoint:     http://domain.com:9000 # add this entry, then change to your own domain & Minio port

cdn: # S3 configuration
  accessKey:    Q3AM3UQ867SPQQA43P2F # change to your Minio Access Key
  accessSecret: zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG # change to your Minio Access Secret
  bucket:       bucket-name # change to your Minio bucket name
  region:       us-east-1 # change to your Minio region
+  endpoint:     http://domain.com.id:9000 # add this entry, then change to your own domain & Minio port

```

## Signal Android

`libsignal/service/src/main/java/org/whispersystems/signalservice/internal/push/PushServiceSocket.java`

```diff

public class PushServiceSocket {

   private static final String KBS_AUTH_PATH                  = "/v1/backup/auth";

-  private static final String ATTACHMENT_KEY_DOWNLOAD_PATH   = "attachments/%s";
-  private static final String ATTACHMENT_ID_DOWNLOAD_PATH    = "attachments/%d";
-  private static final String ATTACHMENT_UPLOAD_PATH         = "attachments/";
+  private static final String ATTACHMENT_KEY_DOWNLOAD_PATH   = "%s";
+  private static final String ATTACHMENT_ID_DOWNLOAD_PATH    = "%d";
+  private static final String ATTACHMENT_UPLOAD_PATH         = "";
   private static final String AVATAR_UPLOAD_PATH             = "";

   private static final String STICKER_MANIFEST_PATH          = "stickers/%s/manifest.proto";


RequestBody requestBody = new MultipartBody.Builder()
                                               .setType(MultipartBody.FORM)
                                               .addFormDataPart("acl", acl)
                                               .addFormDataPart("key", key)
                                               .addFormDataPart("policy", policy)
+                                               .addFormDataPart("success_action_status", "200")
                                               .addFormDataPart("Content-Type", contentType)
                                               .addFormDataPart("x-amz-algorithm", algorithm)
                                               .addFormDataPart("x-amz-credential", credential)
                                               .addFormDataPart("x-amz-date", date)
                                               .addFormDataPart("x-amz-signature", signature)
                                               .addFormDataPart("file", "file", file)
                                               .build();
```

`app/build.gradle`

```diff
versionCode canonicalVersionCode * postFixSize
        versionName canonicalVersionName

        minSdkVersion 19
        targetSdkVersion 28
        multiDexEnabled true

        vectorDrawables.useSupportLibrary = true
        project.ext.set("archivesBaseName", "Signal");


       buildConfigField "long", "BUILD_TIMESTAMP", getLastCommitTimestamp() + "L"
-        buildConfigField "String", "SIGNAL_URL", "\"https://textsecure-service.whispersystems.org\""
-        buildConfigField "String", "STORAGE_URL", "\"https://storage.signal.org\""
-        buildConfigField "String", "SIGNAL_CDN_URL", "\"https://cdn.signal.org\""
-        buildConfigField "String", "SIGNAL_CDN2_URL", "\"https://cdn2.signal.org\""
-        buildConfigField "String", "SIGNAL_CONTACT_DISCOVERY_URL", "\"https://api.directory.signal.org\""
+        buildConfigField "String", "SIGNAL_URL", "\"https://your-domain.com\""
+        buildConfigField "String", "STORAGE_URL", "\"https://your-domain.com\""
+        buildConfigField "String", "SIGNAL_CDN_URL", "\"https://your-domain.com/your-bucket-name\""
+        buildConfigField "String", "SIGNAL_CDN2_URL", "\"https://your-domain.com/your-bucket-name\""
+        buildConfigField "String", "SIGNAL_CONTACT_DISCOVERY_URL", "\"https://your-domain.com\""
        buildConfigField "String", "SIGNAL_SERVICE_STATUS_URL", "\"uptime.signal.org\""
        buildConfigField "String", "SIGNAL_KEY_BACKUP_URL", "\"https://api.backup.signal.org\""
        buildConfigField "String", "CONTENT_PROXY_HOST", "\"contentproxy.signal.org\""
        buildConfigField "int", "CONTENT_PROXY_PORT", "443"
        buildConfigField "String", "SIGNAL_AGENT", "\"OWA\""
        buildConfigField "String", "CDS_MRENCLAVE", "\"cd6cfc342937b23b1bdd3bbf9721aa5615ac9ff50a75c5527d441cd3276826c9\""
        buildConfigField "String", "KBS_ENCLAVE_NAME", "\"fe7c1bfae98f9b073d220366ea31163ee82f6d04bead774f71ca8e5c40847bfe\""
        buildConfigField "String", "KBS_MRENCLAVE", "\"a3baab19ef6ce6f34ab9ebb25ba722725ae44a8872dc0ff08ad6d83a9489de87\""
        buildConfigField "String", "UNIDENTIFIED_SENDER_TRUST_ROOT", "\"BXu6QIKVz5MA8gstzfOgRQGqyLqOwNKHL6INkv3IHWMF\""
        buildConfigField "String", "ZKGROUP_SERVER_PUBLIC_PARAMS", "\"AMhf5ywVwITZMsff/eCyudZx9JDmkkkbV6PInzG4p8x3VqVJSFiMvnvlEKWuRob/1eaIetR31IYeAbm0NdOuHH8Qi+Rexi1wLlpzIo1gstHWBfZzy1+qHRV5A4TqPp15YzBPm0WSggW6PbSn+F4lf57VCnHF7p8SvzAA2ZZJPYJURt8X7bbg+H3i+PEjH9DXItNEqs2sNcug37xZQDLm7X0=\""
        buildConfigField "String[]", "LANGUAGES", "new String[]{\"" + autoResConfig().collect { s -> s.replace('-r', '_') }.join('", "') + '"}'
        buildConfigField "int", "CANONICAL_VERSION_CODE", "$canonicalVersionCode"
```
