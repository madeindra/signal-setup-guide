// service/src/main/java/org/whispersystems/textsecuregcm/controllers/AttachmentControllerV1.java

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