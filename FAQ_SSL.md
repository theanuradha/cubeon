### How to setup communication with https / SSL based repositories ###

First get the public certificate of the server. You can use your favourite browser to save it (try clicking on the lock icon and look for export or save button).

With this certificate you can now create a truststore.

```
keytool -import -alias <your-server-alias-here> -file <server-certificate-here> -keystore <name-of-the-truststore-file> –storepass <your-password-for-the-truststore>
```

Answer the question for trusting the certificate with yes.

Place the created truststore file in your netbeans/etc directory.

Now add the following to your netbeans.conf

```
netbeans_default_options=" ... -J-Djavax.net.ssl.trustStore=../etc/<truststore-file> -J-Djavax.net.ssl.trustStorePassword=<password-for-truststore> ..."
```

This should make connections via SSL possible.