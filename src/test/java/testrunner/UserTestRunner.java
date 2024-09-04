package testrunner;

import controller.TransactionController;
import io.qameta.allure.Allure;
import org.apache.commons.configuration.ConfigurationException;
import org.testng.annotations.Test;
import controller.UserController;
import setup.TransactionModel;
import utils.Utils;

import java.io.IOException;
import java.util.Map;

public class UserTestRunner {
    @Test(priority = 1)
    public void testDoLoginAdmin() throws IOException, ConfigurationException {
        UserController userController= new UserController();
        userController.doLogin("admin@roadtocareer.net","1234");
        Allure.description("Admin Login Successfully");
    }


    @Test(priority = 2)
    public void testCreateAndSaveUsers() throws IOException, ConfigurationException {
        UserController userController = new UserController();
        userController.createAndSaveUsers();// Call the method that creates and saves users

    }

}