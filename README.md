# wit-java-challenge
### Java Challenge for WIT

## Code execution
This project has been designed using Spring Boot, Kafka and Docker. To run it, you need to have Docker installed on your machine.

1. Clone the repository
2. Open a terminal and navigate to the root directory of the project
3. Run the following command to build the project:
```
docker-compose up --build
```
4. The project will start, and you can access the API at `http://localhost:8080`. The tests are run automatically when the project is built.

## API Endpoints
The project has the following API endpoints:

`http://localhost:8080/sum?a=1&b=2`

`http://localhost:8080/subtraction?a=1&b=2`

`http://localhost:8080/multiplication?a=1&b=2`

`http://localhost:8080/division?a=1&b=2`


