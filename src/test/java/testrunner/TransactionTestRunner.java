package testrunner;

import controller.TransactionController;
import io.qameta.allure.Allure;
import io.restassured.path.json.JsonPath;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import setup.Setup;
import setup.TransactionModel;
import utils.Utils;

import java.io.IOException;
import java.util.Map;

public class TransactionTestRunner extends Setup {

    private TransactionController trnx;
    private String systemToken;
    private String agentToken;
    private String customer1Token;
    private String customer2Token;

    @BeforeClass
    public void setUp() throws Exception {
        initConfig();
        trnx = new TransactionController(prop);
    }

    @Test(priority = 1, description = "Deposit from system to agent")
    public void depositSystemToAgent() throws ConfigurationException, IOException {
        // Log in as system user and extract token
        systemToken = Utils.loginAndGetToken("system@roadtocareer.net", "1234");
        // Log the token received from the login response
        System.out.println("Login Response Token: " + systemToken);

        // Set the token for the transaction controller
        trnx.setToken(systemToken); // Set system token

        // Log the token being used for the transaction
        System.out.println("Token used for transaction: " + trnx.getToken());

        TransactionModel trnxModel = new TransactionModel(
                "SYSTEM", // System account
                prop.getProperty("createdAgentPhone"), // Agent's phone
                5000 // Amount
        );

        // Before performing the transaction
        System.out.println("Using Token: " + trnx.getToken());
        System.out.println("From Account: SYSTEM");
        System.out.println("To Account: " + prop.getProperty("createdAgentPhone"));
        System.out.println("Amount: 5000");

        // Perform the transaction
        JsonPath jsonPath = trnx.performTransaction(trnxModel, "/transaction/deposit");

        // Check for success
        String messageActual = jsonPath.get("message");
        System.out.println("Transaction Response Message: " + messageActual);

        // Assert the success message
        Assert.assertTrue(messageActual.contains("Deposit successful"), "Transaction failed: " + messageActual);

        // Log result to Allure
        Allure.description("Deposit Money to Agent from SYSTEM Successfully");
    }

    @Test(priority = 2, description = "Deposit agent to customer 1")
    public void depositAgentToCustomer1() throws Exception {
        // Log in as agent and extract token
        Map<String, String> agentDetails = Utils.getUserDetailsByRole("./src/test/resources/users.json", "Agent");
        agentToken = Utils.loginAndGetToken(agentDetails.get("email"), agentDetails.get("password"));
        trnx.setToken(agentToken); // Set agent token

        TransactionModel trnxModel = new TransactionModel(
                prop.getProperty("createdAgentPhone"), // Agent's phone
                prop.getProperty("CUSTOMER_PHONE_1"), // Customer 1 phone
                1500 // Amount
        );

        JsonPath jsonPath = trnx.performTransaction(trnxModel, "/transaction/deposit");
        String messageActual = jsonPath.get("message");

        Assert.assertTrue(messageActual.contains("Deposit successful"), "Transaction failed: " + messageActual);
        Allure.description("Deposit Money to Customer1 from Agent Successfully");
    }

    @Test(priority = 3, description = "Withdraw 500 tk by the customer to the agent")
    public void withdrawCustomerToAgent() throws Exception {
        // Log in as customer 1 and extract token
        Map<String, String> customer1Details = Utils.getUserDetailsByRole("./src/test/resources/users.json", "Customer", 1);
        customer1Token = Utils.loginAndGetToken(customer1Details.get("email"), customer1Details.get("password"));
        trnx.setToken(customer1Token); // Set customer 1 token

        TransactionModel trnxModel = new TransactionModel(
                prop.getProperty("CUSTOMER_PHONE_1"), // Customer's phone
                prop.getProperty("createdAgentPhone"), // Agent's phone
                500 // Amount
        );

        JsonPath jsonPath = trnx.performTransaction(trnxModel, "/transaction/withdraw");
        String messageActual = jsonPath.get("message");

        Assert.assertTrue(messageActual.contains("Withdraw successful"), "Transaction failed: " + messageActual);
        Allure.description("Withdraw by Customer1 to Agent Successfully");
    }

    @Test(priority = 4, description = "Send money 500 tk to another customer")
    public void sendMoneyToCustomer() {
        trnx.setToken(customer1Token); // Set customer 1 token

        TransactionModel trnxModel = new TransactionModel(
                prop.getProperty("CUSTOMER_PHONE_1"), // From customer 1 phone
                prop.getProperty("CUSTOMER_PHONE_2"), // To customer 2 phone
                500 // Amount
        );

        JsonPath jsonPath = trnx.performTransaction(trnxModel, "/transaction/sendmoney");
        String messageActual = jsonPath.get("message");

        Assert.assertTrue(messageActual.contains("Send money successful"), "Transaction failed: " + messageActual);
        Allure.description("Send Money to Customer2 by Customer1 Successfully");
    }

    @Test(priority = 5, description = "Payment 100 tk to any merchant")
    public void paymentToMerchant() throws Exception {
        // Log in as customer 2 and extract token
        Map<String, String> customer2Details = Utils.getUserDetailsByRole("./src/test/resources/users.json", "Customer", 2);
        customer2Token = Utils.loginAndGetToken(customer2Details.get("email"), customer2Details.get("password"));
        trnx.setToken(customer2Token); // Set customer 2 token

        TransactionModel trnxModel = new TransactionModel(
                prop.getProperty("CUSTOMER_PHONE_2"), // From customer phone
                "01502232136", // Merchant's phone
                100 // Amount
        );

        JsonPath jsonPath = trnx.performTransaction(trnxModel, "/transaction/payment");
        String messageActual = jsonPath.get("message");

        Assert.assertTrue(messageActual.contains("Payment successful"), "Transaction failed: " + messageActual);
        Allure.description("Payment to Merchant by Customer2 Successfully");
    }

    @Test(priority = 6, description = "Check balance of the recipient customer")
    public void checkBalance() {
        trnx.setToken(customer2Token); // Set customer 2 token

        Double currentBalance = trnx.checkBalance(prop.getProperty("CUSTOMER_PHONE_2"));

        System.out.println("Customer Balance: " + currentBalance);

        Assert.assertNotNull(currentBalance, "Balance should not be null");
        Assert.assertTrue(currentBalance >= 0, "Balance should be non-negative");

        Allure.description("Check Balance of Customer2");
    }
}
