package petstore;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PetStoreTests {
    public String[] petSoreBaseUri;

    {
        try {
            petSoreBaseUri = configuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(groups = {"Functional", "Happy_Path"}, description = "Verify order can be placed for pet correctly")
    public void _1_petStoreOrder() throws IOException {

        //Arrange
        RestAssured.baseURI = petSoreBaseUri[0];

        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json; charset=utf8");

        //Act
        var response = request.body("{\"id\": \"6178881\",\"petId\": \"1\",\"quantity\": \"1\",\"shipDate\": \"2022-04-03T11:40:11.305Z\",\"status\": \"placed\",\"complete\": \"true\"}")

                .post("store/order");

        //Assert
        int statusCode = response.statusCode();
        var responseMessage = response.asString();
        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(responseMessage.contains("6178881"));
    }

    @Test(groups = {"Functional", "Happy_Path"}, description = "Verify pets status available can find through te filter")
    public void _2_petFindByStatus() throws IOException {

        //Arrange
        RestAssured.baseURI = petSoreBaseUri[0];
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json; charset=utf8");

        //Act
        var response = request

                .get("pet/findByStatus?status=available");
        var responseMessage = response.asString();

        //Assert
        int statusCode = response.statusCode();
        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(responseMessage.contains("available"));
    }

    @Test(groups = {"Functional", "Negative"}, description = "Verify deletion with invalid order ID returns an error")
    public void _3_petDeletionWithInvalidOrderId() throws IOException {

        //Arrange
        RestAssured.baseURI = petSoreBaseUri[0];
        var orderID = 11111;
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json; charset=utf8");

        //Act
        var response = request
                .delete("store/order/" + orderID + "");

        //Assert
        int statusCode = response.statusCode();
        var responseMessage = response.asString();
        Assert.assertEquals(statusCode, 404);
        Assert.assertTrue(responseMessage.contains("Order Not Found"));
    }

    @Test(groups = {"Functional", "Happy_Path"}, description = "Verify new pet can be added to the store")
    public void _4_addNewPet() throws IOException {

        //Arrange
        RestAssured.baseURI = petSoreBaseUri[0];
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json; charset=utf8");

        //Act
        var response = request.body(" {\"id\": 1677799,\"category\": {\"id\": 898888,\"name\":\"category\"}, \"name\": \"new_dog\", \"photoUrls\": [ \"string\"  ], \"tags\": [ {\"id\": 677,\"name\": \"someName\"}], \"status\": \"available\"}")
                .post("pet");

        //Assert
        int statusCode = response.statusCode();
        var responseMessage = response.asString();
        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(responseMessage.contains("898888"));
        Assert.assertTrue(responseMessage.contains("new_dog"));
    }

    @Test(groups = {"Functional", "Happy_Path"}, description = "Verify existing pet details can be updated")
    public void _5_updateExistingPet() throws IOException {

        //Arrange
        RestAssured.baseURI = petSoreBaseUri[0];
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json; charset=utf8");

        //Act
        var response = request.body(" {\"id\": 1677799,\"category\": {\"id\": 898888,\"name\":\"string\"}, \"name\": \"MyDogieNameUpdated\", \"photoUrls\": [ \"string\"  ], \"tags\": [ {\"id\": 677,\"name\": \"someName\"}], \"status\": \"available\"}")
                .put("pet");

        //Assert
        int statusCode = response.statusCode();
        var responseMessage = response.asString();
        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(responseMessage.contains("1677799"));
        Assert.assertTrue(responseMessage.contains("MyDogieNameUpdated"));
    }

    @Test(groups = {"Functional", "Happy_Path"}, description = "Verify user is logged out in user session")
    public void _6_userLogout() throws IOException {

        //Arrange
        RestAssured.baseURI = petSoreBaseUri[0];

        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json; charset=utf8");

        //Act
        var response = request.get("user/logout");
        var responseMessage = response.asString();

        //Assert
        int statusCode = response.statusCode();
        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(responseMessage.contains("ok"));
    }

    @Test(groups = {"UseCase"}, description = "Verify user can be created, listed, updated and log-in in pet store system ")
    public void _7_userOperations() throws IOException {

        //Arrange
        String userName = "rostestuser";
        String userFirstName = "testuserfirstname";
        String userLastName = "testuserlastname";
        String userPassword = "test345167";

        RestAssured.baseURI = petSoreBaseUri[0];
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json; charset=utf8");

        //Create User with valid inputs
        //Act
        var createUserResponse = request.body("{\"id\": 561,\"username\": \"" + userName + "\",\"firstName\": \"" + userFirstName + "\",\"lastName\": \"" + userLastName + "\",\"email\": \"testuser123@gmail.com\",\"password\": \"" + userPassword + "\",\"phone\": \"12121212\",\"userStatus\": 1 }")
                .post("user");

        //Assert
        int createUserStatusCode = createUserResponse.statusCode();
        Assert.assertEquals(createUserStatusCode, 200);

        //Get User by username
        //Act
        var getUserResponse = request.get("user/" + userName + "");

        //Assert
        int getUserStatusCode = getUserResponse.statusCode();
        Assert.assertEquals(getUserStatusCode, 200);

        //User Login with valid credentials
        //Act
        var loginUserResponse = request.get("user/login?username=" + userName + "&password=" + userPassword + "");

        //Assert
        int loginUserStatusCode = loginUserResponse.statusCode();
        Assert.assertEquals(loginUserStatusCode, 200);

        //Update the user details with username
        //Act
        var updateUserResponse = request.body("{\"id\": 561,\"username\": \"" + userName + "\",\"firstName\": \"" + userFirstName + "\",\"lastName\": \"" + userLastName + "\",\"email\": \"testuserupdated123@gmail.com\",\"password\": \"" + userPassword + "\",\"phone\": \"12121256\",\"userStatus\": 1 }")
                .put("user/" + userName + "");

        //Assert
        int updateUserStatusCode = updateUserResponse.statusCode();
        Assert.assertEquals(updateUserStatusCode, 200);
    }

    @Test(groups = {"Functional", "Negative"}, description = "Verify user try to delete with incorrect username returns an error ")
    public void _8_deleteIncorrectUser() throws IOException {

        //Arrange
        int incorrectUserName = 1222;

        RestAssured.baseURI = petSoreBaseUri[0];

        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json; charset=utf8");

        //Act
        var deleteIncorrectUserResponse = request.delete("user/" + incorrectUserName + "");

        //Assert
        int incorrectUserDeleteStatusCode = deleteIncorrectUserResponse.statusCode();
        Assert.assertEquals(incorrectUserDeleteStatusCode, 404);
    }

    public String[] configuration() throws IOException {

        String petBaseUri;

        //This section required when the inputs are given through config files
        /*
        Properties properties = new Properties();
        String basePath = new File("src/main/resources/config-petstore.properties").getAbsolutePath();
        FileInputStream fileInputStream = new FileInputStream(basePath);
        properties.load(fileInputStream);
        petBaseUri = properties.getProperty("PETSTOREURI");
        */


        petBaseUri = System.getenv("PETSTOREURI");

        return new String[]{petBaseUri};
    }
}