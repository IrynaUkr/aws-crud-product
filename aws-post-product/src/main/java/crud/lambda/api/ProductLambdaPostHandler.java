package crud.lambda.api;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import crud.lambda.api.model.Product;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;


public class ProductLambdaPostHandler implements RequestStreamHandler {

    private static final String DYNAMO_TABLE = "Products";


    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(output);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        JSONParser parser = new JSONParser();
        JSONObject responseObject = new JSONObject();
        JSONObject responseBody = new JSONObject();

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        DynamoDB dynamoDB = new DynamoDB(client);

        try {
            JSONObject reqObject = (JSONObject) parser.parse(reader);

            if (reqObject.get("body") != null) {
                Product product = new Product((String) reqObject.get("body"));

                Item item = dynamoDB.getTable(DYNAMO_TABLE).getItem("id", product.getId());
                if (item == null) {

                    dynamoDB.getTable(DYNAMO_TABLE)
                            .putItem(new PutItemSpec().withItem(new Item()
                                    .withNumber("id", product.getId())
                                    .withString("name", product.getName())
                                    .withNumber("price", product.getPrice())));
                    responseBody.put("message", "New Item created");
                    responseObject.put("statusCode", 200);
                    responseObject.put("body", responseBody.toString());
                } else {
                    responseObject.put("statusCode", 400);
                }
            }
        } catch (Exception e) {
            responseObject.put("statusCode", 400);
            responseObject.put("error", e);
        }
        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }
}


