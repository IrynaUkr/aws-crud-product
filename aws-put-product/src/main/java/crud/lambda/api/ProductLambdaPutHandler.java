package crud.lambda.api;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import crud.lambda.api.model.Product;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class ProductLambdaPutHandler implements RequestStreamHandler {

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

                Map<String, String> expressionAttributeNames = new HashMap<String, String>();
                expressionAttributeNames.put("#name", "name");
                UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                        .withPrimaryKey("id", product.getId())
                        .withUpdateExpression("set #na = :name")
                        .withNameMap(new NameMap()
                                .with("#na", "name"))
                        .withValueMap(new ValueMap()
                                .withString(":name", product.getName()))
                        .withReturnValues(ReturnValue.ALL_NEW);
                dynamoDB.getTable(DYNAMO_TABLE).updateItem(updateItemSpec);

                responseBody.put("message", " Item updated");
                responseObject.put("statusCode", 200);
                responseObject.put("body", responseBody.toString());
            } else {
                responseObject.put("statusCode", 400);
                responseBody.put("message", " body of request is null");
            }
        } catch (Exception e) {
            responseObject.put("statusCode", 428);
            responseBody.put("message", " exception occurs ");
            responseObject.put("error", e);
        }
        writer.write(responseObject.toString());
        reader.close();
        writer.close();
    }

}
