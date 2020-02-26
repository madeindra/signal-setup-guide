// service/src/main/java/org/whispersystems/textsecuregcm/controllers/ProfileController.java

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
                        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("https://abc.xxx.com:9000", "us-east-1"))
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