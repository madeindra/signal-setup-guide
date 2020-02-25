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