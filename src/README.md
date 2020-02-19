The is a standalone money transfer program with HTTP REST API - this has a minimum basic functionality to create accounts / locate 
and transfer funds between accounts.

Technology pre-requisites: 

Java 8 
Maven
H2 embedded database

build application:
mvn clean package

To run application:
java -jar target/revolut-moneytransfer.jar 8080
java -jar target/revolut-moneytransfer.jar (a default port 8050 is used) 
