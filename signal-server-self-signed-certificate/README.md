# Using Self-Signed Certificate

## Generating CA & Self Signed Certificate

To create your own self-signed certificate, remember you also need to be your own Certificate Authorithy.

Generate our CA private key (give password)
```
openssl genrsa -des3 -out myCA.key 2048
```

Generate CA root certificate
```
openssl req -x509 -new -nodes -key myCA.key -sha256 -days 1825 -out myCA.pem
```

Generate Your Private key
```
openssl genrsa -out localhost.key 2048
```

Generate Your Certificate Signing Request
```
openssl req -new -key localhost.key -out localhost.csr
```

Create a file named `localhost.ext` and add these lines to the file
```
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names

[alt_names]
DNS.1 = localhost
DNS.2 = 127.0.0.1
```

Run to create certificate (CA's Private Key Password needed)
```
openssl x509 -req -in localhost.csr -CA myCA.pem -CAkey myCA.key -CAcreateserial \
-out localhost.crt -days 1825 -sha256 -extfile localhost.ext
```

## Modifying Signal-Android to allow self-signed certificate

1. Open `PushServiceSocket.java` located in `libsignal/service/src/main/java/org/whispersystems/signalservice/internal/push/PushServiceSocket.java`.

Add these import statement and modify the existing okHttpClient methods called `createConnectionClient` and  `createAttachmentClient`
```
// PushServiceSocket.java

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;

  private OkHttpClient createConnectionClient(SignalUrl url) {
    try {
      final TrustManager[] trustAllCerts = new TrustManager[]{
              new X509TrustManager() {

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                               String authType) throws
                        CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                               String authType) throws
                        CertificateException {
                }
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                  return new java.security.cert.X509Certificate[]{};
                }
              }
      };

      SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

      builder.hostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      });
      return builder.build();
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new AssertionError(e);
    }
  }

  private OkHttpClient createAttachmentClient() {
    try {
      final TrustManager[] trustAllCerts = new TrustManager[]{
              new X509TrustManager() {

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                               String authType) throws
                        CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                               String authType) throws
                        CertificateException {
                }
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                  return new java.security.cert.X509Certificate[]{};
                }
              }
      };

      SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

      builder.hostnameVerifier(new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      });
      return builder.build();
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new AssertionError(e);
    }
  }
```

2. Open `WebSocketConnection.java` located in `libsignal/service/src/main/java/org/whispersystems/signalservice/internal/websocket/WebSocketConnection.java`.

Add these import statement and modify the existing void methods called `connect()`
```
// WebSocketConnection.java

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;

public synchronized void connect() {
    Log.w(TAG, "WSC connect()...");

    if (client == null) {
      String filledUri;

      if (credentialsProvider.isPresent()) {
        String identifier = credentialsProvider.get().getUuid() != null ? credentialsProvider.get().getUuid().toString() : credentialsProvider.get().getE164();
        filledUri = String.format(wsUri, identifier, credentialsProvider.get().getPassword());
      } else {
        filledUri = wsUri;
      }

      final TrustManager[] trustAllCerts = new TrustManager[]{
              new X509TrustManager() {

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                               String authType) throws
                        CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                               String authType) throws
                        CertificateException {
                }
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                  return new java.security.cert.X509Certificate[]{};
                }
              }
      };

      try {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

        builder.hostnameVerifier(new HostnameVerifier() {
          @Override
          public boolean verify(String hostname, SSLSession session) {
            return true;
          }
        });

        OkHttpClient okHttpClient = builder.build();

        Request.Builder requestBuilder = new Request.Builder().url(filledUri);

        if (userAgent != null) {
          requestBuilder.addHeader("X-Signal-Agent", userAgent);
        }

        if (listener != null) {
          listener.onConnecting();
        }

        this.connected = false;
        this.client    = okHttpClient.newWebSocket(requestBuilder.build(), this);
      } catch (NoSuchAlgorithmException | KeyManagementException e) {
        throw new AssertionError(e);
      }
    }
  }
```

3. You're done. Remember to use `https://your.own.ip.address:port` when you change the signal server url in `app/build.gradle`.