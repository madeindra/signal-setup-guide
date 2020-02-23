# Signal Desktop
This guid is written by using Signal Desktop on branch Master version 1.30.0-beta.4

## Requirement
* NVM 12.4.0

## How To
1. Install xcode (Macbook)
```
xcode-select --install # Install Command Line Tools if you haven't already.
sudo xcode-select --switch /Library/Developer/CommandLineTools # Enable command line tools
```

2. Install .Net 4.5.1 & Windows SDK 8.1 & Windows Build Tools (Win 7)
```
npm install --global --production --add-python-to-path windows-build-tools
```

3. Install python, gcc, g++, make (linux)

4. Clone signal-desktop

5. Mount signal desktop directory
```
cd Signal-Desktop
```

6. Install yarn
```
npm install —global yarn
```

7. Install & build with yarn
```
yarn install —frozen-lockfile
```

8. Generate final JS & CSS
```
yarn grunt
```

9. Generate full-set icon
```
yarn icon-gen
```

10. Build with webpack
```
yarn build:webpack
```

11. You can test with
```
yarn test 
```

12. Start the app
```
yarn start
```

13. To connect to own production server, `create local-development.json`, the value is the same as `production.json` but without `updateEnabled`.


## Using own Server
1. Update deafult.json ServerURL & CDN by using your Server URL & CDN url

2. Update CertificateAuthority using CA’s SSL Certificate

3. Update serverTrustRoot using CAPublicKey (Also used in android as UNIDENTIFIED SENDER TRUST ROOT)

4. Update `getAttachment` and `putAttachment` to remove `/attachments` (Same with android)

5. Give `certificateAuthority` a default value of CDN’s ROOT CA Cert on `getAttachment` and `putAttachment`

6. `yarn generate`

7. `yarn build`

8. `yarn start`


## FAQ
Q: How could I get certificate authority?

A: I called it the certificate of the certificate issuer. You can try browsing a https web using chrome, click on lock pad beside the URL address bar, then click on "Certificate (Valid)", you will see the top most & orange certificate, that is what you need, drag it to your desktop to save it.
