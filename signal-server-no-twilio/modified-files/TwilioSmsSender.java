// TwilioSmsSender.java

public CompletableFuture<Boolean> deliverSmsVerification(String destination, Optional<String> clientType, String verificationCode) {
    Map<String, String> requestParameters = new HashMap<>();
    requestParameters.put("To", destination);
  
    // Print the verification code to Console, input it on login/registration
    logger.info("Your OTP is :" + verificationCode);

    if (Util.isEmpty(messagingServicesId)) {
      requestParameters.put("From", getRandom(random, numbers));
    } else {
      requestParameters.put("MessagingServiceSid", messagingServicesId);
    }

    if ("ios".equals(clientType.orElse(null))) {
      requestParameters.put("Body", String.format(SmsSender.SMS_IOS_VERIFICATION_TEXT, verificationCode, verificationCode));
    } else if ("android-ng".equals(clientType.orElse(null))) {
      requestParameters.put("Body", String.format(SmsSender.SMS_ANDROID_NG_VERIFICATION_TEXT, verificationCode));
    } else {
      requestParameters.put("Body", String.format(SmsSender.SMS_VERIFICATION_TEXT, verificationCode));
    }

    HttpRequest request = HttpRequest.newBuilder()
                                     .uri(smsUri)
                                     .POST(FormDataBodyPublisher.of(requestParameters))
                                     .header("Content-Type", "application/x-www-form-urlencoded")
                                     .header("Authorization", "Basic " + Base64.encodeBytes((accountId + ":" + accountToken).getBytes()))
                                     .build();

    smsMeter.mark();

    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                     .thenApply(this::parseResponse)
                     .handle(this::processResponse);
  }
