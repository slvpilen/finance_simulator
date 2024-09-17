# To run server:

## For development

`mvn spring-boot:run -Dspring-boot.run.profiles=dev`

## For production

`mvn spring-boot:run -Dspring-boot.run.profiles=prod`

## Excecute jar file for prod:

`java -jar api-1.0-SNAPSHOT.jar --spring.profiles.active=dev`
