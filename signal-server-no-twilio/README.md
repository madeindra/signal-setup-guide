## Removing Twilio

Currently, the only way to remove twilio depends on project’s goal, you may want to substitute Twilio with other SMS service, you may want to send the OTP by mail, almost all goals require you to get the OTP before forwarding it with another means, here is how you can get the OTP value.

Edit the TwilioSmsSender.java class and with this additional line of code you will get to print the OTP on screen.

`TwilioSmsSender.java`
```
public CompletableFuture<Boolean> deliverSmsVerification(String destination, Optional<String> clientType, String verificationCode) {
    Map<String, String> requestParameters = new HashMap<>();
    requestParameters.put("To", destination);
  
    // Print the verification code to Console, input it on login/registration
    logger.info("Your OTP is :" + verificationCode);

...
```

## Using test Number
Try adding `testDevices` to `config.yml`, number is the phone number and code is the OTP

```
testDevices:
  - number: "+1234567890"
    code: 123456
```