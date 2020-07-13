// service/src/main/java/org/whispersystems/textsecuregcm/s3/UrlSigner.java

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

  public String geturl( String bucketname, String attachemtnId, HttpMethod method) throws NoSuchAlgorithmException,
	        IOException, InvalidKeyException, XmlPullParserException, MinioException {	
	    
	    String url = null;
	    
	
	 MinioClient minioClient = new MinioClient("http://domain.com:9000", "YOUR-MINIO-ACCESS-KEY", "YOUR-MINIO-ACCESS-SECRET");
	  
	    try {
	    	if(method==HttpMethod.PUT){		    		
	    		url = minioClient.presignedPutObject(bucketname, attachemtnId, 60 * 60 * 24);
	    	}
	    	if(method==HttpMethod.GET){		    		 
	    		url = minioClient.presignedGetObject(bucketname, attachemtnId);
	    	}
	        System.out.println(url);
	    } catch(MinioException e) {
	      System.out.println("Error occurred: " + e);
	    } catch (java.security.InvalidKeyException e) {
			e.printStackTrace();
		}
	
	    return url;
	}
