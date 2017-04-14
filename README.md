# Tiny jwt server

This is a small application for generating and validating JWT tokens with Spring Boot and JJWT.

Tiny was tested via SpringBoot jar and Tomcat 9M19.

Just a few configuration steps to run ahead.

## Configs & Profiles

A few configurations only:

* **net.diogosilverio.jwt.issuer** - This server issuer's name;
* **net.diogosilverio.jwt.audience** - The audience you expect to handle;
* **net.diogosilverio.jwt.key** - The safe place you put your keys.

Profiles:

* **dev/prd** - Expiration @ 20 mins;
* **tes** - Expiration @ 5 secs.

Configuration @ _application.properties_.

## Private Key for JWT

Generate the private and public keys

* A new PEM must be generated: `openssl genrsa -out tiny-server.pem 4096`
* Then when need a public key: `openssl genrsa -in tiny-server.pem -pubout -out tiny-server.pub`
* Now we convert our PEM to a pkcs8 format: `openssl pkcs8 -topk8 -inform PEM -outform DER -in tiny-server -out tiny-server.der -nocrypt`
* And the public one too: `openssl rsa -in tiny-server.pem -pubout -outform DER -out tiny-server.pub`

Save yout keys at a safe place(not the war itself). :)

## API

Check the Api by running and visiting **http://host/context/swagger-ui.html**.