## Running The Application

To run this project you need to follow the following steps:
- Download the project to your local environment, and import it into you preferred IDE.
- Under src/main/resources, create a new file, name it ```application-<profile>.properties```, replacing ```<profile>``` with your username. Example ```application-john.properties```.
- Under src/main/resources, create a new file, name it ```application-test.properties```, this will hold configuration properties for testing purposes.
- Navigate to [spoonacular API](https://spoonacular.com/food-api), create new account, and generate your own api-key.
- Override ```spoonacular.api.key``` property in both, ```application-<profile>.properties``` and ```application-test.properties```, and assign your api-key value to it in both files.
- Add the following to your VM options ```-Dspring.profiles.active=<profile>```, note that ```profile``` needs to be replaced as before.

After completing the above steps, you should be able to run the application.

To explore the endpoints, in your browser, navigate to [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html), given that you kept the default server port 8080 as is.

In this project, I used the reactive project, Spring WebFlux instead of MVC RESTTemplate for many reasons, such that it applies the non-blocking approach of handling requests, which will be using less threads, thus, better performance.

One thing to notice, is that I increased the max in-memory size of ```Mono``` objects to 3 MB, since it's reasonable size to handle the size of objects returned from the API. I didn't use ```Flux``` given that spoonacular API is not implemented to stream data, instead, its returning recipes as a single unit.

Test cases covers both integration test and unit test, the actual spoonacular API was test, while I used Mockito to mock spoonacular API and Recipe service in RecipeServiceImpl and RecipeController testing respectively.
