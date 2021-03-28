# Minor consent API

> **Capability to retrieve, add and remove consents to a minor**

![Platform](https://img.shields.io/static/v1?label=Platform&message=Java%208%20|%20Jakarta%20EE%208&color=lightgreen)
![Runtime](https://img.shields.io/static/v1?label=Runtime&message=IBM%20WLP%2020.0.0.6&color=lightgreen)

## Table of Contents
- [Introduction](#introduction)
- [Objective](#objective)
- [Tech & Specifications](#technologies--specifications)
- [Error TRacking](#error-tracking)
- Security
    - [Transport](#transport-security)
    - [Access](#access-security)
- Local setup
    - [Pre requisites](#pre-requisites)
    - [Local Setup](#how-to-run-locally)
    - [Getting access token](#get-access-token)
    - [Accessing Database](#access-database)
    - [Accessing Keycloak](#access-keyclaok)

## Introduction
- This API can be used to add\retrieve\remove minor consents. Any major through any app which has "consent" scope, can add\retrieve\remove the consent
- To retrieve the list of all consents the client application should have the consent-admin scope
- It uses a Postgres database to record the consents and exposes the services through REST
- The security aspect is maintained with help of api-token generated through keycloak server

[Back to TOC](#table-of-contents)
### Objective
- As per the business requirement a minor who have the consents from their parents (existing clients), can register himself\herself on different apps
- So the registration process needs to verify the consent of the minor

[Back to TOC](#table-of-contents)
### Technologies & Specifications

- Language: Java 1.8
- Java EE: Jakarta 8
- Runtime: IBM Websphere Liberty 20.0.0.6
- Features:
    - jaxrs-2.1
    - mpConfig-1.4
    - mpMetrics-2.3
    - mpHealth-2.2
    - beanValidation-2.0
    - mpJwt-1.1
    - ejbLite-3.2
    - concurrent-1.0
    - jsonb-1.0

[Back to TOC](#table-of-contents)
### Error Tracking
- Application makes use of a thread local variable (reference variable), which is returned in the response if any error occurs.
- Which can be grep'ed in the log file (messages.log) for the whole processing upto the error point, right from the point when request enters into the system

[Back to TOC](#table-of-contents)
### Transport security
System uses TLSv1.2

[Back to TOC](#table-of-contents)
### Access security
All the endpoints are secured using api-token (jwt)

[Back to TOC](#table-of-contents)

### Pre requisites
- JDK 1.8+
- Maven 3+
- Docker runtime 

[Back to TOC](#table-of-contents)

### How to run locally?

> Step 1: Clone the repository
```
> git clone 
```

> Step 2: Start postgres & keycloak containers
```
> cd minor-consent
> docker-compose up 
```
> Step 3: Create test realm, text client, consent & consent-admin scopes by using the
- Login to admin console on https://localhost:8443/auth/admin
- hover on "Master" and click on "Add realm"
- On the right section, click on select file and choose the config from src/main/resources/keycloakConfig.json
- Once realm creation is done, select the test realm, click on "Clients" from the left pane, click on client ID "test", select the tab "Credentials" and click on "Regenerate Secret" and copy the new generated secret
> Step 4: Update application use the new secret
- Update the CLIENT_SECRET field in src/main/java/tum/ret/rity/minor/consent/constants/ApplicationConstants.java to have the value copied in previous step
> Step 5: Start the server
```
> mvn clean liberty:dev
```
> Step 6: Ran the test
- As the application is running in dev mode, the tests can be run on demand by pressing enter on console

[Back to TOC](#table-of-contents)

### Access keyclaok
Access admin console as https://localhost:8443/auth/admin 
username: admin
password: admin

[Back to TOC](#table-of-contents)

### Get access token
```
curl --location --request POST 'https://localhost:8443/auth/realms/test/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=test' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'client_secret=<YOUR_GENERATED_SECRET>' \
--data-urlencode 'scope=consent-admin'
```
[Back to TOC](#table-of-contents)

### Access Database
Access the url http://localhost:8081 and enter following details

- System: PostgreSQL
- Server: db
- Username: postgres
- Password: password

In the left pane, select DB as MRT_CONSENT and Schema as minor_consent_schema

[Back to TOC](#table-of-contents)
