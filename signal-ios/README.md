# Signal iOS
This guide is written by using Signal-iOS branch Master version 3.20.10

## Requirement
* XCode 11.x (Latest Stable)

## How to
1. Install Dependency.
```
make dependencies
```

2. Open `Signal.xcworkspace` using XCode.

3. Open `SignalServiceKit/TSConstants.swift` to direct the app to your own server. Change the variables in `TSConstantsStaging` and `TSConstantsProduction`.
```
    public let textSecureWebSocketAPI = "wss://your-domain.com/v1/websocket/"
    public let textSecureServerURL = "https://your-domain.com/"
    public let textSecureCDNServerURL = "https://your-cdn-domain.com"
    
    ...

    public let storageServiceURL = "https://your-domain.com"
    public let kUDTrustRoot = "public-key-generated-in-signal-server-step-3"

    ...

    }
```
