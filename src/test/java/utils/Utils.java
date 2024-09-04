package utils;

import controller.UserController;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import setup.UserModel;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Utils  {

    public static void setEnvVar(String key, String value) throws ConfigurationException {
        PropertiesConfiguration config=new PropertiesConfiguration("./src/test/resources/config.properties");
        config.setProperty(key,value);
        config.save();
    }

    public static void savePhoneNumbersToProperties(String key1, String value1, String key2, String value2) {
        Properties properties = new Properties();
        properties.setProperty(key1, value1);
        properties.setProperty(key2, value2);
    }

    public static int generateRandomId(int max,int min){

       //Random rand = new Random();
       double random = Math.random()*(max-min)+min;
        int randomId = (int) random;
        return randomId;

    }
//    public static String generateRandomPassword(){
//        String pass = "Nopass@%"+generateRandomId(100,999);
//        return pass;
//    }
//
// public static String generatePhone_number() {
//    int randomNumber = generateRandomId(100000, 999999);
//    // Format the number to always have 6 digits, with leading zeros if necessary
//    String formattedNumber = String.format("%06d", randomNumber);
//    return "0171" + formattedNumber; // This will always be 11 characters long
//}

public static void saveUsers(List<UserModel> users) throws IOException {
    JSONArray usersArray = new JSONArray();
    for (UserModel user : users) {
        JSONObject userObject = new JSONObject();
        userObject.put("name", user.getName());
        userObject.put("email", user.getEmail());
        userObject.put("password", user.getPassword());
        userObject.put("phone_number", user.getPhone_number());
        userObject.put("nid", user.getNid());
        userObject.put("role", user.getRole());
        usersArray.add(userObject);
    }

    try (FileWriter file = new FileWriter("./src/test/resources/users.json")) {
        file.write(usersArray.toString());
    }

}

    public static Map<String, String> getUserDetailsByRole(String filePath, String role, int index) throws Exception {
        JSONParser jsonParser = new JSONParser();
        Map<String, String> userDetails = new HashMap<>();

        FileReader reader = new FileReader(filePath);
        Object obj = jsonParser.parse(reader);
        JSONArray userList = (JSONArray) obj;

        int customerCount = 0;
        for (Object userObject : userList) {
            JSONObject user = (JSONObject) userObject;
            String userRole = (String) user.get("role");

            if (role.equals(userRole)) {
                customerCount++;
                if (role.equals("Customer") && customerCount == index) {
                    userDetails.put("email", (String) user.get("email"));
                    userDetails.put("password", (String) user.get("password"));
                    break;
                } else if (!role.equals("Customer")) {
                    userDetails.put("email", (String) user.get("email"));
                    userDetails.put("password", (String) user.get("password"));
                    break;
                }
            }
        }

        return userDetails;
    }


    public static Map<String, String> getUserDetailsByRole(String filePath, String role) throws Exception {
        JSONParser jsonParser = new JSONParser();
        Map<String, String> userDetails = new HashMap<>();

        FileReader reader = new FileReader(filePath);
        Object obj = jsonParser.parse(reader);
        JSONArray userList = (JSONArray) obj;

        for (Object userObject : userList) {
            JSONObject user = (JSONObject) userObject;
            String userRole = (String) user.get("role");

            if (role.equals(userRole)) {
                userDetails.put("email", (String) user.get("email"));
                userDetails.put("password", (String) user.get("password"));
                break; // Exit loop after finding the first match
            }
        }

        return userDetails;
    }

    public static String loginAndGetToken(String email, String password) throws IOException, ConfigurationException {
        UserController userController = new UserController();
        return userController.doLogin(email, password);
    }

}


