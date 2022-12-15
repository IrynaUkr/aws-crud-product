package crud.lambda.api;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import crud.lambda.api.model.Product;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;


public class ProductLambdaDeleteHandler implements RequestStreamHandler {

    private static final String DYNAMO_TABLE = "Products";

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

        OutputStreamWriter writer = new OutputStreamWriter(output);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        JSONParser parser = new JSONParser(); // this will help us parse the request object
        JSONObject responseObject = new JSONObject(); // we will add to this object for our api response
        JSONObject responseBody = new JSONObject();// we will add the item to this object

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);

        try {
            JSONObject reqObject = (JSONObject) parser.parse(reader);

            JSONObject pps = (JSONObject) reqObject.get("pathParameters");

            if (pps.get("id") != null) {
                int id = Integer.parseInt((String) pps.get("id"));
                dynamoDB.getTable(DYNAMO_TABLE).deleteItem("id", id);
            }

            responseBody.put("message", "Item deleted");
            responseObject.put("statusCode", 200);
            responseObject.put("body", responseBody.toString());


        } catch (Exception e) {
            responseObject.put("statusCode", 400);
            responseObject.put("error", e);
        }

        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }

}
