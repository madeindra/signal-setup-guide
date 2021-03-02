// service/src/test/java/org/whispersystems/textsecuregcm/tests/util/UrlSignerTest.Java

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
