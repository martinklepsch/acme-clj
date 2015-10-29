# acme-clj

# Trust api.letsencrypt.org

1. Download certificate

        openssl s_client -connect acme-staging.api.letsencrypt.org:443 < /dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > letsencrypt.crt

2. Copy System truststore

        cp $JAVA_HOME/jre/lib/security/cacerts $PWD/truststore/

3. Add letsencrypt cert to truststore. Default password on OS X is `changeit`.

        keytool -import -alias acme-staging.api.letsencrypt.org -keystore $PWD/truststore/cacerts -file $PWD/truststore/letsencrypt.crt

4. Test with [SSLPoke](https://confluence.atlassian.com/display/KB/Unable+to+Connect+to+SSL+Services+due+to+PKIX+Path+Building+Failed) class

        curl -L https://confluence.atlassian.com/download/attachments/779355358/SSLPoke.class > SSLPoke.class
        java -Djavax.net.ssl.trustStore=$PWD/truststore/cacerts SSLPoke acme-staging.api.letsencrypt.org 443
