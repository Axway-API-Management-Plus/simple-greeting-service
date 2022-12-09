# Simple Greeting Service

Simple greeting service to be used as a demo backend for API Manager demonstration.

## Features

The service provides a user specific greeting message.
Users are able to change their message.

It supports following methods (for details, see OpenAPI spec `src/openapi/greeting-service/reference/greeting-service.json`).

### GET /greetings
Returns the default greeting message.

Example:
```
$ curl -ks https://localhost:8443/greetings \
--cert src/certs/demo-backend-client.crt \
--key  src/certs/demo-backend-client.key
{"message":"Hello World!","timestamp":1670624571107}
```

### GET /greetings?user={userid}
Returns the greeting message of a user.
If no user specific greeting message exists, the default message will be returned.

Example:
```
$ curl -ks https://localhost:8443/greetings?user=bob \
--cert src/certs/demo-backend-client.crt \
--key  src/certs/demo-backend-client.key
{"message":"Hello World!","timestamp":1670624642078}
```

### POST /greetings?user={userid}
Set a greeting message for the specified user.

Example:
```
$ curl -ks -X POST https://localhost:8443/greetings?user=bob \
--cert src/certs/demo-backend-client.crt \
--key  src/certs/demo-backend-client.key \
-H "Content-Type: application/json" \
-d '{"message": "Good Morning!"}'
{"message":"bob: Good Morning!","timestamp":1670624792172}
```

### DELETE /greetings?user={userid}
Delete the greeting message of the specified user.

Example:
```
$ curl -ks -X DELETE https://localhost:8443/greetings?user=bob \
--cert src/certs/demo-backend-client.crt \
--key  src/certs/demo-backend-client.key
```

## Security

This version enforces mutual TLS.
To access the service, a client certificate issued by `src/certs/demo-sub-ca.crt` is required.

__ATTENTION:__ The access to the user specific messages is not restricted.

## Certificates

The service uses self-signed certificates which are located in the `src/certs` folder.
The passphrase for certificates and Java Key Stores (JKS) is `changeme`.

## Build & Run

Build package & start the backend service:

```
$ ./mvnw clean package
$ java -jar target/quarkus-app/quarkus-run.jar
```

Build a single _über-jar_:

```
./mvnw package -Dquarkus.package.type=uber-jar
```
