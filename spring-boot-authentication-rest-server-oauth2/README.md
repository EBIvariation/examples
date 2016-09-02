# Spring Boot based oauth2 authentication server and rest content server

The example contains two different spring-boot applications. They can be deployed together in the same server or in different servers.

To compile both applications execute from the main directory
```
mvn clean install
```

To test the example, go to each server in two different consoles and execute
```
mvn spring-boot:run
```
This will initialize the server for each one. The server will generate a database with valid client
applications that can use the validation server (In this case it only contains credentials for the
rest content server) and user, groups and roles information to authenticate and authorize the user.

The database for grants its generated with the clases described in package:
```
uk.ac.ebi.eva.persistence.entities
```

In short, there are users, groups and roles. A user can have multiple roles, groups can have multiple
users and multiple roles. So a user hes its own roles and those that come from the group. The roles
are not exclusive to "users or groups only" and can appear in both at the same time. At the end if
the user has ROLE_ADMIN or is in a group with ROLE_ADMIN then the user has ROLE_ADMIN.

The server could be modified to accept different types of user databases and systems like LDAP.

The server should accept all the authentication types of oauth2, including flow html one, but it is
the default Spring authentication webpage.

The rest content server has four access points

```
/heartbeat
/secure
/securesudo
/securesudo2
```

Heartbeat is a unsecured service that can be accesed with a get petition, i.e.
```
curl localhost:9000/heartbeat
```

The rest of the services require a oauth2 authentication token. To get a authentication code we need
to make a call to the oauth2 endpoint following the oauth2 specification. i.e.
```
curl acme:acmesecret@localhost:9999/uaa/oauth/token -d grant_type=password -d username=user -d password=password
```

The example has a in memory database populated with some basic data, if executed the previous command you will
receive a message with a bearer oauth2 credential

```
{"access_token":"27647b94-1f9f-4945-ae8f-6521d48fdcad","token_type":"bearer","refresh_token":"871e7f86-d483-492e-af4d-1fdf034f9a80","expires_in":35999,"scope":"openId"}
```

And to access one of the secure rest services we send the access_token like this

```
curl -H "Authorization: Bearer 27647b94-1f9f-4945-ae8f-6521d48fdcad" localhost:9000/secure
```

Aditionally we can filter user access to data inside the rest endpoint as can be seen in the examples securesudo and securesudo2 that
require `ROLE_ADMIN` access.

Also, note that those examples show how to generate error codes from exceptions in spring rest services.
