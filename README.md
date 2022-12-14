# Simple Greeting Service

Simple greeting service to be used as a demo backend for API Manager demonstration.

## Features

The service provides a user specific greeting message.
Users are able to change their message.

It supports following methods (for details, see OpenAPI spec `src/openapi/greeting-service/reference/greeting-service.json`).

### GET /greetings
Returns the default greeting message.

Required scopes: `greetings` or `greetings.set`

Example:
```
$ curl -ks https://localhost:8443/greetings \
-H "Authorization: Bearer ...token..." \
--cert src/certs/demo-apim-client.crt \
--key  src/certs/demo-apim-client.key
{"message":"Hello World!","timestamp":1670624571107}
```

### GET /greetings?user={userid}
Returns the greeting message of a user.
If no user specific greeting message exists, the default message will be returned.

Required scope: `greetings` or `greetings.set`

Example:
```
$ curl -ks https://localhost:8443/greetings?user=bob \
-H "Authorization: Bearer ...token..." \
--cert src/certs/demo-apim-client.crt \
--key  src/certs/demo-apim-client.key
{"message":"Hello World!","timestamp":1670624642078}
```

### POST /greetings?user={userid}
Set a greeting message for the specified user.

Required scope: `greetings.set`

Example:
```
$ curl -ks -X POST https://localhost:8443/greetings?user=bob \
-H "Authorization: Bearer ...token..." \
--cert src/certs/demo-apim-client.crt \
--key  src/certs/demo-apim-client.key \
-H "Content-Type: application/json" \
-d '{"message": "Good Morning!"}'
{"message":"bob: Good Morning!","timestamp":1670624792172}
```

### DELETE /greetings?user={userid}
Delete the greeting message of the specified user.

Required scope: `greetings.set`

Example:
```
$ curl -ks -X DELETE https://localhost:8443/greetings?user=bob \
-H "Authorization: Bearer ...token..." \
--cert src/certs/demo-apim-client.crt \
--key  src/certs/demo-apim-client.key
```

## Security

* enforced mutual TLS
* checks issuer of client certificate (must be signed by `src/certs/demo-sub-ca.crt`)
* checks CN of client certificate (must be `apim-client`)
* secured with OAuth 2
    * user authenticated by token
    * token validated by introspection to Authorization Server
    * specified user must match `sub` claim
    * scopes `greetings` or `greetings.set` required.

![Security](docs/images/simple-greeting-service-security.png)

Check the [guide](docs/secure-with-apim.md) for how to virtualize and the secure the API with the API Manager.

## Certificates

The service uses self-signed certificates which are located in the `src/certs` folder.
The passphrase for certificates and Java Key Stores (JKS) is `changeme`.

## Build & Run

Build package & start the backend service:

```
$ ./mvnw clean package
$ java -jar target/quarkus-app/quarkus-run.jar
```

Build a single _??ber-jar_:

```
./mvnw package -Dquarkus.package.type=uber-jar
```
