# 
# Rest API Version switch

This example shows how you can create a router to contemplate different versions of the same API using the same address for all versions.

The version number has to be passed as a query parameter, head parameter or as part of the content type.

Query parameter
```
curl -H "Content-Type: application/json" -X POST http://localhost:8080/test?api-version=2 -d '{"message":"Test message" }'
```

Head parameter
```
curl -H "api-version: 1" -H "Content-Type: application/json" -X POST http://localhost:8080/test -d '{"number":"10" }'
```

Content type parameter
```
curl -H "Content-Type: application/json;api-version=1" -X POST http://localhost:8080/test -d '{"number":"15" }'
```

This will make a internal forward  to the routes:
```
http://localhost:8080/v1/test
http://localhost:8080/v2/test
```

Payload accepted by test version1
```
{"number":"15" }
```
Payload accepted by test version2
```
{"message":"Test message" }
```

If the switch doesn't receive any version marker it will return a bad request code, if the payload doesn't validate against the structure defined by each version of the rest service, the server will return a bad request.
 
The error code is returned using hateoas convention by using the [VndError](https://github.com/blongden/vnd.error) field in the body.