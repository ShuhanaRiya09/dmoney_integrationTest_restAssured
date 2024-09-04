package controller;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import setup.Setup;
import setup.UserModel;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static utils.Utils.savePhoneNumbersToProperties;

public class UserController extends Setup {
    private List<UserModel> users;
    private Faker faker;

    public UserController() throws IOException {
        initConfig();
        users = new ArrayList<>();
        faker = new Faker();
    }
    public String doLogin(String email, String password) throws ConfigurationException {
        // Set the base URL
        RestAssured.baseURI = prop.getProperty("baseUrl");

        // Create a UserModel object and set email and password
        UserModel model = new UserModel();
        model.setEmail(email);
        model.setPassword(password);

        // Send the POST request to login
        Response res = given()
                .contentType("application/json")
                .body(model)
                .post("/user/login");

        // Print the status code and response for debugging
        int statusCode = res.getStatusCode();
        System.out.println("Status Code: " + statusCode);
        System.out.println("Login Response: " + res.asString());

        // Handle non-200 responses
        if (statusCode != 200) {
            // Handle the error based on status code
            if (statusCode == 403) {
                throw new RuntimeException("403 Forbidden: You don't have permission to access this resource.");
            } else if (statusCode == 400) {
                throw new RuntimeException("400 Bad Request: The server could not understand the request.");
            } else {
                throw new RuntimeException("Login failed with status code: " + statusCode);
            }
        }

        // Extract the token from the JSON response
        JsonPath jsonPath = res.jsonPath();
        String token = jsonPath.getString("token");
        System.out.println("Token: " + token);

        // Save the token with "Bearer " prefix in environment variables
        Utils.setEnvVar("token", "bearer " + token);

        // Return the token
        return "bearer " + token;
    }


    // Method to create a user via the API and add to the list
    public void createUser(String role) throws ConfigurationException {
        UserModel model = generateUserModel(faker, role);
        JsonPath jsonResponse = sendCreateUserRequest(model);

        System.out.println("Response: " + jsonResponse.prettyPrint());
        // Extract the 'name' from the 'user' object
        String userName = jsonResponse.getString("user.name");
        System.out.println("User created: " + userName);

        // Optionally, you could add logic to handle or verify the response if needed
        users.add(model);
    }

    // Method to send the API request to create a user
    public JsonPath sendCreateUserRequest(UserModel model) throws ConfigurationException {
        RestAssured.baseURI = prop.getProperty("baseUrl");
        Response res = given().contentType("application/json")
                .header("Authorization", prop.getProperty("token")) // Token now includes "Bearer "
                .header("X-AUTH-SECRET-KEY", prop.getProperty("partnerKey"))
                .body(model)
                .post("/user/create");

        //System.out.println("Create User Response: " + res.asString());
        return res.jsonPath();
    }

    // Method to generate a UserModel object with Faker data
    public UserModel generateUserModel(Faker faker, String role) {
        UserModel model = new UserModel();
        model.setName(faker.name().fullName());
        model.setEmail(faker.internet().emailAddress().toLowerCase());
        model.setPassword("P@ssword123");
        String phoneNumber="01502"+Utils.generateRandomId(100000,999999);
        model.setPhone_number(phoneNumber);
        model.setNid(String.valueOf(Utils.generateRandomId(10000000,99999999)));
        model.setRole(role);
        return model;
    }

    // Method to create and save users
    public void createAndSaveUsers() throws IOException, ConfigurationException {
        // Create 2 customer users
        for (int i = 0; i < 2; i++) {
            createUser("Customer");
        }

        // Create 1 agent user
        createUser("Agent");

        // Save all users to a JSON file
        Utils.saveUsers(users);

        // Extract phone numbers of two customers
        List<String> customerPhoneNumbers = new ArrayList<>();
        for (UserModel user : users) {
            if ("Customer".equals(user.getRole())) {
                customerPhoneNumbers.add(user.getPhone_number());
                if (customerPhoneNumbers.size() == 2) {
                    break;
                }
            }
        }

        // Set the phone numbers as environment variables and save to properties file
        if (customerPhoneNumbers.size() >= 2) {
            Utils.setEnvVar("CUSTOMER_PHONE_1", customerPhoneNumbers.get(0));
            Utils.setEnvVar("CUSTOMER_PHONE_2", customerPhoneNumbers.get(1));
            savePhoneNumbersToProperties("CUSTOMER_PHONE_1", customerPhoneNumbers.get(0), "CUSTOMER_PHONE_2", customerPhoneNumbers.get(1));

            // Extract the agent's phone number
            String agentPhoneNumber = null;

            for (UserModel user : users) {
                if ("Agent".equals(user.getRole())) {
                    agentPhoneNumber = user.getPhone_number();
                    break;
                }
            }

            // Save the agent's phone number to the properties file
            if (agentPhoneNumber != null) {
                Utils.setEnvVar("createdAgentPhone", agentPhoneNumber);
                System.out.println("Agent's phone number: " + agentPhoneNumber);
            } else {
                System.out.println("Agent not found.");
            }
        }
    }
}
