# Amazon Web Service (AWS) Setup Guide for Signal
Currently, Signal use AWS for attachment and CDS queue. However you can skip this step if you don't want to use AWS, I wrote another guide on replacing S3 & CDN with Minio. But if you need AWS here you go.

## IAM for Access Key & Secret
1. Login to AWS Console and click on your name, select on `My Security Credentials`.

2. Expand the tab `Access keys (access key ID and secret access key)`.

3. Click on `Create New Access Key`.

4. Click on `Show Access Key` to show your Access Key & Secret.

5. Take note of it and keep it safe. You will need it for your Signal Server config.yml

## S3 for Attachments & Profile Picture

1. Login to AWS Console and search for `S3`.

2. Click on `Create Bucket`.

3. Name your `Bucket name` and select your `Region`, then select `Next`.

4. On `Configure options` tab, select `Next`.

5. On `Set permission` tab, remove the check on `Block all public access` and check on `I acknowledge that the current settings may result in this bucket and the objects within becoming public`, then select `Next`.

6. On `Review` tab, click `Create bucket`.

7. Open your bucket, then select `Permission` tab, and select `Access Control List` sub tab.

8. On `Public access` select `Everyone` and check all on `Access to the objects` and `Access to this bucket's ACL`.

9. Do the same on `S3 log delivery group` in `Log Delivery`.

10. Select `Bucket Policy` sub tab and write this (change `your-bucket-name` to your bucket name).

```
{
    "Version": "2008-10-17",
    "Statement": [
        {
            "Sid": "AllowPublicRead",
            "Effect": "Allow",
            "Principal": {
                "AWS": "*"
            },
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::your-bucket-name/*"
        }
    ]
}
```

11. On `Access point` tab, click `Create access point`.

12. Give `Access point name` and select `Internet` on `Network access type`, remove the check on `Block all public access`.



## Cloudfront CDN for Attachments & Profile Picture

1. Login to AWS Console and search for `CloudFront`.

2. Click on `Create Distribution`.

3. Under `Web` click `Get Started`.

4. On `Origin Domain Name` select your `Bucket`.

5. On `Viewer Protocol Policy` select `Redirect HTTP to HTTPS`.

6. On `Allowed HTTP Methods` select `GET, HEAD, OPTIONS, PUT, POST, PATCH, DELETE`.

7. Finish by selecting on `Create Distribution`.

## SQS for CDS Queue

1. Login to AWS Console and search for `SQS`.

2. Click on `Create new queue`.

3. Give a name in `Queue Name` with format `name.fifo`.

4. Select `FIFO Queue`, then select `Quick Create Queue`.

5. Select your queue and find the `URL`, you will need it for the Signal Server config and CDS config.