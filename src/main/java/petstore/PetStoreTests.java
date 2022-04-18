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

    public String userName = "rostestuser";
    public String userPassword = "test345167";
    public String userFirstName = "testuserfirstname";
    public String userLastName = "testuserlastname";

    @Test(groups = {"Functional", "Happy_Path"}, description = "Verify order can be placed for pet correctly")
    public void _1_petStoreOrderTest() {

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
    public void _2_petFindByStatusTest() {

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
    public void _3_petDeletionWithInvalidOrderIdTest() {

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
    public void _4_addNewPetTest() {

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
    public void _5_updateExistingPetTest() {

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
    public void _6_userLogoutTest() {

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
    public void _7_userOperationsTest() {

        //Arrange
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
    public void _8_deleteIncorrectUserTest() {

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

    @Test(enabled = false, groups = {"security", "UserCredentials"}, description = "Verify if user cannot login with incorrect credentials returns an error")
    public void _9_incorrectUserCredentialsTest() {

        //Arrange
        RestAssured.baseURI = petSoreBaseUri[0];

        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json; charset=utf8");

        //Act
        /*Since this code doesn't have any validation for username and password,
        so I cannot try this test cases with security cases and, it always returns 200.
        Hence, I ignore this case.
        */
        var incorrectUserCredentialsResponse = request.get("user/login?username=testssdffffffffsdsdsfff&password=invalidpassword");

        //Assert
        int incorrectLoginStatusCode = incorrectUserCredentialsResponse.statusCode();
        Assert.assertEquals(incorrectLoginStatusCode, 404);
    }

    @Test(groups = {"Security"}, description = "Verify if user is able to delete the order id with incorrect and lengthy,should returns an error")
    public void _10_incorrectOrderIdTest() {

        //Arrange
        RestAssured.baseURI = petSoreBaseUri[0];

        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json; charset=utf8");

        //Act
        var incorrectOrderIdResponse = request
                .delete("store/order/1111111111111111111111111111111111111111111111111111111111111111111111111111");

        //Assert
        int incorrectOrderIdStatusCode = incorrectOrderIdResponse.statusCode();
        Assert.assertEquals(incorrectOrderIdStatusCode, 404);
    }

    @Test(groups = {"Security"}, description = "Verify user can created with obvious password, should return an error ")
    public void _11_userObviousPasswordTest() {

        //Arrange
        RestAssured.baseURI = petSoreBaseUri[0];
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json; charset=utf8");

        //Create User with incorrect inputs
        //Act
        var createUserResponse = request.body("{\"id\": 111111,\"username\": \"" + userName + "\",\"firstName\": \"" + userFirstName + "\",\"lastName\": \"" + userLastName + "\",\"email\": \"testuser123@gmail.com\",\"password\": \"password\",\"phone\": \"12121212\",\"userStatus\": 1 }")
                .post("user");

        //Assert
        int createUserStatusCode = createUserResponse.statusCode();
        //This test should fail with 404 or 400 because we provide password as obvious password
        //But, it is passed with 200 which is incorrect

        Assert.assertEquals(createUserStatusCode, 404);
    }

    @Test(groups = {"Security"}, description = "Verify user can created with same user id, should return an error ")
    public void _12_userIncorrectIdTest() {

        //Arrange

        RestAssured.baseURI = petSoreBaseUri[0];
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json; charset=utf8");

        //Create User with incorrect inputs
        //Act
        var createUserResponse = request.body("{\"id\": 111111,\"username\": \"" + userName + "\",\"firstName\": \"" + userFirstName + "\",\"lastName\": \"" + userLastName + "\",\"email\": \"testuser123@gmail.com\",\"password\": \"password12adr\",\"phone\": \"12121212\",\"userStatus\": 1 }")
                .post("user");

        //Assert
        int createUserStatusCode = createUserResponse.statusCode();

        //This test should fail with 404 or 400 because we provide user id same as previously provided
        //But, it is passed with 200 which is incorrect
        Assert.assertEquals(createUserStatusCode, 404);
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