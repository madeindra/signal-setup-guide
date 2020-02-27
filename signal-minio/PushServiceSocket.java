// libsignal/service/src/main/main/java/org/whispersystems/signalservice/internal/push/PushServiceSocket.java

public void setProfileAvatar(ProfileAvatarData profileAvatar)
      throws NonSuccessfulResponseCodeException, PushNetworkException
  {
    String                        response       = makeServiceRequest(String.format(PROFILE_PATH, "form/avatar"), "GET", null);
    ProfileAvatarUploadAttributes formAttributes;

    try {
      formAttributes = JsonUtil.fromJson(response, ProfileAvatarUploadAttributes.class);
    } catch (IOException e) {
      Log.w(TAG, e);
      throw new NonSuccessfulResponseCodeException("Unable to parse entity");
    }

    if (profileAvatar != null) {
      uploadToCdn("change-to-bucket-name", formAttributes.getAcl(), formAttributes.getKey(),
                  formAttributes.getPolicy(), formAttributes.getAlgorithm(),
                  formAttributes.getCredential(), formAttributes.getDate(),
                  formAttributes.getSignature(), profileAvatar.getData(),
                  profileAvatar.getContentType(), profileAvatar.getDataLength(),
                  profileAvatar.getOutputStreamFactory(), null);
    }
  }



public Pair<Long, byte[]> uploadAttachment(PushAttachmentData attachment, AttachmentUploadAttributes uploadAttributes)
      throws PushNetworkException, NonSuccessfulResponseCodeException
  {
    long   id     = Long.parseLong(uploadAttributes.getAttachmentId());
    byte[] digest = uploadToCdn("change-to-bucket-name", uploadAttributes.getAcl(), uploadAttributes.getKey(),
                                uploadAttributes.getPolicy(), uploadAttributes.getAlgorithm(),
                                uploadAttributes.getCredential(), uploadAttributes.getDate(),
                                uploadAttributes.getSignature(), attachment.getData(),
                                "application/octet-stream", attachment.getDataSize(),
                                attachment.getOutputStreamFactory(), attachment.getListener());

    return new Pair<>(id, digest);
  }



public void retrieveAttachment(long attachmentId, File destination, int maxSizeBytes, ProgressListener listener)
      throws NonSuccessfulResponseCodeException, PushNetworkException
  {
    downloadFromCdn(destination, String.format(Locale.US, "change-to-bucket-name/%d", attachmentId), maxSizeBytes, listener);
  }

public void retrieveProfileAvatar(String path, File destination, int maxSizeBytes)
      throws NonSuccessfulResponseCodeException, PushNetworkException
  {
    downloadFromCdn(destination, String.format(Locale.US, "change-to-bucket-name/%s", path), maxSizeBytes, null);
  }