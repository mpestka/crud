# Spring boot crud example 

This is example of REST (almost "ful" - missing hyperlinks HATEOS) crud application.  
Links given in this readme, are assuming that application is run on localhost port 8080.

## API

All endpointa are configured tu run under `/api` context (can be changed in application.properties).  
The API is automatically docummented using *springdoc-openapi-ui* and can be viewed after starting application under URI:  
http://localhost:8080/api/v3/api-docs  
It deploys also swagger UI here:  
http://localhost:8080/api/swagger-ui/index.html

### Endpoints:  


 Method   | Endpoint             | Description
 ---------|----------------------|--------------
 `GET`    | `/api/products`      | Retrieve a list of all products
 `POST`   | `/api/products`      | Create a new product
 `PUT`    | `/api/products/{id}` | Update product by id  
 `DELETE` | `/api/products/{id}` | Delete a product (soft delete)



### JSON product representation
```javascript
{
    id: 1,                  // integer
    name: "Product name",   // string
    price: 100,             // integer (in cents)
    created: "2020-03-22T09:50:09.421+0000" // UTC timestamp (read-only)
}
```
`id` is unique identifier of the product - automatically generated on product creation.  
Intentionally using database id here to make it simpler, although probably some natural (business) key woud be better (missing spec about SKU).  

Due to time restrictions no orders implemented.

## Storage
JPA is used as persistence layer, (well, not the best solution in long term IMHO, but quickest and good enough for CRUD). It also allows to choose almost any relational database.  
Existing implementation uses H2 database (by default in-memory), but easily configured to persist data in the file (see [application.properties](src/main/resources/application.properties) file comments).  
Database entity has also an additional field comparing to product json representation `deleted` timestamp that allows for soft product delete.   
There is no REST API endpoint to show deleted products but these can be viewed in H2 console:  
http://localhost:8080/api/h2-console/  
(after setting `spring.h2.console.enabled=true` - see [application.properties](src/main/resources/application.properties))  

In order to change database to different one, the appropriate dependency with desired database driver must be added to `build.gradle` dependencies, as well as db connection properties must be configured in [application.properties](src/main/resources/application.properties) file.


## Running and testing application

There are the following test modes configured:
1. Unit tests (standard `test` gradle target)
2. Integration tests (run after unit tests)
3. There is also mentioned earlier swagger UI that allows for manual tests)

Integration tests just run application locally on random port and uses REST api to check simple usage scenario (using in-memory H2 database instance).  

Both unit tests and integration tests can be run separately with gradle (and have different sources location)

### Startup
Java 8 is required to build and run the application.  
After git clone, just run in repository folder:  

#### Build
To build the application (also triggers all unit & integration tests and outpus results to console)
```
./gradlew build
```

#### Start
to start application
```
./gradlew bootRun
```

#### Links:  
##### Documentation
http://localhost:8080/api/v3/api-docs  
http://localhost:8080/api/swagger-ui/index.html  
##### Endpoints:
http://localhost:8080/api/products
##### H2 console (when enabled)
http://localhost:8080/api/h2-console/
