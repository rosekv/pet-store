
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class PetStoreTests {

    @Test(groups = {"Functional", "Happy_Path"}, description = "Verify placed an order for pet done correctly")
    public void petStoreOrder() throws IOException {

        //Arrange
        String config[] = configuration();


        RestAssured.baseURI = config[0];

        //Act
        RequestSpecification request = RestAssured.given();

        request.header("Content-Type", "application/json; charset=utf8");

        //Act
        var response = request.body("{\"id\": \"6178881\",\"petId\": \"1\",\"quantity\": \"1\",\"shipDate\": \"2022-04-03T11:40:11.305Z\",\"status\": \"placed\",\"complete\": \"true\"}")

                .post("store/order");
        var responseMessage = response.asString();


        //Assert
        int statusCode = response.statusCode();
        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(responseMessage.contains("6178881"));
    }


    public String[] configuration() throws IOException {
        String petBaseUri;

        //This section required when the inputs are given through config files and disable the enabled section
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
