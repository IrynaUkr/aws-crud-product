### Task AWS: ApiGateway, Lambda, DynamoDB

At the end of this homework you need to have:

  API Gateway with two endpoints to upload information about your products (id, name, picture URL, price) and to update information;
  Lambda that processes that upload (check if data is already present in DynamoDB and put it if it is not present);
  Lambda that processes information updates;
  DynamoDB with 1 Table with product information.         

Implement small console application with generated API gateway sdk to create and update information
(https://docs.aws.amazon.com/apigateway/latest/developerguide/how-to-call-apigateway-generated-java-sdk.html) 

1. create DynamoDB table
2. create Lambda, use this code to it
3. create API Gateway
4. create a simple maven Java project
    4.1 add dependencies:
    4.1.1. include dependency aws-lambda-java-core. 
        This library gives us two interfaces for handler method (requestHandler, RequestStreamHandler)
    4.1.2. aws-java-sdk-dynamodb
        This sdk library has the client classes that are used for communicating with DynamoDb service.
    4.1.3 json-simple(for working with json objects)
    4.1.4.gson(to create json string from java object and java object from json string)
    4.1.5 build plugins:
        * maven- compiler-plugin (to compile the source code  of maven project to executable program)
        * maven-shade-plugin (to package the artifact in an uber-jar.
          Uber-jar is a jar that contains everything. 
          It takes all the dependencies and puts them in a one big JAR)
   4.2.create Product class which can be converted to json
   4.3 create ProductLambdaHandler, which implements RequestStreamHandler, and override the method HandleRequest.
       parameter InputStream includes all the incoming data(pathparameters, queryStringParameters, request body, request header)
       parameter OutputStream is where we send or write the result.
       parameter Context provides methods and properties that give info about invocation, function, execution environment etc
5.
