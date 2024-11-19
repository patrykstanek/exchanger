To setup database: 
- Run posgreSQL (from the project directory):
```
  docker-compose -f ./docker/docker-compose.yml up -d
```
Credentials can be found in the docker-compose.yml

Run application:
```
 ./gradlew bootRun
```

or directly from your IDE (e.g. from Intellij)