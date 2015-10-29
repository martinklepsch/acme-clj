(set-env! :dependencies '[[it.zero11/acme-client "0.1.0"]
                          [amazonica "0.3.37"]
                          [javax.ws.rs/javax.ws.rs-api "2.0.1"]
                          [org.glassfish.jersey.core/jersey-client "2.22.1"]])


(require '[amazonica.aws.s3 :as s3]
         '[clojure.java.io :as io])
(import [it.zero11.acme Acme AcmeChallengeListener]
        [it.zero11.acme.storage.impl DefaultCertificateStorage])
(import [javax.ws.rs.client Client ClientBuilder])
        
;; Acme acme = new Acme(CA_STAGING_URL,
;; 						new DefaultCertificateStorage(),
;; 						new FTPChallengeListener(args[0], args[1], args[2], args[3]), true);

;; 				acme.getCertificate(args[0], args[4], null);

(def ca-staging-url "https://acme-staging.api.letsencrypt.org/acme")
(def agreement-url "https://letsencrypt.org/documents/LE-SA-v1.0-June-23-2015.pdf")
(def test-bucket "static-site-cljs-io-sitebucket-1969npf1zvwoh")
(def challenge-file-prefix ".well-known/acme-challenge/")

(defn create-challenge-files [token body]
  (s3/put-object :bucket-name test-bucket
                 :key (str challenge-file-prefix token)
                 :input-stream (io/input-stream body)))

(defn delete-challenge-files []
  (->> (s3/list-objects :bucket-name test-bucket :prefix challenge-file-prefix)
       (s3/delete-objects :bucket-name test-bucket :keys)))

(def s3-challenge-listener
  (reify AcmeChallengeListener
    (challengeSimpleHTTP [this domain token challenge-uri challenge-body]
      (create-challenge-files token challenge-body))
    (challengeCompleted [this domain]
      (delete-challenge-files))
    (challengeFailed [this domain]
      (delete-challenge-files))))

(comment 
  (def acme (Acme. ca-staging-url (DefaultCertificateStorage.) s3-challenge-listener))

  (.getCertificate acme "cljs.io" agreement-url nil)

  )

