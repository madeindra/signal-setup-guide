## CA & Self Signed

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

Create `localhost.ext`
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

