# Signal iOS
This guide is written by using Signal-iOS branch Master version 3.13.2 build 3.13.2.6

## Requirement
* XCode 11.x (Latest Stable)
* Signal Server v3.21 (When I use 2.92 it always got error when profiling)
* A named cdn domain with 1 CA SSL


## How to
1. Clone the signal iOS from github
```
git clone --recurse-submodules https://github.com/signalapp/Signal-iOS
```
2. Install Dependency.
```
cd Signal-iOS && make dependencies
```
3. Open `Signal.xcworkspace` XCode.

4. Open `SignalServiceKit/TSConstants.swift` to direct the app to your own server. Change the variables in `TSConstantsStaging` and `TSConstantsProduction`.
``` 
    public let textSecureWebSocketAPI = "wss://your-domain.com/v1/websocket/"
    public let textSecureServerURL = "https://your-domain.com/"
    public let textSecureCDN0ServerURL = "https://your-cdn-domain.com"
    public let textSecureCDN2ServerURL = "https://your-cdn-domain.com"
    public let contactDiscoveryURL = "https://your-domain.com"
    public let keyBackupURL = "https://your-domain.com"
    public let storageServiceURL = "https://your-cdn-domain.com"
    public let kUDTrustRoot = "public-key-generated-in-signal-server-step-3"
    ...
    
    public let serverPublicParamsBase64 = "change-to-zkparams-public-key"
    }
```

5. Open `OWSAttachmentDownloads.m` and `OWSUploadV2.m` to change your downloaded attachment so it match with the android version.
```diff
    OWSAttachmentDownloads.m
    
    - (void)downloadJob:(OWSAttachmentDownloadJob *)job
        attachmentPointer:(TSAttachmentPointer *)attachmentPointer
                  success:(void (^)(NSString *encryptedDataPath))successHandler
                  failure:(void (^)(NSURLSessionTask *_Nullable task, NSError *error))failureHandlerParam
    {
        OWSAssertDebug(job);
        OWSAssertDebug(attachmentPointer);

        AFHTTPSessionManager *manager = [self cdnSessionManagerForCdnNumber:attachmentPointer.cdnNumber];
        manager.completionQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
        NSString *urlPath;
        if (attachmentPointer.cdnKey.length > 0) {
            urlPath = [NSString
-            stringWithFormat:@"attachments/%@", -> this line 
                [attachmentPointer.cdnKey
                    stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLPathAllowedCharacterSet]];
        } else {
-            urlPath = [NSString stringWithFormat:@"attachments/%llu", attachmentPointer.serverId]; -> this line
        }
        NSURL *url = [[NSURL alloc] initWithString:urlPath relativeToURL:manager.baseURL];
```

```diff
    OWSUploadV2.m
    
    - (AnyPromise *)parseFormAndUpload:(nullable id)formResponseObject
                     progressBlock:(UploadProgressBlock)progressBlock
{
    OWSUploadForm *_Nullable form = [OWSUploadForm parseDictionary:formResponseObject];
    if (!form) {
        return [AnyPromise
            promiseWithValue:OWSErrorWithCodeDescription(OWSErrorCodeUploadFailed, @"Invalid upload form.")];
    }
    UInt64 serverId = form.attachmentId.unsignedLongLongValue;
    if (serverId < 1) {
        return [AnyPromise
            promiseWithValue:OWSErrorWithCodeDescription(OWSErrorCodeUploadFailed, @"Invalid upload form.")];
    }

    self.serverId = serverId;

    __weak OWSAttachmentUploadV2 *weakSelf = self;
    NSString *uploadUrlPath = @"attachments/"; -> this line
    return [OWSUploadV2 uploadObjcWithData:self.attachmentData
                                uploadForm:form
                             uploadUrlPath:uploadUrlPath
                             progressBlock:progressBlock]
        .then(^{
            weakSelf.uploadTimestamp = NSDate.ows_millisecondTimeStamp;
        });
}

```

Remove the `attachments/` those line code

6. Convert your SSL certificate to DER format and change the `textsecure.cer` on `SignalServiceKit/Resources/Certificates/textsecure.cer`

7. Run your project with Simulator or iOS Phone

