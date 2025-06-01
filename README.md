Build the project from the root folder with the following Maven command:
mvn clean install

To both start the Spring microservice and the game itself at once, there needs to be two terminals open at once. 
In one terminal, to start the Spring microservice, type the following command in the PointSystem directory: mvn spring-boot:run

then in the other terminal, to start the game type the following command from the project root folder (AsteroidFX), type mvn:exec:exec
