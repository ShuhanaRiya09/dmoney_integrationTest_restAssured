package controller;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import setup.Setup;
import setup.TransactionModel;

import java.util.Properties;

import static io.restassured.RestAssured.given;

public class TransactionController extends Setup {

    public String token;

    public TransactionController(Properties prop) {
        this.prop = prop;
        RestAssured.baseURI = prop.getProperty("baseUrl");
    }

    // Method to set the token
    public void setToken(String token) {
        this.token = token;
    }


    // Method to get the token
    public String getToken() {
        return this.token;
    }

    // Ensure the token is set before making a request
    private void ensureTokenIsSet() {
        if (this.token == null) {
            this.token = prop.getProperty("token");
        }
    }

    // General method to handle transactions
    public JsonPath performTransaction(TransactionModel trnxModel, String endpoint) {
        ensureTokenIsSet();
        // Log the full request details
        System.out.println("Performing transaction with the following details:");



        // Log the token being used
        System.out.println("Using token: " + this.token);
        System.out.println("Endpoint: " + endpoint);
        System.out.println("Transaction Model: " + trnxModel);

        Response res = given()
                .contentType("application/json")
                .header("Authorization", "bearer " + this.token)  // Updated to include Bearer token
                .header("X-AUTH-SECRET-KEY", prop.getProperty("partnerKey"))
                .body(trnxModel)
                .post(endpoint);

        // Log the response for further debugging
        System.out.println("Response Status Code: " + res.getStatusCode());
        System.out.println("Response Body: " + res.getBody().asString());

        switch (res.getStatusCode()) {
            case 201:
                return res.jsonPath();
            case 208:
                System.out.println("Transaction already reported: " + res.getBody().asString());
                return null;
            default:
                throw new RuntimeException("Transaction failed with status code: " + res.getStatusCode() +
                        " and response body: " + res.getBody().asString());
        }
    }

    public Double checkBalance(String customerPhone) {
        ensureTokenIsSet();

        Response res = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + this.token)  // Updated to include Bearer token
                .header("X-AUTH-SECRET-KEY", prop.getProperty("partnerKey"))
                .get("/transaction/balance/" + customerPhone);

        if (res.getStatusCode() == 200) {
            JsonPath jsonPath = res.jsonPath();

            // Safely extract the value, handle potential nulls
            Double balance = jsonPath.getDouble("balance");
            if (balance != null) {
                return balance;
            } else {
                throw new RuntimeException("Balance is null in the response: " + res.getBody().asString());
            }
        } else {
            throw new RuntimeException("Failed to check balance with status code: " + res.getStatusCode() +
                    " and response body: " + res.getBody().asString());
        }
    }
}
