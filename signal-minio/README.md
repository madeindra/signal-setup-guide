## MinIO for AWS Replacement

If you are interested to use MinIO instead of AWS S3 or Cloudfront, this is how you can do that.  To simplify development, you can deploy minio using docker-compose.

1. The `docker-compose.yml` is already bundled here. To create persistent volume you can add `./data:/data` to `volumes` entry in minio. If you want to use https instead of http, add `./config:/root/.minio` to the entry too. **Remember to choose a name for your bucket.**

2. If you want https, create a dir called `config/certs`. Copy your SSL Certificate there and rename it `public.crt`, also copy your SSL Private Key there and rename it `private.key`.

3. Run the minio with `docker-compose` command.
```
docker-compose up -d
```

4. Update your signal server `config.yml` to reflect minio’s configuration.
```
...

attachments: # MinIO configuration
  accessKey:    Q3AM3UQ867SPQQA43P2F # Default, change to your own access key
  accessSecret: zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG # Default, change to your own secret key
  bucket:       change-to-your-bucket-name # use your bucket name here
  region:       us-east-1 # Default region

cdn: # MinIO configuration
  accessKey:    Q3AM3UQ867SPQQA43P2F # Default, change to your own access key
  accessSecret: zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG # Default, change to your own secret key
  bucket:       change-to-your-bucket-name # use your bucket name here
  region:       us-east-1 # Default region 

...
```

5. Update your nginx configuration to rediract to minio. Restart Nginx after updating this.
```
...

server {
 listen 443;
 server_name domain.com;

 # To allow special characters in headers
 ignore_invalid_headers off;
 
 # Allow any size file to be uploaded.
 # Set to a value such as 1000m; to restrict file size to a specific value
 client_max_body_size 0;
 
 # To disable buffering
 proxy_buffering off;
 
    location /change-to-your-bucket-name {
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto $scheme;
      proxy_set_header Host $http_host;

      proxy_connect_timeout 300;
      # Default is HTTP/1, keepalive is only enabled in HTTP/1.1
      proxy_http_version 1.1;
      proxy_set_header Connection "";
      chunked_transfer_encoding off;

      proxy_pass http://127.0.0.1:9000/change-to-your-bucket-name;
    }

    location /minio {
      proxy_set_header X-Real-IP $remote_addr;
      proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      proxy_set_header X-Forwarded-Proto $scheme;
      proxy_set_header Host $http_host;

      proxy_connect_timeout 300;
      # Default is HTTP/1, keepalive is only enabled in HTTP/1.1
      proxy_http_version 1.1;
      proxy_set_header Connection "";
      chunked_transfer_encoding off;

      proxy_pass http://127.0.0.1:9000;
    }
}


...
```

6. Modify your Signal Server project file, first update dependency by adding these values to `service/pom.xml` inside <dependencies> tag.
```
...

<dependency>
    <groupId>xmlpull</groupId>
    <artifactId>xmlpull</artifactId>
    <version>1.1.3.1</version>
</dependency>

<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>6.0.11</version>
</dependency>

...
```

7. Open `AttachmentControllerV1.java` located in `service/src/main/java/org/whispersystems/textsecuregcm/controllers/AttachmentControllerV1.java`. Add the import statements and modify existing function.
```
import org.xmlpull.v1.XmlPullParserException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import io.minio.errors.MinioException;



@Timed
@GET
@Produces(MediaType.APPLICATION_JSON)
public AttachmentDescriptorV1 allocateAttachment(@Auth Account account)
    throws RateLimitExceededException, InvalidKeyException, NoSuchAlgorithmException, IOException, XmlPullParserException, MinioException
{
  if (account.isRateLimited()) {
    rateLimiters.getAttachmentLimiter().validate(account.getNumber());
  }

  long attachmentId = generateAttachmentId();
  String  url          = urlSigner.getPreSignedUrl(attachmentId, HttpMethod.PUT);

  return new AttachmentDescriptorV1(attachmentId, url);

}



@Timed
@GET
@Produces(MediaType.APPLICATION_JSON)
@Path("/{attachmentId}")
public AttachmentUri redirectToAttachment(@Auth                      Account account,
                                          @PathParam("attachmentId") long    attachmentId)
    throws IOException, InvalidKeyException, NoSuchAlgorithmException, XmlPullParserException, MinioException
{
  return new AttachmentUri(new URL(urlSigner.getPreSignedUrl(attachmentId, HttpMethod.GET)));
}
```

8. Open `ProfileController.java` located in `service/src/main/java/org/whispersystems/textsecuregcm/controllers/ProfileController.java`. Add the import statements and modify existing function.
```
import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;



public ProfileController(RateLimiters rateLimiters,
                           AccountsManager accountsManager,
                           UsernamesManager usernamesManager,
                           CdnConfiguration profilesConfiguration)
  {
    AWSCredentials         credentials         = new BasicAWSCredentials(profilesConfiguration.getAccessKey(), profilesConfiguration.getAccessSecret());
    AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

    this.rateLimiters       = rateLimiters;
    this.accountsManager    = accountsManager;
    this.usernamesManager   = usernamesManager;
    this.bucket             = profilesConfiguration.getBucket();
    
    ClientConfiguration clientConfiguration = new ClientConfiguration();
    clientConfiguration.setSignerOverride("AWSS3V4SignerType");
    
    this.s3client           = AmazonS3ClientBuilder
                        .standard()
                        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://domain.com:9000", "your-bucket-region"))
                        .withPathStyleAccessEnabled(true)
                        .withClientConfiguration(clientConfiguration)
                        .withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .build();

    this.policyGenerator  = new PostPolicyGenerator(profilesConfiguration.getRegion(),
                                                    profilesConfiguration.getBucket(),
                                                    profilesConfiguration.getAccessKey());

    this.policySigner     = new PolicySigner(profilesConfiguration.getAccessSecret(),
                                             profilesConfiguration.getRegion());
  }
```

9. Open `UrlSigner.java` located in `service/src/main/java/org/whispersystems/textsecuregcm/s3/UrlSigner.java`. Add the import statements and modify existing function, remember to change the URL and credential in minioClient variable. **Remember to change the `minioClient` value**
```
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.errors.MinioException;



public String getPreSignedUrl(long attachmentId, HttpMethod method) throws InvalidKeyException, NoSuchAlgorithmException, IOException, XmlPullParserException, MinioException {
  		String request = geturl(bucket, String.valueOf(attachmentId), method);    		
  		return request;
  }

  public String geturl( String bucketname, String attachmentId, HttpMethod method) throws NoSuchAlgorithmException,
	        IOException, InvalidKeyException, XmlPullParserException, MinioException {	
	    
	    String url = null;
	    
	
	 MinioClient minioClient = new MinioClient("http://domain.com:9000", "YOUR-MINIO-ACCESS-KEY", "YOUR-MINIO-ACCESS-SECRET");
	  
	    try {
	    	if(method==HttpMethod.PUT){		    		
	    		url = minioClient.presignedPutObject(bucketname, attachmentId, 60 * 60 * 24);
	    	}
	    	if(method==HttpMethod.GET){		    		 
	    		url = minioClient.presignedGetObject(bucketname, attachmentId);
	    	}
	        System.out.println(url);
	    } catch(MinioException e) {
	      System.out.println("Error occurred: " + e);
	    } catch (java.security.InvalidKeyException e) {
			e.printStackTrace();
		}
	
	    return url;
	}
```

10. Open `UrlSignerTest.java ` located in `service/src/test/java/org/whispersystems/textsecuregcm/tests/util/UrlSignerTest.Java`. Add double slash to comment code inside the test function.
```
 @Test
  public void testTransferAcceleration() {
    //UrlSigner signer = new UrlSigner("foo", "bar", "attachments-test");
    //URL url = signer.getPreSignedUrl(1234, HttpMethod.GET, false);

    //assertThat(url).hasHost("attachments-test.s3-accelerate.amazonaws.com");
  }

  @Test
  public void testTransferUnaccelerated() {
    //UrlSigner signer = new UrlSigner("foo", "bar", "attachments-test");
    //URL url = signer.getPreSignedUrl(1234, HttpMethod.GET, true);

    //assertThat(url).hasHost("s3.amazonaws.com");
  }
```

11. Recompile your signal server and re-run it. It could take some time since it needs time to download new dependencies added to pom.xml.


12. Update your CDN URL in android `build.gradle`. Sync your project and rebuild it.
```
...

buildConfigField "String", "SIGNAL_CDN_URL", "\"https://domain.com/change-to-your-bucket-name\""

...
```



Bucket policy 'public' allow read & write from anonymous users without authentication, to disable this, you can modify your docker-compose for minio and modify mc service by changing `public` to `download`. 
```
/usr/bin/mc policy set download s3/name-of-bucket;
```

By modifying your bucket policy to `download` it will enable `read-only` access, anonymous users still able to see the files in your Web GUI.

To disable access to minio through web browser, delete `MINIO_BROWSER=off` from the environment in docker-compose for minio.

## Checking MinIO Deployment

To check if there are some error on your MinIO deployment, use `minio.sh` from this repository.

Prepare a file to be uploaded and run this script
```
./minio.sh your-bucket-name your-file-to-be-uploaded
```

If you cannot run the script, please give it an execute permission
```
chmod +x ./minio.sh
```

You will see Success Code `200` if you succeed in uploading the file, if not then there might be some error with your MinIO deployment. s